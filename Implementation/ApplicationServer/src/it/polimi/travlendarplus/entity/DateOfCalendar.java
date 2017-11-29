package it.polimi.travlendarplus.entity;

import java.time.LocalDate;
import java.util.ArrayList;

public class DateOfCalendar {
    private LocalDate date;
    private ArrayList<GenericEvent> listOfEvents;
    //private ArrayList<EnvironmentConditions> conditions;


    public DateOfCalendar(LocalDate date, ArrayList<GenericEvent> listOfEvents) {
        this.date = date;
        this.listOfEvents = listOfEvents;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setListOfEvents(ArrayList<GenericEvent> listOfEvents) {
        this.listOfEvents = listOfEvents;
    }
}
