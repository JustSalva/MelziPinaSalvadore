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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class SwapFunctionTest {
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
        doNothing().when( scheduleManager ).saveForSwap( any( ArrayList.class ) );

    }

    @Test
    public void swapEventsTestBaseCase () throws GMapsGeneralException {
        //2018/01/20 h:14:30 - 15:30
        eventToAdd = PathManagerSettingsTest.setEvent( 6, 1516458600, 1516462200, true, false,
                PathManagerSettingsTest.abbadia, PathManagerSettingsTest.maggianico, PathManagerSettingsTest.toe1 );
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
                        false, true, true, true, true );
            }
        } );
        when( scheduleManager.areEventsOverlapFree( any( Event.class ), any( Event.class ) ) ).thenAnswer( new Answer < Object >() {
            @Override
            public Object answer ( InvocationOnMock invocation ) throws Throwable {
                Event event1 = ( Event ) invocation.getArguments()[ 0 ];
                Event event2 = ( Event ) invocation.getArguments()[ 1 ];
                return ( !( event1.getId() == eventToAdd.getId() && event2.getId() == 3 ) );
            }
        } );

        /* I expect:
            - 3rd event in the schedule swapped out due to overlapping
            - event to add inserted
            - 4th event in the schedule with different path
        */
        List < GenericEvent > res = pathManager.swapEvents( eventToAdd, privateMeans, publicMeans );
        assertEquals( 3, res.size() );
        assertEquals( false, res.get( 0 ).isScheduled() );
        assertEquals( true, res.get( 1 ).isScheduled() );
        assertEquals( true, res.get( 2 ).isScheduled() );
        assertEquals( 3, res.get( 0 ).getId() );
        assertEquals( 6, res.get( 1 ).getId() );
        assertEquals( 4, res.get( 2 ).getId() );
    }

    @Test
    public void swapEventsTestRemovePrev () throws GMapsGeneralException {
        //2018/01/20 h:13:00 - 14:00
        eventToAdd = PathManagerSettingsTest.setEvent( 6, 1516453200, 1516456800, true, false,
                PathManagerSettingsTest.abbadia, PathManagerSettingsTest.maggianico, PathManagerSettingsTest.toe1 );
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
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, false,
                        true, true, true, true, true );
            }
        } );
        when( scheduleManager.areEventsOverlapFree( any( Event.class ), any( Event.class ) ) ).thenReturn( true );

        /* I expect:
            - 2nd event in the schedule swapped out due to infeasibility of previous travel calculated
            - event to add inserted
            - 3rd event in the schedule with different path
        */
        List < GenericEvent > res = pathManager.swapEvents( eventToAdd, privateMeans, publicMeans );
        assertEquals( 3, res.size() );
        assertEquals( false, res.get( 0 ).isScheduled() );
        assertEquals( true, res.get( 1 ).isScheduled() );
        assertEquals( true, res.get( 2 ).isScheduled() );
        assertEquals( 2, res.get( 0 ).getId() );
        assertEquals( 6, res.get( 1 ).getId() );
        assertEquals( 3, res.get( 2 ).getId() );
    }

    @Test
    public void swapEventsTestRemoveFoll () throws GMapsGeneralException {
        //2018/01/20 h:17:00 - 17:59
        eventToAdd = PathManagerSettingsTest.setEvent( 6, 1516467600, 1516471140, true, false,
                PathManagerSettingsTest.abbadia, PathManagerSettingsTest.maggianico, PathManagerSettingsTest.toe1 );
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
                        true, false, true, true, true );
            }
        } );
        when( scheduleManager.areEventsOverlapFree( any( Event.class ), any( Event.class ) ) ).thenReturn( true );

        /* I expect:
            - 4th event in the schedule swapped out due to infeasibility of following travel calculated
            - event to add inserted
            - 5th event in the schedule with different path
        */
        List < GenericEvent > res = pathManager.swapEvents( eventToAdd, privateMeans, publicMeans );
        assertEquals( 3, res.size() );
        assertEquals( false, res.get( 0 ).isScheduled() );
        assertEquals( true, res.get( 1 ).isScheduled() );
        assertEquals( true, res.get( 2 ).isScheduled() );
        assertEquals( 4, res.get( 0 ).getId() );
        assertEquals( 6, res.get( 1 ).getId() );
        assertEquals( 5, res.get( 2 ).getId() );
    }

    @Test
    public void swapEventsTestInsertFirstEvent () throws GMapsGeneralException {
        //2018/01/20 h:9:00 - 10:00
        eventToAdd = PathManagerSettingsTest.setEvent( 6, 1516438800, 1516442400, true, false,
                PathManagerSettingsTest.abbadia, PathManagerSettingsTest.maggianico, PathManagerSettingsTest.toe1 );
        when( scheduleManager.getSchedule() ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( true, true,
                        true, true, true, true, true );
            }
        } ).thenAnswer( new Answer < ScheduleHolder >() {
            @Override
            public ScheduleHolder answer ( InvocationOnMock invocation ) throws Throwable {
                return simulatedSchedule = PathManagerSettingsTest.createScheduleHolder( false, true,
                        true, true, true, true, true );
            }
        } );
        when( scheduleManager.areEventsOverlapFree( any( Event.class ), any( Event.class ) ) ).thenAnswer( new Answer < Object >() {
            @Override
            public Object answer ( InvocationOnMock invocation ) throws Throwable {
                Event event1 = ( Event ) invocation.getArguments()[ 0 ];
                Event event2 = ( Event ) invocation.getArguments()[ 1 ];
                return ( !( event1.getId() == eventToAdd.getId() && event2.getId() == 1 ) );
            }
        } );

        /* I expect:
            - 1st event in the schedule swapped out due to overapping
            - event to add inserted
            - 2nd event in the schedule with different path
        */
        List < GenericEvent > res = pathManager.swapEvents( eventToAdd, privateMeans, publicMeans );
        assertEquals( 3, res.size() );
        assertEquals( false, res.get( 0 ).isScheduled() );
        assertEquals( true, res.get( 1 ).isScheduled() );
        assertEquals( true, res.get( 2 ).isScheduled() );
        assertEquals( 1, res.get( 0 ).getId() );
        assertEquals( 6, res.get( 1 ).getId() );
        assertEquals( 2, res.get( 2 ).getId() );
        // Checking that first travel is between the same location.
        assertTrue( ( ( Event ) res.get( 1 ) ).getFeasiblePath().getTotalLength() < 1 );
    }

    @Test
    public void swapEventsTestInsertLastEvent () throws GMapsGeneralException {
        //2018/01/20 h:21:00 - 21:30
        eventToAdd = PathManagerSettingsTest.setEvent( 6, 1516482000, 1516483800, true, false,
                PathManagerSettingsTest.abbadia, PathManagerSettingsTest.maggianico, PathManagerSettingsTest.toe1 );
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
                        true, true, false, true, true );
            }
        } );
        when( scheduleManager.areEventsOverlapFree( any( Event.class ), any( Event.class ) ) ).thenAnswer( new Answer < Object >() {
            @Override
            public Object answer ( InvocationOnMock invocation ) throws Throwable {
                Event event1 = ( Event ) invocation.getArguments()[ 0 ];
                Event event2 = ( Event ) invocation.getArguments()[ 1 ];
                return ( !( event1.getId() == eventToAdd.getId() && event2.getId() == 5 ) );
            }
        } );

        /* I expect:
            - 5th event in the schedule swapped out due to overapping
            - event to add inserted
        */
        List < GenericEvent > res = pathManager.swapEvents( eventToAdd, privateMeans, publicMeans );
        assertEquals( 2, res.size() );
        assertEquals( false, res.get( 0 ).isScheduled() );
        assertEquals( true, res.get( 1 ).isScheduled() );
        assertEquals( 5, res.get( 0 ).getId() );
        assertEquals( 6, res.get( 1 ).getId() );
    }
}