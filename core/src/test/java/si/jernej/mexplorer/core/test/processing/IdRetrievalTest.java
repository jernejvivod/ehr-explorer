package si.jernej.mexplorer.core.test.processing;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.processing.IdRetrieval;
import si.jernej.mexplorer.core.service.TargetExtractionService;
import si.jernej.mexplorer.processorapi.v1.model.IdRetrievalFilterSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.IdRetrievalSpecDto;
import si.jernej.mexplorer.test.ATestBase;

class IdRetrievalTest extends ATestBase
{
    @Override
    protected Weld loadWeld(Weld weld)
    {
        return weld.addPackages(
                true,
                getClass(),
                IdRetrieval.class,
                TargetExtractionService.class,
                MimicEntityManager.class
        );
    }

    @Inject
    private IdRetrieval idRetrieval;

    @ParameterizedTest
    @CsvSource({
            "Wrong, hadmId",
            "AdmissionsEntity, wrong",
            "Wrong, wrong"
    })
    void wrongEntityNamePropertyNamePair(String entityName, String propertyName)
    {
        IdRetrievalSpecDto idRetrievalSpecDto = new IdRetrievalSpecDto();
        idRetrievalSpecDto.setEntityName(entityName);
        idRetrievalSpecDto.setIdProperty(propertyName);
        Assertions.assertThrows(ValidationCoreException.class, () -> idRetrieval.retrieveIds(idRetrievalSpecDto));
    }

    @Test
    void basicWithoutFilter()
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
    void basicWithFilterWithWrongPropertyName()
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
    void basicWithSingleLessOrMoreFilter(IdRetrievalFilterSpecDto.ComparatorEnum comparatorEnum, short hospitalExpireFlag)
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
    void basicWithSingleEqualFilter()
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
    void basicWithTwoFilters()
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
    void filterBySameAndLinkedEntityTwoFilters()
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
}
