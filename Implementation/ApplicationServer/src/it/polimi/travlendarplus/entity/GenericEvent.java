package it.polimi.travlendarplus.entity;


import javax.persistence.*;
import java.time.LocalTime;

@Entity(name = "GENERIC_EVENT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "EVENT_TYPE")
public abstract class GenericEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(nullable = false, name = "NAME")
    private String name;

    @Column(name = "STARTING_TIME")
    private LocalTime startingTime;

    @Column(name = "ENDING_TIME")
    private LocalTime endingTime;

    @Column(name = "IS_SCHEDULED")
    private boolean isScheduled;

    @OneToOne
    @JoinColumn(name="PERIODICITY_ID")
    private Period periodicity;

    @ManyToOne
    @JoinColumn(name="DATE_OF_CALENDAR_ID")
    private DateOfCalendar date;


    public GenericEvent() {
    }

    public GenericEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, Period periodicity, DateOfCalendar date) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicity = periodicity;
        this.date = date;
    }

    //constructor for generic event with no periodicity
    public GenericEvent(String name, LocalTime startingTime, LocalTime endingTime, boolean isScheduled, DateOfCalendar date) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.isScheduled = isScheduled;
        this.periodicity = new Period(null,null, 0);
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public LocalTime getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(LocalTime endingTime) {
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
