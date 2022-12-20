package si.jernej.mexplorer.core.test.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.util.EntityUtils;
import si.jernej.mexplorer.entity.AdmissionsEntity;
import si.jernej.mexplorer.entity.PatientsEntity;
import si.jernej.mexplorer.test.ATestBase;

public class EntityUtilsTest extends ATestBase
{
    private final Map<String, Set<String>> entityToLinkedEntities = Map.ofEntries(
            Map.entry("A", Set.of("B", "C", "D")),
            Map.entry("B", Set.of("A")),
            Map.entry("C", Set.of("A", "E")),
            Map.entry("D", Set.of("A", "F", "G")),
            Map.entry("E", Set.of("C")),
            Map.entry("F", Set.of("D", "I")),
            Map.entry("G", Set.of("D", "H")),
            Map.entry("H", Set.of("G", "I")),
            Map.entry("I", Set.of("F", "H")),
            Map.entry("J", Set.of("K"))
    );

    @Test
    void propertyNameToEntityName()
    {
        Assertions.assertEquals("AdmissionsEntity", EntityUtils.propertyNameToEntityName("admissionsEntity"));
        Assertions.assertEquals("AdmissionsEntity", EntityUtils.propertyNameToEntityName("admissionsEntitys"));
    }

    @Test
    void entityNameToPropertyName()
    {
        Assertions.assertEquals("admissionsEntity", EntityUtils.entityNameToPropertyName("AdmissionsEntity", true));
        Assertions.assertEquals("admissionsEntitys", EntityUtils.entityNameToPropertyName("AdmissionsEntity", false));
    }

    @Test
    void computeForeignKeyPath()
    {
        List<String> fkp1 = EntityUtils.computeForeignKeyPath("A", "A", entityToLinkedEntities);
        List<String> fkp2 = EntityUtils.computeForeignKeyPath("A", "B", entityToLinkedEntities);
        List<String> fkp3 = EntityUtils.computeForeignKeyPath("A", "E", entityToLinkedEntities);
        List<String> fkp4 = EntityUtils.computeForeignKeyPath("A", "I", entityToLinkedEntities);

        Assertions.assertEquals(fkp1, List.of("A"));
        Assertions.assertEquals(fkp2, List.of("A", "B"));
        Assertions.assertEquals(fkp3, List.of("A", "C", "E"));
        Assertions.assertEquals(fkp4, List.of("A", "D", "F", "I"));

    }

    @Test
    void computeForeignKeyPathInvalidEntityNames()
    {
        Assertions.assertThrows(ValidationCoreException.class, () -> EntityUtils.computeForeignKeyPath("A", "NONEXISTENT", entityToLinkedEntities));
        Assertions.assertThrows(ValidationCoreException.class, () -> EntityUtils.computeForeignKeyPath("NONEXISTENT", "NONEXISTENT", entityToLinkedEntities));
    }

    @Test
    void computeForeignKeyPathNonexistentPath()
    {
        Assertions.assertThrows(ValidationCoreException.class, () -> EntityUtils.computeForeignKeyPath("A", "J", entityToLinkedEntities));
    }

    @Test
    void computeEntityToLinkedEntitiesMap()
    {
        Map<String, Set<String>> entityToLinkedEntities = EntityUtils.computeEntityToLinkedEntitiesMap(em.getMetamodel());
        Assertions.assertTrue(entityToLinkedEntities.get("PatientsEntity").contains("AdmissionsEntity"));
        Assertions.assertTrue(entityToLinkedEntities.get("AdmissionsEntity").contains("PatientsEntity"));
        Assertions.assertTrue(entityToLinkedEntities.get("AdmissionsEntity").contains("NoteEventsEntity"));
        Assertions.assertTrue(entityToLinkedEntities.get("NoteEventsEntity").contains("AdmissionsEntity"));
    }

    @Test
    void traverseForeignKeyPathAdmissionsEntityToNoteEventsEntity()
    {
        long hadmId = 100006;
        Object testAdmission = em.createQuery("SELECT a FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", AdmissionsEntity.class)
                .setParameter("hadmId", hadmId)
                .getSingleResult();
        long expectedCount = em.createQuery("SELECT COUNT(n) FROM AdmissionsEntity a LEFT JOIN a.noteEventsEntitys n WHERE a.hadmId=:hadmId", Long.class)
                .setParameter("hadmId", hadmId)
                .getSingleResult();
        Set<Object> foreignKeyPathEndEntities = EntityUtils.traverseForeignKeyPath(testAdmission, List.of("AdmissionsEntity", "NoteEventsEntity"));
        Assertions.assertEquals(expectedCount, foreignKeyPathEndEntities.size());
    }

    @Test
    void computeIdPropertyValuesForForeignPathEnd()
    {
        long hadmId = 100006;
        Object testAdmission = em.createQuery("SELECT a FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", AdmissionsEntity.class)
                .setParameter("hadmId", hadmId)
                .getSingleResult();

        Set<Long> expectedIds = em.createQuery("SELECT n.rowId FROM AdmissionsEntity a LEFT JOIN a.noteEventsEntitys n WHERE a.hadmId=:hadmId", Long.class)
                .setParameter("hadmId", hadmId)
                .getResultStream().collect(Collectors.toSet());

        Set<Object> ids = EntityUtils.computeIdPropertyValuesForForeignPathEnd(testAdmission, List.of("AdmissionsEntity", "NoteEventsEntity"));

        Assertions.assertEquals(expectedIds.size(), ids.size());
        Assertions.assertEquals(expectedIds, ids);
    }

    @Test
    void testTraverseSingularForeignKeyPath()
    {
        long hadmId = 100006;
        Object testAdmission = em.createQuery("SELECT a FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", AdmissionsEntity.class)
                .setParameter("hadmId", hadmId)
                .getSingleResult();
        Object res = EntityUtils.traverseSingularForeignKeyPath(testAdmission, List.of("AdmissionsEntity", "PatientsEntity"));
        Assertions.assertInstanceOf(PatientsEntity.class, res);
        Assertions.assertEquals(9895L, ((PatientsEntity) res).getSubjectId());
    }

    @Test
    void testTraverseSingularForeignKeyPathMultiple()
    {
        List<Long> hadmIds = List.of(100001L, 100003L, 100006L, 100007L);

        Map<Long, Long> hadmIdToExpectedLinkedSubjectId = Map.ofEntries(
                Map.entry(100001L, 58526L),
                Map.entry(100003L, 54610L),
                Map.entry(100006L, 9895L),
                Map.entry(100007L, 23018L)
        );

        List<AdmissionsEntity> admissionEntitys = em.createQuery("SELECT a FROM AdmissionsEntity a WHERE a.hadmId IN (:hadmIds)", AdmissionsEntity.class)
                .setParameter("hadmIds", hadmIds)
                .getResultList();

        List<Long> hadmIdsExtracted = admissionEntitys.stream().map(AdmissionsEntity::getHadmId).toList();

        List<Object> res = EntityUtils.traverseSingularForeignKeyPath(admissionEntitys, List.of("AdmissionsEntity", "PatientsEntity"));

        for (int i = 0; i < hadmIdsExtracted.size(); i++)
        {
            Assertions.assertEquals(hadmIdToExpectedLinkedSubjectId.get(hadmIdsExtracted.get(i)), ((PatientsEntity) (res.get(i))).getSubjectId());
        }
    }

    @Test
    void testGetForeignKeyPathsFromPropertySpecEmptyPropertySpec()
    {
        String rootEntityName = "AdmissionsEntity";
        PropertySpec propertySpec = new PropertySpec();

        List<List<String>> res = EntityUtils.getForeignKeyPathsFromPropertySpec(rootEntityName, propertySpec, em.getMetamodel());

        Assertions.assertNotNull(res);
        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.contains(List.of("AdmissionsEntity")));
    }

    @Test
    void testGetForeignKeyPathsFromPropertySpecOnlyRootEntity()
    {
        String rootEntityName = "AdmissionsEntity";

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "insurance");
        propertySpec.addEntry("AdmissionsEntity", "language");
        propertySpec.addEntry("AdmissionsEntity", "religion");

        List<List<String>> res = EntityUtils.getForeignKeyPathsFromPropertySpec(rootEntityName, propertySpec, em.getMetamodel());

        Assertions.assertNotNull(res);
        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.contains(List.of("AdmissionsEntity")));
    }

    @Test
    void testGetForeignKeyPathsFromPropertySpecSimple()
    {
        String rootEntityName = "AdmissionsEntity";

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "insurance");
        propertySpec.addEntry("AdmissionsEntity", "language");
        propertySpec.addEntry("AdmissionsEntity", "religion");
        propertySpec.addEntry("AdmissionsEntity", "patientsEntity");

        propertySpec.addEntry("PatientsEntity", "gender");
        propertySpec.addEntry("PatientsEntity", "expireFlag");

        List<List<String>> res = EntityUtils.getForeignKeyPathsFromPropertySpec(rootEntityName, propertySpec, em.getMetamodel());

        Assertions.assertNotNull(res);
        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.contains(List.of("AdmissionsEntity", "PatientsEntity")));
    }

    @Test
    void testGetForeignKeyPathsFromPropertySpecTwoPaths()
    {
        String rootEntityName = "AdmissionsEntity";

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "insurance");
        propertySpec.addEntry("AdmissionsEntity", "language");
        propertySpec.addEntry("AdmissionsEntity", "religion");
        propertySpec.addEntry("AdmissionsEntity", "patientsEntity");
        propertySpec.addEntry("AdmissionsEntity", "icuStaysEntitys");

        propertySpec.addEntry("PatientsEntity", "gender");
        propertySpec.addEntry("PatientsEntity", "expireFlag");

        propertySpec.addEntry("IcuStaysEntity", "dbSource");

        List<List<String>> res = EntityUtils.getForeignKeyPathsFromPropertySpec(rootEntityName, propertySpec, em.getMetamodel());

        Assertions.assertNotNull(res);
        Assertions.assertEquals(2, res.size());
        Assertions.assertTrue(res.contains(List.of("AdmissionsEntity", "PatientsEntity")));
        Assertions.assertTrue(res.contains(List.of("AdmissionsEntity", "IcuStaysEntity")));
    }

    @Test
    void testGetForeignKeyPathsFromPropertySpecTwoPathsSameEndEntity()
    {
        String rootEntityName = "AdmissionsEntity";

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "insurance");
        propertySpec.addEntry("AdmissionsEntity", "language");
        propertySpec.addEntry("AdmissionsEntity", "religion");
        propertySpec.addEntry("AdmissionsEntity", "datetimeEventsEntitys");
        propertySpec.addEntry("AdmissionsEntity", "icuStaysEntitys");

        propertySpec.addEntry("DatetimeEventsEntity", "error");
        propertySpec.addEntry("DatetimeEventsEntity", "dItemsEntity");

        propertySpec.addEntry("DItemsEntity", "label");

        propertySpec.addEntry("IcuStaysEntity", "dbSource");
        propertySpec.addEntry("IcuStaysEntity", "procedureEventsMvEntitys");

        propertySpec.addEntry("ProcedureEventsMvEntity", "isOpenBag");
        propertySpec.addEntry("ProcedureEventsMvEntity", "dItemsEntity");

        List<List<String>> res = EntityUtils.getForeignKeyPathsFromPropertySpec(rootEntityName, propertySpec, em.getMetamodel());

        Assertions.assertNotNull(res);
        Assertions.assertEquals(2, res.size());
        Assertions.assertTrue(res.contains(List.of("AdmissionsEntity", "IcuStaysEntity", "ProcedureEventsMvEntity", "DItemsEntity")));
        Assertions.assertTrue(res.contains(List.of("AdmissionsEntity", "DatetimeEventsEntity", "DItemsEntity")));
    }

    @Test
    void testGetTransitionPairsFromForeignKeyPath()
    {
        Set<Pair<String, String>> res = EntityUtils.getTransitionPairsFromForeignKeyPath(
                List.of(
                        List.of("A", "B", "C"),
                        List.of("A", "E", "F"),
                        List.of("A", "B", "C", "D", "G")
                )
        );
        Assertions.assertEquals(
                Set.of(
                        Pair.of("A", "B"),
                        Pair.of("B", "C"),
                        Pair.of("A", "E"),
                        Pair.of("E", "F"),
                        Pair.of("C", "D"),
                        Pair.of("D", "G")
                ),
                res
        );
    }
}
