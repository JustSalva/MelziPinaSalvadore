package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntityTest;
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
public class BreakEventTest extends GenericEntityTest {

    private BreakEvent testBreakEvent;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment()
                .addClass( BreakEvent.class )
                .addClass( GenericEvent.class );
    }

    public static BreakEvent getTestBreakEvent( int index ) {
        return new BreakEvent( "name" + index, Instant.ofEpochSecond( 10 * index ), Instant.ofEpochSecond( 60 * index ),
                false, 23 * index );

    }

    @Before
    public void setUp() throws Exception {
        User user = new User( "email", "name", "surname", "password" );

        testBreakEvent = new BreakEvent( "name", Instant.ofEpochSecond( 120 ), Instant.ofEpochSecond( 500 ),
                false, 100 );
        testBreakEvent.setUser( user );

        super.preparePersistenceTest();
    }

    @Test
    public void eventIsFoundUsingJpqlQuery() {

        BreakEvent retrievedEvent =
                entityManager.createQuery( " SELECT break FROM BREAK_EVENT break", BreakEvent.class )
                        .getSingleResult();
        assertSameBreakEvents( retrievedEvent );
    }


    public void assertSameBreakEvents( BreakEvent retrievedEvent ) {
        Assert.assertEquals( testBreakEvent.getName(), retrievedEvent.getName() );
        Assert.assertEquals( testBreakEvent.getStartingTime(), retrievedEvent.getStartingTime() );
        Assert.assertEquals( testBreakEvent.getEndingTime(), retrievedEvent.getEndingTime() );
        Assert.assertEquals( testBreakEvent.isScheduled(), retrievedEvent.isScheduled() );
        Assert.assertEquals( testBreakEvent.getMinimumTime(), retrievedEvent.getMinimumTime() );
    }

    public void assertSameBreakEvents(BreakEvent testBreakEvent, BreakEvent retrievedEvent ) {
        this.testBreakEvent = testBreakEvent;
        assertSameBreakEvents( retrievedEvent );
    }

    @Override
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM BREAK_EVENT " ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        entityManager.persist( testBreakEvent );
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
