package si.jernej.mexplorer.test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class PersistenceContextManager
{
    private static final Logger logger = LoggerFactory.getLogger(PersistenceContextManager.class);

    private static final PersistenceContextManager pcm = new PersistenceContextManager();
    private EntityManager entityManager;
    private EntityManagerFactory managerFactory;

    public PersistenceContextManager()
    {
        initEntityManagerFactory();
    }

    public static EntityManager em()
    {
        return singleton().getEntityManager();
    }

    public static EntityManagerFactory emFactory()
    {
        return singleton().managerFactory;
    }

    public static PersistenceContextManager singleton()
    {
        return pcm;
    }

    public void initEntityManagerFactory()
    {
        try
        {
            setUpManagerFactory();
        }
        catch (RuntimeException e)
        {
            logger.error("Error while initializing persistence map ... ", e);
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Error while initializing persistence map ... ", e);
            throw new RuntimeException(e);
        }
    }

    private void setUpManagerFactory()
    {
        if (managerFactory == null || !managerFactory.isOpen())
        {
            Map<String, String> persistenceMap = new HashMap<>();
            persistenceMap.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/mimic");  // TODO load properties using eonbits
            persistenceMap.put("javax.persistence.jdbc.user", "postgres");
            persistenceMap.put("javax.persistence.jdbc.password", "postgres");
            persistenceMap.put("javax.persistence.query.timeout", String.format("%d", Duration.ofMinutes(2).toMillis()));
            managerFactory = Persistence.createEntityManagerFactory("primary", persistenceMap);
        }
    }

    public void closeManagerFactory()
    {
        if (managerFactory != null)
            managerFactory.close();
    }

    public void closeEntityManager()
    {
        if (entityManager != null && entityManager.isOpen())
            entityManager.close();
    }

    public EntityManager getEntityManager()
    {
        if (entityManager == null || !entityManager.isOpen())
            setUpEntityManager();

        return entityManager;
    }

    private void setUpEntityManager()
    {
        setUpManagerFactory();
        entityManager = managerFactory.createEntityManager();
    }
}
