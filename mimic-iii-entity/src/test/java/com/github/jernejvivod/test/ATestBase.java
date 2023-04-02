package com.github.jernejvivod.test;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

@EnableAutoWeld
public abstract class ATestBase
{
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(createWeld())
            .inject(this)
            .activate(RequestScoped.class, SessionScoped.class)
            .setPersistenceUnitFactory(ip -> PersistenceContextManager.emFactory())
            .setPersistenceContextFactory(ip -> PersistenceContextManager.em())
            .build();

    private Weld createWeld()
    {
        Weld weld = WeldInitiator.createWeld()
                .addPackage(true, getClass());
        return loadWeld(weld);
    }

    protected Weld loadWeld(Weld weld)
    {
        return weld;
    }

    @PersistenceContext
    protected EntityManager em;
}
