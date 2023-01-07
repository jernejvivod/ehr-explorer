package si.jernej.mexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import si.jernej.mexplorer.core.service.StatsService;
import si.jernej.mexplorer.processorapi.v1.api.StatsApi;

@Stateless
public class StatsEndpoint implements StatsApi
{
    @Inject
    private StatsService statsService;

    @Override
    public Response allStats()
    {
        return Response.ok().entity(statsService.allStats()).build();
    }

    @Override
    public Response entityStats(String tableName)
    {
        return Response.ok().entity(statsService.tableStats(tableName)).build();
    }
}
