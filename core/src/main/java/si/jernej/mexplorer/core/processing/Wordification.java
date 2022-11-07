package si.jernej.mexplorer.core.processing;

import static si.jernej.mexplorer.core.util.Constants.COMPOSITE_TABLE_NAME;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.persistence.Table;
import javax.ws.rs.InternalServerErrorException;

import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;

@Dependent
public class Wordification
{

    /**
     * Enum used to specify which concatenation schema to use.
     * {@code ZERO} means not to use any concatenations
     * {@code ONE} means to concatenate pairs of features
     * {@code TWO} means to concatenate triplets of features
     */
    public enum ConcatenationScheme
    {
        ZERO,
        ONE,
        TWO
    }

    /**
     * Implementation of the Wordification algorithm for an entity with a specified id.
     *
     * @param rootEntity the root entity for which to compute the results of Wordification
     * @param propertySpec specifies which properties of which entities to include in the Wordification algorithm
     * @param valueTransformer {@link ValueTransformer} instance used to specify the value transformations
     * @param compositeColumnCreator {@link CompositeColumnCreator} instance used to specify the creation of composite columns
     * @param concatenationScheme {@link ConcatenationScheme} instance used to specify the word concatenations
     * @return {@code List} of obtained words for specified root entity
     */
    public List<String> wordify(Object rootEntity, PropertySpec propertySpec, ValueTransformer valueTransformer, CompositeColumnCreator compositeColumnCreator, ConcatenationScheme concatenationScheme)
    {
        // list of resulting words
        List<String> wordsAll = new ArrayList<>();

        // initialize set of visited tables.
        Set<String> visitedEntities = new HashSet<>();

        // initialize BFS queue
        Queue<Object> bfsQueue = new LinkedList<>();
        bfsQueue.add(rootEntity);

        try
        {
            while (!bfsQueue.isEmpty())
            {
                // Get next entity from queue and get its simple class name.
                Object nxt = bfsQueue.remove();
                String entityName = nxt.getClass().getSimpleName();

                // Add to set of visited entities.
                if (visitedEntities.contains(entityName))
                {
                    continue;
                }
                else
                {
                    visitedEntities.add(entityName);
                }

                // Initialize list for words obtained from next table.
                List<String> wordsForEntity = new ArrayList<>();

                // Go over entity's properties.
                for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(nxt.getClass()).getPropertyDescriptors())
                {

                    Object nxtProperty = propertyDescriptor.getReadMethod().invoke(nxt);

                    // if property should be included as a word
                    if (propertySpec.containsEntry(entityName, propertyDescriptor.getName()))
                    {
                        wordsForEntity.add("%s@%s@%s".formatted(entityName, propertyDescriptor.getName(), valueTransformer.applyTransform(entityName, propertyDescriptor.getName(), nxtProperty)).toLowerCase().replace(' ', '_'));
                    }
                    // if property collection of linked entities
                    else if (nxtProperty instanceof Collection && !((Collection<?>) nxtProperty).isEmpty())
                    {
                        Class<?> aClass = ((Collection<?>) nxtProperty).iterator().next().getClass();

                        // If collection of linked entities that were not yet visited, add to queue.
                        if (!visitedEntities.contains(aClass.getSimpleName()) && aClass.isAnnotationPresent(Table.class))
                        {
                            bfsQueue.addAll((Collection<?>) nxtProperty);
                        }
                    }
                    else if (nxtProperty != null)
                    {

                        Class<?> aClass = nxtProperty.getClass();

                        // If property linked entity and not yet visited, add to queue.
                        if (!visitedEntities.contains(nxtProperty.getClass().getSimpleName()) && aClass.isAnnotationPresent(Table.class))
                        {
                            bfsQueue.add(nxtProperty);
                        }
                    }
                }

                // Add all words and concatenations for entity to results list.
                wordsAll.addAll(addConcatenations(wordsForEntity, concatenationScheme));
            }
        }
        catch (IntrospectionException | IllegalAccessException | InvocationTargetException e)
        {
            throw new InternalServerErrorException("error computing Wordification");
        }

        // Add values from composite columns.
        Map<String, List<Object>> compositeColumns = compositeColumnCreator.processEntries(Collections.singletonList(rootEntity));
        List<String> wordsForComposite = new ArrayList<>();
        compositeColumns.forEach((k, l) -> l.forEach(v -> wordsForComposite.add(String.format("%s@%s@%s", COMPOSITE_TABLE_NAME, k, valueTransformer.applyTransform(COMPOSITE_TABLE_NAME, k, v)).toLowerCase().replace(' ', '_'))));

        // Add all words and concatenations for composite table to results list.
        wordsAll.addAll(addConcatenations(wordsForComposite, concatenationScheme));

        return wordsAll;
    }

    /**
     * Construct word concatenation features from list of provided words and return list of original words with
     * concatenations appended.
     *
     * @param words {@code List} of words for which to add concatenations
     * @param concatenationScheme which concatenation schema to use
     */
    private List<String> addConcatenations(List<String> words, ConcatenationScheme concatenationScheme)
    {

        List<String> wordsWithConcatenations = new ArrayList<>(words);

        switch (concatenationScheme)
        {

            case ZERO -> {
            }

            case ONE -> addConcatenationsOne(words, wordsWithConcatenations);

            case TWO -> {
                addConcatenationsOne(words, wordsWithConcatenations);
                addConcatenationsTwo(words, wordsWithConcatenations);
            }

        }
        return wordsWithConcatenations;
    }

    /**
     * Add t_p_v__t_p'_v' type composite words.
     */
    private void addConcatenationsOne(List<String> words, List<String> wordsWithConcatenations)
    {
        for (int i = 0; i < words.size() - 1; i++)
        {
            for (int j = i + 1; j < words.size(); j++)
            {
                wordsWithConcatenations.add(String.format("%s@@%s", words.get(i), words.get(j)));
            }
        }
    }

    /**
     * Add t_p_v__t_p'_v'__t_p''_v'' type composite words.
     */
    private void addConcatenationsTwo(List<String> words, List<String> wordsWithConcatenations)
    {
        for (int i = 0; i < words.size() - 2; i++)
        {
            for (int j = i + 1; j < words.size() - 1; j++)
            {
                for (int k = j + 1; k < words.size(); k++)
                {
                    wordsWithConcatenations.add(String.format("%s@@%s@@%s", words.get(i), words.get(j), words.get(k)));
                }
            }
        }
    }

}
