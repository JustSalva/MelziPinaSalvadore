package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.beans.calendarManager.support.PathCombination;
import it.polimi.travlendarplus.beans.calendarManager.support.ScheduleHolder;
import it.polimi.travlendarplus.entities.calendar.BreakEvent;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.calendar.GenericEvent;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import javax.ejb.Stateless;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ScheduleManager extends UserManager {

    private final static float MAX_WALKING_LENGTH = 2.5f;

    public final static long SECONDS_IN_A_DAY = 24 * 60 * 60;

    private ScheduleHolder schedule;

    public ScheduleHolder getSchedule () {
        return schedule;
    }

    // day is a long corresponding to the unix time at 12:00 of the day wanted.
    public ScheduleHolder getScheduleByDay ( long day ) {
        setSchedule( Instant.ofEpochSecond( day ), SECONDS_IN_A_DAY / 2 );
        return schedule;
    }

    /* This function is called before the calculation of possible paths for an event that could be added.
     Paths are not considered in this function because the addition of an event will cause
     the recalculation of involved paths.*/
    public boolean isEventOverlapFreeIntoSchedule ( Event event, boolean forSwap ) {
        if ( !forSwap ) {
            setSchedule( event.getStartingTime(), SECONDS_IN_A_DAY );
        }
        // Checking if the event is overlapping free with regard of scheduled events.
        for ( Event scheduledEvent : schedule.getEvents() ) {
            if ( !( areEventsOverlapFree( event, scheduledEvent ) ) ) {
                return false;
            }
        }
        // Checking if existing breaks will be still feasible with the addition of the new event.
        for ( BreakEvent scheduledBreak : schedule.getBreaks() ) {
            if ( !scheduledBreak.isMinimumEnsuredNoPathRegard( getEventsIntoInterval(
                    schedule.getListWithNewEvent( event ), scheduledBreak ) ) ) {
                return false;
            }
        }
        return true;
    }

    // This function is called when a breakEvent could be added into the schedule.
    public boolean isBreakOverlapFreeIntoSchedule ( BreakEvent breakEvent, boolean forSwap ) {
        if ( !forSwap ) {
            setSchedule( breakEvent.getStartingTime(), SECONDS_IN_A_DAY );
        }
        // In the system is not allowed the overlapping of two break events.
        for ( BreakEvent scheduledBreak : schedule.getBreaks() ) {
            if ( !areEventsOverlapFree( breakEvent, scheduledBreak ) ) {
                return false;
            }
        }
        // Events and paths are taken into account in order to check if the minimum duration of a breakEvent can be ensured.
        List < Event > involvedEvents = getEventsIntoIntervalWithPathRegard( schedule.getEvents(), breakEvent );
        return breakEvent.isMinimumEnsuredWithPathRegard( involvedEvents );
    }

    // delta parameter represents an interval in seconds used to filter the user's events.
    // This function is called in order to calculate the schedule of a certain period of time.
    public void setSchedule ( Instant centralTime, long delta ) {
        ArrayList < Event > events = new ArrayList < Event >();
        ArrayList < BreakEvent > breaks = new ArrayList < BreakEvent >();
        Instant startingTime = Instant.ofEpochSecond( centralTime.getEpochSecond() - delta );
        Instant endingTime = Instant.ofEpochSecond( centralTime.getEpochSecond() + delta );
        // Obtaining events into the schedule of the specified day.
        for ( Event event : getCurrentUser().getEvents() ) {
            if ( event.isScheduled() && !event.getStartingTime().isBefore( startingTime ) &&
                    !event.getStartingTime().isAfter( endingTime ) ) {
                events.add( event );
            }
        }
        Collections.sort( events );
        // Obtaining breaks into the schedule of the specified day.
        for ( BreakEvent breakEvent : getCurrentUser().getBreaks() ) {
            if ( breakEvent.isScheduled() && !breakEvent.getStartingTime().isBefore( startingTime ) &&
                    !breakEvent.getStartingTime().isAfter( endingTime ) ) {
                breaks.add( breakEvent );
            }
        }
        Collections.sort( breaks );
        // The schedule is stored and can be used for next operations.
        schedule = new ScheduleHolder( events, breaks );
    }

    // It returns null if the event would be the first in the schedule of that day.
    public Event getPossiblePreviousEvent ( Instant startingTime ) {
        long visitedLimit = startingTime.getEpochSecond() - SECONDS_IN_A_DAY;
        for ( int i = 0; i < schedule.getEvents().size(); i++ ) {
            if ( startingTime.isBefore( schedule.getEvents().get( i ).getStartingTime() ) ) {
                return i == 0 ? getFurtherPreviousEvent( Instant.ofEpochSecond( visitedLimit ) )
                        : schedule.getEvents().get( i - 1 );
            }
        }
        return ( schedule.getEvents().isEmpty() ) ? getFurtherPreviousEvent( Instant.ofEpochSecond( visitedLimit ) ) :
                schedule.getEvents().get( schedule.getEvents().size() - 1 );
    }

    private Event getFurtherPreviousEvent ( Instant visitedLimit ) {
        ArrayList < Event > events = new ArrayList < Event >();
        for ( Event event : currentUser.getEvents() ) {
            if ( event.isScheduled() && event.getStartingTime().isBefore( visitedLimit ) ) {
                events.add( event );
            }
        }
        Collections.sort( events );
        return ( !events.isEmpty() ) ? events.get( events.size() - 1 ) : null;
    }

    // It returns null if the event would be the last in the schedule of that day.
    public Event getPossibleFollowingEvent ( Instant startingTime ) {
        long visitedLimit = startingTime.getEpochSecond() + SECONDS_IN_A_DAY;
        for ( int i = 0; i < schedule.getEvents().size(); i++ ) {
            if ( startingTime.isBefore( schedule.getEvents().get( i ).getStartingTime() ) ) {
                return schedule.getEvents().get( i );
            }
        }
        return getFurtherFollowingEvent( Instant.ofEpochSecond( visitedLimit ) );
    }

    private Event getFurtherFollowingEvent ( Instant visitedLimit ) {
        ArrayList < Event > events = new ArrayList < Event >();
        for ( Event event : currentUser.getEvents() ) {
            if ( event.isScheduled() && event.getStartingTime().isAfter( visitedLimit ) ) {
                events.add( event );
            }
        }
        Collections.sort( events );
        return ( !events.isEmpty() ) ? events.get( 0 ) : null;
    }

    // It returns true if the two events are not overlapping.
    public boolean areEventsOverlapFree ( GenericEvent event, GenericEvent scheduledEvent ) {
        return !event.getStartingTime().isBefore( scheduledEvent.getEndingTime() ) ||
                !event.getEndingTime().isAfter( scheduledEvent.getStartingTime() );
    }

    public long overlappingTime ( GenericEvent event1, GenericEvent event2 ) {
        if ( areEventsOverlapFree( event1, event2 ) ) {
            return 0;
        }
        return Math.min( event1.getEndingTime().getEpochSecond(), event2.getEndingTime().getEpochSecond() ) -
                Math.max( event2.getStartingTime().getEpochSecond(), event1.getStartingTime().getEpochSecond() );
    }

    public long overlappingEventWithBreak ( Event event, BreakEvent breakEv ) {
        long ovEventTime = overlappingTime( event, breakEv );
        return ovEventTime + ( ( event.getFeasiblePath().getStartingTime().isBefore( breakEv.getEndingTime() ) &&
                event.getFeasiblePath().getEndingTime().isAfter( breakEv.getStartingTime() ) )
                ? Math.min( event.getFeasiblePath().getEndingTime().getEpochSecond(), breakEv.getEndingTime().
                getEpochSecond() ) - Math.max( breakEv.getStartingTime().getEpochSecond(),
                event.getStartingTime().getEpochSecond() )
                : 0 );

    }

    // It returns an ArrayList of events into "events" that are overlapping with "intervalEvent" .
    // Possible paths are not taken into account.
    private List < Event > getEventsIntoInterval ( List < Event > events, GenericEvent intervalEvent ) {
        List < Event > involvedEvents = new ArrayList < Event >();
        for ( Event event : events ) {
            if ( event.getEndingTime().isAfter( intervalEvent.getStartingTime() ) &&
                    event.getStartingTime().isBefore( intervalEvent.getEndingTime() ) ) {
                involvedEvents.add( event );
            }
        }
        return involvedEvents;
    }

    // It returns an ArrayList of events into "events" that are overlapping with "intervalEvent" .
    // It considers also the event-related paths. The ArrayList passed as a param must be ordered.
    public List < Event > getEventsIntoIntervalWithPathRegard ( List < Event > events, GenericEvent intervalEvent ) {
        List < Event > involvedEvents = new ArrayList < Event >();
        // Event-related paths are taken into account in order to check if there is an overlap.
        for ( Event event : events ) {
            if ( event.getEndingTime().isAfter( intervalEvent.getStartingTime() ) &&
                    event.getFeasiblePath().getStartingTime().isBefore( intervalEvent.getEndingTime() ) ) {
                involvedEvents.add( event );
            }
        }
        return involvedEvents;
    }

    // This function is called after that all feasible previous and following paths are calculated.
    // The two ArrayList passed as params contain possible travels that don't overlap with prev/foll events.
    // It determines if a combination of prev and foll path is feasible according to the scheduled breaks.
    public List < PathCombination > getFeasiblePathCombinations ( Event event, List < Travel > previous,
                                                                  List < Travel > following ) {
        List < PathCombination > feasibleComb = new ArrayList < PathCombination >();
        if ( getPossibleFollowingEvent( event.getStartingTime() ) == null ) {
            return getFeasiblePathCombinationsLastEventCase( event, previous );
        }
        for ( Travel prev : previous ) {
            for ( Travel foll : following ) {
                boolean combinationFeasible = true;
                // Analyzing for each combination of prev/foll paths if it would be feasible with each scheduled break.
                for ( BreakEvent breakEvent : schedule.getBreaks() ) {
                    List < Event > simulList = schedule.getListWithNewEventPaths( prev, foll, event );
                    List < Event > simulInvolved = getEventsIntoIntervalWithPathRegard( simulList, breakEvent );
                    // simulInvolved contains events that happen in breakEvent interval.
                    if ( !breakEvent.isMinimumEnsuredWithPathRegard( simulInvolved ) ) {
                        combinationFeasible = false;
                    }
                }
                if ( combinationFeasible ) {
                    feasibleComb.add( new PathCombination( prev, foll ) );
                }
            }
        }
        // Deleting long walking path...if there are alternatives
        feasibleComb = removeLongWalkingPath( feasibleComb );
        return feasibleComb;
    }

    // It is used when the event to be added into the schedule would be the last: it doesn't have a following path.
    private List < PathCombination > getFeasiblePathCombinationsLastEventCase ( Event event, List < Travel > previous ) {
        List < PathCombination > feasibleComb = new ArrayList < PathCombination >();
        for ( Travel prev : previous ) {
            boolean combinationFeasible = true;
            // Analyzing for each prev path if it would be feasible with each scheduled break.
            for ( BreakEvent breakEvent : schedule.getBreaks() ) {
                List < Event > simulList = schedule.getListWithNewEventPaths( prev, null, event );
                List < Event > simulInvolved = getEventsIntoIntervalWithPathRegard( simulList, breakEvent );
                if ( !breakEvent.isMinimumEnsuredWithPathRegard( simulInvolved ) ) {
                    combinationFeasible = false;
                }
            }
            if ( combinationFeasible ) {
                feasibleComb.add( new PathCombination( prev, null ) );
            }
        }

        return feasibleComb;
    }

    public void saveForSwap ( List < GenericEvent > eventsToSave ) {
        for ( GenericEvent e : eventsToSave ) {
            e.save();
        }
        currentUser.save();
    }

    private List < PathCombination > removeLongWalkingPath ( List < PathCombination > possiblePaths ) {
        ArrayList < PathCombination > noWalkingLong = new ArrayList < PathCombination >();
        for ( PathCombination comb : possiblePaths ) {
            boolean walkingLong = false;
            for ( TravelComponent travComp : comb.getPrevPath().getMiniTravels() ) {
                if ( travComp.getMeanUsed().getType().getParam().equals( "walking" ) &&
                        travComp.getLength() > MAX_WALKING_LENGTH ) {
                    walkingLong = true;
                }
            }
            for ( TravelComponent travComp : comb.getFollPath().getMiniTravels() ) {
                if ( travComp.getMeanUsed().getType().getParam().equals( "walking" ) &&
                        travComp.getLength() > MAX_WALKING_LENGTH ) {
                    walkingLong = true;
                }
            }
            if ( !walkingLong ) {
                noWalkingLong.add( comb );
            }
        }
        return ( !noWalkingLong.isEmpty() ) ? noWalkingLong : possiblePaths;
    }

}
