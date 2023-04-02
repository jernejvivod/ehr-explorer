package com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.manager;

import com.github.jernejvivod.ehrexplorer.mimiciii.entity.PatientsEntity;

import javax.annotation.CheckForNull;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Dependent
public class DbEntityManager
{
    @PersistenceContext
    private EntityManager em;

    public List<Object[]> getResultListForExtractPatientDiedDuringAdmissionTarget(@CheckForNull List<Long> ids, @CheckForNull Integer ageLim)
    {
        if (ids != null && ids.isEmpty())
        {
            return List.of();
        }

        //language=JPAQL
        String query = """
                SELECT a.hadmId, a.hospitalExpireFlag FROM AdmissionsEntity a
                WHERE (EXTRACT(DAY FROM (a.admitTime - a.patientsEntity.dob)) / 365.2425) >= :ageLim
                """;

        query += (ids != null ? " AND a.hadmId IN (:ids)" : "");

        final TypedQuery<Object[]> q = em.createQuery(query, Object[].class).setParameter("ageLim", ageLim != null ? ageLim.doubleValue() : 0.0);

        if (ids != null)
        {
            q.setParameter("ids", ids);
        }

        return q.getResultList();
    }

    public List<PatientsEntity> fetchPatientForTargetExtractionHospitalReadmission(@CheckForNull List<Long> ids)
    {
        if (ids != null && ids.isEmpty())
        {
            return List.of();
        }

        //language=JPAQL
        String query = """
                SELECT DISTINCT p FROM PatientsEntity p
                INNER JOIN FETCH p.admissionsEntitys adms
                WHERE NOT EXISTS(SELECT adms FROM p.admissionsEntitys adms WHERE (adms.admitTime IS NULL OR adms.dischTime IS NULL))
                """;

        query += (ids != null ? " AND p.subjectId IN (:ids)" : "");

        TypedQuery<PatientsEntity> q = em.createQuery(query, PatientsEntity.class);

        if (ids != null)
        {
            q.setParameter("ids", ids).getResultList();
        }

        return q.getResultList();
    }

    public List<PatientsEntity> fetchPatientsForTargetExtractionIcuReadmission(@CheckForNull List<Long> ids)
    {
        if (ids != null && ids.isEmpty())
        {
            return List.of();
        }
        //language=JPAQL
        String query = """
                SELECT DISTINCT p FROM PatientsEntity p
                INNER JOIN FETCH p.icuStaysEntitys ics
                INNER JOIN FETCH ics.admissionsEntity a
                WHERE NOT EXISTS(SELECT ics FROM p.icuStaysEntitys ics WHERE (ics.inTime IS NULL OR ics.outTime IS NULL))
                """;

        query += (ids != null ? " AND p.subjectId IN (:ids)" : "");

        TypedQuery<PatientsEntity> q = em.createQuery(query, PatientsEntity.class);

        if (ids != null)
        {
            q.setParameter("ids", ids).getResultList();
        }

        return q.getResultList();
    }
}
