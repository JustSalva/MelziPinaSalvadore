package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntityTest;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith( Arquillian.class )
public class EventTest extends GenericEntityTest{

    private Event testEvent;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment().addClass( Event.class );
    }

    /*@Before
    public void setUp() throws Exception {
        TypeOfEvent type = findTypeOfEvent( eventMessage.getIdTypeOfEvent() );
        Location departure = null;
        if(!eventMessage.isPrevLocChoice())
            departure = findLocation( eventMessage.getDeparture() );
        Location arrival = findLocation( eventMessage.getEventLocation() );
        return new Event( eventMessage.getName(), eventMessage.getStartingTime(), eventMessage.getEndingTime(),
                false, null, eventMessage.getDescription(), eventMessage.isPrevLocChoice(), type,
                arrival, departure);

        testEvents.add( new Event( 123, 456, "address1" ) );
        testEvents.add( new Location( 147, 258, "address2" ) );
        super.preparePersistenceTest();
    }

    @Test
    public void allLocationAreFoundUsingJpqlQuery() {
        //all locations loaded
        List < Location > retrievedLocations =
                entityManager.createQuery(" SELECT location FROM LOCATION location ORDER BY location.address", Location.class)
                        .getResultList();
        assertContainsAllLocations( retrievedLocations );
    }


    private void assertContainsAllLocations( List< Location > retrievedLocations ) {

        Assert.assertEquals( testEvents.size(), retrievedLocations.size() );
        assertLocationsAreEquals( testEvents.get( 0 ), retrievedLocations.get( 0 ) );
        assertLocationsAreEquals( testEvents.get( 1 ), retrievedLocations.get( 1 ) );
        assertLocationsAreEquals( testEvents.get( 2 ), retrievedLocations.get( 2 ) );


    }

    public void assertLocationsAreEquals(Location expected, Location retrieved ) {
        Assert.assertEquals( expected.getAddress(), retrieved.getAddress() );
        Assert.assertEquals( expected.getLatitude(), retrieved.getLatitude() , 0);
        Assert.assertEquals( expected.getLongitude(), retrieved.getLongitude(), 0 );
    }*/


    @Override
    protected void clearTableQuery(){
        entityManager.createQuery( "DELETE FROM LOCATION " ).executeUpdate();
    }

    @Override
    protected void loadTestData(){
        entityManager.persist( testEvent );
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

    public static Location getTestEvent( int index ){
        return new Location( index, index, "address" + index );
    }
}
