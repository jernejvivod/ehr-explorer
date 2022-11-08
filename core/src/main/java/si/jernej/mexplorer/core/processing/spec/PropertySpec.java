package si.jernej.mexplorer.core.processing.spec;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class representing the specification of which properties of which entities to include for the
 * Wordification algorithm.
 */
public class PropertySpec
{
    private final Map<String, Set<String>> entityToPropertiesToProcess;

    public PropertySpec()
    {
        this.entityToPropertiesToProcess = new HashMap<>();
    }

    /**
     * Add entry to the specification.
     *
     * @param entity name of entity
     * @param property name of the entity's property
     */
    public void addEntry(String entity, String property)
    {
        entityToPropertiesToProcess.computeIfAbsent(entity, e -> new HashSet<>()).add(property);
    }

    /**
     * Add entries to the specification.
     *
     * @param entity name of entity
     * @param properties name of entity's property
     */
    public void addEntry(String entity, Collection<String> properties)
    {
        entityToPropertiesToProcess.computeIfAbsent(entity, e -> new HashSet<>()).addAll(properties);
    }

    /**
     * Check if property name for entity specified.
     *
     * @param entity name of entity
     * @param propertyName name of entity's property
     * @return is the entity's property specified to be part of the Wordification algorithm's output
     */
    public boolean containsEntry(String entity, String propertyName)
    {
        return this.entityToPropertiesToProcess.containsKey(entity) && this.entityToPropertiesToProcess.get(entity).contains(propertyName);
    }

    public Map<String, Set<String>> getEntityToPropertiesToProcess()
    {
        return entityToPropertiesToProcess;
    }
}
