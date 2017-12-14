package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntityTest;

import it.polimi.travlendarplus.entities.Location;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class EventTest extends GenericEntityTest{

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment().addClass( Event.class );
    }

    @Before
    public void setUp() throws Exception {
       super.preparePersistenceTest();
    }

    @Override
    protected void clearTableQuery() {
       // entityManager.createQuery( "DELETE FROM EVENT" ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        //TODO
    }

    @Test
    public void name() {

    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }
}
