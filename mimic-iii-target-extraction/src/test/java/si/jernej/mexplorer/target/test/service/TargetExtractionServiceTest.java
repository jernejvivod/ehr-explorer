package si.jernej.mexplorer.target.test.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.processorapi.v1.model.ExtractedTargetDto;
import si.jernej.mexplorer.processorapi.v1.model.TargetExtractionSpecDto;
import si.jernej.mexplorer.target.test.ATargetExtractionTest;
import si.jernej.mexplorer.target.service.TargetExtractionService;

class TargetExtractionServiceTest extends ATargetExtractionTest
{
    @Inject
    private TargetExtractionService targetExtractionService;

    @Test
    void testPatientDiedDuringAdmissionTargetExtraction()
    {
        TargetExtractionSpecDto targetExtractionSpecDto = new TargetExtractionSpecDto();
        targetExtractionSpecDto.setTargetType(TargetExtractionSpecDto.TargetTypeEnum.PATIENT_DIED_DURING_ADMISSION);
        targetExtractionSpecDto.setIds(
                List.of(
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
                )
        );

        List<ExtractedTargetDto> extractedTargetDtos = targetExtractionService.computeTarget(targetExtractionSpecDto);

        for (ExtractedTargetDto extractedTargetDto : extractedTargetDtos)
        {
            short expectedVal = em.createQuery("SELECT a.hospitalExpireFlag FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", Short.class)
                    .setParameter("hadmId", extractedTargetDto.getRootEntityId())
                    .getSingleResult();

            Assertions.assertEquals(expectedVal, extractedTargetDto.getTargetValue());
        }
    }

    @Test
    void testExtractReadmissionTarget()
    {
        TargetExtractionSpecDto targetExtractionSpecDto = new TargetExtractionSpecDto();
        targetExtractionSpecDto.setTargetType(TargetExtractionSpecDto.TargetTypeEnum.HOSPITAL_READMISSION_HAPPENED);
        targetExtractionSpecDto.setIds(
                List.of(
                        36L
                )
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ExtractedTargetDto extractedTargetDto182104 = new ExtractedTargetDto()
                .rootEntityId(36L)
                .targetEntityId(182104L)
                .targetValue(1)
                .dateTimeLimit(LocalDateTime.parse("2131-05-08 14:00:00", formatter));
        ExtractedTargetDto extractedTargetDto122659 = new ExtractedTargetDto()
                .rootEntityId(36L)
                .targetEntityId(122659L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2131-05-25 13:30:00", formatter));
        ExtractedTargetDto extractedTargetDto165660 = new ExtractedTargetDto()
                .rootEntityId(36L)
                .targetEntityId(165660L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2134-05-20 13:16:00", formatter));

        // get results and perform assertions
        List<ExtractedTargetDto> extractedTargetDtos = targetExtractionService.computeTarget(targetExtractionSpecDto);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertEquals(3, extractedTargetDtos.size());

        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto182104));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto122659));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto165660));
    }

    @Test
    void testExtractIcuReadmissionTarget()
    {
        TargetExtractionSpecDto targetExtractionSpecDto = new TargetExtractionSpecDto();
        targetExtractionSpecDto.setTargetType(TargetExtractionSpecDto.TargetTypeEnum.ICU_STAY_READMISSION_HAPPENED);
        targetExtractionSpecDto.setIds(
                List.of(
                        131L
                )
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ExtractedTargetDto extractedTargetDto237399 = new ExtractedTargetDto()
                .rootEntityId(131L)
                .targetEntityId(237399L)
                .targetValue(1)
                .dateTimeLimit(LocalDateTime.parse("2143-12-07 22:12:09", formatter));
        ExtractedTargetDto extractedTargetDto280415 = new ExtractedTargetDto()
                .rootEntityId(131L)
                .targetEntityId(280415L)
                .targetValue(0)
                .dateTimeLimit(LocalDateTime.parse("2143-12-12 12:37:44", formatter));

        // get results and perform assertions
        List<ExtractedTargetDto> extractedTargetDtos = targetExtractionService.computeTarget(targetExtractionSpecDto);

        Assertions.assertNotNull(extractedTargetDtos);
        Assertions.assertEquals(2, extractedTargetDtos.size());

        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto237399));
        Assertions.assertTrue(extractedTargetDtos.contains(extractedTargetDto280415));
    }
}
