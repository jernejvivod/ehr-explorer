package si.jernej.mexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.jernej.mexplorer.core.processing.IdRetrieval;
import si.jernej.mexplorer.processorapi.v1.api.IdsApi;
import si.jernej.mexplorer.processorapi.v1.model.IdRetrievalSpecDto;

@Stateless
public class IdsEndpoint implements IdsApi
{
    private static final Logger logger = LoggerFactory.getLogger(IdsEndpoint.class);

    @Inject
    private IdRetrieval idRetrieval;

    @Override
    public Response ids(IdRetrievalSpecDto idRetrievalSpecDto)
    {
        logger.info("extracting IDs");
        return Response.ok().entity(idRetrieval.retrieveIds(idRetrievalSpecDto)).build();
    }
}
