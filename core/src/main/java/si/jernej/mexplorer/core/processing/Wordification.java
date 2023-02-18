package si.jernej.mexplorer.core.processing;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.ws.rs.InternalServerErrorException;

import org.apache.commons.lang3.tuple.Pair;

import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;
import si.jernej.mexplorer.core.processing.util.OrderedEntityPropertyDescriptors;
import si.jernej.mexplorer.core.processing.util.WordificationUtil;

@Dependent
public class Wordification
{
    @Inject
    private OrderedEntityPropertyDescriptors orderedEntityPropertyDescriptors;

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
     * @param transitionPairsFromForeignKeyPath set of pairs of entity names that occur on foreign key paths obtained from the specified {@link PropertySpec}
     * @param timeLim time limit to apply to linked entities
     * @param ignoreNull ignore null values when computing Wordification results or treat them as valid values
     * @return {@code List} of obtained words for specified root entity
     */
    public List<String> wordify(
            Object rootEntity,
            PropertySpec propertySpec,
            ValueTransformer valueTransformer,
            CompositeColumnCreator compositeColumnCreator,
            ConcatenationScheme concatenationScheme,
            Set<Pair<String, String>> transitionPairsFromForeignKeyPath,
            @CheckForNull LocalDateTime timeLim,
            boolean ignoreNull
    )
    {
        // list of resulting words
        List<String> wordsAll = new ArrayList<>();

        // initialize BFS queue
        LinkedList<Object> dfsStack = new LinkedList<>();
        dfsStack.add(rootEntity);

        try
        {
            while (!dfsStack.isEmpty())
            {
                // get next entity from queue and get its simple class name
                Object nxt = dfsStack.pop();
                String entityName = nxt.getClass().getSimpleName();

                // initialize list for words obtained from next table
                List<String> wordsForEntity = new ArrayList<>();

                // go over entity's properties
                for (PropertyDescriptor propertyDescriptor : Arrays.stream(orderedEntityPropertyDescriptors.getForEntity(nxt.getClass())).filter(p -> propertySpec.containsEntry(entityName, p.getName())).toList())
                {
                    Class<?> propertyType = propertyDescriptor.getPropertyType();

                    // if property should be included as a word
                    if (propertySpec.containsEntry(entityName, propertyDescriptor.getName()) && !propertyType.isAnnotationPresent(Entity.class) && !Collection.class.isAssignableFrom(propertyType))
                    {
                        Object nxtPropertyVal = propertyDescriptor.getReadMethod().invoke(nxt);
                        Object nxtPropertyValTransformed = valueTransformer.applyTransform(entityName, propertyDescriptor.getName(), nxtPropertyVal);

                        if (!(ignoreNull && nxtPropertyValTransformed == null))
                        {
                            wordsForEntity.add("%s@%s@%s".formatted(
                                            entityName,
                                            propertyDescriptor.getName(),
                                            nxtPropertyValTransformed
                                    )
                                    .toLowerCase()
                                    .replace(' ', '_'));
                        }
                    }

                    else if (Collection.class.isAssignableFrom(propertyType))
                    {
                        // if property collection of linked entities
                        Class<?> linkedEntityClass = (Class<?>) ((ParameterizedType) nxt.getClass().getDeclaredField(propertyDescriptor.getName()).getGenericType()).getActualTypeArguments()[0];

                        // if collection of linked entities that were not yet visited, add to queue
                        if (linkedEntityClass.isAnnotationPresent(Entity.class) &&
                            transitionPairsFromForeignKeyPath.contains(Pair.of(entityName, linkedEntityClass.getSimpleName()))
                        )
                        {
                            Collection<?> nxtPropertyVal = (Collection<?>) propertyDescriptor.getReadMethod().invoke(nxt);
                            WordificationUtil.pushLinkedCollectionToStack(dfsStack, propertySpec, nxtPropertyVal, linkedEntityClass, timeLim);
                        }
                    }
                    else if (propertyType.isAnnotationPresent(Entity.class))
                    {
                        // if single linked entity
                        Object nxtPropertyVal = propertyDescriptor.getReadMethod().invoke(nxt);
                        if (nxtPropertyVal == null)
                            continue;

                        Class<?> linkedEntityClass = nxtPropertyVal.getClass();

                        // if property linked entity and not yet visited, add to queue
                        if (linkedEntityClass.isAnnotationPresent(Entity.class) &&
                            transitionPairsFromForeignKeyPath.contains(Pair.of(entityName, linkedEntityClass.getSimpleName()))
                        )
                        {
                            dfsStack.push(nxtPropertyVal);
                        }
                    }
                }

                // add words for composite properties for entity
                wordsForEntity.addAll(WordificationUtil.getCompositePropertiesForEntity(nxt, entityName, propertySpec, valueTransformer));

                // add all words and concatenations for entity to results list
                wordsAll.addAll(WordificationUtil.addConcatenations(wordsForEntity, concatenationScheme));
            }
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e)
        {
            throw new InternalServerErrorException("Error computing Wordification.");
        }

        // add all words and concatenations for composite table to result list
        List<String> wordsForComposite = WordificationUtil.getWordsForCompositeColumns(compositeColumnCreator, valueTransformer, rootEntity);
        wordsAll.addAll(WordificationUtil.addConcatenations(wordsForComposite, concatenationScheme));

        return wordsAll;
    }
}
