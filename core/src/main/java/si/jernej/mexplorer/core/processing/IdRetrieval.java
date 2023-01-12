package si.jernej.mexplorer.core.processing;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.util.EntityUtils;
import si.jernej.mexplorer.processorapi.v1.model.ForeignKeyPathIdRetrievalSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.IdRetrievalFilterSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.IdRetrievalSpecDto;

@Stateless
public class IdRetrieval
{
    private static final Logger logger = LoggerFactory.getLogger(IdRetrieval.class);

    @Inject
    private MimicEntityManager mimicEntityManager;

    /**
     * Retrieve ids of specified entities with specified entity filtering.
     *
     * @param idRetrievalSpecDto specification for filtering of entities
     * @return list of retrieved ids
     */
    public Set<Object> retrieveIds(IdRetrievalSpecDto idRetrievalSpecDto)
    {
        logger.info(".retrieveIds extracting ids");
        logger.info(".retrieveIds root entity name: {}, root entity ID property {}, number of filter specifications {}",
                idRetrievalSpecDto.getEntityName(),
                idRetrievalSpecDto.getIdProperty(),
                Optional.ofNullable(idRetrievalSpecDto.getFilterSpecs()).map(List::size).orElse(0)
        );

        // retrieve all specified entities for filtering
        List<Object> entitiesAll = mimicEntityManager.getAllSpecifiedEntitiesWithNonNullIdProperty(idRetrievalSpecDto.getEntityName(), idRetrievalSpecDto.getIdProperty());  // TODO should fetch based on filters

        Set<Object> entitiesFiltered = filter(entitiesAll, idRetrievalSpecDto.getFilterSpecs());

        // get and return id values
        return getIdValues(entitiesFiltered, idRetrievalSpecDto.getIdProperty());
    }

    public Set<Object> retrieveIdsUsingForeignKeyPath(ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto)
    {
        List<String> foreignKeyPath = foreignKeyPathIdRetrievalSpecDto.getForeignKeyPath();

        List<Object> entitiesEndFkPath = mimicEntityManager.fetchFkPathEndEntitiesAndIdsForForeignKeyPath(
                        foreignKeyPath,
                        foreignKeyPathIdRetrievalSpecDto.getRootEntityIdProperty(),
                        foreignKeyPathIdRetrievalSpecDto.getEndEntityIdProperty(),
                        new HashSet<>(foreignKeyPathIdRetrievalSpecDto.getRootEntityIds())
                )
                .map(e -> e[0])
                .toList();

        // TODO map to IDs and perform fetches for filtering

        Set<Object> entitiesFiltered = filter(entitiesEndFkPath, foreignKeyPathIdRetrievalSpecDto.getFilterSpecs());

        return getIdValues(entitiesFiltered, foreignKeyPathIdRetrievalSpecDto.getEndEntityIdProperty());
    }

    private Set<Object> filter(List<Object> entities, List<IdRetrievalFilterSpecDto> filterSpecs)
    {
        // set current set of filtered entities and initialize empty set for adding entities for the next filtering
        Set<Object> entitiesFiltered = new HashSet<>(entities);
        Set<Object> entitiesFilteredNxt = new HashSet<>();

        if (filterSpecs != null && !filterSpecs.isEmpty())
        {
            for (IdRetrievalFilterSpecDto filterSpec : filterSpecs)
            {
                for (Object entity : entitiesFiltered)
                {
                    // get entity at end of foreign key path and make sure path is singular
                    EntityUtils.assertForeignKeyPathValid(filterSpec.getForeignKeyPath(), mimicEntityManager.getMetamodel());
                    Set<Object> entityEndFkPath = EntityUtils.traverseForeignKeyPath(entity, filterSpec.getForeignKeyPath());

                    if (entityEndFkPath.size() > 1)
                        throw new ValidationCoreException("Entity used to filter the ids should be reachable by an all-singular path");

                    // filter
                    Object entityForFiltering = entityEndFkPath.iterator().next();
                    try
                    {
                        // compare property for filtering
                        Object propertyForFiltering = PropertyUtils.getProperty(entityForFiltering, filterSpec.getPropertyName());
                        if (propertyForFiltering != null)
                        {
                            boolean compResult = switch (filterSpec.getComparator())
                                    {
                                        case LESS -> ((Comparable) propertyForFiltering).compareTo(filterSpec.getPropertyVal()) < 0;
                                        case MORE -> ((Comparable) propertyForFiltering).compareTo(filterSpec.getPropertyVal()) > 0;
                                        case EQUAL -> ((Comparable) propertyForFiltering).compareTo(filterSpec.getPropertyVal()) == 0;
                                    };

                            // if filtering criteria satisfied add to list of filtered entities
                            if (compResult)
                            {
                                entitiesFilteredNxt.add(entity);
                            }
                        }
                    }
                    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                    {
                        throw new ValidationCoreException("Specified property '%s' for filtering of entity '%s' is not valid".formatted(filterSpec.getPropertyName(), entityForFiltering.getClass().getSimpleName()));
                    }
                }
                entitiesFiltered = new HashSet<>(entitiesFilteredNxt);
                entitiesFilteredNxt.clear();
            }
        }

        return entitiesFiltered;
    }

    private Set<Object> getIdValues(Set<Object> entitiesFiltered, String idPropertyName)
    {
        Set<Object> ids = new HashSet<>();
        for (Object entity : entitiesFiltered)
        {
            try
            {
                ids.add(PropertyUtils.getProperty(entity, idPropertyName));
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                throw new ValidationCoreException("Specified property '%s' for filtering of entity '%s' is not valid".formatted(idPropertyName, entity.getClass().getSimpleName()));
            }
        }
        return ids;
    }
}
