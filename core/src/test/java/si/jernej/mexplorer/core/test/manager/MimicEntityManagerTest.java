package si.jernej.mexplorer.core.test.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.test.ACoreTest;
import si.jernej.mexplorer.entity.AdmissionsEntity;
import si.jernej.mexplorer.entity.PatientsEntity;

public class MimicEntityManagerTest extends ACoreTest
{
    @Inject
    private MimicEntityManager mimicEntityManager;

    @Test
    void testForeignKeyPathToIsSingularMapEmpty()
    {
        final List<String> foreignKeyPath = List.of();

        boolean[] foreignKeyPathIsSingularMask = mimicEntityManager.foreignKeyPathToIsSingularMask(foreignKeyPath);

        Assertions.assertArrayEquals(new boolean[] {}, foreignKeyPathIsSingularMask);
    }

    @Test
    void testForeignKeyPathToIsSingularMapTwoWrongEntity()
    {
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "PatientsEntity", "Wrong");

        Assertions.assertThrows(ValidationCoreException.class, () -> mimicEntityManager.foreignKeyPathToIsSingularMask(foreignKeyPath));
    }

    @Test
    void testForeignKeyPathToIsSingularMapOnePlural()
    {
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "NoteEventsEntity");

        boolean[] foreignKeyPathIsSingularMask = mimicEntityManager.foreignKeyPathToIsSingularMask(foreignKeyPath);

        Assertions.assertArrayEquals(new boolean[] { false }, foreignKeyPathIsSingularMask);
    }

    @Test
    void testForeignKeyPathToIsSingularMapOneSingular()
    {
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "PatientsEntity");

        boolean[] foreignKeyPathIsSingularMask = mimicEntityManager.foreignKeyPathToIsSingularMask(foreignKeyPath);

        Assertions.assertArrayEquals(new boolean[] { true }, foreignKeyPathIsSingularMask);
    }

    @Test
    void testForeignKeyPathToIsSingularMapTwo()
    {
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "PatientsEntity", "IcuStaysEntity");

        boolean[] foreignKeyPathIsSingularMask = mimicEntityManager.foreignKeyPathToIsSingularMask(foreignKeyPath);

        Assertions.assertArrayEquals(new boolean[] { true, false }, foreignKeyPathIsSingularMask);
    }

    @Test
    void testComputeIdPropertyValuesForForeignPathWrongEntityName()
    {
        final Set<Long> hadmIds = Set.of(100001L, 100003L, 100006L);
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "Wrong");

        Assertions.assertThrows(ValidationCoreException.class, () -> mimicEntityManager.mapRootEntityIdsToClinicalText(
                        hadmIds,
                        foreignKeyPath,
                        "hadmId",
                        "rowId",
                        "text",
                        null,
                        null
                )
        );
    }

    @Test
    void testComputeIdPropertyValuesForForeignPathEndEmptyIds()
    {
        final Set<Long> hadmIds = Set.of();
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "NoteEventsEntity");

        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> res = mimicEntityManager.mapRootEntityIdsToClinicalText(
                hadmIds,
                foreignKeyPath,
                "hadmId",
                "rowId",
                "text",
                List.of("chartdate", "charttime"),
                null
        );

        Assertions.assertEquals(0, res.size());
    }

    @Test
    void testComputeIdPropertyValuesForForeignPathEndSingleId()
    {
        final Set<Long> hadmIds = Set.of(100001L);
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "NoteEventsEntity");

        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> res = mimicEntityManager.mapRootEntityIdsToClinicalText(
                hadmIds,
                foreignKeyPath,
                "hadmId",
                "rowId",
                "text",
                List.of("chartDate", "chartTime"),
                null
        );

        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.containsKey(100001L));
        Assertions.assertTrue(
                res.get(100001L).stream()
                        .map(MimicEntityManager.ClinicalTextExtractionQueryResult::clinicalTextEntityId)
                        .collect(Collectors.toSet())
                        .containsAll(Set.of(1206584L, 42102L))
        );
    }

    @Test
    void testComputeIdPropertyValuesForForeignPathEndThreeIds()
    {
        final Set<Long> hadmIds = Set.of(100001L, 100003L, 100006L);
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "NoteEventsEntity");

        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> res = mimicEntityManager.mapRootEntityIdsToClinicalText(
                hadmIds,
                foreignKeyPath,
                "hadmId",
                "rowId",
                "text",
                List.of("chartDate", "chartTime"),
                null
        );

        Assertions.assertTrue(res.containsKey(100001L));
        Assertions.assertTrue(res.containsKey(100003L));
        Assertions.assertTrue(res.containsKey(100006L));
        Assertions.assertTrue(res.get(100001L).stream().map(MimicEntityManager.ClinicalTextExtractionQueryResult::clinicalTextEntityId).collect(Collectors.toSet()).containsAll(Set.of(1206584L, 42102L)));
        Assertions.assertTrue(res.get(100003L).stream().map(MimicEntityManager.ClinicalTextExtractionQueryResult::clinicalTextEntityId).collect(Collectors.toSet()).containsAll(Set.of(1072532L, 1072573L, 567564L, 193462L, 567572L, 567662L, 567580L, 567697L, 567608L, 567591L, 567637L, 567670L, 567651L, 76338L, 567809L, 567731L, 567727L, 567732L, 567726L, 567730L, 567771L, 567802L, 567803L, 567773L, 567804L, 19215L)));
        Assertions.assertTrue(res.get(100006L).stream().map(MimicEntityManager.ClinicalTextExtractionQueryResult::clinicalTextEntityId).collect(Collectors.toSet()).containsAll(Set.of(783338L, 1392730L, 148403L, 1392731L, 1392732L, 1392733L, 1392734L, 1392735L, 1392736L, 1392737L, 1392738L, 1392739L, 1392740L, 783579L, 1392741L, 68597L, 1392742L, 1392743L, 783851L, 784026L, 8772L, 55972L)));
    }

    @Test
    void testComputeIdPropertyValuesForForeignPathEndOneElementPath()
    {
        final Set<Long> ids = Set.of(1L);
        final List<String> foreignKeyPath = List.of("NoteEventsEntity");

        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> res = mimicEntityManager.mapRootEntityIdsToClinicalText(
                ids,
                foreignKeyPath,
                "rowId",
                "rowId",
                "text",
                List.of("chartDate", "chartTime"),
                null
        );

        Assertions.assertTrue(res.containsKey(1L));
        Assertions.assertTrue(res.get(1L).stream().map(MimicEntityManager.ClinicalTextExtractionQueryResult::clinicalTextEntityId).collect(Collectors.toSet()).containsAll(Set.of(1L)));
    }

    @Test
    void testComputeIdPropertyValuesForForeignPathEndEmptyForeignKeyPath()
    {
        final Set<Long> hadmIds = Set.of(100001L);
        final List<String> foreignKeyPath = List.of();

        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> res = mimicEntityManager.mapRootEntityIdsToClinicalText(
                hadmIds,
                foreignKeyPath,
                "hadmId",
                "rowId",
                "text",
                List.of("chartdate", "charttime"),
                null
        );

        Assertions.assertEquals(0, res.size());
    }

    @Test
    void testComputeIdPropertyValuesForForeignPathEndEmptyForeignKeyPathEmptyIds()
    {
        final Set<Long> hadmIds = Set.of();
        final List<String> foreignKeyPath = List.of();

        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> res = mimicEntityManager.mapRootEntityIdsToClinicalText(
                hadmIds,
                foreignKeyPath,
                "hadmId",
                "rowId",
                "text",
                List.of("chartdate", "charttime"),
                null
        );

        Assertions.assertEquals(0, res.size());
    }

    @Test
    void testForeignKeyPathToPropertyNamesEmpty()
    {
        final List<String> foreignKeyPath = List.of();

        List<String> res = mimicEntityManager.foreignKeyPathToPropertyNames(foreignKeyPath);

        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void testForeignKeyPathToPropertyNamesWrongEntityName()
    {
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "Wrong", "IcuStaysEntity");

        Assertions.assertThrows(ValidationCoreException.class, () -> mimicEntityManager.foreignKeyPathToPropertyNames(foreignKeyPath));
    }

    @Test
    void testForeignKeyPathToPropertyNamesOneEntity()
    {
        final List<String> foreignKeyPath = List.of("AdmissionsEntity");

        List<String> res = mimicEntityManager.foreignKeyPathToPropertyNames(foreignKeyPath);

        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void testForeignKeyPathToPropertyNamesTwoEntities()
    {
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "NoteEventsEntity");

        List<String> res = mimicEntityManager.foreignKeyPathToPropertyNames(foreignKeyPath);

        Assertions.assertNotNull(res);
        Assertions.assertEquals(List.of("noteEventsEntitys"), res);
    }

    @Test
    void testForeignKeyPathToPropertyNamesThreeEntities()
    {
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "PatientsEntity", "IcuStaysEntity");

        List<String> res = mimicEntityManager.foreignKeyPathToPropertyNames(foreignKeyPath);

        Assertions.assertNotNull(res);
        Assertions.assertEquals(List.of("patientsEntity", "icuStaysEntitys"), res);
    }

    @Test
    void fetchRootEntitiesForForeignKeyPathsEmptyForeignKeyPaths()
    {
        String rootEntityName = "AdmissionsEntity";
        List<List<String>> foreignKeyPaths = List.of(List.of());
        Set<Long> hadmIds = Set.of(100001L);

        Assertions.assertThrows(ValidationCoreException.class, () -> mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(rootEntityName, foreignKeyPaths, "hadmId", hadmIds));
    }

    @Test
    void fetchRootEntitiesForForeignKeyPathsWrongEntityName()
    {
        Set<Long> hadmIds = Set.of(100001L);

        Assertions.assertThrows(ValidationCoreException.class, () -> mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths("Wrong", List.of(List.of("AdmissionsEntity", "NoteEventsEntity")), "hadmId", hadmIds));
        Assertions.assertThrows(ValidationCoreException.class, () -> mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths("AdmissionsEntity", List.of(List.of("AdmissionsEntity", "Wrong")), "hadmId", hadmIds));
        Assertions.assertThrows(ValidationCoreException.class, () -> mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths("AdmissionsEntity", List.of(List.of("AdmissionsEntity", "NoteEventsEntity")), "wrong", hadmIds));
    }

    @Test
    void fetchRootEntitiesForForeignKeyPathsNoForeignKeyPaths()
    {
        String rootEntityName = "AdmissionsEntity";
        List<List<String>> foreignKeyPaths = List.of();
        final Set<Long> hadmIds = Set.of(100001L, 100003L, 100006L);

        Stream<Object[]> res = mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(rootEntityName, foreignKeyPaths, "hadmId", hadmIds);

        Assertions.assertNotNull(res);
        List<Object[]> resList = res.toList();
        Assertions.assertEquals(3, resList.size());
        Assertions.assertTrue(resList.stream().allMatch(r -> r[0] instanceof AdmissionsEntity));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100001L));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100003L));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100006L));
    }

    @Test
    void fetchRootEntitiesForForeignKeyPathsSimple()
    {
        String rootEntityName = "AdmissionsEntity";
        List<List<String>> foreignKeyPaths = List.of(List.of("AdmissionsEntity", "NoteEventsEntity"));
        Set<Long> hadmIds = Set.of(100001L);

        Stream<Object[]> res = mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(rootEntityName, foreignKeyPaths, "hadmId", hadmIds);

        Assertions.assertNotNull(res);
        List<Object[]> resList = res.toList();
        Assertions.assertTrue(resList.stream().allMatch(r -> r[0] instanceof AdmissionsEntity));
        Assertions.assertTrue(resList.stream().allMatch(r -> ((long) r[1]) == 100001L));
    }

    @Test
    void fetchRootEntitiesForForeignKeyPathsThreeIds()
    {
        String rootEntityName = "AdmissionsEntity";
        List<List<String>> foreignKeyPaths = List.of(List.of("AdmissionsEntity", "NoteEventsEntity"));
        final Set<Long> hadmIds = Set.of(100001L, 100003L, 100006L);

        Stream<Object[]> res = mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(rootEntityName, foreignKeyPaths, "hadmId", hadmIds);

        Assertions.assertNotNull(res);
        List<Object[]> resList = res.toList();
        Assertions.assertEquals(3, resList.size());
        Assertions.assertTrue(resList.stream().allMatch(r -> r[0] instanceof AdmissionsEntity));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100001L));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100003L));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100006L));
    }

    @Test
    void fetchRootEntitiesForForeignKeyPathsTwoForeignKeyPaths()
    {
        String rootEntityName = "AdmissionsEntity";
        List<List<String>> foreignKeyPaths = List.of(List.of("AdmissionsEntity", "NoteEventsEntity"), List.of("AdmissionsEntity", "PatientsEntity", "IcuStaysEntity"));
        final Set<Long> hadmIds = Set.of(100001L, 100003L, 100006L);

        Stream<Object[]> res = mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(rootEntityName, foreignKeyPaths, "hadmId", hadmIds);

        Assertions.assertNotNull(res);
        List<Object[]> resList = res.toList();
        Assertions.assertEquals(3, resList.size());
        Assertions.assertTrue(resList.stream().allMatch(r -> r[0] instanceof AdmissionsEntity));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100001L));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100003L));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100006L));
    }

    @Test
    void fetchRootEntitiesForForeignKeyPathsSharedPath()
    {
        String rootEntityName = "AdmissionsEntity";
        List<List<String>> foreignKeyPaths = List.of(List.of("AdmissionsEntity", "NoteEventsEntity"), List.of("AdmissionsEntity", "NoteEventsEntity", "PatientsEntity"));
        final Set<Long> hadmIds = Set.of(100001L, 100003L, 100006L);

        Stream<Object[]> res = mimicEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(rootEntityName, foreignKeyPaths, "hadmId", hadmIds);

        Assertions.assertNotNull(res);
        List<Object[]> resList = res.toList();
        Assertions.assertEquals(3, resList.size());
        Assertions.assertTrue(resList.stream().allMatch(r -> r[0] instanceof AdmissionsEntity));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100001L));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100003L));
        Assertions.assertTrue(resList.stream().anyMatch(r -> (long) r[1] == 100006L));
    }

    @Test
    void testFetchFkPathEndEntitiesAndIdsForForeignKeyPathEmptyForeignKeyPath()
    {
        Assertions.assertThrows(Exception.class, () -> mimicEntityManager.fetchFkPathEndEntitiesAndIdsForForeignKeyPath(
                        List.of(),
                        "test",
                        "test",
                        Set.of(249)
                )
        );
    }

    @Test
    void testFetchFkPathEndEntitiesAndIdsForForeignKeyPathWrongEntityName()
    {
        Assertions.assertThrows(Exception.class, () -> mimicEntityManager.fetchFkPathEndEntitiesAndIdsForForeignKeyPath(
                        List.of("PatientsEntity", "Wrong"),
                        "wrong",
                        "wrong",
                        Set.of(249)
                )
        );
    }

    @Test
    void testFetchFkPathEndEntitiesAndIdsForForeignKeyPathWrongEntityIdPropertyName()
    {
        Assertions.assertThrows(Exception.class, () -> mimicEntityManager.fetchFkPathEndEntitiesAndIdsForForeignKeyPath(
                        List.of("PatientsEntity", "IcuStaysEntity"),
                        "subjectId",
                        "wrong",
                        Set.of(249)
                )
        );

        Assertions.assertThrows(Exception.class, () -> mimicEntityManager.fetchFkPathEndEntitiesAndIdsForForeignKeyPath(
                        List.of("PatientsEntity", "IcuStaysEntity"),
                        "wrong",
                        "icuStayId",
                        Set.of(249)
                )
        );
    }

    @Test
    void testFetchFkPathEndEntitiesAndIdsForForeignKeyPathEmptyIds()
    {
        Stream<Object[]> res = mimicEntityManager.fetchFkPathEndEntitiesAndIdsForForeignKeyPath(
                List.of("PatientsEntity", "IcuStaysEntity"),
                "subjectId",
                "icuStayId",
                Set.of()
        );
        Assertions.assertEquals(0, res.count());
    }

    @Test
    void testFetchFkPathEndEntitiesAndIdsForForeignKeyPathSimple()
    {
        Stream<Object[]> res = mimicEntityManager.fetchFkPathEndEntitiesAndIdsForForeignKeyPath(
                List.of("PatientsEntity", "IcuStaysEntity"),
                "subjectId",
                "icuStayId",
                Set.of(17L, 21L)
        );

        Assertions.assertNotNull(res);
        List<Object[]> resList = res.toList();
        Assertions.assertEquals(4, resList.size());
        Assertions.assertTrue(resList.stream().map(e -> e[1]).anyMatch(e -> e.equals(277042L)));
        Assertions.assertTrue(resList.stream().map(e -> e[1]).anyMatch(e -> e.equals(257980L)));
        Assertions.assertTrue(resList.stream().map(e -> e[1]).anyMatch(e -> e.equals(217847L)));
        Assertions.assertTrue(resList.stream().map(e -> e[1]).anyMatch(e -> e.equals(216859L)));
    }

    @Test
    void testGetNonNullIdsOfEntity()
    {
        List<Object> res = mimicEntityManager.getNonNullIdsOfEntity("AdmissionsEntity", "hadmId");
        List<Long> expectedIds = em.createQuery("SELECT a.hadmId FROM AdmissionsEntity a WHERE a.hadmId IS NOT NULL", Long.class).getResultList();
        Assertions.assertEquals(expectedIds.size(), res.size());
        Assertions.assertTrue(res.containsAll(expectedIds));
    }

    @Test
    void getResultListForExtractPatientDiedDuringAdmissionTarget()
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

        List<Object[]> resIdsAndTarget = mimicEntityManager.getResultListForExtractPatientDiedDuringAdmissionTarget(ids, null);

        for (Object[] res : resIdsAndTarget)
        {
            short expectedVal = em.createQuery("SELECT a.hospitalExpireFlag FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", Short.class)
                    .setParameter("hadmId", res[0])
                    .getSingleResult();

            Assertions.assertEquals(expectedVal, res[1]);
        }
    }

    @Test
    void getResultListForExtractReadmissionTargetEmptyIds()
    {
        List<PatientsEntity> res = mimicEntityManager.fetchPatientForTargetExtractionHospitalReadmission(List.of());

        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void getResultListForExtractReadmissionTarget()
    {
        List<Long> ids = List.of(
                249L,
                250L,
                251L,
                252L,
                253L,
                255L,
                256L,
                257L,
                258L,
                260L,
                261L,
                262L,
                263L,
                264L,
                265L,
                266L,
                267L,
                268L,
                269L,
                270L,
                663L,
                664L,
                665L,
                666L,
                667L,
                668L
        );

        List<PatientsEntity> res = mimicEntityManager.fetchPatientForTargetExtractionHospitalReadmission(ids);

        Assertions.assertEquals(ids.size(), res.size());
        Assertions.assertTrue(ids.containsAll(res.stream().map(PatientsEntity::getSubjectId).toList()));
    }
}
