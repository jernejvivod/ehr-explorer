package si.jernej.mexplorer.core.test.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.processing.Wordification;
import si.jernej.mexplorer.core.service.ClinicalTextService;
import si.jernej.mexplorer.processorapi.v1.model.ClinicalTextConfigDto;
import si.jernej.mexplorer.processorapi.v1.model.ClinicalTextResultDto;
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
    void joinClinicalTextForEntityEmpty()
    {
        Set<ClinicalTextResultDto> clinicalTextResultDtos = clinicalTextService.joinClinicalTextForEntity(Map.of());
        Assertions.assertTrue(clinicalTextResultDtos.isEmpty());
    }

    @Test
    void joinClinicalTextForEntityEmptyBasic()
    {
        Map<Object, List<ImmutablePair<String, Timestamp>>> val = Map.of(
                1L, List.of(ImmutablePair.of("this", null), ImmutablePair.of("is", null), ImmutablePair.of("a", null), ImmutablePair.of("test", null)),
                2L, List.of(ImmutablePair.of("Hello", null), ImmutablePair.of("to", null), ImmutablePair.of("the", null), ImmutablePair.of("World", null))
        );

        Set<ClinicalTextResultDto> clinicalTextResultDtos = clinicalTextService.joinClinicalTextForEntity(val);

        ClinicalTextResultDto clinicalTextResultDtoExpected1 = new ClinicalTextResultDto();
        clinicalTextResultDtoExpected1.setRootEntityId(1L);
        clinicalTextResultDtoExpected1.setText("this is a test");

        ClinicalTextResultDto clinicalTextResultDtoExpected2 = new ClinicalTextResultDto();
        clinicalTextResultDtoExpected2.setRootEntityId(2L);
        clinicalTextResultDtoExpected2.setText("Hello to the World");

        Assertions.assertTrue(clinicalTextResultDtos.contains(clinicalTextResultDtoExpected1));
        Assertions.assertTrue(clinicalTextResultDtos.contains(clinicalTextResultDtoExpected2));
    }

    @Test
    void extractClinicalTextWithEmptyIdList()
    {
        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of());
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        var rootEntityIdTotextsAndChartTimes = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertTrue(rootEntityIdTotextsAndChartTimes.isEmpty());
        Assertions.assertTrue(clinicalTextService.joinClinicalTextForEntity(rootEntityIdTotextsAndChartTimes).isEmpty());
    }

    @Test
    void extractAllClinicalTextForSingleNonexistentId()
    {
        final long rootEntityId = 100000L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        var rootEntityIdToTextsAndChartTimes = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(rootEntityIdToTextsAndChartTimes);
        Assertions.assertTrue(rootEntityIdToTextsAndChartTimes.isEmpty());
    }

    @Test
    void extractAllClinicalTextForSingleId()
    {
        final long rootEntityId = 100001L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        var rootEntityIdTotextsAndChartTimes = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(rootEntityIdTotextsAndChartTimes);
        Assertions.assertFalse(rootEntityIdTotextsAndChartTimes.isEmpty());
        Assertions.assertEquals(1, rootEntityIdTotextsAndChartTimes.size());
        Assertions.assertTrue(rootEntityIdTotextsAndChartTimes.containsKey(rootEntityId));
        Assertions.assertEquals(2, rootEntityIdTotextsAndChartTimes.get(rootEntityId).size());

        List<String> texts = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.charttime", String.class)
                .setParameter("hadmId", rootEntityId)
                .getResultList();

        Assertions.assertEquals(texts.get(0), rootEntityIdTotextsAndChartTimes.get(rootEntityId).get(0).getLeft());
        Assertions.assertEquals(texts.get(1), rootEntityIdTotextsAndChartTimes.get(rootEntityId).get(1).getLeft());
    }

    @Test
    void extractClinicalTextForSingleIdWithDateLimit()
    {
        final long rootEntityId = 100001L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        DataRangeSpecDto dataRangeSpecDto = new DataRangeSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);

        clinicalTextConfigDto.setDataRangeSpec(dataRangeSpecDto);

        var rootEntityIdTotextsAndChartTimes = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(rootEntityIdTotextsAndChartTimes);
        Assertions.assertFalse(rootEntityIdTotextsAndChartTimes.isEmpty());
        Assertions.assertEquals(1, rootEntityIdTotextsAndChartTimes.size());
        Assertions.assertTrue(rootEntityIdTotextsAndChartTimes.containsKey(rootEntityId));
        Assertions.assertEquals(1, rootEntityIdTotextsAndChartTimes.get(rootEntityId).size());

        List<String> texts = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.charttime ASC", String.class)
                .setParameter("hadmId", rootEntityId)
                .getResultList();

        Assertions.assertEquals(texts.get(0), rootEntityIdTotextsAndChartTimes.get(rootEntityId).get(0).getLeft());
    }

    @Test
    void extractAllClinicalTextForTwoIds()
    {
        final long rootEntityId1 = 100001L;
        final long rootEntityId2 = 100006L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId1, rootEntityId2));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        var rootEntityIdTotextsAndChartTimes = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(rootEntityIdTotextsAndChartTimes);
        Assertions.assertFalse(rootEntityIdTotextsAndChartTimes.isEmpty());
        Assertions.assertEquals(2, rootEntityIdTotextsAndChartTimes.size());
        Assertions.assertTrue(rootEntityIdTotextsAndChartTimes.containsKey(rootEntityId1));
        Assertions.assertTrue(rootEntityIdTotextsAndChartTimes.containsKey(rootEntityId2));

        List<String> texts1 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.chartdate ASC, n.charttime ASC", String.class)
                .setParameter("hadmId", rootEntityId1)
                .getResultList();

        List<String> texts2 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.chartdate ASC, n.charttime ASC", String.class)
                .setParameter("hadmId", rootEntityId2)
                .getResultList();

        Assertions.assertEquals(texts1.size(), rootEntityIdTotextsAndChartTimes.get(rootEntityId1).size());
        Assertions.assertEquals(texts2.size(), rootEntityIdTotextsAndChartTimes.get(rootEntityId2).size());

        for (int i = 0; i < texts1.size(); i++)
        {
            Assertions.assertEquals(texts1.get(i), rootEntityIdTotextsAndChartTimes.get(rootEntityId1).get(i).getLeft());
        }

        for (int i = 0; i < texts2.size(); i++)
        {
            Assertions.assertEquals(texts2.get(i), rootEntityIdTotextsAndChartTimes.get(rootEntityId2).get(i).getLeft());
        }
    }

    @Test
    void extractClinicalTextForTwoIdsWithDateLimit()
    {
        final long rootEntityId1 = 100001L;
        final long rootEntityId2 = 100006L;

        ClinicalTextConfigDto clinicalTextConfigDto = new ClinicalTextConfigDto();
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(rootEntityId1, rootEntityId2));
        clinicalTextConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        DataRangeSpecDto dataRangeSpecDto = new DataRangeSpecDto();
        dataRangeSpecDto.setFirstMinutes(1440);
        clinicalTextConfigDto.setDataRangeSpec(dataRangeSpecDto);

        var rootEntityIdTotextsAndChartTimes = clinicalTextService.extractClinicalText(clinicalTextConfigDto);
        Assertions.assertNotNull(rootEntityIdTotextsAndChartTimes);
        Assertions.assertFalse(rootEntityIdTotextsAndChartTimes.isEmpty());
        Assertions.assertEquals(2, rootEntityIdTotextsAndChartTimes.size());
        Assertions.assertTrue(rootEntityIdTotextsAndChartTimes.containsKey(rootEntityId1));
        Assertions.assertTrue(rootEntityIdTotextsAndChartTimes.containsKey(rootEntityId2));

        List<String> texts1 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.chartdate ASC, n.charttime ASC", String.class)
                .setParameter("hadmId", rootEntityId1)
                .getResultList();

        List<String> texts2 = em.createQuery("SELECT n.text FROM NoteEventsEntity n WHERE n.admissionsEntity.hadmId=:hadmId ORDER BY n.chartdate ASC, n.charttime ASC", String.class)
                .setParameter("hadmId", rootEntityId2)
                .getResultList();

        final int expectedSize1 = 1;
        final int expectedSize2 = 5;

        Assertions.assertEquals(expectedSize1, rootEntityIdTotextsAndChartTimes.get(rootEntityId1).size());
        Assertions.assertEquals(expectedSize2, rootEntityIdTotextsAndChartTimes.get(rootEntityId2).size());

        for (int i = 0; i < expectedSize1; i++)
        {
            Assertions.assertEquals(texts1.get(i), rootEntityIdTotextsAndChartTimes.get(rootEntityId1).get(i).getLeft());
        }

        for (int i = 0; i < expectedSize2; i++)
        {
            Assertions.assertEquals(texts2.get(i), rootEntityIdTotextsAndChartTimes.get(rootEntityId2).get(i).getLeft());
        }
    }
}
