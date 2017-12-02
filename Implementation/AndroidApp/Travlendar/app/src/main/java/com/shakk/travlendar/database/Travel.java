package com.shakk.travlendar.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import java.util.List;

@Entity
public class Travel {
    private String ID;
    private String description;
    private GenericEvent event;
    @ColumnInfo(name = "travel_components")
    private List<TravelComponent> travelComponents;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GenericEvent getEvent() {
        return event;
    }

    public void setEvent(GenericEvent event) {
        this.event = event;
    }

    public List<TravelComponent> getTravelComponents() {
        return travelComponents;
    }

    public void setTravelComponents(List<TravelComponent> travelComponents) {
        this.travelComponents = travelComponents;
    }
}
