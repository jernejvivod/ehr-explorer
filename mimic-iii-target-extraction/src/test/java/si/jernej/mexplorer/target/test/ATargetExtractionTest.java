package si.jernej.mexplorer.target.test;

import org.jboss.weld.environment.se.Weld;

import si.jernej.mexplorer.target.manager.DbEntityManager;
import si.jernej.mexplorer.target.processing.TargetExtraction;
import si.jernej.mexplorer.target.service.TargetExtractionService;
import si.jernej.mexplorer.test.ATestBase;

public abstract class ATargetExtractionTest extends ATestBase
{
    @Override
    protected Weld loadWeld(Weld weld)
    {
        return weld.addPackages(
                true,
                getClass(),
                DbEntityManager.class,
                TargetExtraction.class,
                TargetExtractionService.class
        );
    }
}
