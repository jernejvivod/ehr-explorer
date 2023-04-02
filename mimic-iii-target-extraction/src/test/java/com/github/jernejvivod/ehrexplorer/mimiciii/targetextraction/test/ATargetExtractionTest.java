package com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.test;

import org.jboss.weld.environment.se.Weld;

import com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.processing.TargetExtraction;
import com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.manager.DbEntityManager;
import com.github.jernejvivod.ehrexplorer.mimiciii.targetextraction.service.TargetExtractionService;
import com.github.jernejvivod.test.ATestBase;

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
