package it.polimi.travlendarplus.entity;

import java.time.LocalTime;

public class TravelComponent {
    private LocalTime startingTime;
    private LocalTime endingTime;
    private float length;
    private Location departure;
    private Location arrival;
    private TravelMean meanUsed;

    public TravelComponent(LocalTime startingTime, LocalTime endingTime, float length, Location departure, Location arrival, TravelMean meanUsed) {
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.length = length;
        this.departure = departure;
        this.arrival = arrival;
        this.meanUsed = meanUsed;
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
