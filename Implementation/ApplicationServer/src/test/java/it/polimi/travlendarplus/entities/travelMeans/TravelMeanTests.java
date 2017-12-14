package it.polimi.travlendarplus.entities.travelMeans;

import it.polimi.travlendarplus.entities.GenericEntityTest;
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
public class TravelMeanTests extends GenericEntityTest {


    private List< TravelMean > testTravelMeans;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment()
                .addClass( PrivateTravelMean.class )
                .addClass( PublicTravelMean.class )
                .addClass( SharingTravelMean.class );
    }

    @Before
    public void setUp() throws Exception {
        testTravelMeans = new ArrayList<>();
        testTravelMeans.add( new PrivateTravelMean( "name", TravelMeanEnum.CAR, 200 ) );
        testTravelMeans.add( new PublicTravelMean( "name2", TravelMeanEnum.BUS, 1544 ) );
        testTravelMeans.add( new SharingTravelMean( "name3", TravelMeanEnum.SHARING_BIKE, 1544, 10 ) );
        super.preparePersistenceTest();
    }

    @Test
    public void allTravelMeanAreFoundUsingJpqlQuery() {
        List< TravelMean > retrievedTravelMeans = new ArrayList<>();
        retrievedTravelMeans.add( entityManager.createQuery( " " +
                "SELECT privateTravelMean " +
                "FROM PRIVATE_TRAVEL_MEAN privateTravelMean " +
                "ORDER BY privateTravelMean.name", PrivateTravelMean.class )
                .getSingleResult() );

        retrievedTravelMeans.add( entityManager.createQuery( " " +
                "SELECT publicTravelMean " +
                "FROM PUBLIC_TRAVEL_MEAN publicTravelMean " +
                "ORDER BY publicTravelMean.name", PublicTravelMean.class )
                .getSingleResult() );

        retrievedTravelMeans.add( entityManager.createQuery( " " +
                "SELECT sharingTravelMean " +
                "FROM SHARING_TRAVEL_MEAN sharingTravelMean " +
                "ORDER BY sharingTravelMean.name", SharingTravelMean.class )
                .getSingleResult() );

        assertContainsAllTravelMeans( retrievedTravelMeans );
    }


    private void assertContainsAllTravelMeans( List< TravelMean > retrievedTravelMeans ) {
        Assert.assertEquals( testTravelMeans.size(), retrievedTravelMeans.size() );
        assertTravelMeansAreEquals( testTravelMeans.get( 0 ), retrievedTravelMeans.get( 0 ) );
        assertTravelMeansAreEquals( testTravelMeans.get( 1 ), retrievedTravelMeans.get( 1 ) );
        assertTravelMeansAreEquals( testTravelMeans.get( 2 ), retrievedTravelMeans.get( 2 ) );
    }

    public void assertTravelMeansAreEquals( TravelMean expected, TravelMean retrieved ) {
        Assert.assertEquals( expected.getName(), retrieved.getName() );
        Assert.assertEquals( expected.getType(), retrieved.getType() );
        Assert.assertEquals( expected.getEco(), retrieved.getEco(), 0 );
    }


    @Override
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM PRIVATE_TRAVEL_MEAN " ).executeUpdate();
        entityManager.createQuery( "DELETE FROM PUBLIC_TRAVEL_MEAN " ).executeUpdate();
        entityManager.createQuery( "DELETE FROM SHARING_TRAVEL_MEAN " ).executeUpdate();

    }

    @Override
    protected void loadTestData() {
        for ( TravelMean travelMean : testTravelMeans ) {
            entityManager.persist( travelMean );
        }
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }


}
