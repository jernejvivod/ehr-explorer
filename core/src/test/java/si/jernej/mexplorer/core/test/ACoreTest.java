package si.jernej.mexplorer.core.test;

import org.jboss.weld.environment.se.Weld;

import si.jernej.mexplorer.core.manager.DbEntityManager;
import si.jernej.mexplorer.core.processing.Wordification;
import si.jernej.mexplorer.core.service.ClinicalTextService;
import si.jernej.mexplorer.test.ATestBase;

public abstract class ACoreTest extends ATestBase
{
    @Override
    protected Weld loadWeld(Weld weld)
    {
        return weld.addPackages(
                true,
                getClass(),
                ClinicalTextService.class,
                Wordification.class,
                DbEntityManager.class
        );
    }
}
