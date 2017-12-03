package it.polimi.travlendarplus.entities.calendar;


import it.polimi.travlendarplus.entities.GeneralEntity;
import it.polimi.travlendarplus.entities.Timestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "GENERIC_EVENT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "EVENT_TYPE")
public abstract class GenericEvent extends GeneralEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, name = "NAME")
    private String name;

    @Column(name = "STARTING_TIME")
    private Instant startingTime;

    @Column(name = "ENDING_TIME")
    private Instant endingTime;

    @Column(name = "IS_SCHEDULED")
    private boolean isScheduled;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="PERIODICITY_ID")
    private Period periodicity;

    @ManyToOne
    @JoinColumn(name="DATE_OF_CALENDAR_ID")
    private DateOfCalendar date;

    @Embedded
    private Timestamp lastUpdate;

    public GenericEvent() {
    }

    public GenericEvent(String name, Instant startingTime, Instant endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicity = periodicity;
        this.date = date;
    }

    //constructor for generic event with no periodicity
    public GenericEvent(String name, Instant startingTime, Instant endingTime, boolean isScheduled, DateOfCalendar date) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicity = new Period(null,null, 0);
        this.date = date;
    }

    public long  getId() {
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

    public Instant getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(Instant startingTime) {
        this.startingTime = startingTime;
    }

    public Instant getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(Instant endingTime) {
        this.endingTime = endingTime;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public Period getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Period periodicity) {
        this.periodicity = periodicity;
    }

    public DateOfCalendar getDate() {
        return date;
    }

    public void setDate(DateOfCalendar date) {
        this.date = date;
    }

}
