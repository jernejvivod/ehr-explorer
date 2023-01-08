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
                List.of("chartdate", "charttime")
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
                List.of("chartDate", "chartTime")
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
                List.of("chartDate", "chartTime")
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
                List.of("chartDate", "chartTime")
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
                List.of("chartdate", "charttime")
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
                List.of("chartdate", "charttime")
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
}
