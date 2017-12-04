package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GeneralEntity;

import javax.persistence.*;
import java.time.Instant;


@Entity(name = "DATE_OF_CALENDAR")
public class DateOfCalendar extends GeneralEntity{

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

    public static DateOfCalendar load(long key){
        return GeneralEntity.load( DateOfCalendar.class, key );
    }

    @Override
    public boolean isAlreadyInDb() {
        return load(date) != null;
    }
}
