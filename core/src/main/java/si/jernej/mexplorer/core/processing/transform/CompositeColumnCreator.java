package si.jernej.mexplorer.core.processing.transform;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

import javax.persistence.metamodel.Metamodel;

import org.apache.commons.beanutils.PropertyUtils;

import si.jernej.mexplorer.common.exception.ValidationCoreException;
import si.jernej.mexplorer.core.util.EntityUtils;

/**
 * Class implementing functionality for creating composite columns.
 */
public class CompositeColumnCreator
{
    /**
     * Record representing a specification entry for the creation of a composite column.
     */
    public record Entry(List<String> foreignKeyPath1, String property1, List<String> foreignKeyPath2, String property2, String compositeName, BiFunction<Object, Object, Object> combiner)
    {

        public List<String> getForeignKeyPath1()
        {
            return foreignKeyPath1;
        }

        public List<String> getForeignKeyPath2()
        {
            return foreignKeyPath2;
        }

        public String getProperty1()
        {
            return property1;
        }

        public String getProperty2()
        {
            return property2;
        }

        public String getCompositeName()
        {
            return compositeName;
        }

        public BiFunction<Object, Object, Object> getCombiner()
        {
            return combiner;
        }
    }

    private final List<Entry> entries;

    public CompositeColumnCreator()
    {
        this.entries = new ArrayList<>();
    }

    public List<Entry> getEntries()
    {
        return entries;
    }

    /**
     * Add specification entry for a composite column creation.
     *
     * @param foreignKeyPath1 foreign key path to the first property constituting the composite column
     * @param property1 name of first property constituting the composite column
     * @param foreignKeyPath2 foreign key path to the second property constituting the composite column
     * @param property2 name of second property constituting the composite column
     * @param compositeName name of the new composite property
     * @param combiner BiFunction instance used to combine the property columns forming the composite column
     */
    public void addEntry(List<String> foreignKeyPath1, String property1, List<String> foreignKeyPath2, String property2, String compositeName, BinaryOperator<Object> combiner)
    {
        entries.add(new Entry(foreignKeyPath1, property1, foreignKeyPath2, property2, compositeName, combiner));
    }

    /**
     * Create composite columns in new table as specified by the added entries.
     *
     * @param rootEntities {@code List} of root entities
     * @return created composite columns in order of specified entries
     */
    public Map<String, List<Object>> processEntries(List<?> rootEntities)
    {
        // set columns in new table for entity
        Map<String, List<Object>> resultsForEntity = new HashMap<>();

        // Go over entries.
        for (Entry entry : entries)
        {

            // Initialize lists for column values to be combined.
            List<Object> res1Prop = new ArrayList<>();
            List<Object> res2Prop = new ArrayList<>();

            // Get first list of entities containing the property to be combined.
            List<Object> res1 = EntityUtils.traverseSingularForeignKeyPath(rootEntities, entry.getForeignKeyPath1());

            // Get second list of entities containing the property to be combined.
            List<Object> res2 = EntityUtils.traverseSingularForeignKeyPath(rootEntities, entry.getForeignKeyPath2());

            // Get list of property values for both columns forming the composite column.
            for (Object r : res1)
            {
                try
                {
                    res1Prop.add(PropertyUtils.getProperty(r, entry.getProperty1()));
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    throw new ValidationCoreException("Error accessing property '%s'".formatted(entry.getProperty1()));
                }
            }

            for (Object r : res2)
            {
                try
                {
                    res2Prop.add(PropertyUtils.getProperty(r, entry.getProperty2()));
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    throw new ValidationCoreException("Error accessing property '%s'".formatted(entry.getProperty2()));
                }
            }

            if (res1Prop.size() != res2Prop.size())
            {
                throw new ValidationCoreException("Size of columns forming the composite column is not equal.");
            }

            // Combine properties of entities.
            resultsForEntity.put(
                    entry.getCompositeName(),
                    IntStream.range(0, res1Prop.size())
                            .mapToObj(i -> entry.getCombiner().apply(res1Prop.get(i), res2Prop.get(i)))
                            .toList()
            );
        }

        return resultsForEntity;
    }

    public void assertValid(Metamodel metamodel)
    {
        this.entries.forEach(e -> {
            EntityUtils.assertForeignKeyPathValid(e.getForeignKeyPath1(), metamodel);
            EntityUtils.assertForeignKeyPathValid(e.getForeignKeyPath2(), metamodel);

            EntityUtils.assertEntityAndPropertyValid(e.getForeignKeyPath1().get(e.getForeignKeyPath1().size() - 1), e.getProperty1(), metamodel);
            EntityUtils.assertEntityAndPropertyValid(e.getForeignKeyPath2().get(e.getForeignKeyPath2().size() - 1), e.getProperty2(), metamodel);
        });
    }
}