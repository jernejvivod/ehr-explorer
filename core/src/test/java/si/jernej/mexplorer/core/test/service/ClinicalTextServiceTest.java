package si.jernej.mexplorer.core.test.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;

import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.processing.Wordification;
import si.jernej.mexplorer.core.service.ClinicalTextService;
import si.jernej.mexplorer.processorapi.v1.model.ClinicalTextConfigDto;
import si.jernej.mexplorer.processorapi.v1.model.DataRangeSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.RootEntitiesSpecDto;
import si.jernej.mexplorer.test.ATestBase;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClinicalTextServiceTest extends ATestBase
{
    @Override
    protected Weld loadWeld(Weld weld)
    {
        return weld.addPackages(
                true,
                getClass(),
                ClinicalTextService.class,
                Wordification.class,
                MimicEntityManager.class
        );
    }

    @Inject
    private ClinicalTextService clinicalTextService;

    @Test
    void extractClinicalTextWithEmptyIdList()
    {
        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setClinicalTextEntityName("NoteEventsEntity");
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setDateTimePropertiesNames(List.of("charttime", "chartdate"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of());
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        var res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void extractAllClinicalTextForSingleNonexistentId()
    {
        final long rootEntityId = 100000L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setClinicalTextEntityName("NoteEventsEntity");
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setDateTimePropertiesNames(List.of("charttime", "chartdate"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        var res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void extractAllClinicalTextForSingleId()
    {
        final long rootEntityId = 100001L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setClinicalTextEntityName("NoteEventsEntity");
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setDateTimePropertiesNames(List.of("charttime", "chartdate"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        var res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId));

        List<String> texts = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.charttime, n.chartdate", String.class)
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
        clinicalTextConfigDto.setClinicalTextEntityName("NoteEventsEntity");
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setDateTimePropertiesNames(List.of("charttime"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        DataRangeSpecDto dataRangeSpecDto = new DataRangeSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);

        clinicalTextConfigDto.setDataRangeSpec(dataRangeSpecDto);

        var res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId));

        List<String> texts = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId AND n.charttime IS NOT NULL ORDER BY n.charttime ASC", String.class)
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
        clinicalTextConfigDto.setClinicalTextEntityName("NoteEventsEntity");
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setDateTimePropertiesNames(List.of("charttime", "chartdate"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId1, rootEntityId2));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        var res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(2, res.size());
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId1));
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId2));

        List<String> texts1 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.charttime, n.chartdate ASC", String.class)
                .setParameter("hadmId", rootEntityId1)
                .getResultList();

        List<String> texts2 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.charttime, n.chartdate ASC", String.class)
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
        clinicalTextConfigDto.setClinicalTextEntityName("NoteEventsEntity");
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setDateTimePropertiesNames(List.of("charttime"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId1, rootEntityId2));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        DataRangeSpecDto dataRangeSpecDto = new DataRangeSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);
        clinicalTextConfigDto.setDataRangeSpec(dataRangeSpecDto);

        var res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(2, res.size());
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId1));
        Assertions.assertTrue(res.stream().anyMatch(r -> r.getRootEntityId() == rootEntityId2));

        Stream<String> texts1 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId AND n.charttime != null ORDER BY n.charttime ASC", String.class)
                .setParameter("hadmId", rootEntityId1)
                .getResultStream();

        Stream<String> texts2 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId AND n.charttime != null ORDER BY n.charttime ASC", String.class)
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
    @Timeout(value=30)
    void extractClinicalText30PercentIdsWithDateTimeLimit()
    {
        List<Long> ids = em.createQuery("SELECT a.hadmId FROM AdmissionsEntity a", Long.class)
                .setMaxResults(17693)
                .getResultList();

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setClinicalTextEntityName("NoteEventsEntity");
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setDateTimePropertiesNames(List.of("charttime"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(ids);
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        DataRangeSpecDto dataRangeSpecDto = new DataRangeSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);
        clinicalTextConfigDto.setDataRangeSpec(dataRangeSpecDto);

        var res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
    }

    @Test
    @Timeout(value=30)
    void extractClinicalText50PercentIdsWithDateTimeLimit()
    {
        List<Long> ids = em.createQuery("SELECT a.hadmId FROM AdmissionsEntity a", Long.class)
                .setMaxResults(29488)
                .getResultList();

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        clinicalTextConfigDto.setClinicalTextEntityName("NoteEventsEntity");
        clinicalTextConfigDto.setTextPropertyName("text");
        clinicalTextConfigDto.setClinicalTextEntityIdPropertyName("rowId");
        clinicalTextConfigDto.setDateTimePropertiesNames(List.of("charttime"));
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(ids);
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        DataRangeSpecDto dataRangeSpecDto = new DataRangeSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);
        clinicalTextConfigDto.setDataRangeSpec(dataRangeSpecDto);

        var res = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
    }
}
