package si.jernej.mexplorer.core.processing.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.ws.rs.InternalServerErrorException;

import si.jernej.mexplorer.core.util.EntityUtils;
import si.jernej.mexplorer.entity.annotation.PropertyOrder;

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
            Map<String, Integer> propertyNameToPosition = new HashMap<>();

            Set<Field> fields = EntityUtils.getFieldsUpToObject(entityType.getJavaType());

            for (Field declaredField : fields)
            {
                if (!declaredField.isAnnotationPresent(PropertyOrder.class))
                {
                    throw new InternalServerErrorException("Annotation %s not specified for property '%s' of entity '%s'".formatted(
                            PropertyOrder.class.getName(),
                            declaredField.getName(),
                            entityType.getJavaType().getSimpleName()
                    ));
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

            // put property descriptors in order
            PropertyDescriptor[] propertyDescriptorsOrdered = new PropertyDescriptor[fields.size()];
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

            this.classToOrderedPropertyDescriptors.put(entityType.getJavaType(), propertyDescriptorsOrdered);
        }
    }

    public PropertyDescriptor[] getForEntity(Class<?> entityClass)
    {
        return Optional.ofNullable(this.classToOrderedPropertyDescriptors.get(entityClass))
                .orElseThrow(() -> new InternalServerErrorException("Ordered property descriptors for entity '%s' not found.".formatted(entityClass.getSimpleName())));
    }

}
