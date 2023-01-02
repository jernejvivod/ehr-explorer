package si.jernej.mexplorer.core.test.processing;

import java.util.List;

import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.processing.TargetExtraction;
import si.jernej.mexplorer.core.service.TargetExtractionService;
import si.jernej.mexplorer.processorapi.v1.model.ExtractedTargetDto;
import si.jernej.mexplorer.test.ATestBase;

class TargetExtractionTest extends ATestBase
{
    @Override
    protected Weld loadWeld(Weld weld)
    {
        return weld.addPackages(
                true,
                getClass(),
                TargetExtractionService.class,
                TargetExtraction.class,
                MimicEntityManager.class
        );
    }

    @Inject
    private TargetExtraction targetExtraction;

    @Test
    void testExtractPatientDiedDuringAdmissionTarget()
    {
        List<Long> ids = List.of(
                100001L,
                100003L,
                100006L,
                100007L,
                100009L,
                100010L,
                100011L,
                100012L,
                100014L,
                100016L,
                100017L,
                100018L,
                100019L,
                100020L,
                100021L,
                100023L,
                100024L,
                100025L,
                100028L,
                100029L,
                100030L,
                100031L,
                100033L,
                100034L,
                100035L,
                100036L,
                100037L,
                100038L,
                100039L,
                100040L,
                100041L,
                100044L,
                100045L,
                100046L,
                100047L,
                100050L,
                100052L,
                100053L,
                100055L,
                100058L,
                100059L,
                100060L,
                100061L
        );

        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractPatientDiedDuringAdmissionTarget(ids);

        for (ExtractedTargetDto extractedTargetDto : extractedTargetDtos)
        {
            short expectedVal = em.createQuery("SELECT a.hospitalExpireFlag FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", Short.class)
                    .setParameter("hadmId", extractedTargetDto.getRootEntityId())
                    .getSingleResult();

            Assertions.assertEquals(expectedVal, extractedTargetDto.getTargetValue());
        }
    }

}
