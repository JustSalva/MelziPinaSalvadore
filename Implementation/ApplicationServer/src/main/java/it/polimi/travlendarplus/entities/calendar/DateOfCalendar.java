package it.polimi.travlendarplus.entities.calendar;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name = "DATE_OF_CALENDAR")
public class DateOfCalendar {

    @Id
    private Instant date;

    //TODO private ArrayList<EnvironmentConditions> conditions;

    public DateOfCalendar() {
    }

    public DateOfCalendar(Instant date) {
        this.date = date;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

}
