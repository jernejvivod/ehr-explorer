package si.jernej.mexplorer.core.service;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.lang3.NotImplementedException;

import si.jernej.mexplorer.processorapi.v1.model.ColumnStatsDto;

@Stateless
public class StatsService
{
    public List<ColumnStatsDto> allStats()
    {
        // TODO implement
        throw new NotImplementedException("To be implemented");
    }

    public List<ColumnStatsDto> tableStats(String tableName)
    {
        // TODO implement
        throw new NotImplementedException("To be implemented");
    }
}
