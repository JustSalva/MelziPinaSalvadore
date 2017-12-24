package it.polimi.travlendarplus.beans.calendarManager.pathManager;

import it.polimi.travlendarplus.beans.calendarManager.EJBTestInjector;
import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.beans.calendarManager.PreferenceManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( Parameterized.class )
public class PathManagerTest {
    PathManager pathManager;
    ScheduleManager scheduleManager;
    PreferenceManager preferenceManager;
    Random rdm = new Random();

    @Parameterized.Parameters( name
            = "{index}: Test with first={0}, second={1}, third={2}" )

    public static Iterable < Object[] > data () {
        return Arrays.asList( new Object[][]{
                { true, false, true },
                { false, true, true },
                { true, true, false },
                { true, true, true }
        } );
    }

    private final boolean first;
    private final boolean second;
    private final boolean third;

    public PathManagerTest ( boolean first, boolean second, boolean third ) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Before
    public void test () throws Exception {
        this.pathManager = new PathManager();
        // create mocks
        this.scheduleManager = mock( ScheduleManager.class );
        this.preferenceManager = mock( PreferenceManager.class );
        // inject
        final EJBTestInjector injector = new EJBTestInjector();
        injector.assign( ScheduleManager.class, scheduleManager );
        injector.assign( PreferenceManager.class, preferenceManager );
        injector.inject( this.pathManager );
    }

    @Test
    public void calculatePathsTest () throws GMapsGeneralException {
        PathManagerSettingsTest.baseConfiguration( first, second, third );

        when( scheduleManager.getPossiblePreviousEvent( PathManagerSettingsTest.e1.getStartingTime() ) ).thenReturn( null );
        when( scheduleManager.getPossiblePreviousEvent( PathManagerSettingsTest.e2.getStartingTime() ) ).thenReturn( PathManagerSettingsTest.e1 );
        when( scheduleManager.getPossiblePreviousEvent( PathManagerSettingsTest.e3.getStartingTime() ) ).thenReturn( PathManagerSettingsTest.e2 );
        when( scheduleManager.getPossiblePreviousEvent( PathManagerSettingsTest.e4.getStartingTime() ) ).thenReturn( PathManagerSettingsTest.e1 );
        when( scheduleManager.getPossibleFollowingEvent( PathManagerSettingsTest.e1.getStartingTime() ) ).thenReturn( PathManagerSettingsTest.e2 );
        when( scheduleManager.getPossibleFollowingEvent( PathManagerSettingsTest.e2.getStartingTime() ) ).thenReturn( PathManagerSettingsTest.e3 );
        when( scheduleManager.getPossibleFollowingEvent( PathManagerSettingsTest.e3.getStartingTime() ) ).thenReturn( null );
        when( scheduleManager.getPossibleFollowingEvent( PathManagerSettingsTest.e4.getStartingTime() ) ).thenReturn( PathManagerSettingsTest.e2 );
        when( preferenceManager.checkConstraints( any( Travel.class ), any( TypeOfEvent.class ) ) ).thenReturn( true );
        when( scheduleManager.getFeasiblePathCombinations( any( Event.class ), any( ArrayList.class ), any( ArrayList.class ) ) ).thenAnswer( new Answer < ArrayList < PathCombination > >() {
            @Override
            public ArrayList < PathCombination > answer ( InvocationOnMock invocation ) throws Throwable {
                ArrayList < Travel > prev = ( ArrayList < Travel > ) invocation.getArguments()[ 1 ];
                ArrayList < Travel > foll = ( ArrayList < Travel > ) invocation.getArguments()[ 2 ];
                ArrayList < PathCombination > combs = new ArrayList < PathCombination >();

                combs.add( new PathCombination( ( prev.size() > 0 ) ? prev.get( rdm.nextInt( prev.size() ) ) : null,
                        ( foll.size() > 0 ) ? foll.get( rdm.nextInt( foll.size() ) ) : null ) );
                return combs;
            }
        } );
        when( preferenceManager.findBestPath( any( ArrayList.class ), any( TypeOfEvent.class ) ) ).thenAnswer( new Answer < PathCombination >() {
            @Override
            public PathCombination answer ( InvocationOnMock invocation ) throws Throwable {
                ArrayList < PathCombination > combs = ( ArrayList < PathCombination > ) invocation.getArguments()[ 0 ];
                return combs.get( 0 );
            }
        } );

        if ( first && !second && third ) {
            centralEvent();
        } else if ( !first && second ) {
            PathCombination comb = PathManagerSettingsTest.getPathCombinationTest( pathManager, PathManagerSettingsTest.e1 );
            assertTrue( !comb.getFollPath().getEndingTime().isAfter( PathManagerSettingsTest.e2.getStartingTime() ) );
            assertTrue( comb.getPrevPath() == null || !comb.getPrevPath().getEndingTime().isAfter( PathManagerSettingsTest.e1.getStartingTime() ) );
            assertTrue( !comb.getFollPath().getStartingTime().isBefore( PathManagerSettingsTest.e1.getEndingTime() ) );
        } else if ( second && !third ) {
            PathCombination comb = PathManagerSettingsTest.getPathCombinationTest( pathManager, PathManagerSettingsTest.e3 );
            assertTrue( !comb.getPrevPath().getStartingTime().isBefore( PathManagerSettingsTest.e2.getEndingTime() ) );
            assertTrue( !comb.getPrevPath().getEndingTime().isAfter( PathManagerSettingsTest.e3.getStartingTime() ) );
            assertTrue( comb.getFollPath() == null || !comb.getFollPath().getStartingTime().isBefore( PathManagerSettingsTest.e3.getEndingTime() ) );
        } else if ( first && second && third ) {
            PathCombination comb = PathManagerSettingsTest.getPathCombinationTest( pathManager, PathManagerSettingsTest.e4 );
            assertTrue( !comb.getPrevPath().getStartingTime().isBefore( PathManagerSettingsTest.e1.getEndingTime() ) );
            assertTrue( !comb.getFollPath().getEndingTime().isAfter( PathManagerSettingsTest.e2.getStartingTime() ) );
            assertTrue( !comb.getPrevPath().getEndingTime().isAfter( PathManagerSettingsTest.e4.getStartingTime() ) );
            assertTrue( !comb.getFollPath().getStartingTime().isBefore( PathManagerSettingsTest.e4.getEndingTime() ) );
        }

    }

    private boolean areSameLocations ( Location l1, Location l2 ) {
        return l1.getLatitude() - l2.getLatitude() < 0.001 && l1.getLongitude() - l2.getLongitude() < 0.001;
    }

    private void centralEvent () throws GMapsGeneralException {
        PathCombination comb = PathManagerSettingsTest.getPathCombinationTest( pathManager, PathManagerSettingsTest.e2 );
        assertTrue( !comb.getPrevPath().getStartingTime().isBefore( PathManagerSettingsTest.e1.getEndingTime() ) );
        assertTrue( !comb.getFollPath().getEndingTime().isAfter( PathManagerSettingsTest.e3.getStartingTime() ) );
        assertTrue( !comb.getPrevPath().getEndingTime().isAfter( PathManagerSettingsTest.e2.getStartingTime() ) );
        assertTrue( !comb.getFollPath().getStartingTime().isBefore( PathManagerSettingsTest.e2.getEndingTime() ) );
        if ( PathManagerSettingsTest.e2.isPrevLocChoice() ) {
            assertTrue( areSameLocations( PathManagerSettingsTest.e1.getEventLocation(),
                    comb.getPrevPath().getMiniTravels().get( 0 ).getDeparture() ) );
            assertTrue( areSameLocations( PathManagerSettingsTest.e2.getEventLocation(),
                    comb.getPrevPath().getMiniTravels().get( comb.getPrevPath().getMiniTravels().size() - 1 ).getArrival() ) );
        } else {
            assertTrue( areSameLocations( PathManagerSettingsTest.e2.getDeparture(),
                    comb.getPrevPath().getMiniTravels().get( 0 ).getDeparture() ) );
            assertTrue( areSameLocations( PathManagerSettingsTest.e2.getEventLocation(),
                    comb.getPrevPath().getMiniTravels().get( comb.getPrevPath().getMiniTravels().size() - 1 ).getArrival() ) );
        }
        if ( PathManagerSettingsTest.e3.isPrevLocChoice() ) {
            assertTrue( areSameLocations( PathManagerSettingsTest.e2.getEventLocation(),
                    comb.getFollPath().getMiniTravels().get( 0 ).getDeparture() ) );
            assertTrue( areSameLocations( PathManagerSettingsTest.e3.getEventLocation(),
                    comb.getFollPath().getMiniTravels().get( comb.getFollPath().getMiniTravels().size() - 1 ).getArrival() ) );
        } else {
            assertTrue( areSameLocations( PathManagerSettingsTest.e3.getDeparture(),
                    comb.getFollPath().getMiniTravels().get( 0 ).getDeparture() ) );
            assertTrue( areSameLocations( PathManagerSettingsTest.e3.getEventLocation(),
                    comb.getFollPath().getMiniTravels().get( comb.getFollPath().getMiniTravels().size() - 1 ).getArrival() ) );
        }
    }


}
