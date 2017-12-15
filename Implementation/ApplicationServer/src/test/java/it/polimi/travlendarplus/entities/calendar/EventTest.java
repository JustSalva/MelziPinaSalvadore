package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntityTest;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.LocationTest;
import it.polimi.travlendarplus.entities.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

@RunWith( Arquillian.class )
public class EventTest extends GenericEntityTest {

    private Event testEvent;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment()
                .addClass( Event.class )
                .addClass( GenericEvent.class );
    }

    @Before
    public void setUp() throws Exception {
        Location departure = LocationTest.getTestLocation( 34 );
        Location arrival = LocationTest.getTestLocation( 67 );
        User user = new User( "email", "name", "surname", "password" );

        testEvent = new Event( "name", Instant.ofEpochSecond( 120 ), Instant.ofEpochSecond( 500 ),
                false, null, "description", false, null,
                arrival, departure );
        testEvent.setUser( user );

        super.preparePersistenceTest();
    }

    @Test
    public void eventIsFoundUsingJpqlQuery() {

        Event retrievedEvent =
                entityManager.createQuery( " SELECT event FROM EVENT event", Event.class )
                        .getSingleResult();
        assertSameEvents( retrievedEvent );
    }


    private void assertSameEvents( Event retrievedEvent ) {
        Assert.assertEquals( testEvent.getName(), retrievedEvent.getName() );
        Assert.assertEquals( testEvent.getStartingTime(), retrievedEvent.getStartingTime() );
        Assert.assertEquals( testEvent.getEndingTime(), retrievedEvent.getEndingTime() );
        Assert.assertEquals( testEvent.isScheduled(), retrievedEvent.isScheduled() );
        Assert.assertEquals( testEvent.getDescription(), retrievedEvent.getDescription() );
        Assert.assertEquals( testEvent.isPrevLocChoice(), retrievedEvent.isPrevLocChoice() );
        new LocationTest().assertLocationsAreEquals( testEvent.getEventLocation(), retrievedEvent.getEventLocation() );
        new LocationTest().assertLocationsAreEquals( testEvent.getDeparture(), retrievedEvent.getDeparture() );

    }

    @Override
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM EVENT " ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        entityManager.persist( testEvent.getUser() );
        entityManager.persist( testEvent.getEventLocation() );
        entityManager.persist( testEvent.getDeparture() );
        entityManager.persist( testEvent );
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
