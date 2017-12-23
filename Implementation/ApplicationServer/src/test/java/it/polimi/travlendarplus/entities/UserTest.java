package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.BreakEventTest;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.EventTest;
import it.polimi.travlendarplus.entities.preferences.TypeOfEventTests;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( Arquillian.class )
public class UserTest extends GenericEntityTest {

    private User testUser;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment().addClass( User.class );
    }

    @Before
    public void setUp() throws Exception {
        testUser = new User( "email", "name",
                "surname", "password" );
        testUser.addUserDevice( "newDevice" );

        BreakEvent breakEventTest = BreakEventTest.getTestBreakEvent( 2 );
        breakEventTest.setUser( testUser );
        testUser.addBreak( breakEventTest );

        Event eventTest = EventTest.getTestEvent( 3 );
        eventTest.setUser( testUser );
        testUser.addEvent( eventTest );

        testUser.addPreference( TypeOfEventTests.getTestTypeOfEvent( 10 ) );
        testUser.addLocation( "locationName", LocationTest.getTestLocation( 22 ) );
        super.preparePersistenceTest();
    }

    @Test
    public void userIsFoundUsingJpqlQuery() {
        User retrievedUser =
                entityManager.createQuery( " SELECT user FROM TRAVLENDAR_USER user", User.class )
                        .getSingleResult();
        assertUsersAreEquals( retrievedUser );
    }

    public void assertUsersAreEquals( User retrievedUser ) {

        Assert.assertEquals( testUser.getEmail(), retrievedUser.getEmail() );
        Assert.assertEquals( testUser.getName(), retrievedUser.getName() );
        Assert.assertEquals( testUser.getSurname(), retrievedUser.getSurname() );
        Assert.assertEquals( testUser.getPassword(), retrievedUser.getPassword() );

        Assert.assertEquals( testUser.getBreaks().size(), retrievedUser.getBreaks().size() );
        new BreakEventTest().assertSameBreakEvents(
                testUser.getBreaks().get( 0 ), retrievedUser.getBreaks().get( 0 ) );

        Assert.assertEquals( testUser.getEvents().size(), retrievedUser.getEvents().size() );
        new EventTest().assertSameEvents(
                testUser.getEvents().get( 0 ), retrievedUser.getEvents().get( 0 ) );

        Assert.assertEquals( testUser.getPreferences().size(), retrievedUser.getPreferences().size() );
        new TypeOfEventTests().assertContainsTypeOfEvent(
                testUser.getPreferences().get( 0 ), retrievedUser.getPreferences().get( 0 ) );

        Assert.assertEquals( testUser.getPreferredLocations().size(), retrievedUser.getPreferredLocations().size() );
        Assert.assertEquals( testUser.getPreferredLocations().get( 0 ).getName(),
                retrievedUser.getPreferredLocations().get( 0 ).getName() );
        new LocationTest().assertLocationsAreEquals(
                testUser.getPreferredLocations().get( 0 ).getLocation(),
                retrievedUser.getPreferredLocations().get( 0 ).getLocation() );
    }

    @Override
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM TRAVLENDAR_USER " ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        entityManager.persist( testUser.getEvents().get( 0 ).getEventLocation() );
        entityManager.persist( testUser.getEvents().get( 0 ).getDeparture() );
        entityManager.persist( testUser.getPreferredLocations().get( 0 ).getLocation() );
        entityManager.persist( testUser );
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
