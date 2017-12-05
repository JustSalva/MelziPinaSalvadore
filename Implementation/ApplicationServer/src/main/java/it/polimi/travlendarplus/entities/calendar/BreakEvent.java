package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity(name="BREAK_EVENT")
@DiscriminatorValue("BREAK_EVENT")
public class BreakEvent extends GenericEvent {

    private static final long serialVersionUID = -38523370993953035L;

    @Column(name = "MINIMUM_TIME")
    private long minimumTime; // in seconds

    public BreakEvent(){

    }

    @Override
    public Travel getFeasiblePath() {
        TravelComponent fakeTC = new TravelComponent(getStartingTime(), getEndingTime(), 0, null, null, null);
        ArrayList<TravelComponent> fakeList = new ArrayList<TravelComponent>();
        fakeList.add(fakeTC);
        return new Travel(true, null, fakeList);
    }

    public BreakEvent(String name, Instant startingTime, Instant endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date, long minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, periodicity, date);
        this.minimumTime = minimumTime;
    }

    //constructor for generic event with no periodicity
    public BreakEvent(String name, Instant startingTime, Instant endingTime, boolean isScheduled, DateOfCalendar date, long minimumTime, User user) {
        super(name, startingTime, endingTime, isScheduled, date);
        this.minimumTime = minimumTime;
    }

    public long getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(int minimumTime) {
        this.minimumTime = minimumTime;
    }

    public static BreakEvent load(long key){
        return GenericEntity.load( BreakEvent.class, key );
    }
}
