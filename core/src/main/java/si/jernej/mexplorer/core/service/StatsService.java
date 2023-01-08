package si.jernej.mexplorer.core.service;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.metamodel.EntityType;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.processing.util.OrderedEntityPropertyDescriptors;
import si.jernej.mexplorer.core.util.EntityUtils;
import si.jernej.mexplorer.processorapi.v1.model.EntityStatsDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertyStatsDto;

@Stateless
public class StatsService
{
    @Inject
    private MimicEntityManager mimicEntityManager;
    @Inject
    private OrderedEntityPropertyDescriptors orderedEntityPropertyDescriptors;

    public List<EntityStatsDto> allStats()
    {
        // compute table stats for all tables/entities
        Set<EntityType<?>> entitys = mimicEntityManager.getMetamodel().getEntities();
        List<EntityStatsDto> tableStatsDtoList = new ArrayList<>(entitys.size());
        for (EntityType<?> entity : entitys)
        {
            tableStatsDtoList.add(tableStats(entity.getName()));
        }
        return tableStatsDtoList;
    }

    public EntityStatsDto tableStats(String entityName)
    {
        EntityUtils.assertEntityValid(entityName, mimicEntityManager.getMetamodel());

        EntityType<?> tableEntity = mimicEntityManager.getMetamodel().getEntities()
                .stream()
                .filter(e -> e.getName().equals(entityName))
                .findAny()
                .orElseThrow(() -> new ValidationCoreException(""));

        EntityStatsDto tableStatsDto = new EntityStatsDto();
        tableStatsDto.setEntityName(entityName);

        // construct dynamic query
        StringBuilder query = new StringBuilder("SELECT COUNT(e),\n");

        List<String> columnNamesForStats = new ArrayList<>();
        for (PropertyDescriptor propertyDescriptor : orderedEntityPropertyDescriptors.getForEntity(tableEntity.getJavaType()))
        {
            if (!Collection.class.isAssignableFrom(propertyDescriptor.getPropertyType()))
            {
                columnNamesForStats.add(propertyDescriptor.getName());
                query.append("SUM(CASE WHEN e.%s IS NULL THEN 1 ELSE 0 END),%n".formatted(propertyDescriptor.getName()));
                query.append("COUNT(DISTINCT e.%s),%n".formatted(propertyDescriptor.getName()));
            }
        }
        query.delete(query.length() - 2, query.length()).append("\n");
        query.append("FROM %s e".formatted(entityName));

        // get results
        Object[] resultsForColumnStatsQuery = mimicEntityManager.getResultsForColumnStatsQuery(query.toString());

        // construct result DTO
        tableStatsDto.setNumEntries((Long) resultsForColumnStatsQuery[0]);

        List<PropertyStatsDto> columnStats = new ArrayList<>(resultsForColumnStatsQuery.length);
        for (int i = 1; i < resultsForColumnStatsQuery.length; i += 2)
        {
            PropertyStatsDto columnStatsDto = new PropertyStatsDto();
            columnStatsDto.setPropertyName(columnNamesForStats.get((i - 1) / 2));
            columnStatsDto.setNumNull((Long) resultsForColumnStatsQuery[i]);
            columnStatsDto.setNumUnique((Long) resultsForColumnStatsQuery[i + 1]);

            columnStats.add(columnStatsDto);
        }

        tableStatsDto.setPropertyStats(columnStats);
        return tableStatsDto;
    }
}
