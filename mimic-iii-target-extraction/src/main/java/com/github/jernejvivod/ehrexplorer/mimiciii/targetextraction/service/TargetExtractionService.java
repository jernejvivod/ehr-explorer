package com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.processing.TargetExtraction;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.ExtractedTargetDto;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.TargetExtractionSpecDto;

@Stateless
public class TargetExtractionService
{
    @Inject
    private TargetExtraction targetExtraction;

    public List<ExtractedTargetDto> computeTarget(TargetExtractionSpecDto targetExtractionSpecDto)
    {
        return switch (targetExtractionSpecDto.getTargetType())
        {
            case PATIENT_DIED_DURING_ADMISSION -> targetExtraction.extractPatientDiedDuringAdmissionTarget(
                    targetExtractionSpecDto.getIds(),
                    targetExtractionSpecDto.getAgeLim()
            );
            case HOSPITAL_READMISSION_HAPPENED -> targetExtraction.extractReadmissionTarget(
                    targetExtractionSpecDto.getIds(),
                    targetExtractionSpecDto.getAgeLim(),
                    targetExtractionSpecDto.getMaxDaysIntervalPositive(),
                    targetExtractionSpecDto.getMaxDaysDeathAfterLastPositive()
            );
            case ICU_STAY_READMISSION_HAPPENED -> targetExtraction.extractIcuReadmissionTarget(
                    targetExtractionSpecDto.getIds(),
                    targetExtractionSpecDto.getAgeLim(),
                    targetExtractionSpecDto.getMaxDaysIntervalPositive(),
                    targetExtractionSpecDto.getMaxDaysDeathAfterLastPositive()
            );
        };
    }
}
