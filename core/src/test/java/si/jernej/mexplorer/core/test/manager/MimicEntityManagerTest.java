package si.jernej.mexplorer.core.test.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.test.ATestBase;

public class MimicEntityManagerTest extends ATestBase
{
    @Override
    protected Weld loadWeld(Weld weld)
    {
        return weld.addPackages(
                true,
                getClass(),
                MimicEntityManager.class
        );
    }

    @Inject
    private MimicEntityManager mimicEntityManager;

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
                List.of("chartdate", "charttime")
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
    void testComputeIdPropertyValuesForForeignPathEndTwoIds()
    {
        final Set<Long> hadmIds = Set.of(100001L, 100003L, 100006L);
        final List<String> foreignKeyPath = List.of("AdmissionsEntity", "NoteEventsEntity");

        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> res = mimicEntityManager.mapRootEntityIdsToClinicalText(
                hadmIds,
                foreignKeyPath,
                "hadmId",
                "rowId",
                "text",
                List.of("chartdate", "charttime")
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
        final Set<Long> hadmIds = Set.of(1L);
        final List<String> foreignKeyPath = List.of("NoteEventsEntity");

        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> res = mimicEntityManager.mapRootEntityIdsToClinicalText(
                hadmIds,
                foreignKeyPath,
                "rowId",
                "rowId",
                "text",
                List.of("chartdate", "charttime")
        );

        // Set<Pair<Long, Set<Long>>> expectedResult = Set.of(Pair.of(100001L, Set.of(42102L, 1206584L)));

        Assertions.assertTrue(res.containsKey(1L));
        Assertions.assertTrue(res.get(1L).stream().map(MimicEntityManager.ClinicalTextExtractionQueryResult::clinicalTextEntityId).collect(Collectors.toSet()).containsAll(Set.of(1L)));
    }
}
