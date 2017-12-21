package it.polimi.travlendarplus.database.entity.event;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.polimi.travlendarplus.DateUtility;
import it.polimi.travlendarplus.database.converters.EventTypeConverters;

@Entity(tableName = "generic_event")
public class GenericEvent {
    @PrimaryKey
    private long id;

    private String name;
    private long date;
    @ColumnInfo(name = "start_time")
    private long startTime;
    @ColumnInfo(name = "end_time")
    private long endTime;
    private boolean scheduled;

    @TypeConverters(EventTypeConverters.class)
    private EventType type;

    @Embedded
    private Event event;
    @Embedded
    private BreakEvent breakEvent;

    private boolean periodical;

    @Embedded
    private Period period;

    public GenericEvent(long id, String name, long startTime, long endTime,
                        boolean scheduled) {
        this.id = id;
        this.name = name;
        this.date = startTime - (startTime % 86400);
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduled = scheduled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public BreakEvent getBreakEvent() {
        return breakEvent;
    }

    public void setBreakEvent(BreakEvent breakEvent) {
        this.breakEvent = breakEvent;
    }

    public boolean isPeriodical() {
        return periodical;
    }

    public void setPeriodical(boolean periodical) {
        this.periodical = periodical;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public enum EventType {
        EVENT("event"),
        BREAK("break");

        private String type;

        EventType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}

