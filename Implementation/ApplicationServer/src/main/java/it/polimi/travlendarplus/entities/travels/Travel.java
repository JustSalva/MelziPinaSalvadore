package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.GeneralEntity;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.Timestamp;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "TRAVEL")
public class Travel extends GeneralEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "PREFERRED")
    private boolean preferred;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="RELATED_EVENT")
    private Event relatedEvent;

    @JoinTable(name = "TRAVEL_COMPONENTS")
    @OneToMany(cascade = CascadeType.ALL)
    private List<TravelComponent> miniTravels;

    @Embedded
    private Timestamp lastUpdate;

    public Travel() {
    }

    public Travel(boolean preferred, Event relatedEvent, ArrayList<TravelComponent> miniTravels) {
        this.preferred = preferred;
        this.relatedEvent = relatedEvent;
        this.miniTravels = miniTravels;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
            totalMinutes += component.deltaTimeInMinutes();
        return totalMinutes;
    }

    //amount of time in minutes between departure and arrival time
    public int getTotalTime() {
        return (getEndingTime().getHour() - getStartingTime().getHour()) * 60 +
                getEndingTime().getMinute() - getStartingTime().getMinute();
    }

    public int getTotalLength() {
        //TODO
        return 0;
    }

    private LocalTime getStartingTime() {
        return miniTravels.get(0).getStartingTime();
    }

    private LocalTime getEndingTime() {
        return miniTravels.get(miniTravels.size()-1).getEndingTime();
    }

    private Location getStartingLocation() {
        return miniTravels.get(0).getDeparture();
    }

    private Location getEndingLocation() {
        return miniTravels.get(miniTravels.size()-1).getArrival();
    }

    public int numberOfChanges() {
        return miniTravels.size() - 1;
    }

    public static Travel load(long key){
        return GeneralEntity.load( Travel.class, key );
    }

    @Override
    public boolean isAlreadyInDb() {
        return load(id) != null;
    }
}
