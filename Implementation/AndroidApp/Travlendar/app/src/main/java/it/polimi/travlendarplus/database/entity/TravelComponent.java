package it.polimi.travlendarplus.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import it.polimi.travlendarplus.database.entity.event.GenericEvent;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * DB travel component entity.
 */
@Entity( foreignKeys = {
        @ForeignKey(
                entity = GenericEvent.class,
                parentColumns = "id",
                childColumns = "event_id",
                onUpdate = CASCADE,
                onDelete = CASCADE
        )
}, tableName = "travel_component" )
public class TravelComponent {

    @PrimaryKey
    private long id;

    private float length;
    @ColumnInfo( name = "event_id" )
    private long eventId;

    @ColumnInfo( name = "ticket_id" )
    private long ticketId;

    @ColumnInfo( name = "travel_mean" )
    private String travelMean;

    @ColumnInfo( name = "departure_location" )
    private String departureLocation;
    @ColumnInfo( name = "arrival_location" )
    private String arrivalLocation;

    @ColumnInfo( name = "start_time" )
    private long startTime;
    @ColumnInfo( name = "end_time" )
    private long endTime;

    public TravelComponent ( long id, float length, long eventId, String travelMean,
                             String departureLocation, String arrivalLocation,
                             long startTime, long endTime ) {
        this.id = id;
        this.length = length;
        this.eventId = eventId;
        this.ticketId = 0;
        this.travelMean = travelMean;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getId () {
        return id;
    }

    public void setId ( long id ) {
        this.id = id;
    }

    public float getLength () {
        return length;
    }

    public void setLength ( float length ) {
        this.length = length;
    }

    public long getEventId () {
        return eventId;
    }

    public void setEventId ( long eventId ) {
        this.eventId = eventId;
    }

    public long getTicketId () {
        return ticketId;
    }

    public void setTicketId ( long ticketId ) {
        this.ticketId = ticketId;
    }

    public String getTravelMean () {
        return travelMean;
    }

    public void setTravelMean ( String travelMean ) {
        this.travelMean = travelMean;
    }

    public String getDepartureLocation () {
        return departureLocation;
    }

    public void setDepartureLocation ( String departureLocation ) {
        this.departureLocation = departureLocation;
    }

    public String getArrivalLocation () {
        return arrivalLocation;
    }

    public void setArrivalLocation ( String arrivalLocation ) {
        this.arrivalLocation = arrivalLocation;
    }

    public long getStartTime () {
        return startTime;
    }

    public void setStartTime ( long startTime ) {
        this.startTime = startTime;
    }

    public long getEndTime () {
        return endTime;
    }

    public void setEndTime ( long endTime ) {
        this.endTime = endTime;
    }
}
