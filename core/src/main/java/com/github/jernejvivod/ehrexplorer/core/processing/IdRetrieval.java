package com.github.jernejvivod.ehrexplorer.core.processing;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jernejvivod.ehrexplorer.common.exception.ValidationCoreException;
import com.github.jernejvivod.ehrexplorer.core.manager.DbEntityManager;
import com.github.jernejvivod.ehrexplorer.core.util.EntityUtils;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.ForeignKeyPathIdRetrievalSpecDto;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.IdRetrievalFilterSpecDto;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.IdRetrievalSpecDto;

@Stateless
public class IdRetrieval
{
    private static final Logger logger = LoggerFactory.getLogger(IdRetrieval.class);

    @Inject
    private DbEntityManager dbEntityManager;

    /**
     * Retrieve ids of specified entities with specified entity filtering.
     *
     * @param idRetrievalSpecDto specification for filtering of entities
     * @return {@link Set} of retrieved ids
     */
    public Set<Object> retrieveIds(IdRetrievalSpecDto idRetrievalSpecDto)
    {
        logger.info("Retrieving IDs.");

        // retrieve ids
        List<Object> ids = dbEntityManager.getNonNullIdsOfEntity(idRetrievalSpecDto.getEntityName(), idRetrievalSpecDto.getIdProperty());

        // fetch entities for filtering
        List<List<String>> foreignKeyPathsForFilterSpecs = idRetrievalSpecDto.getFilterSpecs().stream()
                .map(IdRetrievalFilterSpecDto::getForeignKeyPath)
                .toList();

        List<Object> entitiesFetchedForFiltering = dbEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(
                        idRetrievalSpecDto.getEntityName(),
                        foreignKeyPathsForFilterSpecs,
                        idRetrievalSpecDto.getIdProperty(),
                        new HashSet<>(ids)
                )
                .map(e -> e[0])
                .toList();

        Set<Object> entitiesFiltered = filterEntitiesUsingFilterSpecs(entitiesFetchedForFiltering, idRetrievalSpecDto.getFilterSpecs());

        // get and return id values
        return getIdValues(entitiesFiltered, idRetrievalSpecDto.getIdProperty());
    }

    public Set<Object> retrieveIdsUsingForeignKeyPath(ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto)
    {
        logger.info("Retrieving IDs using FK path.");

        List<String> foreignKeyPath = foreignKeyPathIdRetrievalSpecDto.getForeignKeyPath();

        // retrieve ids
        List<Object> idsEndEntitiesForForeignKeyPath = dbEntityManager.fetchFkPathEndEntitiesAndIdsForForeignKeyPath(
                        foreignKeyPath,
                        foreignKeyPathIdRetrievalSpecDto.getRootEntityIdProperty(),
                        foreignKeyPathIdRetrievalSpecDto.getEndEntityIdProperty(),
                        new HashSet<>(foreignKeyPathIdRetrievalSpecDto.getRootEntityIds())
                )
                .map(e -> e[1])
                .filter(Objects::nonNull)
                .toList();

        // fetch entities for filtering
        List<List<String>> foreignKeyPathsForFilterSpecs = foreignKeyPathIdRetrievalSpecDto.getFilterSpecs().stream()
                .map(IdRetrievalFilterSpecDto::getForeignKeyPath)
                .toList();

        List<Object> entitiesFetchedForFiltering = dbEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(
                        foreignKeyPath.get(foreignKeyPath.size() - 1),
                        foreignKeyPathsForFilterSpecs,
                        foreignKeyPathIdRetrievalSpecDto.getEndEntityIdProperty(),
                        new HashSet<>(idsEndEntitiesForForeignKeyPath)
                )
                .map(e -> e[0])
                .toList();

        Set<Object> entitiesFiltered = filterEntitiesUsingFilterSpecs(entitiesFetchedForFiltering, foreignKeyPathIdRetrievalSpecDto.getFilterSpecs());

        // get and return id values
        return getIdValues(entitiesFiltered, foreignKeyPathIdRetrievalSpecDto.getEndEntityIdProperty());
    }

    private Set<Object> filterEntitiesUsingFilterSpecs(List<Object> entities, List<IdRetrievalFilterSpecDto> filterSpecs)
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
                    EntityUtils.assertForeignKeyPathValid(filterSpec.getForeignKeyPath(), dbEntityManager.getMetamodel());
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
