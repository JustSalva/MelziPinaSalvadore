package it.polimi.travlendarplus.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.messages.calendarMessages.CalendarMessage;

public class LocationMessage extends CalendarMessage{

    private static final long serialVersionUID = -5709220684464695817L;

    private double latitude;
    private double longitude;
    private String address;

    public LocationMessage() {
    }

    public LocationMessage( double latitude, double longitude, String address ) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }
}
