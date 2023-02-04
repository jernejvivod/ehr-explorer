package si.jernej.mexplorer.core.processing.util;

import static si.jernej.mexplorer.core.util.Constants.COMPOSITE_TABLE_NAME;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.beanutils.PropertyUtils;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.processing.Wordification;
import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;

public final class WordificationUtil
{
    private WordificationUtil()
    {
    }

    /**
     * Construct word concatenation features from list of provided words and return list of original words with
     * concatenations appended.
     *
     * @param words {@code List} of words for which to add concatenations
     * @param concatenationScheme which concatenation schema to use
     */
    public static List<String> addConcatenations(List<String> words, Wordification.ConcatenationScheme concatenationScheme)
    {

        List<String> wordsWithConcatenations = new ArrayList<>(words);

        switch (concatenationScheme)
        {

            case ZERO ->
            {
            }

            case ONE -> addConcatenationsOne(words, wordsWithConcatenations);

            case TWO ->
            {
                addConcatenationsOne(words, wordsWithConcatenations);
                addConcatenationsTwo(words, wordsWithConcatenations);
            }

        }
        return wordsWithConcatenations;
    }

    /**
     * Add t_p_v__t_p'_v' type composite words.
     */
    private static void addConcatenationsOne(List<String> words, List<String> wordsWithConcatenations)
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
    private static void addConcatenationsTwo(List<String> words, List<String> wordsWithConcatenations)
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

    /**
     * Add linked collections to BFS queue and apply any sorting.
     */
    public static void pushLinkedCollectionToStack(List<Object> dfsStack, PropertySpec propertySpec, Collection<?> collection, Class<?> linkedEntityClass)
    {
        List<?> linkedEntitiesList = propertySpec.getSortProperty(linkedEntityClass.getSimpleName())
                .<List<?>>map(sp -> {
                            try
                            {
                                // get type of property by which to sort. Check if the type implements java.lang.Comparable.
                                Class<?> sortPropertyType = linkedEntityClass.getDeclaredField(sp).getType();
                                if (!Comparable.class.isAssignableFrom(sortPropertyType))
                                {
                                    throw new ValidationCoreException("Values of property '%s' of entity '%s' must implement the Comparable interface"
                                            .formatted(
                                                    propertySpec.getSortProperty(linkedEntityClass.getSimpleName()).orElseThrow(),
                                                    linkedEntityClass.getSimpleName()
                                            )
                                    );
                                }
                            }
                            catch (NoSuchFieldException ignored)
                            {
                                throw new ValidationCoreException("Unknown property '%s' of entity '%s'".formatted(sp, linkedEntityClass.getSimpleName()));
                            }

                            // sort linked entities by specified property
                            return collection.stream().sorted((a, b) -> {
                                try
                                {
                                    Object prop1 = PropertyUtils.getProperty(a, sp);
                                    Object prop2 = PropertyUtils.getProperty(b, sp);

                                    if (prop1 == null && prop2 == null)
                                    {
                                        return 0;
                                    }
                                    else if (prop1 == null)
                                    {
                                        return 1;
                                    }
                                    else if (prop2 == null)
                                    {
                                        return -1;
                                    }

                                    //noinspection unchecked
                                    return ((Comparable<Object>) prop1).compareTo(prop2);
                                }
                                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                                {
                                    throw new ValidationCoreException("Error accessing sort property '%s' of entity '%s'".formatted(propertySpec.getSortProperty(linkedEntityClass.getSimpleName()), linkedEntityClass.getSimpleName()));
                                }
                            }).toList();
                        }
                )
                .orElse(new ArrayList<>(collection));

        dfsStack.addAll(0, applyDurationLimitIfSpecified(linkedEntitiesList, linkedEntityClass, propertySpec));
    }

    /**
     * Apply any specified duration limit to linked entities representing time series data.
     */
    public static List<?> applyDurationLimitIfSpecified(List<?> linkedEntitiesList, Class<?> linkedEntityClass, PropertySpec propertySpec)
    {
        Optional<String> durationLimitSpecForEntity = propertySpec.getPropertyForDurationLimitForEntity(linkedEntityClass.getSimpleName());

        if (durationLimitSpecForEntity.isPresent())
        {
            return linkedEntitiesList.stream().filter(e -> {
                try
                {
                    LocalDateTime dateTimePropertyForLimVal = (LocalDateTime) PropertyUtils.getProperty(e, durationLimitSpecForEntity.get());
                    return dateTimePropertyForLimVal.equals(propertySpec.getDurationLim()) || dateTimePropertyForLimVal.isBefore(propertySpec.getDurationLim());
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
                {
                    throw new ValidationCoreException("Error accessing property '%s' of entity '%s'".formatted(durationLimitSpecForEntity.get(), linkedEntityClass.getSimpleName()));
                }
            }).toList();
        }
        else
        {
            return linkedEntitiesList;
        }
    }

    /**
     * Compute words for composite columns.
     */
    public static List<String> getWordsForCompositeColumns(CompositeColumnCreator compositeColumnCreator, ValueTransformer valueTransformer, Object rootEntity)
    {
        // add values from composite columns
        Map<String, List<Object>> compositeColumns = compositeColumnCreator.processEntries(List.of(rootEntity));
        List<String> wordsForComposite = new ArrayList<>();
        compositeColumns.forEach((columnName, columnValues) -> columnValues.forEach(
                        v -> wordsForComposite.add(
                                String.format("%s@%s@%s", COMPOSITE_TABLE_NAME, columnName, valueTransformer.applyTransform(COMPOSITE_TABLE_NAME, columnName, v))
                                        .toLowerCase()
                                        .replace(' ', '_')
                        )
                )
        );
        return wordsForComposite;
    }
}
