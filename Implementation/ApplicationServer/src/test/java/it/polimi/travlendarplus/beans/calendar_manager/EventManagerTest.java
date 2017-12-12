package it.polimi.travlendarplus.beans.calendar_manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith( Arquillian.class )
public class EventManagerTest {

    private static final String container = "glassfish-remote";

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
