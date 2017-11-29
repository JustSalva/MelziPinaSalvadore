package it.polimi.travlendarplus.entity;

import javax.persistence.*;
import java.time.LocalTime;

@Entity(name = "TRAVEL_COMPONENT")
public class TravelComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(name = "STARTING_TIME")
    private LocalTime startingTime;

    @Column(name = "ENDING_TIME")
    private LocalTime endingTime;

    @Column(name = "LENGHT")
    private float length;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="DEPARTURE_LATITUDE", referencedColumnName="latitude"),
            @JoinColumn(name="DEPARTURE_LONGITUDE", referencedColumnName="longitude")
    })
    private Location departure;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="ARRIVAL_LATITUDE", referencedColumnName="latitude"),
            @JoinColumn(name="ARRIVAL_LONGITUDE", referencedColumnName="longitude")
    })
    private Location arrival;

    @ManyToOne
    @JoinColumn(name="TRAVEL_MEAN_USED")
    private TravelMean meanUsed;

    public TravelComponent() {
    }

    public TravelComponent(LocalTime startingTime, LocalTime endingTime, float length, Location departure, Location arrival, TravelMean meanUsed) {
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.length = length;
        this.departure = departure;
        this.arrival = arrival;
        this.meanUsed = meanUsed;
    }

    public String getId() {
        return id;
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

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public Location getDeparture() {
        return departure;
    }

    public void setDeparture(Location departure) {
        this.departure = departure;
    }

    public Location getArrival() {
        return arrival;
    }

    public void setArrival(Location arrival) {
        this.arrival = arrival;
    }

    public TravelMean getMeanUsed() {
        return meanUsed;
    }

    public void setMeanUsed(TravelMean meanUsed) {
        this.meanUsed = meanUsed;
    }

    public int deltaTimeInMinutes() {
        return (endingTime.getHour() - startingTime.getHour()) * 60 + endingTime.getMinute() - startingTime.getMinute();
    }
}
