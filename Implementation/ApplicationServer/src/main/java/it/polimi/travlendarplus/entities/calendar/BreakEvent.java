package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

@Entity(name="BREAK_EVENT")
@DiscriminatorValue("BREAK_EVENT")
public class BreakEvent extends GenericEvent {

    private static final long serialVersionUID = -38523370993953035L;

    @Column(name = "MINIMUM_TIME")
    private long minimumTime; // in seconds

    public BreakEvent(){

    }

    public BreakEvent(String name, Instant startingTime, Instant endingTime, boolean isScheduled,
                      Period periodicity, long minimumTime) {
        super(name, startingTime, endingTime, isScheduled, periodicity);
        this.minimumTime = minimumTime;
    }

    //constructor for generic event with no periodicity
    public BreakEvent(String name, Instant startingTime, Instant endingTime, boolean isScheduled, long minimumTime) {
        super(name, startingTime, endingTime, isScheduled);
        this.minimumTime = minimumTime;
    }

    public long getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(int minimumTime) {
        this.minimumTime = minimumTime;
    }

    public static BreakEvent load(long key) throws EntityNotFoundException {
        return GenericEntity.load( BreakEvent.class, key );
    }

    //Every Event in events ArrayList has an overlap with the interval of BreakEvent
    public boolean isMinimumEnsuredNoPathRegard(ArrayList<Event> events) {
        if (events.size()==0)
            return true;
        //checking if there is enough time before the first event
        if(minimumTime <= Duration.between(getStartingTime(), events.get(0).getStartingTime()).getSeconds())
            return true;
        //checking if there is enough time between two events
        for(int i=0; i<events.size()-1; i++)
            if(minimumTime <= Duration.between(events.get(i).getEndingTime(),
                    events.get(i+1).getStartingTime()).getSeconds())
                return true;
        //checking if there is enough time after the last event
        return minimumTime <= Duration.between(events.get(events.size()-1).getEndingTime(),
                getEndingTime()).getSeconds();
    }

    public boolean isMinimumEnsuredWithPathRegard(ArrayList<Event> events) {
        if (events.size()==0)
            return true;
        if(events.size() == 1) {
            //if the first event has no previous events
            if (events.get(0).getFeasiblePath() == null && minimumTime <=
                    Duration.between(getStartingTime(), events.get(0).getStartingTime()).getSeconds())
                return true;
            //checking before the first event if it has a previous event (checking also of the path)
            else if (enoughTimeBeforeFirstEvent(events.get(0)))
                return true;
        }
        if(events.size()>1){
            //checking between first event and following path
            if(minimumTime <= Duration.between(events.get(0).getEndingTime(),
                    events.get(1).getFeasiblePath().getStartingTime()).getSeconds())
                return true;
            //checking if there is enough time between an event and the previous/following path
            for(int i=1; i<events.size()-1; i++) {
                if(minimumTime <= Duration.between(events.get(i).getFeasiblePath().getEndingTime(), events.get(i).
                        getStartingTime()).getSeconds() || minimumTime <= Duration.between(events.get(i).
                        getEndingTime(), events.get(i+1).getFeasiblePath().getStartingTime()).getSeconds())
                    return true;
            }
        }
        //checking if there is enough time with the last event
        return enoughTimeWithLastEvent(events.get(events.size()-1));
    }

    //use dwhen event.getFeasiblePath() is different from NULL
    private boolean enoughTimeBeforeFirstEvent(Event event) {
        Travel path = event.getFeasiblePath();
        return minimumTime <= Duration.between(getStartingTime(), path.getStartingTime()).getSeconds() ||
                minimumTime <= Math.min(getEndingTime().getEpochSecond(), event.getStartingTime().getEpochSecond()) -
                        Math.max(path.getEndingTime().getEpochSecond(), getStartingTime().getEpochSecond());
    }

    //use dwhen event.getFeasiblePath() is different from NULL
    private boolean enoughTimeWithLastEvent(Event event) {
        Travel path = event.getFeasiblePath();
        return  minimumTime <= Math.min(getEndingTime().getEpochSecond(), event.getStartingTime().getEpochSecond()) -
                Math.max(path.getEndingTime().getEpochSecond(), getStartingTime().getEpochSecond()) ||
                minimumTime <= Duration.between(event.getEndingTime(), getEndingTime()).getSeconds();
    }

}
