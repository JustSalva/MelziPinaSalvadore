package it.polimi.travlendarplus.entities.calendar;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;


@Entity(name = "DATE_OF_CALENDAR")
public class DateOfCalendar {

    @Id
    private long date;

    //TODO private ArrayList<EnvironmentConditions> conditions;

    public DateOfCalendar() {
    }

    public DateOfCalendar(long date) {
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

}
