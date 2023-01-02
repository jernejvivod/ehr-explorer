package si.jernej.mexplorer.core.processing;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.jernej.mexplorer.core.manager.MimicEntityManager;
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
    public List<ExtractedTargetDto> extractPatientDiedDuringAdmissionTarget(List<Long> ids)
    {
        logger.info("extracting target 'patient died during admission' ({} ids)", ids.size());

        List<Object[]> results = mimicEntityManager.getResultListForExtractPatientDiedDuringAdmissionTarget(ids);

        return results.stream().map(r -> {
            ExtractedTargetDto extractedTargetDto = new ExtractedTargetDto();
            extractedTargetDto.setRootEntityId((Long) r[0]);
            extractedTargetDto.setTargetValue(((Short) r[1]).intValue());
            return extractedTargetDto;
        }).toList();
    }
}
