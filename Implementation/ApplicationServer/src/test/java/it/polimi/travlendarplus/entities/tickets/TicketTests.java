package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GenericEntityTest;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.LocationTest;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RunWith( Arquillian.class )
public class TicketTests extends GenericEntityTest{

    private List< Ticket > testTickets;
    private Location testDeparture;
    private Location testArrival;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment()
                .addClass( Ticket.class )
                .addClass( DistanceTicket.class )
                .addClass( GenericTicket.class )
                .addClass( PathTicket.class )
                .addClass( PeriodTicket.class );
    }

    @Before
    public void setUp() throws Exception {
        testTickets = new ArrayList<>(  );
        ArrayList < PublicTravelMean > publicTravelMeans = new ArrayList<>( );
        publicTravelMeans.add( new PublicTravelMean( "name", TravelMeanEnum.BUS, 1544 ) );

        DistanceTicket distanceTicket = new DistanceTicket( 123, publicTravelMeans, 200 );
        testDeparture = LocationTest.getTestLocation( 1 );
        testArrival = LocationTest.getTestLocation( 2 );
        testTickets.add( distanceTicket );
        testTickets.add( new GenericTicket( 147, publicTravelMeans, "line_name1" ) );
        testTickets.add( new PathTicket( 789, publicTravelMeans, "line_name2",
                testDeparture, testArrival) );
        testTickets.add( new PeriodTicket( 789, publicTravelMeans, "name",
                Instant.ofEpochSecond( 100 ), Instant.ofEpochSecond( 200 ), distanceTicket) );
        super.preparePersistenceTest();
    }

    @Test
    public void allTicketsAreFoundUsingJpqlQuery() {

        List < Ticket > retrievedTickets = new ArrayList<>( );
        retrievedTickets.add( entityManager.createQuery(
                "SELECT ticket FROM DISTANCE_TICKET ticket ", DistanceTicket.class )
                .getSingleResult() );
        retrievedTickets.addAll( entityManager.createQuery(
                "SELECT ticket FROM GENERIC_TICKET ticket ORDER BY ticket.lineName", GenericTicket.class )
                .getResultList() );
        retrievedTickets.add( entityManager.createQuery(
                "SELECT ticket FROM PERIOD_TICKET ticket ", PeriodTicket.class )
                .getSingleResult() );
        assertContainsAllTickets( retrievedTickets );
    }


    private void assertContainsAllTickets( List< Ticket > retrievedTickets ) {
        Assert.assertEquals( testTickets.size(), retrievedTickets.size() );
        assertTicketsAreEquals( testTickets.get( 0 ), retrievedTickets.get( 0 ) );
        assertTicketsAreEquals( testTickets.get( 1 ), retrievedTickets.get( 1 ) );
        assertTicketsAreEquals( testTickets.get( 2 ), retrievedTickets.get( 2 ) );
        assertTicketsAreEquals( testTickets.get( 3 ), retrievedTickets.get( 3 ) );
    }

    public void assertTicketsAreEquals(Ticket expected, Ticket retrieved ) {
        Assert.assertEquals( expected.getCost(), retrieved.getCost() , 0);
        Assert.assertEquals( expected.getRelatedTo().get( 0 ).getName(), retrieved.getRelatedTo().get( 0 ).getName() );
        Assert.assertEquals( expected.getRelatedTo().get( 0 ).getType(), retrieved.getRelatedTo().get( 0 ).getType() );
        Assert.assertEquals( expected.getRelatedTo().get( 0 ).getEco(), retrieved.getRelatedTo().get( 0 ).getEco(), 0 );
    }


    @Override
    protected void clearTableQuery(){
        entityManager.createQuery( "DELETE FROM DISTANCE_TICKET " ).executeUpdate();
        entityManager.createQuery( "DELETE FROM GENERIC_TICKET " ).executeUpdate();
        entityManager.createQuery( "DELETE FROM PATH_TICKET " ).executeUpdate();
        entityManager.createQuery( "DELETE FROM PERIOD_TICKET " ).executeUpdate();
    }

    @Override
    protected void loadTestData(){
        entityManager.persist( testDeparture );
        entityManager.persist( testArrival );
        for ( Ticket Ticket : testTickets ){
            entityManager.persist( Ticket );
        }
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
