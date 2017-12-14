package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.GenericEntityTest;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.LocationTest;
import it.polimi.travlendarplus.entities.travelMeans.PrivateTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith( Arquillian.class )
public class TravelComponentTest extends GenericEntityTest{

    private List< TravelComponent > testTravelComponents;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment().addClass( TravelComponent.class );
    }

    @Before
    public void setUp() throws Exception {
        testTravelComponents = new ArrayList<>(  );
        testTravelComponents.add( new TravelComponent( Instant.ofEpochSecond( 200 ), Instant.ofEpochSecond( 200 ),
                200, LocationTest.getTestLocations( 3 ), LocationTest.getTestLocations( 4),
                new PublicTravelMean( "name1", TravelMeanEnum.BUS,10 ) ) );
        testTravelComponents.add( new TravelComponent( Instant.ofEpochSecond( 400 ), Instant.ofEpochSecond( 500 ),
                500, LocationTest.getTestLocations( 7 ), LocationTest.getTestLocations( 8),
                new PrivateTravelMean( "name2", TravelMeanEnum.CAR,20 ) ) );
        super.preparePersistenceTest();
    }

    @Test
    public void allLocationAreFoundUsingJpqlQuery() {
        //all locations loaded
        List < TravelComponent > retrievedComponents =
                entityManager.createQuery(
                        " SELECT travel FROM TRAVEL_COMPONENT travel ORDER BY travel.length", TravelComponent.class)
                        .getResultList();
        assertContainsAllTravelComponents( retrievedComponents );
    }


    private void assertContainsAllTravelComponents( List< TravelComponent > retrievedComponents ) {

        Assert.assertEquals( testTravelComponents.size(), retrievedComponents.size() );
        assertTravelComponentsAreEquals( testTravelComponents.get( 0 ), retrievedComponents.get( 0 ) );
        assertTravelComponentsAreEquals( testTravelComponents.get( 1 ), retrievedComponents.get( 1 ) );
    }

    private void assertTravelComponentsAreEquals(TravelComponent expected, TravelComponent retrieved ) {
        Assert.assertEquals( expected.getStartingTime(), retrieved.getStartingTime() );
        Assert.assertEquals( expected.getEndingTime(), retrieved.getEndingTime());
        Assert.assertEquals( expected.getLength(), retrieved.getLength(), 0);
        Assert.assertEquals( expected.getMeanUsed().getType(), retrieved.getMeanUsed().getType());
        new LocationTest().assertLocationsAreEquals( expected.getDeparture(), retrieved.getDeparture() );
        new LocationTest().assertLocationsAreEquals( expected.getArrival(), retrieved.getArrival() );
    }


    @Override
    protected void clearTableQuery(){
        entityManager.createQuery( "DELETE FROM TRAVEL_COMPONENT " ).executeUpdate();
    }

    @Override
    protected void loadTestData(){
        for ( TravelComponent travelComponent : testTravelComponents ){
            entityManager.persist( travelComponent.getArrival());
            entityManager.persist( travelComponent.getDeparture() );
            entityManager.persist( travelComponent.getMeanUsed() );
            entityManager.persist( travelComponent );
        }
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }
}
