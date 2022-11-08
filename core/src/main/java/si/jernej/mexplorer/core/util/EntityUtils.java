package si.jernej.mexplorer.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Id;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Metamodel;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.processing.spec.PropertySpec;

public final class EntityUtils
{
    private EntityUtils()
    {
    }

    /**
     * Compute entity name for specified property name for that entity.
     *
     * @param propertyName specified property name
     * @return computed entity name
     */
    public static String propertyNameToEntityName(String propertyName)
    {
        String capitalizedAttributeName = StringUtils.capitalize(propertyName);
        return capitalizedAttributeName.charAt(capitalizedAttributeName.length() - 1) == 's' ?
                capitalizedAttributeName.substring(0, capitalizedAttributeName.length() - 1) :
                capitalizedAttributeName;
    }

    /**
     * Compute property name for specified entity name.
     *
     * @param entityName specified entity name
     * @param singular is the property singular or plural
     * @return computed property name
     */
    public static String entityNameToPropertyName(String entityName, boolean singular)
    {
        String uncapitalizedEntityName = StringUtils.uncapitalize(entityName);
        return singular ? uncapitalizedEntityName : uncapitalizedEntityName + 's';
    }

    /**
     * Compute foreign key path from root entity table to target entity table.
     *
     * @param rootEntityName name of root entity
     * @param targetEntityName name of target entity
     * @param entityToLinkedEntities mapping of entity names to name of their linked entities
     * @return computed foreign key path
     * @throws IllegalArgumentException if path not found
     */
    public static List<String> computeForeignKeyPath(String rootEntityName, String targetEntityName, Map<String, Set<String>> entityToLinkedEntities) throws IllegalArgumentException
    {
        if (!entityToLinkedEntities.containsKey(rootEntityName))
        {
            throw new ValidationCoreException("Root entity not found");
        }

        // run BFS to find path
        Queue<List<String>> bfsQueue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        bfsQueue.add(new ArrayList<>(Collections.singletonList(rootEntityName)));
        while (!bfsQueue.isEmpty())
        {
            List<String> pathNxt = bfsQueue.remove();
            if (pathNxt.get(pathNxt.size() - 1).equals(targetEntityName))
            {
                return pathNxt;
            }

            visited.add(pathNxt.get(pathNxt.size() - 1));

            for (String linkedEntity : entityToLinkedEntities.get(pathNxt.get(pathNxt.size() - 1)))
            {
                if (!visited.contains(linkedEntity))
                {
                    List<String> pathToAdd = new ArrayList<>(pathNxt);
                    pathToAdd.add(linkedEntity);
                    bfsQueue.add(pathToAdd);
                }
            }
        }
        throw new ValidationCoreException("Target entity not found");
    }

    /**
     * Compute mapping of JPA entity names to names of linked JPA entities.
     *
     * @param metamodel JPA Metamodel instance
     * @return computed mapping
     */
    public static Map<String, Set<String>> computeEntityToLinkedEntitiesMap(Metamodel metamodel)
    {
        Set<String> managedEntityNames = metamodel.getEntities().stream().map(e -> e.getJavaType().getSimpleName()).collect(Collectors.toSet());

        return metamodel.getEntities().stream().collect(Collectors.toMap(
                e -> e.getJavaType().getSimpleName(),
                e -> {
                    Set<String> linkedEntities = new HashSet<>();
                    e.getDeclaredAttributes().forEach(a -> {
                        String attributeEntityName = EntityUtils.propertyNameToEntityName(a.getName());
                        if (managedEntityNames.contains(attributeEntityName))
                        {
                            linkedEntities.add(attributeEntityName);
                        }
                    });
                    return linkedEntities;
                }
        ));
    }

    public static Map<String, Set<String>> computeEntityNameToAttributes(Metamodel metamodel)
    {
        return metamodel
                .getManagedTypes().stream()
                .collect(Collectors.toMap(
                                m -> m.getJavaType().getSimpleName(),
                                m -> m.getAttributes().stream()
                                        .map(Attribute::getName)
                                        .collect(Collectors.toSet())
                        )
                );
    }

    /**
     * Get Set of ID property values for entities at end of specified foreign key path.
     *
     * @param rootEntity root entity
     * @param foreignKeyPath foreign key path
     * @return computed ID property values
     */
    public static <T> Set<T> computeIdPropertyValuesForForeignPathEnd(Object rootEntity, List<String> foreignKeyPath)
    {
        Set<Object> endEntities = traverseForeignKeyPath(rootEntity, foreignKeyPath);
        Set<T> res = new HashSet<>();
        if (!endEntities.isEmpty())
        {
            String idMethodName = Arrays.stream(endEntities.iterator().next().getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Id.class)).findAny().orElseThrow().getName();
            for (Object endEntity : endEntities)
            {
                try
                {
                    //noinspection unchecked
                    res.add((T) MethodUtils.invokeMethod(endEntity, idMethodName, null));
                }
                catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * Get entities at end of specified foreign key path for specified root entity.
     *
     * @param rootEntity root entity at start of foreign key path
     * @param foreignKeyPath foreign key path
     * @return set of entities at end of foreign key path
     */
    public static Set<Object> traverseForeignKeyPath(Object rootEntity, List<String> foreignKeyPath)
    {
        Set<Object> objects = new HashSet<>(Set.of(rootEntity));
        for (int i = 1; i < foreignKeyPath.size(); i++)
        {
            String keySingular = EntityUtils.entityNameToPropertyName(foreignKeyPath.get(i), true);
            String keyPlural = EntityUtils.entityNameToPropertyName(foreignKeyPath.get(i), false);
            Set<Object> newEntities = new HashSet<>();
            for (Object object : objects)
            {
                try
                {
                    if (PropertyUtils.isReadable(object, keySingular))
                    {
                        newEntities.add(PropertyUtils.getProperty(object, keySingular));
                    }
                    else if (PropertyUtils.isReadable(object, keyPlural))
                    {
                        newEntities.addAll((Collection<?>) PropertyUtils.getProperty(object, keyPlural));
                    }
                    else
                    {
                        throw new ValidationCoreException("Non-existent key '%s'".formatted(foreignKeyPath.get(i)));
                    }
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
            objects = newEntities;
        }
        return objects;
    }

    public static Object traverseSingularForeignKeyPath(Object rootEntity, List<String> foreignKeyPath)
    {
        Object object = rootEntity;
        for (int i = 1; i < foreignKeyPath.size(); i++)
        {
            String key = EntityUtils.entityNameToPropertyName(foreignKeyPath.get(i), true);
            try
            {
                if (PropertyUtils.isReadable(object, key))
                {
                    object = PropertyUtils.getProperty(object, key);
                }
                else
                {
                    throw new ValidationCoreException("Non-existent singular key '%s'".formatted(foreignKeyPath.get(i)));
                }
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                throw new ValidationCoreException("Error applying singular key '%s'".formatted(foreignKeyPath.get(i)));
            }
        }
        return object;
    }

    public static List<Object> traverseSingularForeignKeyPath(List<?> rootEntitys, List<String> foreignKeyPath)
    {
        return rootEntitys.stream().map(e -> traverseSingularForeignKeyPath(e, foreignKeyPath)).toList();
    }

    public static List<List<String>> getForeignKeyPathsFromPropertySpec(String rootEntityName, PropertySpec propertySpec, Metamodel metamodel)
    {
        final Map<String, Set<String>> entityToPropertiesToProcess = propertySpec.getEntityToPropertiesToProcess();
        final Map<String, Set<String>> entityToLinkedEntities = computeEntityToLinkedEntitiesMap(metamodel);
        final List<List<String>> res = new ArrayList<>();

        entityToPropertiesToProcess.keySet().stream()
                .filter(k -> !k.equals(rootEntityName))
                .forEach(entityName ->
                        res.add(computeForeignKeyPath(rootEntityName, entityName, entityToLinkedEntities))
                );

        return res;
    }
}
