package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.Timestamp;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "TRAVEL")
public class Travel extends EntityWithLongKey {

    private static final long serialVersionUID = 3515840069172744899L;

    @Column(name = "PREFERRED")
    private boolean preferred;

    @ManyToOne( fetch=FetchType.LAZY )
    @JoinColumn(name="RELATED_EVENT")
    private Event relatedEvent;

    @JoinTable(name = "TRAVEL_COMPONENTS")
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List<TravelComponent> miniTravels;

    @Embedded
    private Timestamp lastUpdate;

    public Travel() {
        this.lastUpdate = new Timestamp();
    }

    public Travel(boolean preferred, Event relatedEvent, ArrayList<TravelComponent> miniTravels) {
        this.preferred = preferred;
        this.relatedEvent = relatedEvent;
        this.miniTravels = miniTravels;
        this.lastUpdate = new Timestamp();
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public Event getRelatedEvent() {
        return relatedEvent;
    }

    public void setRelatedEvent(Event relatedEvent) {
        this.relatedEvent = relatedEvent;
    }

    public List<TravelComponent> getMiniTravels() {
        return Collections.unmodifiableList(miniTravels);
    }

    public void setMiniTravels(ArrayList<TravelComponent> miniTravels) {
        this.miniTravels = miniTravels;
    }

    //amount of time in minutes spent on travel
    public int getTravelTime() {
        int totalMinutes = 0;
        for(TravelComponent component: miniTravels)
            totalMinutes += component.deltaTimeInSeconds();
        return totalMinutes;
    }

    //amount of time in seconds between departure and arrival time
    public long getTotalTime() {
        return getEndingTime().getEpochSecond() - getStartingTime().getEpochSecond();
    }

    public int getTotalLength() {
        //TODO
        return 0;
    }

    private Instant getStartingTime() {
        return miniTravels.get(0).getStartingTime();
    }

    private Instant getEndingTime() {
        return miniTravels.get(miniTravels.size()-1).getEndingTime();
    }

    private Location getStartingLocation() {
        return miniTravels.get(0).getDeparture();
    }

    private Location getEndingLocation() {
        return miniTravels.get(miniTravels.size()-1).getArrival();
    }

    public void setMiniTravels(List<TravelComponent> miniTravels) {
        this.miniTravels = miniTravels;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int numberOfChanges() {
        return miniTravels.size() - 1;
    }

    public static Travel load(long key){
        return GenericEntity.load( Travel.class, key );
    }

}
