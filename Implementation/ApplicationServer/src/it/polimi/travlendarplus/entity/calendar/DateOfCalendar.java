package it.polimi.travlendarplus.entity.calendar;

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

    //TODO private ArrayList<EnvironmentConditions> conditions;

    public DateOfCalendar() {
    }

    public DateOfCalendar(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
