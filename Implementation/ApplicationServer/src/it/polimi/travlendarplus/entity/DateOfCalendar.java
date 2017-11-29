package it.polimi.travlendarplus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "DATE_OF_CALENDAR")
public class DateOfCalendar {

    @Id
    private LocalDate date;

    //private List<GenericEvent> listOfEvents; TODO WHY?
    //TODO private ArrayList<EnvironmentConditions> conditions;

    public DateOfCalendar() {
    }

    public DateOfCalendar(LocalDate date) {
        this.date = date;
        //this.listOfEvents = new ArrayList<GenericEvent>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
/*
    public List<GenericEvent> getListOfEvents() {
        return Collections.unmodifiableList(listOfEvents);
    }

    public void setListOfEvents(ArrayList<GenericEvent> listOfEvents) {
        this.listOfEvents = listOfEvents;
    }

    public void addEvent(GenericEvent event) {
        this.listOfEvents.add(event);
    }*/
}
