package si.jernej.mexplorer.core.processing;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.persistence.Entity;
import javax.ws.rs.InternalServerErrorException;

import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;
import si.jernej.mexplorer.core.processing.util.WordificationUtil;

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
                // get next entity from queue and get its simple class name
                Object nxt = bfsQueue.remove();
                String entityName = nxt.getClass().getSimpleName();

                // initialize list for words obtained from next table
                List<String> wordsForEntity = new ArrayList<>();

                // go over entity's properties
                for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(nxt.getClass()).getPropertyDescriptors())
                {
                    Object nxtProperty = propertyDescriptor.getReadMethod().invoke(nxt);

                    // if property should be included as a word
                    if (propertySpec.containsEntry(entityName, propertyDescriptor.getName()))
                    {
                        wordsForEntity.add("%s@%s@%s".formatted(
                                        entityName,
                                        propertyDescriptor.getName(),
                                        valueTransformer.applyTransform(entityName, propertyDescriptor.getName(), nxtProperty)
                                )
                                .toLowerCase()
                                .replace(' ', '_'));
                    }

                    else if (nxtProperty instanceof Collection<?> collection && !collection.isEmpty())
                    {
                        // if property collection of linked entities
                        Class<?> linkedEntityClass = collection.iterator().next().getClass();

                        // if collection of linked entities that were not yet visited, add to queue
                        if (!visitedEntities.contains(linkedEntityClass.getSimpleName()) && linkedEntityClass.isAnnotationPresent(Entity.class))
                        {
                            WordificationUtil.addLinkedCollectionToQueue(bfsQueue, propertySpec, collection, linkedEntityClass);
                            visitedEntities.add(linkedEntityClass.getSimpleName());
                        }
                    }
                    else if (nxtProperty != null)
                    {
                        // if single linked entity
                        Class<?> linkedEntityClass = nxtProperty.getClass();

                        // if property linked entity and not yet visited, add to queue
                        if (!visitedEntities.contains(linkedEntityClass.getSimpleName()) && linkedEntityClass.isAnnotationPresent(Entity.class))
                        {
                            bfsQueue.add(nxtProperty);
                            visitedEntities.add(linkedEntityClass.getSimpleName());
                        }
                    }
                }

                // add all words and concatenations for entity to results list
                wordsAll.addAll(WordificationUtil.addConcatenations(wordsForEntity, concatenationScheme));
            }
        }
        catch (IntrospectionException | IllegalAccessException | InvocationTargetException e)
        {
            throw new InternalServerErrorException("Error computing Wordification");
        }

        // add all words and concatenations for composite table to result list
        List<String> wordsForComposite = WordificationUtil.getWordsForCompositeColumns(compositeColumnCreator, valueTransformer, rootEntity);
        wordsAll.addAll(WordificationUtil.addConcatenations(wordsForComposite, concatenationScheme));

        return wordsAll;
    }
}
