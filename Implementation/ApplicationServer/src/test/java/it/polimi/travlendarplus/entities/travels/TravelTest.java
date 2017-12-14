package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.GenericEntityTest;
import it.polimi.travlendarplus.entities.calendar.Event;
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
public class TravelTest extends GenericEntityTest {
    private Travel testTravel;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment().addClass( Travel.class );
    }

    public static Travel getTestTravel( int index ) {
        ArrayList< TravelComponent > firstTravelComponents = new ArrayList<>();
        firstTravelComponents.add( TravelComponentTest.getTestTravelComponent( index ) );
        firstTravelComponents.add( TravelComponentTest.getTestTravelComponent( 2 * index ) );
        return new Travel( new Event(), firstTravelComponents );
    }

    @Before
    public void setUp() throws Exception {
        ArrayList< TravelComponent > firstTravelComponents = new ArrayList<>();
        firstTravelComponents.add( TravelComponentTest.getTestTravelComponent( 1 ) );
        firstTravelComponents.add( TravelComponentTest.getTestTravelComponent( 2 ) );
        testTravel = new Travel( null, firstTravelComponents );

        super.preparePersistenceTest();
    }

    @Test
    public void allTravelsAreFoundUsingJpqlQuery() {

        List< Travel > retrievedTravels =
                entityManager.createQuery(
                        " SELECT travel FROM TRAVEL travel", Travel.class )
                        .getResultList();
        assertContainsAllTravels( retrievedTravels );
    }

    private void assertContainsAllTravels( List< Travel > retrievedTravels ) {

        Assert.assertEquals( 1, retrievedTravels.size() );
        assertTravelsAreEquals( testTravel, retrievedTravels.get( 0 ) );
    }

    private void assertTravelsAreEquals( Travel expected, Travel retrieved ) {
        new TravelComponentTest().assertContainsAllTravelComponents( expected.getMiniTravels(),
                retrieved.getMiniTravels() );
    }

    @Override
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM TRAVEL " ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        for ( TravelComponent travelComponent : testTravel.getMiniTravels() ) {
            entityManager.persist( travelComponent.getMeanUsed() );
            entityManager.persist( travelComponent.getArrival() );
            entityManager.persist( travelComponent.getDeparture() );
            entityManager.persist( travelComponent );
        }
        entityManager.persist( testTravel );
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
