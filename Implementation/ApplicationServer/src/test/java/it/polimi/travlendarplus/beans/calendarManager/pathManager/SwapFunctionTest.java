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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SwapFunctionTest {
    PathManager pathManager;
    ScheduleManager scheduleManager;
    PreferenceManager preferenceManager;
    Random rdm = new Random();

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
        injector.inject( this.pathManager );

        PathManagerSettingsTest.baseSet();
    }

    @Test
    public void swapEventsTest () {
        //2018/01/20 h:14:30 - 15:30
        Event eventToAdd = PathManagerSettingsTest.setEvent(6, 1516458600, 1516462200, true, false,
                        PathManagerSettingsTest.abbadia, PathManagerSettingsTest.maggianico, PathManagerSettingsTest.toe1 );

        when(preferenceManager.checkConstraints( any(Travel.class), any(TypeOfEvent.class) )).thenReturn( true );
        when(scheduleManager.getPossibleFollowingEvent( any(Instant.class) )).thenAnswer (new Answer<Event> () {
           @Override
           public Event answer (InvocationOnMock invocation) throws Throwable {
               return PathManagerSettingsTest.e4sw;
           }
        });
        when(scheduleManager.getSchedule()).thenAnswer( new Answer<ScheduleHolder> () {
            @Override
            public ScheduleHolder answer( InvocationOnMock invocation) throws Throwable {
                return PathManagerSettingsTest.createScheduleHolder(true, true, true, true, true,
                                true, true);
            }
        }).thenAnswer( new Answer<ScheduleHolder> () {
            @Override
            public ScheduleHolder answer( InvocationOnMock invocation) throws Throwable {
                return PathManagerSettingsTest.createScheduleHolder(true, true, false, true, true,
                                true, true);
            }
        } );
        when(scheduleManager.getFeasiblePathCombinations( any(Event.class), any(ArrayList.class), any(ArrayList.class) )).thenAnswer( new Answer<ArrayList<PathCombination>>() {
            @Override
            public ArrayList<PathCombination> answer (InvocationOnMock invocation) throws Throwable {
                ArrayList<Travel> prev = (ArrayList<Travel> ) invocation.getArguments()[1];
                ArrayList<Travel> foll = (ArrayList<Travel> ) invocation.getArguments()[2];
                ArrayList<PathCombination> combs = new ArrayList <PathCombination>(  );

                combs.add(new PathCombination( (prev.size() > 0) ? prev.get( rdm.nextInt( prev.size() ) ) : null,
                                (foll.size() > 0) ? foll.get( rdm.nextInt( foll.size() ) ) : null ));
                return combs;
            }
        } );
        when(scheduleManager.areEventsOverlapFree( any(Event.class), any(Event.class) )).thenAnswer( new Answer<Object>() {
            @Override
            public Object answer (InvocationOnMock invocation) throws Throwable {
                Event event1 = (Event ) invocation.getArguments()[0];
                Event event2 = (Event ) invocation.getArguments()[1];
                return(!(event1.getId() == eventToAdd.getId() && event2.getId() == 3));
            }
        } );
        when(preferenceManager.findBestPath( anyList(), any(TypeOfEvent.class) )).thenAnswer( new Answer<PathCombination>() {
           @Override
           public PathCombination answer (InvocationOnMock invocation) throws Throwable {
               ArrayList<PathCombination> combs = (ArrayList< PathCombination>)invocation.getArguments()[0];
               return combs.get( 0 );
            }
        });

        List<TravelMeanEnum> privateMeans = new ArrayList < TravelMeanEnum >();
        List<TravelMeanEnum> publicMeans = new ArrayList < TravelMeanEnum >();
        publicMeans.add( TravelMeanEnum.BUS );
        privateMeans.add( TravelMeanEnum.CAR );

        List<GenericEvent> res = pathManager.swapEvents( eventToAdd, privateMeans, publicMeans);
        assertEquals(3, res.size());
        assertEquals( false, res.get( 0 ).isScheduled() );
        assertEquals( true, res.get( 1 ).isScheduled() );
        assertEquals( true, res.get( 2 ).isScheduled() );
        assertEquals( 3, res.get(0).getId() );
        assertEquals( 6, res.get( 1 ).getId() );
        assertEquals( 4, res.get(2).getId() );
    }
}