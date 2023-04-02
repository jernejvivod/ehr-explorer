package com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.service.TargetExtractionService;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.api.TargetApi;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.TargetExtractionSpecDto;

@Stateless
public class TargetEndpoint implements TargetApi
{
    private static final Logger logger = LoggerFactory.getLogger(TargetEndpoint.class);

    @Inject
    private TargetExtractionService targetExtractionService;

    @Override
    public Response targetExtraction(TargetExtractionSpecDto targetExtractionSpecDto)
    {
        logger.info("Extracting target values (request={}).", targetExtractionSpecDto);
        return Response.ok().entity(targetExtractionService.computeTarget(targetExtractionSpecDto)).build();
    }
}
