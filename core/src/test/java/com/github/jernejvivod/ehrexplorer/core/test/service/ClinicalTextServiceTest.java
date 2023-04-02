package com.github.jernejvivod.ehrexplorer.core.test.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.jernejvivod.ehrexplorer.core.service.ClinicalTextService;
import com.github.jernejvivod.ehrexplorer.core.test.ACoreTest;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.ClinicalTextConfigDto;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.ClinicalTextExtractionDurationSpecDto;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.ClinicalTextResultDto;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.RootEntitiesSpecDto;

class ClinicalTextServiceTest extends ACoreTest
{
    @Inject
    private ClinicalTextService clinicalTextService;

    @Test
    void extractClinicalTextWithEmptyIdList()
    {
        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("AdmissionsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of());
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        Set<ClinicalTextResultDto> res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void extractAllClinicalTextForSingleNonexistentId()
    {
        final long rootEntityId = 100000L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("AdmissionsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setClinicalTextDateTimePropertiesNames(List.of("chartTime", "chartDate"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        Set<ClinicalTextResultDto> res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void extractAllClinicalTextForSingleId()
    {
        final long rootEntityId = 100001L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("AdmissionsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setClinicalTextDateTimePropertiesNames(List.of("chartTime", "chartDate"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        Set<ClinicalTextResultDto> res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId));

        List<String> texts = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.chartTime, n.chartDate", String.class)
                .setParameter("hadmId", rootEntityId)
                .getResultList();

        String joinedText = String.join(" ", texts);
        Assertions.assertEquals(joinedText, res.stream().filter(r -> r.getRootEntityId() == rootEntityId).findAny().orElseGet(Assertions::fail).getText());
    }

    @Test
    void extractClinicalTextForSingleIdWithDateTimeLimit()
    {
        final long rootEntityId = 100001L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("AdmissionsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setClinicalTextDateTimePropertiesNames(List.of("chartTime"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        ClinicalTextExtractionDurationSpecDto dataRangeSpecDto = new ClinicalTextExtractionDurationSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);

        clinicalTextConfigDto.setClinicalTextExtractionDurationSpec(dataRangeSpecDto);

        Set<ClinicalTextResultDto> res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId));

        List<String> texts = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId AND n.chartTime IS NOT NULL ORDER BY n.chartTime ASC", String.class)
                .setParameter("hadmId", rootEntityId)
                .getResultList();

        Assertions.assertEquals(texts.get(0), res.stream().filter(r -> r.getRootEntityId() == rootEntityId).findAny().orElseGet(Assertions::fail).getText());
    }

    @Test
    void extractAllClinicalTextForTwoIds()
    {
        final long rootEntityId1 = 100001L;
        final long rootEntityId2 = 100006L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("AdmissionsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setClinicalTextDateTimePropertiesNames(List.of("chartTime", "chartDate"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId1, rootEntityId2));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        Set<ClinicalTextResultDto> res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(2, res.size());
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId1));
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId2));

        List<String> texts1 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.chartTime, n.chartDate ASC", String.class)
                .setParameter("hadmId", rootEntityId1)
                .getResultList();

        List<String> texts2 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.chartTime, n.chartDate ASC", String.class)
                .setParameter("hadmId", rootEntityId2)
                .getResultList();

        String joinedText1 = String.join(" ", texts1);
        String joinedText2 = String.join(" ", texts2);

        Assertions.assertEquals(joinedText1, res.stream().filter(r -> r.getRootEntityId() == rootEntityId1).findAny().orElseGet(Assertions::fail).getText());
        Assertions.assertEquals(joinedText2, res.stream().filter(r -> r.getRootEntityId() == rootEntityId2).findAny().orElseGet(Assertions::fail).getText());
    }

    @Test
    void extractClinicalTextForTwoIdsWithDateTimeLimit()
    {
        final long rootEntityId1 = 100001L;
        final long rootEntityId2 = 100006L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("AdmissionsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setClinicalTextDateTimePropertiesNames(List.of("chartTime"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId1, rootEntityId2));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        ClinicalTextExtractionDurationSpecDto dataRangeSpecDto = new ClinicalTextExtractionDurationSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);
        clinicalTextConfigDto.setClinicalTextExtractionDurationSpec(dataRangeSpecDto);

        Set<ClinicalTextResultDto> res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(2, res.size());
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId1));
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId2));

        Stream<String> texts1 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId AND n.chartTime != null ORDER BY n.chartTime ASC", String.class)
                .setParameter("hadmId", rootEntityId1)
                .getResultStream();

        Stream<String> texts2 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId AND n.chartTime != null ORDER BY n.chartTime ASC", String.class)
                .setParameter("hadmId", rootEntityId2)
                .getResultStream();

        final int expectedSize1 = 1;
        final int expectedSize2 = 4;

        String joinedText1 = texts1.limit(expectedSize1).collect(Collectors.joining(" "));
        String joinedText2 = texts2.limit(expectedSize2).collect(Collectors.joining(" "));

        Assertions.assertEquals(joinedText1, res.stream().filter(r -> r.getRootEntityId() == rootEntityId1).findAny().orElseGet(Assertions::fail).getText());
        Assertions.assertEquals(joinedText2, res.stream().filter(r -> r.getRootEntityId() == rootEntityId2).findAny().orElseGet(Assertions::fail).getText());
    }

    @Test
    @Timeout(value = 30)
    void extractClinicalText30PercentIdsWithDateTimeLimit()
    {
        List<Long> ids = em.createQuery("SELECT a.hadmId FROM AdmissionsEntity a", Long.class)
                .setMaxResults(17693)
                .getResultList();

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("AdmissionsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setClinicalTextDateTimePropertiesNames(List.of("chartTime"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(ids);
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        ClinicalTextExtractionDurationSpecDto dataRangeSpecDto = new ClinicalTextExtractionDurationSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);
        clinicalTextConfigDto.setClinicalTextExtractionDurationSpec(dataRangeSpecDto);

        Set<ClinicalTextResultDto> res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
    }

    @Test
    @Timeout(value = 60)
    void extractClinicalText50PercentIdsWithDateTimeLimit()
    {
        List<Long> ids = em.createQuery("SELECT a.hadmId FROM AdmissionsEntity a", Long.class)
                .setMaxResults(29488)
                .getResultList();

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("AdmissionsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setClinicalTextDateTimePropertiesNames(List.of("chartTime"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(ids);
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        ClinicalTextExtractionDurationSpecDto dataRangeSpecDto = new ClinicalTextExtractionDurationSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);
        clinicalTextConfigDto.setClinicalTextExtractionDurationSpec(dataRangeSpecDto);

        Set<ClinicalTextResultDto> res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
    }

    @Test
    void testExtractClinicalTextForIcuStayEntity()
    {
        final long rootEntityIdFirstIcuStay = 221194L;
        final long rootEntityIdSecondIcuStay = 275958L;

        //language=JPAQL
        final String textsQuery = """
                SELECT n.text FROM NoteEventsEntity n
                JOIN n.patientsEntity p
                JOIN p.icuStaysEntitys icus
                WHERE icus.icuStayId=:icuStayId AND ((n.chartTime IS NOT NULL AND n.chartTime < icus.outTime) OR (n.chartTime IS NULL AND n.chartDate IS NOT NULL AND n.chartDate < icus.outTime))
                ORDER BY n.chartTime, n.chartDate, n.rowId ASC
                """;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setForeignKeyPath(List.of("IcuStaysEntity", "PatientsEntity", "NoteEventsEntity"));
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setClinicalTextDateTimePropertiesNames(List.of("chartTime", "chartDate"));
        clinicalTextConfigDto.setRootEntityDatetimePropertyForCutoff("outTime");
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("IcuStaysEntity");
        rootEntitiesSpecDto.setIdProperty("icuStayId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityIdFirstIcuStay));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        // first ICU stay for patient

        final Set<ClinicalTextResultDto> resFirstIcuStay = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(resFirstIcuStay);
        Assertions.assertFalse(resFirstIcuStay.isEmpty());
        Assertions.assertEquals(1, resFirstIcuStay.size());
        Assertions.assertTrue(resFirstIcuStay.stream().anyMatch(r -> r.getRootEntityId() == rootEntityIdFirstIcuStay));

        final String texts = em.createQuery(textsQuery, String.class)
                .setParameter("icuStayId", rootEntityIdFirstIcuStay)
                .getResultStream()
                .collect(Collectors.joining(" "));

        Assertions.assertEquals(texts, resFirstIcuStay.stream().filter(r -> r.getRootEntityId() == rootEntityIdFirstIcuStay).findAny().orElseGet(Assertions::fail).getText());

        // second ICU stay for patient

        rootEntitiesSpecDto.setIds(List.of(rootEntityIdSecondIcuStay));

        final Set<ClinicalTextResultDto> resSecondIcuStay = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(resSecondIcuStay);
        Assertions.assertFalse(resSecondIcuStay.isEmpty());
        Assertions.assertEquals(1, resSecondIcuStay.size());
        Assertions.assertTrue(resSecondIcuStay.stream().anyMatch(r -> r.getRootEntityId() == rootEntityIdSecondIcuStay));

        final String texts2 = em.createQuery(textsQuery, String.class)
                .setParameter("icuStayId", rootEntityIdSecondIcuStay)
                .getResultStream()
                .collect(Collectors.joining(" "));

        Assertions.assertEquals(texts2, resSecondIcuStay.stream().filter(r -> r.getRootEntityId() == rootEntityIdSecondIcuStay).findAny().orElseGet(Assertions::fail).getText());
    }
}
