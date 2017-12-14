package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.GenericEntityTest;
import it.polimi.travlendarplus.entities.Location;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith( Arquillian.class )
public class TypeOfEventTests extends GenericEntityTest{
    private TypeOfEvent testTypeOfEvent;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment()
                .addClass( TypeOfEvent.class )
                .addClass( Constraint.class )
                .addClass( DistanceConstraint.class )
                .addClass( PeriodOfDayConstraint.class );
    }

    @Before
    public void setUp() throws Exception {

        ArrayList< Constraint > constraints = new ArrayList<>();
        constraints.add( new PeriodOfDayConstraint( TravelMeanEnum.BUS, 1,10 ) );
        constraints.add( new DistanceConstraint( TravelMeanEnum.TRAM, 10, 20 ) );
        testTypeOfEvent = new TypeOfEvent( "name", ParamFirstPath.ECO_PATH);
        testTypeOfEvent.addDeactivated( TravelMeanEnum.BIKE );
        testTypeOfEvent.setLimitedBy( constraints );
        super.preparePersistenceTest();
    }

    @Test
    public void allLocationAreFoundUsingJpqlQuery() {

        TypeOfEvent retrievedTypeOfEvent = entityManager.createQuery( " " +
                "SELECT typeOfEvent " +
                "FROM TYPE_OF_EVENT typeOfEvent", TypeOfEvent.class )
                .getSingleResult();
        assertContainsTypeOfEvent( retrievedTypeOfEvent );
    }

    private void assertContainsTypeOfEvent( TypeOfEvent retrievedTypeOfEvent ) {

        Assert.assertEquals( testTypeOfEvent.getName(), retrievedTypeOfEvent.getName() );
        Assert.assertEquals( testTypeOfEvent.getParamFirstPath(), retrievedTypeOfEvent.getParamFirstPath() );
        assertSameLimitedByConstraints( testTypeOfEvent.getLimitedBy(), retrievedTypeOfEvent.getLimitedBy() );
        assertSameDeactivatedTravelMean( testTypeOfEvent.getDeactivate(), retrievedTypeOfEvent.getDeactivate() );
    }

    private void assertSameLimitedByConstraints( List <Constraint> expected, List <Constraint> retrieved ){
        Assert.assertEquals( expected.size(), retrieved.size() );
        Assert.assertEquals( expected.get( 0 ).getConcerns(), retrieved.get( 0 ).getConcerns() );
        Assert.assertEquals( expected.get( 1 ).getConcerns(), retrieved.get( 1 ).getConcerns() );
    }

    private void assertSameDeactivatedTravelMean( List <TravelMeanEnum> expected, List <TravelMeanEnum> retrieved ){
        Assert.assertEquals( expected.size(), retrieved.size() );
        Assert.assertEquals( expected.get( 0 ), retrieved.get( 0 ) );
    }


    @Override
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM TYPE_OF_EVENT " ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        entityManager.persist( testTypeOfEvent );
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
