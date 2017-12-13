package it.polimi.travlendarplus.beans.calendar_manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import static org.junit.Assert.*;

@RunWith( Arquillian.class )
public class EventManagerTest {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction utx;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create( JavaArchive.class )
                .addPackage( EventManager.class.getPackage() )
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
    }

    @Test
    public void name() throws Exception {
        assertTrue( true );
    }
}
