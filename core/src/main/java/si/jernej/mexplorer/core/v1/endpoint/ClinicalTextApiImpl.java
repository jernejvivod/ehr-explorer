package si.jernej.mexplorer.core.v1.endpoint;

import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.jernej.mexplorer.core.service.ClinicalTextService;
import si.jernej.mexplorer.processorapi.v1.api.ClinicalTextApi;
import si.jernej.mexplorer.processorapi.v1.model.ClinicalTextConfigDto;

@Stateless
public class ClinicalTextApiImpl implements ClinicalTextApi
{
    private static final Logger logger = LoggerFactory.getLogger(ClinicalTextApiImpl.class);

    @Inject
    private ClinicalTextService clinicalTextService;

    @Override
    @TransactionTimeout(value = 60, unit = TimeUnit.MINUTES)
    public Response clinicalText(ClinicalTextConfigDto clinicalTextConfigDto)
    {
        logger.info("extracting clinical text");
        return Response.ok().entity(clinicalTextService.extractClinicalText(clinicalTextConfigDto)).build();
    }
}
