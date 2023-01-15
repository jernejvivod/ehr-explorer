package si.jernej.mexplorer.core.test.processing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.processing.TargetExtraction;
import si.jernej.mexplorer.core.test.ACoreTest;
import si.jernej.mexplorer.processorapi.v1.model.ExtractedTargetDto;

class TargetExtractionTest extends ACoreTest
{
    @Inject
    private TargetExtraction targetExtraction;

    @Test
    void testExtractPatientDiedDuringAdmissionTargetEmptyIds()
    {
        List<Long> ids = List.of();

        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractPatientDiedDuringAdmissionTarget(ids, null);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertTrue(extractedTargetDtos.isEmpty());
    }

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

        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractPatientDiedDuringAdmissionTarget(ids, null);

        for (ExtractedTargetDto extractedTargetDto : extractedTargetDtos)
        {
            short expectedVal = em.createQuery("SELECT a.hospitalExpireFlag FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", Short.class)
                    .setParameter("hadmId", extractedTargetDto.getRootEntityId())
                    .getSingleResult();

            Assertions.assertEquals(expectedVal, extractedTargetDto.getTargetValue());
        }
    }

    @Test
    void testExtractReadmissionTargetEmptyIds()
    {
        List<Long> ids = List.of();

        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractReadmissionTarget(ids, null, 30, 30);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertTrue(extractedTargetDtos.isEmpty());
    }

    @Test
    void testExtractReadmissionTarget()
    {
        List<Long> ids = List.of(
                36L,
                711L
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ExtractedTargetDto extractedTargetDto182104 = new ExtractedTargetDto()
                .rootEntityId(182104L)
                .targetValue(1)
                .dateTimeLimit(LocalDateTime.parse("2131-05-08 14:00:00", formatter));
        ExtractedTargetDto extractedTargetDto122659 = new ExtractedTargetDto()
                .rootEntityId(122659L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2131-05-25 13:30:00", formatter));
        ExtractedTargetDto extractedTargetDto165660 = new ExtractedTargetDto()
                .rootEntityId(165660L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2134-05-20 13:16:00", formatter));

        ExtractedTargetDto extractedTargetDto167380 = new ExtractedTargetDto()
                .rootEntityId(167380L)
                .targetValue(1)
                .dateTimeLimit(LocalDateTime.parse("2184-05-15 15:05:00", formatter));
        ExtractedTargetDto extractedTargetDto114791 = new ExtractedTargetDto()
                .rootEntityId(114791L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2184-06-20 10:50:00", formatter));
        ExtractedTargetDto extractedTargetDto120522 = new ExtractedTargetDto()
                .rootEntityId(120522L)
                .targetValue(1)
                .dateTimeLimit(LocalDateTime.parse("2184-10-15 18:11:00", formatter));
        ExtractedTargetDto extractedTargetDto168530 = new ExtractedTargetDto()
                .rootEntityId(168530L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2184-11-05 14:00:00", formatter));
        ExtractedTargetDto extractedTargetDto158767 = new ExtractedTargetDto()
                .rootEntityId(158767L)
                .targetValue(1)
                .dateTimeLimit(LocalDateTime.parse("2185-05-16 17:10:00", formatter));

        // get results and perform assertions
        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractReadmissionTarget(ids, null, 30, 30);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertEquals(8, extractedTargetDtos.size());

        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto182104));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto122659));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto165660));

        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto167380));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto114791));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto120522));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto168530));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto158767));
    }

    @Test
    void testExtractReadmissionTargetAgeLimit()
    {
        List<Long> ids = List.of(
                303L
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // The patient is associated with 3 admissions. The patient's age was below 18 years when the first admission happened. They were over 18 when the second admission happened.

        ExtractedTargetDto extractedTargetDto103013 = new ExtractedTargetDto()
                .rootEntityId(103013L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2163-04-04 20:45:00", formatter));

        // get results and perform assertions
        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractReadmissionTarget(ids, 18, 30, 30);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertEquals(1, extractedTargetDtos.size());

        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto103013));
    }
}
