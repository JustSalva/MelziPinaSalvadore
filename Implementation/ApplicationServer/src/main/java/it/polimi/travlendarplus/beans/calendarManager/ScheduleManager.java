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

    /**
     * Calculates the schedule of a specified day.
     *
     * @param day unix time at 12:00 of the day in which the schedule is required.
     * @return the schedule with events and breaks related to the specified day.
     */
    public ScheduleHolder getScheduleByDay ( long day ) {
        setSchedule( Instant.ofEpochSecond( day ), SECONDS_IN_A_DAY / 2 );
        return schedule;
    }

    /**
     * States if an event has any kind of overlapping into the schedule. This function is called before the calculation
     * of possible paths for an event that could be added. Paths are not considered in this function because the
     * addition of an event will cause the recalculation of involved paths.
     *
     * @param event   event to be added.
     * @param forSwap indicates if the event is an existing and overlapping event that must be swapped into the schedule.
     * @return true if the event has no overlapping with other events and its adding does not cause the impossibility
     * to ensure minimum time for scheduled breaks; false otherwise.
     */
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

    /**
     * States if, in the actual schedule, the minimum amount of time can be ensured for a break event and if the break
     * event has overlapping with other breaks. It is called when a breakEvent could be added into the schedule.
     *
     * @param breakEvent break to be added.
     * @param forSwap    indicates if the break is an existing and overlapping event that must be swapped into the
     *                   schedule.
     * @return true if the break has no overlapping with other breaks and the schedule ensure the minimum amount of time
     * for the break, false otherwise.
     */
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
        // Events and paths are taken into account in order to check if the minimum duration of a breakEvent can be
        // ensured.
        List < Event > involvedEvents = getEventsIntoIntervalWithPathRegard( schedule.getEvents(), breakEvent );
        return breakEvent.isMinimumEnsuredWithPathRegard( involvedEvents );
    }


    /**
     * Calculates the schedule of a certain period of time.
     *
     * @param centralTime the middle time of the period in which the schedule is calculated.
     * @param delta       interval in seconds used to filter the user's events that must be inserted into the schedule.
     */
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


    /**
     * Calculates the previous event to a specified event.
     *
     * @param startingTime starting time of the event of which the previous event is required.
     * @return the previous event if it exists in the considered schedule, otherwise the function calls
     * getFurtherPreviousEvent with the limit of time not yet considered passed as a parameter.
     */
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


    /**
     * Calculates the previous event to a specified event if getPossiblePreviousEvent is not able to obtain it.
     *
     * @param visitedLimit the upper limit of time in which the previous event has already been searched.
     * @return the previous event if it exists, null otherwise.
     */
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

    /**
     * Calculates the following event to a specified event.
     *
     * @param startingTime starting time of the event of which the following event is required.
     * @return the following event if it exists in the considered schedule, otherwise the function calls
     * getFurtherFollowingEvent with the limit of time not yet considered passed as a parameter.
     */
    public Event getPossibleFollowingEvent ( Instant startingTime ) {
        long visitedLimit = startingTime.getEpochSecond() + SECONDS_IN_A_DAY;
        for ( int i = 0; i < schedule.getEvents().size(); i++ ) {
            if ( startingTime.isBefore( schedule.getEvents().get( i ).getStartingTime() ) ) {
                return schedule.getEvents().get( i );
            }
        }
        return getFurtherFollowingEvent( Instant.ofEpochSecond( visitedLimit ) );
    }

    /**
     * Calculates the following event to a specified event if getPossibleFollowingEvent is not able to obtain it.
     *
     * @param visitedLimit the lower limit of time in which the following event has already been searched.
     * @return the following event if it exists, null otherwise.
     */
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


    /**
     * States if two events are overlapping free without considering related-paths.
     *
     * @param event          one of the two events to compare, the not scheduled one.
     * @param scheduledEvent one of the two events to compare, the scheduled one.
     * @return true if the two events are not overlapping, false otherwise.
     */
    public boolean areEventsOverlapFree ( GenericEvent event, GenericEvent scheduledEvent ) {
        return !event.getStartingTime().isBefore( scheduledEvent.getEndingTime() ) ||
                !event.getEndingTime().isAfter( scheduledEvent.getStartingTime() );
    }

    /**
     * Calculates the amount of overlapping time between two events, without considering the related-paths.
     *
     * @param event1 one of the two events.
     * @param event2 one of the two events.
     * @return the amount of time in which the two events are overlapping. 0 if they are not overlapping.
     */
    public long overlappingTime ( GenericEvent event1, GenericEvent event2 ) {
        if ( areEventsOverlapFree( event1, event2 ) ) {
            return 0;
        }
        return Math.min( event1.getEndingTime().getEpochSecond(), event2.getEndingTime().getEpochSecond() ) -
                Math.max( event2.getStartingTime().getEpochSecond(), event1.getStartingTime().getEpochSecond() );
    }

    /**
     * Calculates the amount of overlapping time between an event and a break event, eventually considering the path
     * related to the event.
     *
     * @param event   event of which calculate overlapping time.
     * @param breakEv break of which calculate overlapping time.
     * @return the sum of the overlapping time between the two events and the overlapping time between the path and the
     * break event.
     */
    public long overlappingEventWithBreak ( Event event, BreakEvent breakEv ) {
        long ovEventTime = overlappingTime( event, breakEv );
        return ovEventTime + ( ( event.getFeasiblePath().getStartingTime().isBefore( breakEv.getEndingTime() ) &&
                event.getFeasiblePath().getEndingTime().isAfter( breakEv.getStartingTime() ) )
                ? Math.min( event.getFeasiblePath().getEndingTime().getEpochSecond(), breakEv.getEndingTime().
                getEpochSecond() ) - Math.max( breakEv.getStartingTime().getEpochSecond(),
                event.getStartingTime().getEpochSecond() )
                : 0 );

    }

    /**
     * Calculates which events, from a given list, are in the interval of a generic event, without considering paths.
     *
     * @param events        list of events of which is required to check if they overlap with a specified interval.
     * @param intervalEvent event of which the interval is considered.
     * @return an ArrayList of events into that are overlapping with "intervalEvent" .
     */
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

    /**
     * Calculates which events, from a given list, are in the interval of a generic event, considering also the paths.
     *
     * @param events        list of events of which is required to check if they overlap with a specified interval.
     * @param intervalEvent event of which the interval is considered.
     * @return an ArrayList of events into that are overlapping with "intervalEvent" .
     */
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

    /**
     * Defines, for each possible combination of previous-and-following path of an event, if it can be ensure the
     * minimum amount of time for each scheduled break. This function is called after that all feasible previous and
     * following paths are calculated.
     *
     * @param event     the event of which the paths are related.
     * @param previous  possible travels that don't overlap with previous event and respect user's preferences.
     * @param following possible travels that don't overlap with following event and respect user's preferences.
     * @return all feasible combinations, according to the scheduled break events.
     */
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

    /**
     * Defines, for each possible previous path of an event, if it can be ensure the minimum amount of time for each
     * scheduled break. This function is called after that all feasible previous  paths are calculated, when an event is
     * the last of the schedule.
     *
     * @param event    the event of which the paths are related.
     * @param previous possible travels that don't overlap with previous event and respect user's preferences.
     * @return all feasible combinations, where the following path is null, according to the scheduled break events.
     */
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

    /**
     * Saves on server database all modified events passed as parameter.
     *
     * @param eventsToSave list of modified events that need to be saved.
     */
    public void saveForSwap ( List < GenericEvent > eventsToSave ) {
        for ( GenericEvent e : eventsToSave ) {
            e.save();
        }
        currentUser.save();
    }

    /**
     * Saves an event on server database.
     *
     * @param e event that must be saved.
     */
    public void saveEvent ( GenericEvent e ) {
        e.save();
    }

    /**
     * Saves a path on server database.
     *
     * @param t path that must be saved.
     */
    public void savePath ( Travel t ) {
        for ( TravelComponent tc : t.getMiniTravels() ) {
            tc.getDeparture().save();
            tc.getArrival().save();
        }
        t.save();
    }

    /**
     * Eliminates, if possible, all the solutions that have a travel component with a length greater than
     * MAX_WALKING_LENGTH and BY_FOOT as travel mean. It is possible to eliminate this travels only if there exists at
     * least one combination in the list passed as a parameter that doesn't contain "long walking paths".
     *
     * @param possiblePaths list of combinations of prev-foll path to analize.
     * @return combinations in the list passed as a parameter with no "long walking paths" or the entire list if all
     * the combinations contain at least one "long walking path".
     */
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
