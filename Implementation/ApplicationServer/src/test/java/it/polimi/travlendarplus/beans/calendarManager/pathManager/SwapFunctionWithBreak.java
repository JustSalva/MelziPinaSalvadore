package it.polimi.travlendarplus.beans.calendarManager.pathManager;

import it.polimi.travlendarplus.EJBTestInjector;
import it.polimi.travlendarplus.beans.calendarManager.PathManager;
import it.polimi.travlendarplus.beans.calendarManager.PreferenceManager;
import it.polimi.travlendarplus.beans.calendarManager.ScheduleManager;
import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.beans.calendarManager.support.ScheduleHolder;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.preferences.TypeOfEvent;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsGeneralException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class SwapFunctionWithBreak {
    PathManager pathManager;
    ScheduleManager scheduleManager;
    PreferenceManager preferenceManager;
    Random rdm = new Random();
    List < TravelMeanEnum > privateMeans;
    List < TravelMeanEnum > publicMeans;
    Event eventToAdd;
    ScheduleHolder simulatedSchedule;

    @Before
    public void init () throws Exception {
        this.pathManager = new PathManager();

        // create mocks
        this.scheduleManager = mock( ScheduleManager.class );
        this.preferenceManager = mock( PreferenceManager.class );
        // inject
        final EJBTestInjector injector = new EJBTestInjector();
        injector.assign( ScheduleManager.class, scheduleManager );
        injector.assign( PreferenceManager.class, preferenceManager );
        injector.inject( pathManager );

        PathManagerSettingsTest.baseSet();
        privateMeans = new ArrayList < TravelMeanEnum >();
        publicMeans = new ArrayList < TravelMeanEnum >();
        publicMeans.add( TravelMeanEnum.BUS );
        privateMeans.add( TravelMeanEnum.CAR );

        when( preferenceManager.checkConstraints( any( Travel.class ), any( TypeOfEvent.class ) ) ).thenReturn( true );
        when( scheduleManager.getPossibleFollowingEvent( any( Instant.class ) ) ).thenAnswer( new Answer < Event >() {
            @Override
            public Event answer ( InvocationOnMock invocation ) throws Throwable {
                Instant startingTime = ( Instant ) invocation.getArguments()[ 0 ];
                for ( int i = 0; i < simulatedSchedule.getEvents().size(); i++ )
                    if ( startingTime.isBefore( simulatedSchedule.getEvents().get( i ).getStartingTime() ) )
                        return simulatedSchedule.getEvents().get( i );
                return null;
            }
        } );
        when( scheduleManager.getPossiblePreviousEvent( any( Instant.class ) ) ).thenAnswer( new Answer < Event >() {
            @Override
            public Event answer ( InvocationOnMock invocation ) throws Throwable {
                Instant startingTime = ( Instant ) invocation.getArguments()[ 0 ];
                for ( int i = 0; i < simulatedSchedule.getEvents().size(); i++ )
                    if ( startingTime.isBefore( simulatedSchedule.getEvents().get( i ).getStartingTime() ) )
                        return i == 0 ? null : simulatedSchedule.getEvents().get( i - 1 );
                return simulatedSchedule.getEvents().get( simulatedSchedule.getEvents().size() - 1 );
            }
        } );
        when( preferenceManager.findBestPath( anyList(), any( TypeOfEvent.class ) ) ).thenAnswer( new Answer < PathCombination >() {
            @Override
            public PathCombination answer ( InvocationOnMock invocation ) throws Throwable {
                ArrayList < PathCombination > combs = ( ArrayList < PathCombination > ) invocation.getArguments()[ 0 ];
                return ( combs != null ) ? combs.get( 0 ) : null;
            }
        } );
        doNothing().when( scheduleManager ).saveForSwap( any( ArrayList.class ) );
        when( scheduleManager.areEventsOverlapFree( any( Event.class ), any( Event.class ) ) ).thenReturn( true );
    }

    @Test
    public void swapEventWithFeasibleBreak () throws GMapsGeneralException {
        //2018/01/20 h:16:30 - 17:00
        eventToAdd = PathManagerSettingsTest.setEvent( 6, 1516465800, 1516467600, true, false,
                PathManagerSettingsTest.abbadia, PathManagerSettingsTest.mandello, PathManagerSettingsTest.toe1 );
        when( scheduleManager.getFeasiblePathCombinations( any( Event.class ), any( ArrayList.class ),
                any( ArrayList.class ) ) ).thenAnswer( new Answer < ArrayList < PathCombination > >() {
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
        when( scheduleManager.getSchedule() ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } );

        /* I expect:
            - event to add inserted
            - event with ID = 4 with different path
        */
        List < GenericEvent > res = pathManager.swapEvents( eventToAdd, privateMeans, publicMeans );
        assertEquals( 2, res.size() );
        assertEquals( true, res.get( 0 ).isScheduled() );
        assertEquals( true, res.get( 1 ).isScheduled() );
        assertEquals( 6, res.get( 0 ).getId() );
        assertEquals( 4, res.get( 1 ).getId() );
    }

    @Test
    public void swapEventNoFeasibleBreak () throws GMapsGeneralException {
        //2018/01/20 h:16:30 - 17:30
        eventToAdd = PathManagerSettingsTest.setEvent( 6, 1516465800, 1516469400, true, false,
                PathManagerSettingsTest.abbadia, PathManagerSettingsTest.mandello, PathManagerSettingsTest.toe1 );
        when( scheduleManager.getFeasiblePathCombinations( any( Event.class ), any( ArrayList.class ),
                any( ArrayList.class ) ) ).thenAnswer( new Answer < ArrayList < PathCombination > >() {
            @Override
            public ArrayList < PathCombination > answer ( InvocationOnMock invocation ) throws Throwable {
                return new ArrayList < PathCombination >();
            }
        } ).thenAnswer( new Answer < ArrayList < PathCombination > >() {
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
        when( scheduleManager.getSchedule() ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, false, true );
            }
        } );

        /* I expect:
            - break event with ID = 10 swapped out
            - event to add inserted
            - event with ID = 4 with different path
        */
        List < GenericEvent > res = pathManager.swapEvents( eventToAdd, privateMeans, publicMeans );
        assertEquals( 3, res.size() );
        assertEquals( false, res.get( 0 ).isScheduled() );
        assertEquals( true, res.get( 1 ).isScheduled() );
        assertEquals( true, res.get( 2 ).isScheduled() );
        assertEquals( 10, res.get( 0 ).getId() );
        assertEquals( 6, res.get( 1 ).getId() );
        assertEquals( 4, res.get( 2 ).getId() );
    }
}
