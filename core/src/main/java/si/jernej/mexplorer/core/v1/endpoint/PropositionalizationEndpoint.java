package si.jernej.mexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import si.jernej.mexplorer.core.service.PropositionalizationService;
import si.jernej.mexplorer.processorapi.v1.api.PropositionalizationApi;
import si.jernej.mexplorer.processorapi.v1.model.WordificationConfigDto;

@Stateless
public class PropositionalizationEndpoint implements PropositionalizationApi
{
    @Inject
    private PropositionalizationService propositionalizationService;

    @Override
    public Response wordification(WordificationConfigDto wordificationConfigDto)
    {
        return Response.ok().entity(propositionalizationService.computeWordification(wordificationConfigDto)).build();
    }
}
