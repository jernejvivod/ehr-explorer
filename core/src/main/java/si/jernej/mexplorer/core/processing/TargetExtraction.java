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
import si.jernej.mexplorer.entity.IcuStaysEntity;
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

        return results.stream().map(r -> new ExtractedTargetDto()
                .rootEntityId((Long) r[0])
                .targetEntityId((Long) r[0])
                .targetValue(((Short) r[1]).intValue())
        ).toList();
    }

    public List<ExtractedTargetDto> extractIcuReadmissionTarget(@CheckForNull List<Long> ids, @CheckForNull Integer ageLim, int maxDaysBetweenAdmissionsConsiderPositive, int deathDaysAfterDischargeConsiderPositive)
    {
        logger.info("extracting target 'ICU readmission happened' ({} ids)", ids != null ? ids.size() : "ALL");

        List<PatientsEntity> patients = mimicEntityManager.fetchPatientsForTargetExtractionIcuReadmission(ids);

        record FilteredIcuStaysForPatientAndDod(long subjectId, List<IcuStaysEntity> icuStays, LocalDateTime dod)
        {
        }

        // sort ICU stays by time and filter out ICU stays for which the patient's age was below the specified limit
        List<FilteredIcuStaysForPatientAndDod> filteredIcuStaysForPatients = new ArrayList<>();
        for (PatientsEntity p : patients)
        {
            filteredIcuStaysForPatients.add(
                    new FilteredIcuStaysForPatientAndDod(
                            p.getSubjectId(),
                            p.getIcuStaysEntitys().stream()
                                    .sorted(Comparator.comparing(IcuStaysEntity::getInTime))
                                    .filter(a -> Duration.between(p.getDob(), a.getInTime()).toDays() / 365.2425 >= (ageLim != null ? ageLim.doubleValue() : 0.0))
                                    .toList(),
                            p.getDod()
                    )
            );
        }

        // extract target from ICU stays of patients
        List<ExtractedTargetDto> extractTargetResults = new ArrayList<>();
        for (FilteredIcuStaysForPatientAndDod icuStaysForPatient : filteredIcuStaysForPatients)
        {
            extractTargetResults.addAll(extractIcuReadmissionTargetFromAdmissionsForPatient(icuStaysForPatient.subjectId(), icuStaysForPatient.icuStays(), icuStaysForPatient.dod(), maxDaysBetweenAdmissionsConsiderPositive, deathDaysAfterDischargeConsiderPositive));
        }

        return extractTargetResults;
    }

    private static List<ExtractedTargetDto> extractIcuReadmissionTargetFromAdmissionsForPatient(long subjectId, List<IcuStaysEntity> icuStaysForPatient, @CheckForNull LocalDateTime dod, int maxDaysBetweenAdmissionsConsiderPositive, int deathDaysAfterDischargeConsiderPositive)
    {
        List<ExtractedTargetDto> extractedTargetDtos = new ArrayList<>(icuStaysForPatient.size());

        for (int i = 0; i < icuStaysForPatient.size() - 1; i++)
        {
            IcuStaysEntity icuStayCurrent = icuStaysForPatient.get(i);
            IcuStaysEntity icuStayNext = icuStaysForPatient.get(i + 1);

            ExtractedTargetDto extractedTargetDto = new ExtractedTargetDto()
                    .rootEntityId(subjectId)
                    .targetEntityId(icuStayCurrent.getIcuStayId())
                    .dateTimeLimit(icuStayCurrent.getOutTime());

            // # (1) The patient was transferred to a low-level ward from ICU, but returned to ICU again.
            if (icuStayCurrent.getAdmissionsEntity().getHadmId().equals(icuStayNext.getAdmissionsEntity().getHadmId()))
            {
                extractedTargetDto.setTargetValue(1);
            }
            else
            {
                // # (2) The patient was discharged from the hospital, but returned to the ICU within the next 30 days.
                if (Duration.between(icuStayCurrent.getOutTime(), icuStayNext.getInTime()).toDays() <= maxDaysBetweenAdmissionsConsiderPositive)
                {
                    extractedTargetDto.setTargetValue(2);
                }
                else
                {
                    extractedTargetDto.setTargetValue(0);
                }
            }

            extractedTargetDtos.add(extractedTargetDto);
        }

        IcuStaysEntity icuStayLast = icuStaysForPatient.get(icuStaysForPatient.size() - 1);

        ExtractedTargetDto extractedTargetDto = new ExtractedTargetDto()
                .rootEntityId(subjectId)
                .targetEntityId(icuStayLast.getIcuStayId())
                .dateTimeLimit(icuStayLast.getOutTime())
                .targetValue(0);

        // # (3) The patient was transferred to a low-level wards from ICU, and died later (died in the hospital).
        if (icuStayLast.getAdmissionsEntity().getDeathTime() != null && icuStayLast.getAdmissionsEntity().getDeathTime().isAfter(icuStayLast.getOutTime()))
        {
            extractedTargetDto.setTargetValue(3);
        }

        // # (4) The patient was discharged and died within the next 30 days.
        if (dod != null && icuStayLast.getAdmissionsEntity().getDeathTime() == null && Duration.between(icuStayLast.getOutTime(), dod).toDays() <= deathDaysAfterDischargeConsiderPositive)
        {
            extractedTargetDto.setTargetValue(4);
        }

        extractedTargetDtos.add(extractedTargetDto);

        return extractedTargetDtos;
    }

    public List<ExtractedTargetDto> extractReadmissionTarget(@CheckForNull List<Long> ids, @CheckForNull Integer ageLim, int maxDaysBetweenAdmissionsConsiderPositive, int deathDaysAfterDischargeConsiderPositive)
    {
        logger.info("extracting target 'readmission happened' ({} ids)", ids != null ? ids.size() : "ALL");

        List<PatientsEntity> patients = mimicEntityManager.fetchPatientForTargetExtractionHospitalReadmission(ids);

        record FilteredAdmissionsForPatientAndDod(long subjectId, List<AdmissionsEntity> admissions, LocalDateTime dod)
        {
        }

        // sort admissions by time and filter out admissions for which the patient's age was below the specified limit
        List<FilteredAdmissionsForPatientAndDod> admissionsForPatients = new ArrayList<>();
        for (PatientsEntity p : patients)
        {
            admissionsForPatients.add(
                    new FilteredAdmissionsForPatientAndDod(
                            p.getSubjectId(),
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
            extractTargetResults.addAll(extractHospitalReadmissionTargetFromAdmissionsForPatient(admissionsForPatient.subjectId(), admissionsForPatient.admissions(), admissionsForPatient.dod(), maxDaysBetweenAdmissionsConsiderPositive, deathDaysAfterDischargeConsiderPositive));
        }

        return extractTargetResults;
    }

    private static List<ExtractedTargetDto> extractHospitalReadmissionTargetFromAdmissionsForPatient(long subjectId, List<AdmissionsEntity> admissionsForPatient, @CheckForNull LocalDateTime dod, int maxDaysBetweenAdmissionsConsiderPositive, int deathDaysAfterDischargeConsiderPositive)
    {
        List<ExtractedTargetDto> extractedTargetDtos = new ArrayList<>(admissionsForPatient.size());

        // extract target for admissions for which there is a subsequent admission
        for (int i = 0; i < admissionsForPatient.size() - 1; i++)
        {
            AdmissionsEntity admission = admissionsForPatient.get(i);
            AdmissionsEntity nxtAdmission = admissionsForPatient.get(i + 1);

            ExtractedTargetDto extractedTargetDto = new ExtractedTargetDto()
                    .rootEntityId(subjectId)
                    .targetEntityId(admission.getHadmId())
                    .dateTimeLimit(admission.getDischTime());

            // # (1) If duration between admission less than specified number of days, consider positive.
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

        ExtractedTargetDto extractedTargetDto = new ExtractedTargetDto()
                .rootEntityId(subjectId)
                .targetEntityId(admissionLast.getHadmId())
                .dateTimeLimit(admissionLast.getDischTime());

        // # (2) If patient died specified number of days after being discharged, consider positive.
        if (admissionLast.getHospitalExpireFlag() == 0 && dod != null && Duration.between(admissionLast.getDischTime(), dod).toDays() < deathDaysAfterDischargeConsiderPositive)
        {
            extractedTargetDto.setTargetValue(2);
        }
        else
        {
            extractedTargetDto.setTargetValue(0);
        }

        extractedTargetDtos.add(extractedTargetDto);

        return extractedTargetDtos;
    }
}
