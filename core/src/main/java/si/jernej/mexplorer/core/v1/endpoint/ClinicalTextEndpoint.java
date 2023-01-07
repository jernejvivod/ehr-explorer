package si.jernej.mexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.jernej.mexplorer.core.service.ClinicalTextService;
import si.jernej.mexplorer.processorapi.v1.api.ClinicalTextApi;
import si.jernej.mexplorer.processorapi.v1.model.ClinicalTextConfigDto;

@Stateless
public class ClinicalTextEndpoint implements ClinicalTextApi
{
    private static final Logger logger = LoggerFactory.getLogger(ClinicalTextEndpoint.class);

    @Inject
    private ClinicalTextService clinicalTextService;

    @Override
    public Response clinicalText(ClinicalTextConfigDto clinicalTextConfigDto)
    {
        logger.info("extracting clinical text");
        return Response.ok().entity(clinicalTextService.extractClinicalText(clinicalTextConfigDto)).build();
    }
}
