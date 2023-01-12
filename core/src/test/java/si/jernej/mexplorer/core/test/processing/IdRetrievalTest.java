package si.jernej.mexplorer.core.test.processing;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.processing.IdRetrieval;
import si.jernej.mexplorer.core.test.ACoreTest;
import si.jernej.mexplorer.processorapi.v1.model.ForeignKeyPathIdRetrievalSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.IdRetrievalFilterSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.IdRetrievalSpecDto;

class IdRetrievalTest extends ACoreTest
{
    @Inject
    private IdRetrieval idRetrieval;

    @ParameterizedTest
    @CsvSource({
            "Wrong, hadmId",
            "AdmissionsEntity, wrong",
            "Wrong, wrong"
    })
    void retrieveIdsWrongEntityNamePropertyNamePair(String entityName, String propertyName)
    {
        IdRetrievalSpecDto idRetrievalSpecDto = new IdRetrievalSpecDto();
        idRetrievalSpecDto.setEntityName(entityName);
        idRetrievalSpecDto.setIdProperty(propertyName);
        Assertions.assertThrows(ValidationCoreException.class, () -> idRetrieval.retrieveIds(idRetrievalSpecDto));
    }

    @Test
    void retrieveIdsBasicWithoutFilter()
    {
        IdRetrievalSpecDto idRetrievalSpecDto = new IdRetrievalSpecDto();
        idRetrievalSpecDto.setEntityName("AdmissionsEntity");
        idRetrievalSpecDto.setIdProperty("hadmId");
        Set<Object> res = idRetrieval.retrieveIds(idRetrievalSpecDto);

        long countExpected = em.createQuery("SELECT COUNT(a) FROM AdmissionsEntity a", Long.class).getSingleResult();

        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
        Assertions.assertEquals(countExpected, res.size());
    }

    @Test
    void retrieveIdsBasicWithFilterWithWrongPropertyName()
    {
        IdRetrievalSpecDto idRetrievalSpecDto = new IdRetrievalSpecDto();
        idRetrievalSpecDto.setEntityName("AdmissionsEntity");
        idRetrievalSpecDto.setIdProperty("hadmId");

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto.setForeignKeyPath(List.of("AdmissionsEntity"));
        idRetrievalFilterSpecDto.setPropertyName("wrong");
        idRetrievalFilterSpecDto.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto.setPropertyVal("EMERGENCY");
        idRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto));

        Assertions.assertThrows(ValidationCoreException.class, () -> idRetrieval.retrieveIds(idRetrievalSpecDto));
    }

    @ParameterizedTest
    @CsvSource({
            "LESS, 1",
            "MORE, 0"
    })
    void retrieveIdsBasicWithSingleLessOrMoreFilter(IdRetrievalFilterSpecDto.ComparatorEnum comparatorEnum, short hospitalExpireFlag)
    {
        IdRetrievalSpecDto idRetrievalSpecDto = new IdRetrievalSpecDto();
        idRetrievalSpecDto.setEntityName("AdmissionsEntity");
        idRetrievalSpecDto.setIdProperty("hadmId");

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto.setForeignKeyPath(List.of("AdmissionsEntity"));
        idRetrievalFilterSpecDto.setPropertyName("hospitalExpireFlag");
        idRetrievalFilterSpecDto.setComparator(comparatorEnum);
        idRetrievalFilterSpecDto.setPropertyVal(hospitalExpireFlag);
        idRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto));

        long countExpected = em.createQuery("SELECT COUNT(a) FROM AdmissionsEntity a WHERE a.hospitalExpireFlag=:hospitalExpireFlag", Long.class).setParameter("hospitalExpireFlag", hospitalExpireFlag == (short) 1 ? (short) 0 : (short) 1).getSingleResult();

        Set<Object> res = idRetrieval.retrieveIds(idRetrievalSpecDto);
        Assertions.assertEquals(countExpected, res.size());
    }

    @Test
    void retrieveIdsBasicWithSingleEqualFilter()
    {
        IdRetrievalSpecDto idRetrievalSpecDto = new IdRetrievalSpecDto();
        idRetrievalSpecDto.setEntityName("AdmissionsEntity");
        idRetrievalSpecDto.setIdProperty("hadmId");

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto.setForeignKeyPath(List.of("AdmissionsEntity"));
        idRetrievalFilterSpecDto.setPropertyName("admissionType");
        idRetrievalFilterSpecDto.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto.setPropertyVal("EMERGENCY");
        idRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto));

        long countExpected = em.createQuery("SELECT COUNT(a) FROM AdmissionsEntity a WHERE a.admissionType = 'EMERGENCY'", Long.class).getSingleResult();

        Set<Object> res = idRetrieval.retrieveIds(idRetrievalSpecDto);
        Assertions.assertEquals(countExpected, res.size());
    }

    @Test
    void retrieveIdsBasicWithTwoFilters()
    {
        IdRetrievalSpecDto idRetrievalSpecDto = new IdRetrievalSpecDto();
        idRetrievalSpecDto.setEntityName("AdmissionsEntity");
        idRetrievalSpecDto.setIdProperty("hadmId");

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto1 = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto1.setForeignKeyPath(List.of("AdmissionsEntity"));
        idRetrievalFilterSpecDto1.setPropertyName("hospitalExpireFlag");
        idRetrievalFilterSpecDto1.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.MORE);
        idRetrievalFilterSpecDto1.setPropertyVal((short) 0);

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto2 = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto2.setForeignKeyPath(List.of("AdmissionsEntity"));
        idRetrievalFilterSpecDto2.setPropertyName("admissionType");
        idRetrievalFilterSpecDto2.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto2.setPropertyVal("EMERGENCY");

        idRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto1, idRetrievalFilterSpecDto2));

        long countExpected = em.createQuery("SELECT COUNT(a) FROM AdmissionsEntity a WHERE a.admissionType = 'EMERGENCY' AND a.hospitalExpireFlag=1", Long.class).getSingleResult();

        Set<Object> res = idRetrieval.retrieveIds(idRetrievalSpecDto);
        Assertions.assertEquals(countExpected, res.size());
    }

    @Test
    void retrieveIdsFilterBySameAndLinkedEntityTwoFilters()
    {
        IdRetrievalSpecDto idRetrievalSpecDto = new IdRetrievalSpecDto();
        idRetrievalSpecDto.setEntityName("AdmissionsEntity");
        idRetrievalSpecDto.setIdProperty("hadmId");

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto1 = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto1.setForeignKeyPath(List.of("AdmissionsEntity", "PatientsEntity"));
        idRetrievalFilterSpecDto1.setPropertyName("gender");
        idRetrievalFilterSpecDto1.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto1.setPropertyVal("M");

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto2 = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto2.setForeignKeyPath(List.of("AdmissionsEntity"));
        idRetrievalFilterSpecDto2.setPropertyName("admissionType");
        idRetrievalFilterSpecDto2.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto2.setPropertyVal("EMERGENCY");

        idRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto1, idRetrievalFilterSpecDto2));

        long countExpected = em.createQuery("SELECT COUNT(a) FROM AdmissionsEntity a WHERE a.admissionType = 'EMERGENCY' AND a.patientsEntity.gender='M'", Long.class).getSingleResult();

        Set<Object> res = idRetrieval.retrieveIds(idRetrievalSpecDto);
        Assertions.assertEquals(countExpected, res.size());
    }

    @Test
    void retrieveIdsUsingForeignKeyPathEmptyForeignKeyPath()
    {
        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of());
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("test");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("test");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(List.of(17L, 21L));
        Assertions.assertThrows(ValidationCoreException.class, () -> idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto));
    }

    @Test
    void retrieveIdsUsingForeignKeyPathWrongEntityName()
    {
        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "Wrong"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("wrong");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("wrong");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(List.of(17L, 21L));
        Assertions.assertThrows(ValidationCoreException.class, () -> idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto));
    }

    @Test
    void retrieveIdsUsingForeignKeyPathWrongEntityPropertyName()
    {
        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "IcuStaysEntity"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("subjectId");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("wrong");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(List.of(17L, 21L));
        Assertions.assertThrows(ValidationCoreException.class, () -> idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto));

        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("wrong");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("icuStayId");
        Assertions.assertThrows(ValidationCoreException.class, () -> idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto));
    }

    @Test
    void retrieveIdsUsingForeignKeyPathEmptyIds()
    {
        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "IcuStaysEntity"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("subjectId");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("icuStayId");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(List.of());

        Set<Object> res = idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto);
        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void retrieveIdsUsingForeignKeyPathBasic()
    {
        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "IcuStaysEntity"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("subjectId");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("icuStayId");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(List.of(17L, 21L));

        Set<Object> res = idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto);
        Assertions.assertNotNull(res);

        Assertions.assertTrue(res.stream().anyMatch(e -> e.equals(277042L)));
        Assertions.assertTrue(res.stream().anyMatch(e -> e.equals(257980L)));
        Assertions.assertTrue(res.stream().anyMatch(e -> e.equals(217847L)));
        Assertions.assertTrue(res.stream().anyMatch(e -> e.equals(216859L)));
    }

    @Test
    void retrieveIdsUsingForeignKeyPathBasicWithFilterWithWrongPropertyName()
    {
        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "IcuStaysEntity"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("subjectId");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("icuStayId");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(List.of(17L, 21L));

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto.setForeignKeyPath(List.of("IcuStaysEntity"));
        idRetrievalFilterSpecDto.setPropertyName("wrong");
        idRetrievalFilterSpecDto.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto.setPropertyVal("test");
        foreignKeyPathIdRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto));

        Assertions.assertThrows(ValidationCoreException.class, () -> idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto));
    }

    @ParameterizedTest
    @CsvSource({
            "LESS, 6.0",
            "MORE, 6.0"
    })
    void retrieveIdsUsingForeignKeyPathBasicWithSingleLessOrMoreFilter(IdRetrievalFilterSpecDto.ComparatorEnum comparatorEnum, double los)
    {
        List<Long> ids = List.of(17L, 21L);

        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "IcuStaysEntity"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("subjectId");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("icuStayId");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(ids);

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto.setForeignKeyPath(List.of("IcuStaysEntity"));
        idRetrievalFilterSpecDto.setPropertyName("los");
        idRetrievalFilterSpecDto.setComparator(comparatorEnum);
        idRetrievalFilterSpecDto.setPropertyVal(los);
        foreignKeyPathIdRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto));

        final String query = "SELECT COUNT(i) FROM IcuStaysEntity i WHERE i.los " + (comparatorEnum == IdRetrievalFilterSpecDto.ComparatorEnum.LESS ? "<" : ">") + " :los" + " AND i.patientsEntity.subjectId IN (:ids)";
        long countExpected = em.createQuery(query, Long.class)
                .setParameter("los", los)
                .setParameter("ids", ids)
                .getSingleResult();

        Set<Object> res = idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto);
        Assertions.assertEquals(countExpected, res.size());
    }

    @Test
    void retrieveIdsUsingForeignKeyPathBasicWithSingleEqualFilter()
    {
        List<Long> ids = List.of(17L, 21L);

        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "IcuStaysEntity"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("subjectId");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("icuStayId");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(ids);

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto.setForeignKeyPath(List.of("IcuStaysEntity"));
        idRetrievalFilterSpecDto.setPropertyName("firstCareUnit");
        idRetrievalFilterSpecDto.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto.setPropertyVal("CCU");
        foreignKeyPathIdRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto));

        final String query = "SELECT COUNT(i) FROM IcuStaysEntity i WHERE i.firstCareUnit = 'CCU' AND i.patientsEntity.subjectId IN (:ids)";
        long countExpected = em.createQuery(query, Long.class)
                .setParameter("ids", ids)
                .getSingleResult();

        Set<Object> res = idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto);
        Assertions.assertEquals(countExpected, res.size());
    }

    @Test
    void retrieveIdsUsingForeignKeyPathBasicWithTwoFilters()
    {
        List<Long> ids = List.of(17L, 21L);

        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "IcuStaysEntity"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("subjectId");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("icuStayId");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(ids);

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto1 = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto1.setForeignKeyPath(List.of("IcuStaysEntity"));
        idRetrievalFilterSpecDto1.setPropertyName("firstCareUnit");
        idRetrievalFilterSpecDto1.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto1.setPropertyVal("CCU");

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto2 = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto2.setForeignKeyPath(List.of("IcuStaysEntity"));
        idRetrievalFilterSpecDto2.setPropertyName("firstCareUnit");
        idRetrievalFilterSpecDto2.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto2.setPropertyVal("CCU");

        foreignKeyPathIdRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto1, idRetrievalFilterSpecDto2));

        final String query = """
                SELECT COUNT(i) FROM IcuStaysEntity i 
                WHERE i.firstCareUnit = 'CCU' AND i.los > 4.0 AND i.patientsEntity.subjectId IN (:ids)
                """;
        long countExpected = em.createQuery(query, Long.class)
                .setParameter("ids", ids)
                .getSingleResult();

        Set<Object> res = idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto);
        Assertions.assertEquals(countExpected, res.size());
    }

    @Test
    void retrieveIdsUsingForeignKeyPathFilterBySameAndLinkedEntityTwoFilters()
    {
        List<Long> ids = List.of(17L, 21L);

        ForeignKeyPathIdRetrievalSpecDto foreignKeyPathIdRetrievalSpecDto = new ForeignKeyPathIdRetrievalSpecDto();
        foreignKeyPathIdRetrievalSpecDto.setForeignKeyPath(List.of("PatientsEntity", "IcuStaysEntity"));
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIdProperty("subjectId");
        foreignKeyPathIdRetrievalSpecDto.setEndEntityIdProperty("icuStayId");
        foreignKeyPathIdRetrievalSpecDto.setRootEntityIds(ids);

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto1 = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto1.setForeignKeyPath(List.of("IcuStaysEntity", "PatientsEntity"));
        idRetrievalFilterSpecDto1.setPropertyName("gender");
        idRetrievalFilterSpecDto1.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto1.setPropertyVal("M");

        IdRetrievalFilterSpecDto idRetrievalFilterSpecDto2 = new IdRetrievalFilterSpecDto();
        idRetrievalFilterSpecDto2.setForeignKeyPath(List.of("IcuStaysEntity"));
        idRetrievalFilterSpecDto2.setPropertyName("firstCareUnit");
        idRetrievalFilterSpecDto2.setComparator(IdRetrievalFilterSpecDto.ComparatorEnum.EQUAL);
        idRetrievalFilterSpecDto2.setPropertyVal("CCU");

        foreignKeyPathIdRetrievalSpecDto.setFilterSpecs(List.of(idRetrievalFilterSpecDto1, idRetrievalFilterSpecDto2));

        final String query = """
                SELECT COUNT(i) FROM IcuStaysEntity i 
                WHERE i.firstCareUnit = 'CCU' AND i.patientsEntity.gender = 'M' AND i.patientsEntity.subjectId IN (:ids)
                """;
        long countExpected = em.createQuery(query, Long.class)
                .setParameter("ids", ids)
                .getSingleResult();

        Set<Object> res = idRetrieval.retrieveIdsUsingForeignKeyPath(foreignKeyPathIdRetrievalSpecDto);
        Assertions.assertEquals(countExpected, res.size());
    }
}
