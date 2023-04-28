package com.github.jernejvivod.ehrexplorer.core.processing.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.ws.rs.InternalServerErrorException;

import com.github.jernejvivod.ehrexplorer.annotation.PropertyOrder;
import com.github.jernejvivod.ehrexplorer.core.util.EntityUtils;

@Singleton
public class OrderedEntityPropertyDescriptors
{
    @PersistenceContext
    private EntityManager em;

    private final Map<Class<?>, PropertyDescriptor[]> classToOrderedPropertyDescriptors = new HashMap<>();

    @PostConstruct
    private void postConstruct()
    {
        for (EntityType<?> entityType : em.getMetamodel().getEntities())
        {
            // initialize mapping of property names to associated property position
            Map<String, Integer> propertyNameToPosition = getPropertyNameToPosition(entityType);

            // get ordered property descriptors
            PropertyDescriptor[] propertyDescriptorsOrdered = getOrderedPropertyDescriptors(propertyNameToPosition, entityType);

            this.classToOrderedPropertyDescriptors.put(entityType.getJavaType(), propertyDescriptorsOrdered);
        }
    }

    /**
     * Get mapping from property names to their order for entity.
     *
     * @param entityType entity's type
     * @return mapping for property names to their ordering
     */
    private static Map<String, Integer> getPropertyNameToPosition(EntityType<?> entityType)
    {
        Map<String, Integer> propertyNameToPosition = new HashMap<>();

        // names of properties without PropertyOrder annotations
        Set<String> withoutOrderAnnotation = new HashSet<>();

        Set<Field> fields = EntityUtils.getFieldsUpToObject(entityType.getJavaType());

        // map property names to their positions
        for (Field declaredField : fields)
        {
            if (!declaredField.isAnnotationPresent(PropertyOrder.class))
            {
                withoutOrderAnnotation.add(declaredField.getName());
                continue;
            }

            // set position of property based on annotation
            int propertyPosition = declaredField.getAnnotation(PropertyOrder.class).value();

            if (propertyPosition <= 0 || propertyPosition > fields.size())
            {
                throw new InternalServerErrorException("Wrong position specified for property '%s' of entity '%s'. Specified position must be between 1 and %s.".formatted(
                        declaredField.getName(),
                        entityType.getJavaType().getSimpleName(),
                        fields.size()
                ));
            }

            if (propertyNameToPosition.containsValue(propertyPosition))
            {
                throw new InternalServerErrorException("Position %d of property '%s' of entity '%s' has already been specified.".formatted(
                        propertyPosition,
                        declaredField.getName(),
                        entityType.getJavaType().getSimpleName()
                ));
            }

            propertyNameToPosition.put(declaredField.getName(), propertyPosition);
        }

        // assign top positions to properties without order annotations
        int maxAssigned = propertyNameToPosition.values().stream().max(Integer::compareTo).orElse(0);
        for (String p : withoutOrderAnnotation)
        {
            propertyNameToPosition.put(p, maxAssigned++);
        }

        return propertyNameToPosition;
    }

    /**
     * Get {@link PropertyDescriptor} instances for entity's properties based on property ordering annotation values.
     *
     * @param propertyNameToPosition mapping of property names to their positions (extracted from {@link PropertyOrder} annotations)
     * @param entityType type of entity for which to get the ordered property descriptors
     * @return array of ordered {@link PropertyDescriptor} instances for entity
     */
    private static PropertyDescriptor[] getOrderedPropertyDescriptors(Map<String, Integer> propertyNameToPosition, EntityType<?> entityType)
    {
        // put property descriptors in order
        PropertyDescriptor[] propertyDescriptorsOrdered = new PropertyDescriptor[propertyNameToPosition.size()];
        try
        {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(entityType.getJavaType(), Object.class).getPropertyDescriptors())
            {
                propertyDescriptorsOrdered[propertyNameToPosition.get(propertyDescriptor.getName()) - 1] = propertyDescriptor;
            }
        }
        catch (IntrospectionException ex)
        {
            throw new InternalServerErrorException(ex);
        }
        return propertyDescriptorsOrdered;
    }

    public PropertyDescriptor[] getForEntity(Class<?> entityClass)
    {
        return Optional.ofNullable(this.classToOrderedPropertyDescriptors.get(entityClass))
                .orElseThrow(() -> new InternalServerErrorException("Ordered property descriptors for entity '%s' not found.".formatted(entityClass.getSimpleName())));
    }

}
