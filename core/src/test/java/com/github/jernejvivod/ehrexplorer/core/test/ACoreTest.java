package com.github.jernejvivod.ehrexplorer.core.test;

import org.jboss.weld.environment.se.Weld;

import com.github.jernejvivod.ehrexplorer.core.manager.DbEntityManager;
import com.github.jernejvivod.ehrexplorer.core.processing.Wordification;
import com.github.jernejvivod.ehrexplorer.core.service.ClinicalTextService;

import com.github.jernejvivod.test.ATestBase;

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
