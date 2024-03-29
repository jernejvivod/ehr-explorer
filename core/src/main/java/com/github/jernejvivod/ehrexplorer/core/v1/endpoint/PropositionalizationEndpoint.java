package com.github.jernejvivod.ehrexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jernejvivod.ehrexplorer.core.service.PropositionalizationService;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.api.PropositionalizationApi;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.WordificationConfigDto;

@Stateless
public class PropositionalizationEndpoint implements PropositionalizationApi
{
    private static final Logger logger = LoggerFactory.getLogger(PropositionalizationEndpoint.class);

    @Inject
    private PropositionalizationService propositionalizationService;

    @Override
    public Response wordification(WordificationConfigDto wordificationConfigDto)
    {
        logger.info("Computing Wordification (request={}).", wordificationConfigDto);
        return Response.ok().entity(propositionalizationService.computeWordification(wordificationConfigDto)).build();
    }
}
