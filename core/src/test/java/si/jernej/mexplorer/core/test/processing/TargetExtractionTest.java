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

        // for 36L
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

        // for 711L
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
                .targetValue(2)
                .dateTimeLimit(LocalDateTime.parse("2185-05-16 17:10:00", formatter));

        // get results and perform assertions
        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractReadmissionTarget(ids, null, 30, 30);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertEquals(8, extractedTargetDtos.size());

        // for 36L
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto182104));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto122659));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto165660));

        // for 711L
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

    @Test
    void testExtractIcuReadmissionTargetEmptyIds()
    {
        List<Long> ids = List.of();

        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractIcuReadmissionTarget(ids, null, 30, 30);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertTrue(extractedTargetDtos.isEmpty());
    }

    @Test
    void testExtractIcuReadmissionTarget()
    {
        List<Long> ids = List.of(
                131L,
                36L,
                29744L,
                346L
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // for 131L
        ExtractedTargetDto extractedTargetDto237399 = new ExtractedTargetDto()
                .rootEntityId(237399L)
                .targetValue(1)
                .dateTimeLimit(LocalDateTime.parse("2143-12-07 22:12:09", formatter));
        ExtractedTargetDto extractedTargetDto280415 = new ExtractedTargetDto()
                .rootEntityId(280415L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2143-12-12 12:37:44", formatter));

        // for 36L
        ExtractedTargetDto extractedTargetDto280987 = new ExtractedTargetDto()
                .rootEntityId(280987L)
                .targetValue(2)
                .dateTimeLimit(LocalDateTime.parse("2131-05-05 13:07:03", formatter));
        ExtractedTargetDto extractedTargetDto211200 = new ExtractedTargetDto()
                .rootEntityId(211200L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2131-05-23 19:56:11", formatter));
        ExtractedTargetDto extractedTargetDto241249 = new ExtractedTargetDto()
                .rootEntityId(241249L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2134-05-16 15:14:20", formatter));

        // for 29744L
        ExtractedTargetDto extractedTargetDto259615 = new ExtractedTargetDto()
                .rootEntityId(259615L)
                .targetValue(2)
                .dateTimeLimit(LocalDateTime.parse("2163-02-12 18:42:39", formatter));
        ExtractedTargetDto extractedTargetDto246125 = new ExtractedTargetDto()
                .rootEntityId(246125L)
                .targetValue(4)
                .dateTimeLimit(LocalDateTime.parse("2163-03-15 01:10:43", formatter));

        // for 346L
        ExtractedTargetDto extractedTargetDto258237 = new ExtractedTargetDto()
                .rootEntityId(258237L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2148-12-05 18:30:53", formatter));
        ExtractedTargetDto extractedTargetDto260798 = new ExtractedTargetDto()
                .rootEntityId(260798L)
                .targetValue(3)
                .dateTimeLimit(LocalDateTime.parse("2149-12-04 17:09:36", formatter));

        // get results and perform assertions
        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractIcuReadmissionTarget(ids, null, 30, 30);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertEquals(9, extractedTargetDtos.size());

        // for 131L
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto237399));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto280415));

        // for 36L
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto280987));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto211200));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto241249));

        // for 29744L
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto259615));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto246125));

        // for 346L
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto258237));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto260798));
    }

    @Test
    void testExtractIcuReadmissionTargetAgeLimit()
    {
        List<Long> ids = List.of(
                303L
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // The patient is associated with 2 ICU admissions. The patient's age was below 18 years when the first admission happened. They were over 18 when the second admission happened.
        ExtractedTargetDto extractedTargetDto103013 = new ExtractedTargetDto()
                .rootEntityId(261797L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2163-04-01 17:41:54", formatter));

        // get results and perform assertions
        List<ExtractedTargetDto> extractedTargetDtos = targetExtraction.extractIcuReadmissionTarget(ids, 18, 30, 30);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertEquals(1, extractedTargetDtos.size());

        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto103013));
    }
}
