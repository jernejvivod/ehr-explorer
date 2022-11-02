package si.jernej.mexplorer.core.manager;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Metamodel;

@Dependent
public class MimicEntityManager
{
    @PersistenceContext
    private EntityManager em;

    public Metamodel getMetamodel()
    {
        return em.getMetamodel();
    }

    public List<Object[]> getEntitiesAndIds(String idPropertyName, String rootEntityName, List<Long> ids)
    {
        return em.createQuery(String.format("SELECT e, e.%1$s FROM %2$s e WHERE e.%1$s IN (:ids)", idPropertyName, rootEntityName), Object[].class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
