package si.jernej.mexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import si.jernej.mexplorer.core.service.PropositionalizationService;
import si.jernej.mexplorer.processorapi.v1.api.PropositionalizationApi;
import si.jernej.mexplorer.processorapi.v1.model.WordificationConfigDto;
// import si.jernej.mexplorer.processorapi.v1.model.WordificationTimeSeriesConfigDto;

@Stateless
public class PropositionalizationApiImpl implements PropositionalizationApi
{
    @Inject
    private PropositionalizationService propositionalizationService;

    @Override
    public Response wordification(WordificationConfigDto wordificationConfigDto)
    {
        return Response.ok().entity(propositionalizationService.computeWordification(wordificationConfigDto)).build();
    }

    // @Override
    // public Response timeSeriesWordification(@Valid @NotNull WordificationTimeSeriesConfigDto wordificationTimeSeriesConfigDto)
    // {
    //     throw new NotImplementedException("TODO implement");
    // }
}
