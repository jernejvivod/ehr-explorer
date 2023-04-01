package si.jernej.mexplorer.core.test.processing.transform;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.common.exception.ValidationCoreException;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.test.ACoreTest;
import si.jernej.mexplorer.entity.AdmissionsEntity;

class CompositeColumnCreatorTest extends ACoreTest
{
    @Test
    void testWrongEntityNameOnForeignKeyPath()
    {
        final String compositeName = "ageAtAdmission";

        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity"),
                "admitTime",
                List.of("AdmissionsEntity", "Wrong"),
                "dob",
                compositeName,
                (dateAdmission, dateBirth) -> ChronoUnit.YEARS.between((LocalDateTime) dateBirth, (LocalDateTime) dateAdmission)
        );

        AdmissionsEntity rootAdmissionsEntity = em.createQuery("SELECT a FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", AdmissionsEntity.class)
                .setParameter("hadmId", 100001L)
                .getResultStream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Non-existent hadmId value specified"));

        List<AdmissionsEntity> rootEntities = List.of(rootAdmissionsEntity);
        Assertions.assertThrows(ValidationCoreException.class, () -> compositeColumnCreator.processEntries(rootEntities));
    }

    @Test
    void testWrongPropertyName()
    {
        final String compositeName = "ageAtAdmission";

        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity"),
                "admitTime",
                List.of("AdmissionsEntity", "PatientsEntity"),
                "wrong",
                compositeName,
                (dateAdmission, dateBirth) -> ChronoUnit.YEARS.between((LocalDateTime) dateBirth, (LocalDateTime) dateAdmission)
        );

        AdmissionsEntity rootAdmissionsEntity = em.createQuery("SELECT a FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", AdmissionsEntity.class)
                .setParameter("hadmId", 100001L)
                .getResultStream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Non-existent hadmId value specified"));

        List<AdmissionsEntity> rootEntities = List.of(rootAdmissionsEntity);
        Assertions.assertThrows(ValidationCoreException.class, () -> compositeColumnCreator.processEntries(rootEntities));
    }

    @Test
    void testBasicSingleEntity()
    {
        final String compositeName = "ageAtAdmission";

        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity"),
                "admitTime",
                List.of("AdmissionsEntity", "PatientsEntity"),
                "dob",
                compositeName,
                (dateAdmission, dateBirth) -> ChronoUnit.YEARS.between((LocalDateTime) dateBirth, (LocalDateTime) dateAdmission)
        );

        AdmissionsEntity rootAdmissionsEntity = em.createQuery("SELECT a FROM AdmissionsEntity a WHERE a.hadmId=:hadmId", AdmissionsEntity.class)
                .setParameter("hadmId", 100001L)
                .getResultStream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Non-existent hadmId value specified"));

        Map<String, List<Object>> compositeColumns = compositeColumnCreator.processEntries(List.of(rootAdmissionsEntity));

        Assertions.assertNotNull(compositeColumns);
        Assertions.assertFalse(compositeColumns.isEmpty());
        Assertions.assertEquals(1, compositeColumns.size());
        Assertions.assertTrue(compositeColumns.containsKey(compositeName));
        Assertions.assertEquals(1, compositeColumns.get(compositeName).size());
        Assertions.assertEquals(35L, compositeColumns.get(compositeName).get(0));
    }

    @Test
    void testBasicMultipleEntities()
    {
        final String compositeName = "ageAtAdmission";

        final Map<Long, Long> hadmIdToExpectedAge = Map.ofEntries(
                Map.entry(100001L, 35L),
                Map.entry(100003L, 59L),
                Map.entry(100006L, 48L),
                Map.entry(100007L, 73L)
        );

        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity"),
                "admitTime",
                List.of("AdmissionsEntity", "PatientsEntity"),
                "dob",
                compositeName,
                (dateAdmission, dateBirth) -> ChronoUnit.YEARS.between((LocalDateTime) dateBirth, (LocalDateTime) dateAdmission)
        );

        List<AdmissionsEntity> rootAdmissionsEntitys = em.createQuery("SELECT a FROM AdmissionsEntity a WHERE a.hadmId IN (:hadmIds)", AdmissionsEntity.class)
                .setParameter("hadmIds", List.of(100001L, 100003L, 100006L, 100007L))
                .getResultList();

        List<Long> hadmIds = rootAdmissionsEntitys.stream().map(AdmissionsEntity::getHadmId).toList();

        Map<String, List<Object>> compositeColumns = compositeColumnCreator.processEntries(rootAdmissionsEntitys);

        Assertions.assertNotNull(compositeColumns);
        Assertions.assertFalse(compositeColumns.isEmpty());
        Assertions.assertEquals(1, compositeColumns.size());
        Assertions.assertTrue(compositeColumns.containsKey(compositeName));
        Assertions.assertEquals(4, compositeColumns.get(compositeName).size());

        for (int i = 0; i < compositeColumns.get(compositeName).size(); i++)
        {
            Long hadmId = hadmIds.get(i);
            Assertions.assertEquals(hadmIdToExpectedAge.get(hadmId), compositeColumns.get(compositeName).get(i));
        }
    }

    @Test
    void testCompositeColumnCreatorValidationValid()
    {
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity"),
                "admitTime",
                List.of("AdmissionsEntity", "PatientsEntity"),
                "dob",
                "ageAtAdmission",
                (dateAdmission, dateBirth) -> ChronoUnit.YEARS.between((LocalDateTime) dateBirth, (LocalDateTime) dateAdmission)
        );
        Assertions.assertDoesNotThrow(() -> compositeColumnCreator.assertValid(em.getMetamodel()));
    }

    @Test
    void testCompositeColumnCreatorValidationEmpty()
    {
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        Assertions.assertDoesNotThrow(() -> compositeColumnCreator.assertValid(em.getMetamodel()));
    }

    @Test
    void testCompositeColumnCreatorValidationWrongEntity()
    {
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity"),
                "admitTime",
                List.of("Wrong", "PatientsEntity"),
                "dob",
                "ageAtAdmission",
                (dateAdmission, dateBirth) -> ChronoUnit.YEARS.between((LocalDateTime) dateBirth, (LocalDateTime) dateAdmission)
        );
        Assertions.assertThrows(ValidationCoreException.class, () -> compositeColumnCreator.assertValid(em.getMetamodel()));
    }

    @Test
    void testCompositeColumnCreatorValidationWrongProperty()
    {
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity"),
                "admitTime",
                List.of("AdmissionsEntity", "PatientsEntity"),
                "wrong",
                "ageAtAdmission",
                (dateAdmission, dateBirth) -> ChronoUnit.YEARS.between((LocalDateTime) dateBirth, (LocalDateTime) dateAdmission)
        );
        Assertions.assertThrows(ValidationCoreException.class, () -> compositeColumnCreator.assertValid(em.getMetamodel()));
    }

}
