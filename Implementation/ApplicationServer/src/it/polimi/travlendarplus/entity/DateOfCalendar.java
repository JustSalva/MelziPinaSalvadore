package it.polimi.travlendarplus.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DateOfCalendar {
    private LocalDate date;
    private List<GenericEvent> listOfEvents;
    //TODO private ArrayList<EnvironmentConditions> conditions;

    public DateOfCalendar(LocalDate date) {
        this.date = date;
        this.listOfEvents = new ArrayList<GenericEvent>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<GenericEvent> getListOfEvents() {
        return Collections.unmodifiableList(listOfEvents);
    }

    public void setListOfEvents(ArrayList<GenericEvent> listOfEvents) {
        this.listOfEvents = listOfEvents;
    }

    public void addEvent(GenericEvent event) {
        this.listOfEvents.add(event);
    }
}
