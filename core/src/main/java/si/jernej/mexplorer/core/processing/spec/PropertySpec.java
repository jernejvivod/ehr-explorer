package si.jernej.mexplorer.core.processing.spec;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// TODO add validation

/**
 * Class representing the specification of which properties of which entities to include for the
 * Wordification algorithm.
 */
public class PropertySpec
{
    private final Map<String, Set<String>> entityToPropertiesToProcess;
    private final Map<String, String> sortSpecs;
    private final Map<String, DurationLimitSpec> durationLimitSpecs;

    public record DurationLimitSpec(String propertyName, Duration durationLimit)
    {
    }

    public PropertySpec()
    {
        this.entityToPropertiesToProcess = new HashMap<>();
        this.sortSpecs = new HashMap<>();
        this.durationLimitSpecs = new HashMap<>();
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

    /**
     * Add sort specification
     *
     * @param entity entity for which to apply the sorting
     * @param property the property by which to sort
     */
    public void addSort(String entity, String property)
    {
        this.sortSpecs.put(entity, property);
    }

    /**
     * Get sort property for entity
     *
     * @param entity name of entity
     */
    public Optional<String> getSortProperty(String entity)
    {
        return Optional.ofNullable(this.sortSpecs.get(entity));
    }

    /**
     * Add specification for limiting linked records based on duration.
     *
     * @param entity entity for which to apply the limit
     * @param property property specifying the time
     * @param limit duration specifying the amount of time for which to take the records (measured from the time of the first record)
     */
    public void addDurationLimitSpec(String entity, String property, Duration limit)
    {
        this.durationLimitSpecs.put(entity, new DurationLimitSpec(property, limit));
    }

    /**
     * Get duration limit specification for entity
     *
     * @param entity name of entity
     */
    public Optional<DurationLimitSpec> getDurationLimitSpecForEntity(String entity)
    {
        return Optional.ofNullable(this.durationLimitSpecs.get(entity));
    }

    // getter
    public Map<String, Set<String>> getEntityToPropertiesToProcess()
    {
        return entityToPropertiesToProcess;
    }
}
