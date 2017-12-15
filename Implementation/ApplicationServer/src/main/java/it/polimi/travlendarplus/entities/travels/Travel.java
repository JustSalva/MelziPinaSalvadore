package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "TRAVEL")
public class Travel extends EntityWithLongKey {

    private static final long serialVersionUID = 3515840069172744899L;

    @OneToOne( fetch=FetchType.LAZY )
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

    public Travel(Event relatedEvent, ArrayList<TravelComponent> miniTravels) {
        this.relatedEvent = relatedEvent;
        this.miniTravels = miniTravels;
        this.lastUpdate = new Timestamp();
    }

    public List<TravelComponent> getMiniTravels() {
        return Collections.unmodifiableList(miniTravels);
    }

    public void setMiniTravels(ArrayList<TravelComponent> miniTravels) {
        this.miniTravels = miniTravels;
    }

    // Amount of time (in seconds) spent on travel.
    public long getTravelTime() {
        long totalSeconds = 0;
        for(TravelComponent component: miniTravels)
            totalSeconds += component.deltaTimeInSeconds();
        return totalSeconds;
    }

    // Amount of time in seconds between departure and arrival time
    public long getTotalTime() {
        return getEndingTime().getEpochSecond() - getStartingTime().getEpochSecond();
    }

    // Total length in Km
    public float getTotalLength() {
        float length = 0;
        for(TravelComponent comp: miniTravels)
            length += comp.getLength();
        return length;
    }

    public Instant getStartingTime() {
        return (miniTravels.get(0).getStartingTime().getEpochSecond() > 0) ? miniTravels.get(0).getStartingTime() :
                correctStartingTime();
    }

    private Instant correctStartingTime() {
        int i=0;
        long timeToSub = 0;
        while(miniTravels.get(i).getStartingTime().getEpochSecond() == 0) {
            timeToSub += miniTravels.get(i).getEndingTime().getEpochSecond();
            i++;
        }
        long startingTime = miniTravels.get(i).getStartingTime().getEpochSecond() - timeToSub;
        return Instant.ofEpochSecond(startingTime);

    }

    public Instant getEndingTime() {
        return (miniTravels.get(miniTravels.size()-1).getStartingTime().getEpochSecond() > 0) ?
                miniTravels.get(miniTravels.size()-1).getEndingTime() : correctEndingTime();
    }

    private Instant correctEndingTime() {
        int i = miniTravels.size()-1;
        long timeToAdd = 0;
        while(miniTravels.get(i).getStartingTime().getEpochSecond() == 0) {
            timeToAdd += miniTravels.get(i).getEndingTime().getEpochSecond();
            i--;
        }
        long endingTime = miniTravels.get(i).getEndingTime().getEpochSecond() + timeToAdd;
        return Instant.ofEpochSecond(endingTime);
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

    public static Travel load(long key) throws EntityNotFoundException {
        return GenericEntity.load( Travel.class, key );
    }

    @Override
    public String toString() {
        return "Travel{" +
                "relatedEvent=" + relatedEvent +
                ", miniTravels=" + miniTravels +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
