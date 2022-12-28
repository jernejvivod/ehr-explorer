package si.jernej.mexplorer.core.processing.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.persistence.metamodel.Metamodel;

import si.jernej.mexplorer.core.util.EntityUtils;

/**
 * Class implementing value transformations.
 */
public class ValueTransformer
{
    private final Map<String, Map<String, Function<Object, ?>>> entityToPropertyToTransform;
    private final Function<Object, ?> defaultTransform;

    public ValueTransformer()
    {
        this.entityToPropertyToTransform = new HashMap<>();
        this.defaultTransform = val -> val;
    }

    public ValueTransformer(Function<Object, ?> defaultTransform)
    {
        this.entityToPropertyToTransform = new HashMap<>();
        this.defaultTransform = defaultTransform;
    }

    /**
     * Add transformation for an entity's property.
     *
     * @param entity name of entity for which to add a transformation
     * @param property name of property for which to add a transformation
     * @param transform the transformation to perform
     */
    public void addTransform(String entity, String property, Function<Object, ?> transform)
    {
        entityToPropertyToTransform.computeIfAbsent(entity, e -> new HashMap<>()).put(property, transform);
    }

    /**
     * Apply value transformation.
     *
     * @param entity string representing the name of the entity containing the property to transform
     * @param property the name of the property to transform
     * @param value the value of the property to transform
     * @return transformed value
     */
    public Object applyTransform(String entity, String property, Object value)
    {
        if (entityToPropertyToTransform.containsKey(entity) && entityToPropertyToTransform.get(entity).containsKey(property))
        {
            return entityToPropertyToTransform.get(entity).get(property).apply(value);
        }
        else
        {
            return defaultTransform.apply(value);
        }
    }

    public void assertValid(Metamodel metamodel)
    {
        entityToPropertyToTransform.forEach((entityName, map) -> {
            for (String properyName : map.keySet())
            {
                EntityUtils.assertEntityAndPropertyValid(entityName, properyName, metamodel);
            }
        });
    }
}
