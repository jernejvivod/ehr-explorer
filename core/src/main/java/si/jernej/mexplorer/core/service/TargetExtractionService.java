package si.jernej.mexplorer.core.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import si.jernej.mexplorer.core.processing.TargetExtraction;
import si.jernej.mexplorer.processorapi.v1.model.ExtractedTargetDto;
import si.jernej.mexplorer.processorapi.v1.model.TargetExtractionSpecDto;

@Stateless
public class TargetExtractionService
{
    @Inject
    private TargetExtraction targetExtraction;

    public List<ExtractedTargetDto> computeTarget(TargetExtractionSpecDto targetExtractionSpecDto)
    {
        List<ExtractedTargetDto> targetValues = null;
        if (targetExtractionSpecDto.getTargetType() == TargetExtractionSpecDto.TargetTypeEnum.PATIENT_DIED_DURING_ADMISSION)
        {
            targetValues = targetExtraction.extractPatientDiedDuringAdmissionTarget(targetExtractionSpecDto.getIds());
        }

        return targetValues;
    }
}
