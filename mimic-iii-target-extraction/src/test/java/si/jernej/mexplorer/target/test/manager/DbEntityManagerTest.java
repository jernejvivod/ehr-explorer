package si.jernej.mexplorer.target.test.manager;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.entity.PatientsEntity;
import si.jernej.mexplorer.target.test.ATargetExtractionTest;
import si.jernej.mexplorer.target.manager.DbEntityManager;

class DbEntityManagerTest extends ATargetExtractionTest
{
    @Inject
    private DbEntityManager dbEntityManager;

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

        List<Object[]> resIdsAndTarget = dbEntityManager.getResultListForExtractPatientDiedDuringAdmissionTarget(ids, null);

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
        List<PatientsEntity> res = dbEntityManager.fetchPatientForTargetExtractionHospitalReadmission(List.of());

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

        List<PatientsEntity> res = dbEntityManager.fetchPatientForTargetExtractionHospitalReadmission(ids);

        Assertions.assertEquals(ids.size(), res.size());
        Assertions.assertTrue(ids.containsAll(res.stream().map(PatientsEntity::getSubjectId).toList()));
    }
}
