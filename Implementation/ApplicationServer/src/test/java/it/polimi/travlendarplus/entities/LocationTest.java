package it.polimi.travlendarplus.entities;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith( Arquillian.class )
public class LocationTest extends GenericEntityTest{

    private List< Location > testLocations;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment().addClass( Location.class );
    }

    @Before
    public void setUp() throws Exception {
        testLocations = new ArrayList<>(  );
        testLocations.add( new Location( 123, 456, "address1" ) );
        testLocations.add( new Location( 147, 258, "address2" ) );
        testLocations.add( new Location( 789, 369, "address3" ) );
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

        Assert.assertEquals( testLocations.size(), retrievedLocations.size() );
        assertLocationsAreEquals( testLocations.get( 0 ), retrievedLocations.get( 0 ) );
        assertLocationsAreEquals( testLocations.get( 1 ), retrievedLocations.get( 1 ) );
        assertLocationsAreEquals( testLocations.get( 2 ), retrievedLocations.get( 2 ) );


    }

    private void assertLocationsAreEquals(Location expected, Location retrieved ) {
        Assert.assertEquals( expected.getAddress(), retrieved.getAddress() );
        Assert.assertEquals( expected.getLatitude(), retrieved.getLatitude() , 0);
        Assert.assertEquals( expected.getLongitude(), retrieved.getLongitude(), 0 );
    }


    @Override
    protected void clearTableQuery(){
        entityManager.createQuery( "DELETE FROM LOCATION " ).executeUpdate();
    }

    @Override
    protected void loadTestData(){
        for ( Location location : testLocations ){
            entityManager.persist( location );
        }
    }
}
