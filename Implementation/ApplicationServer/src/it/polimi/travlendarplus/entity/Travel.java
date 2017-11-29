package it.polimi.travlendarplus.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Travel {
    private boolean preferred;
    private Event relatedEvent;
    private List<TravelComponent> miniTravels;

    public Travel(boolean preferred, Event relatedEvent, ArrayList<TravelComponent> miniTravels) {
        this.preferred = preferred;
        this.relatedEvent = relatedEvent;
        this.miniTravels = miniTravels;
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
}
