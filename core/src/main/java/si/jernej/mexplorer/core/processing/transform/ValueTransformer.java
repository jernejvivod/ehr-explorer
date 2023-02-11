package si.jernej.mexplorer.core.processing.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.CheckForNull;
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

    public record Transform(Function<Object, ?> transformFunction) implements Function<Object, Object>
    {
        @Override
        public Object apply(Object o)
        {
            return o != null ? transformFunction.apply(o) : null;
        }
    }

    /**
     * Add transformation for an entity's property.
     *
     * @param entity name of entity for which to add a transformation
     * @param property name of property for which to add a transformation
     * @param transform the transformation to perform
     */
    public void addTransform(String entity, String property, Transform transform)
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

    public void assertValid(Metamodel metamodel, @CheckForNull CompositeColumnCreator compositeColumnCreator)
    {
        entityToPropertyToTransform.forEach((entityName, map) -> {
            for (String properyName : map.keySet())
            {
                if (compositeColumnCreator != null)
                {
                    EntityUtils.assertEntityAndPropertyValid(entityName, properyName, metamodel, compositeColumnCreator);
                }
                else
                {
                    EntityUtils.assertEntityAndPropertyValid(entityName, properyName, metamodel);
                }
            }
        });
    }
}
