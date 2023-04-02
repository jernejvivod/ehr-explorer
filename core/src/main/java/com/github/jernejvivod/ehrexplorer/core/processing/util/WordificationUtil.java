package com.github.jernejvivod.ehrexplorer.core.processing.util;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.apache.commons.beanutils.PropertyUtils;

import com.github.jernejvivod.ehrexplorer.common.exception.ValidationCoreException;
import com.github.jernejvivod.ehrexplorer.core.processing.Wordification;
import com.github.jernejvivod.ehrexplorer.core.processing.spec.PropertySpec;
import com.github.jernejvivod.ehrexplorer.core.processing.transform.CompositeColumnCreator;
import com.github.jernejvivod.ehrexplorer.core.processing.transform.ValueTransformer;
import com.github.jernejvivod.ehrexplorer.core.util.Constants;
import com.github.jernejvivod.ehrexplorer.core.util.EntityUtils;
import com.google.common.collect.Iterables;

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
    public static void pushLinkedCollectionToStack(List<Object> dfsStack, PropertySpec propertySpec, Collection<?> collection, Class<?> linkedEntityClass, @CheckForNull LocalDateTime timeLim)
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

        dfsStack.addAll(0, applyDurationLimitIfSpecified(linkedEntitiesList, linkedEntityClass, propertySpec, timeLim));
    }

    /**
     * Apply any specified duration limit to linked entities representing time series data.
     */
    public static List<?> applyDurationLimitIfSpecified(List<?> linkedEntitiesList, Class<?> linkedEntityClass, PropertySpec propertySpec, @CheckForNull LocalDateTime timeLim)
    {
        Optional<String> durationLimitSpecForEntity = propertySpec.getPropertyForDurationLimitForEntity(linkedEntityClass.getSimpleName());

        if (durationLimitSpecForEntity.isPresent() && timeLim != null)
        {
            return linkedEntitiesList.stream().filter(e -> {
                try
                {
                    LocalDateTime dateTimePropertyForLimVal = (LocalDateTime) PropertyUtils.getProperty(e, durationLimitSpecForEntity.get());
                    return dateTimePropertyForLimVal.equals(timeLim) || dateTimePropertyForLimVal.isBefore(timeLim);
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
                                String.format("%s@%s@%s", Constants.COMPOSITE_TABLE_NAME, columnName, valueTransformer.applyTransform(Constants.COMPOSITE_TABLE_NAME, columnName, v))
                                        .toLowerCase()
                                        .replace(' ', '_')
                        )
                )
        );
        return wordsForComposite;
    }

    /**
     * Get words for composite properties for entity as defined in {@link PropertySpec}.
     */
    public static List<String> getCompositePropertiesForEntity(Object entity, String entityName, PropertySpec propertySpec, ValueTransformer valueTransformer)
    {
        Optional<Set<PropertySpec.CompositePropertySpec>> compositePropertySpecsForEntity = propertySpec.getCompositePropertySpecsForEntity(entityName);

        return compositePropertySpecsForEntity.map(cs -> cs.stream().map(c -> {
                            Object propertyOnThisEntityVal;
                            Object propertyOnOtherEntity;

                            try
                            {
                                propertyOnThisEntityVal = PropertyUtils.getProperty(entity, c.propertyOnThisEntity());
                            }
                            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                            {
                                throw new ValidationCoreException("Error accessing property '%s'".formatted(c.propertyOnThisEntity()));
                            }

                            Object otherEntity = Iterables.getOnlyElement(EntityUtils.traverseSingularForeignKeyPath(List.of(entity), c.foreignKeyPath()));

                            try
                            {
                                propertyOnOtherEntity = PropertyUtils.getProperty(otherEntity, c.propertyOnOtherEntity());
                            }
                            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                            {
                                throw new ValidationCoreException("Error accessing property '%s'".formatted(c.propertyOnOtherEntity()));
                            }

                            Object combinedValue = c.combiner().apply(propertyOnThisEntityVal, propertyOnOtherEntity);

                            return String.format("%s@%s@%s", entityName, c.compositePropertyName(), valueTransformer.applyTransform(entityName, c.compositePropertyName(), combinedValue))
                                    .toLowerCase()
                                    .replace(' ', '_');
                        }
                ).toList()
        ).orElseGet(ArrayList::new);
    }
}
