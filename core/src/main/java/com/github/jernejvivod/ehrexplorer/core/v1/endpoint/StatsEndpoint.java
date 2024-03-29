package com.github.jernejvivod.ehrexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jernejvivod.ehrexplorer.core.service.StatsService;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.api.StatsApi;

@Stateless
public class StatsEndpoint implements StatsApi
{
    private static final Logger logger = LoggerFactory.getLogger(StatsEndpoint.class);

    @Inject
    private StatsService statsService;

    @Override
    public Response allStats()
    {
        logger.info("Computing all stats.");
        return Response.ok().entity(statsService.allStats()).build();
    }

    @Override
    public Response entityStats(String entityName)
    {
        logger.info("Computing entity stats for {}.", entityName);
        return Response.ok().entity(statsService.tableStats(entityName)).build();
    }
}
