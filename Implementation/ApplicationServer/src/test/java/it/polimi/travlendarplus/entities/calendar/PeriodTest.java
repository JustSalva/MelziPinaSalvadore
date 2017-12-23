package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntityTest;
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
public class PeriodTest extends GenericEntityTest {

    private List< Period > testPeriods;

    @Deployment
    public static JavaArchive createDeployment() {
        return GenericEntityTest.createDeployment().addClass( Period.class )
                .addClass( Instant.class );
    }

    public static Period getTestPeriod( int index ) {
        return new Period( Instant.ofEpochSecond( 111*index ), Instant.ofEpochSecond( 222*index ), index );
    }

    @Before
    public void setUp() throws Exception {
        testPeriods = new ArrayList<>();
        testPeriods.add( new Period( Instant.ofEpochSecond( 111 ), Instant.ofEpochSecond( 222 ), 7 ) );
        testPeriods.add( new Period( Instant.ofEpochSecond( 333 ), Instant.ofEpochSecond( 444 ), 10 ) );
        testPeriods.add( new Period( Instant.ofEpochSecond( 555 ), Instant.ofEpochSecond( 666 ), 14 ) );
        super.preparePersistenceTest();
    }

    @Test
    public void allPeriodsAreFoundUsingJpqlQuery() {

        List< Period > retrievedPeriods =
                entityManager.createQuery( " " +
                        "SELECT period " +
                        "FROM PERIOD period " +
                        "ORDER BY period.deltaDays", Period.class )
                        .getResultList();
        assertContainsAllPeriods( retrievedPeriods );
    }

    private void assertContainsAllPeriods( List< Period > retrievedPeriods ) {

        Assert.assertEquals( testPeriods.size(), retrievedPeriods.size() );
        assertPeriodsAreEquals( testPeriods.get( 0 ), retrievedPeriods.get( 0 ) );
        assertPeriodsAreEquals( testPeriods.get( 1 ), retrievedPeriods.get( 1 ) );
        assertPeriodsAreEquals( testPeriods.get( 2 ), retrievedPeriods.get( 2 ) );


    }

    public void assertPeriodsAreEquals( Period expected, Period retrieved ) {
        Assert.assertEquals( expected.getStartingDay(), retrieved.getStartingDay() );
        Assert.assertEquals( expected.getEndingDay(), retrieved.getEndingDay() );
        Assert.assertEquals( expected.getDeltaDays(), retrieved.getDeltaDays() );
    }

    @Override
    protected void clearTableQuery() {
        entityManager.createQuery( "DELETE FROM PERIOD " ).executeUpdate();
    }

    @Override
    protected void loadTestData() {
        for ( Period period : testPeriods ) {
            entityManager.persist( period );
        }
    }

    @After
    public void tearDown() throws Exception {
        super.commitTransaction();
    }

}
