package it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.messages.calendarMessages.CalendarMessage;

public class PreferredLocationMessage extends CalendarMessage{

    private static final long serialVersionUID = -5387947525513320656L;

    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public PreferredLocationMessage() {
    }

    public PreferredLocationMessage( String name, String address, double latitude, double longitude ) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude( double latitude ) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude( double longitude ) {
        this.longitude = longitude;
    }
}
