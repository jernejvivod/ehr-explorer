package si.jernej.mexplorer.core.test.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.util.EntityUtils;
import si.jernej.mexplorer.entity.AdmissionsEntity;
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
        Assertions.assertThrows(IllegalArgumentException.class, () -> EntityUtils.computeForeignKeyPath("A", "NONEXISTENT", entityToLinkedEntities));
        Assertions.assertThrows(IllegalArgumentException.class, () -> EntityUtils.computeForeignKeyPath("NONEXISTENT", "NONEXISTENT", entityToLinkedEntities));
    }

    @Test
    void computeForeignKeyPathNonexistentPath()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EntityUtils.computeForeignKeyPath("A", "J", entityToLinkedEntities));
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
}
