package com.github.jernejvivod.ehrexplorer.core.v1.endpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jernejvivod.ehrexplorer.core.service.ClinicalTextService;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.api.ClinicalTextApi;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.ClinicalTextConfigDto;

@Stateless
public class ClinicalTextEndpoint implements ClinicalTextApi
{
    private static final Logger logger = LoggerFactory.getLogger(ClinicalTextEndpoint.class);

    @Inject
    private ClinicalTextService clinicalTextService;

    @Override
    public Response clinicalText(ClinicalTextConfigDto clinicalTextConfigDto)
    {
        logger.info("Extracting clinical text (request={}).", clinicalTextConfigDto);
        return Response.ok().entity(clinicalTextService.extractClinicalText(clinicalTextConfigDto)).build();
    }
}
