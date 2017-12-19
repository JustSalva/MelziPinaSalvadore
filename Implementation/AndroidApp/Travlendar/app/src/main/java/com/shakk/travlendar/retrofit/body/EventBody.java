package com.shakk.travlendar.retrofit.body;

public class EventBody {

    private String name;
    private String description;
    private String startingTime;
    private String endingTime;
    private Location eventLocation;
    private Location departure;
    private long idTypeOfEvent;
    private boolean prevLocChoice;
    private boolean travelAtLastChoice;

    public EventBody(
            String name,
            String description,
            String startingTime,
            String endingTime,
            Location eventLocation,
            Location departure,
            long idTypeOfEvent,
            boolean prevLocChoice,
            boolean travelAtLastChoice) {
        this.name = name;
        this.description = description;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.eventLocation = eventLocation;
        this.departure = departure;
        this.idTypeOfEvent = idTypeOfEvent;
        this.prevLocChoice = prevLocChoice;
        this.travelAtLastChoice = travelAtLastChoice;
    }

    private class Location {
        private String address;
        private long latitude;
        private long longitude;
    }
}
