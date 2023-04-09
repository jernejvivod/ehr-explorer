package com.github.jernejvivod.ehrexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jernejvivod.ehrexplorer.core.processing.IdRetrieval;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.api.IdsApi;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.ForeignKeyPathIdRetrievalSpecDto;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.IdRetrievalSpecDto;

@Stateless
public class IdsEndpoint implements IdsApi
{
    private static final Logger logger = LoggerFactory.getLogger(IdsEndpoint.class);

    @Inject
    private IdRetrieval idRetrieval;

    @Override
    public Response ids(IdRetrievalSpecDto idRetrievalSpecDto)
    {
        logger.info("Extracting IDs (request={}).", idRetrievalSpecDto);
        return Response.ok().entity(idRetrieval.retrieveIds(idRetrievalSpecDto)).build();
    }

    @Override
    public Response idsFk(ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto)
    {
        logger.info("Extracting IDs of entities on end of foreign key path (request={}).", foreignKeyPathIdRetrievalSpecDto);
        return Response.ok().entity(idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto)).build();
    }
}
