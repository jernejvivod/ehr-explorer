package si.jernej.mexplorer.core.processing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.entity.AdmissionsEntity;
import si.jernej.mexplorer.entity.PatientsEntity;
import si.jernej.mexplorer.processorapi.v1.model.ExtractedTargetDto;

/**
 * Class containing target value extraction functionality.
 */
@Stateless
public class TargetExtraction
{
    private static final Logger logger = LoggerFactory.getLogger(TargetExtraction.class);

    @Inject
    private MimicEntityManager mimicEntityManager;

    /**
     * @param ids ids for root entity (AdmissionsEntity) for which to extract the target values
     * @return root entity (AdmissionsEntity) id and extracted target value
     */
    public List<ExtractedTargetDto> extractPatientDiedDuringAdmissionTarget(@CheckForNull List<Long> ids, @CheckForNull Integer ageLim)
    {
        logger.info("extracting target 'patient died during admission' ({} ids)", ids != null ? ids.size() : "ALL");

        List<Object[]> results = mimicEntityManager.getResultListForExtractPatientDiedDuringAdmissionTarget(ids, ageLim);

        return results.stream().map(r -> {
            ExtractedTargetDto extractedTargetDto = new ExtractedTargetDto();
            extractedTargetDto.setRootEntityId((Long) r[0]);
            extractedTargetDto.setTargetValue(((Short) r[1]).intValue());
            return extractedTargetDto;
        }).toList();
    }

    public List<ExtractedTargetDto> extractReadmissionTarget(@CheckForNull List<Long> ids, @CheckForNull Integer ageLim, int maxDaysBetweenAdmissionsConsiderPositive, int deathDaysAfterDischargeConsiderPositive)
    {
        logger.info("extracting target 'readmission happened' ({} ids)", ids != null ? ids.size() : "ALL");

        List<PatientsEntity> patients = mimicEntityManager.fetchPatientsWithIds(ids);

        record FilteredAdmissionsForPatientAndDod(List<AdmissionsEntity> admissions, LocalDateTime dod)
        {
        }

        // sort admissions by time and filter out admissions for which the patient's age was below the specified limit
        List<FilteredAdmissionsForPatientAndDod> admissionsForPatients = new ArrayList<>();
        for (PatientsEntity p : patients)
        {
            admissionsForPatients.add(
                    new FilteredAdmissionsForPatientAndDod(
                            p.getAdmissionsEntitys().stream()
                                    .sorted(Comparator.comparing(AdmissionsEntity::getAdmitTime))
                                    .filter(a -> Duration.between(p.getDob(), a.getAdmitTime()).toDays() / 365.2425 >= (ageLim != null ? ageLim.doubleValue() : 0.0))
                                    .toList(),
                            p.getDod()
                    )
            );
        }

        // extract target from admissions of patients
        List<ExtractedTargetDto> extractTargetResults = new ArrayList<>();
        for (FilteredAdmissionsForPatientAndDod admissionsForPatient : admissionsForPatients)
        {
            extractTargetResults.addAll(extractReadmissionTargetFromAdmissionsForPatient(admissionsForPatient.admissions(), admissionsForPatient.dod(), maxDaysBetweenAdmissionsConsiderPositive, deathDaysAfterDischargeConsiderPositive));
        }

        return extractTargetResults;
    }

    private static List<ExtractedTargetDto> extractReadmissionTargetFromAdmissionsForPatient(List<AdmissionsEntity> admissionsForPatient, LocalDateTime dod, int maxDaysBetweenAdmissionsConsiderPositive, int deathDaysAfterDischargeConsiderPositive)
    {
        List<ExtractedTargetDto> extractedTargetDtos = new ArrayList<>(admissionsForPatient.size());

        // extract target for admissions for which there is a subsequent admission
        for (int i = 0; i < admissionsForPatient.size() - 1; i++)
        {
            AdmissionsEntity admission = admissionsForPatient.get(i);
            AdmissionsEntity nxtAdmission = admissionsForPatient.get(i + 1);

            ExtractedTargetDto extractedTargetDto = new ExtractedTargetDto();
            extractedTargetDto.setRootEntityId(admission.getHadmId());
            extractedTargetDto.setDateTimeLimit(admission.getDischTime());

            // if duration between admission less than specified number of days, consider positive
            if (Duration.between(admission.getDischTime(), nxtAdmission.getAdmitTime()).toDays() < maxDaysBetweenAdmissionsConsiderPositive)
            {
                extractedTargetDto.setTargetValue(1);
            }
            else
            {
                extractedTargetDto.setTargetValue(0);
            }

            extractedTargetDtos.add(extractedTargetDto);
        }

        // extract target for last admission
        AdmissionsEntity admissionLast = admissionsForPatient.get(admissionsForPatient.size() - 1);

        ExtractedTargetDto extractedTargetDto = new ExtractedTargetDto();
        extractedTargetDto.setRootEntityId(admissionLast.getHadmId());
        extractedTargetDto.setDateTimeLimit(admissionLast.getDischTime());

        // if patient died specified number of days after being discharged, consider positive
        if (admissionLast.getHospitalExpireFlag() == 0 && dod != null && Duration.between(admissionLast.getDischTime(), dod).toDays() < deathDaysAfterDischargeConsiderPositive)
        {
            extractedTargetDto.setTargetValue(1);
        }
        else
        {
            extractedTargetDto.setTargetValue(0);
        }

        extractedTargetDtos.add(extractedTargetDto);

        return extractedTargetDtos;
    }
}
