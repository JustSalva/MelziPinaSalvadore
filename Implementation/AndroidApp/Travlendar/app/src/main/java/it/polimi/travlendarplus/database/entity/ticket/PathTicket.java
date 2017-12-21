package it.polimi.travlendarplus.database.entity.ticket;

import android.arch.persistence.room.ColumnInfo;

/**
 * DB path ticket entity.
 */
public class PathTicket {

    @ColumnInfo(name = "departure_location")
    private String departureLocation;
    @ColumnInfo(name = "arrival_location")
    private String arrivalLocation;

    public PathTicket(String departureLocation, String arrivalLocation) {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }
}
