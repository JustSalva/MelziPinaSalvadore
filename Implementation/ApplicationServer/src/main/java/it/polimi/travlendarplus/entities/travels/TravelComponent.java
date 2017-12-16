package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.travelMeans.TravelMean;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;

@Entity( name = "TRAVEL_COMPONENT" )
public class TravelComponent extends EntityWithLongKey {

    private static final long serialVersionUID = 436484875130667585L;

    @Column( name = "STARTING_TIME" )
    private Instant startingTime;

    @Column( name = "ENDING_TIME" )
    private Instant endingTime;

    @Column( name = "LENGTH" )
    //distance in Km
    private float length;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumns( {
            @JoinColumn( name = "DEPARTURE_LATITUDE", referencedColumnName = "latitude" ),
            @JoinColumn( name = "DEPARTURE_LONGITUDE", referencedColumnName = "longitude" )
    } )
    private Location departure;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumns( {
            @JoinColumn( name = "ARRIVAL_LATITUDE", referencedColumnName = "latitude" ),
            @JoinColumn( name = "ARRIVAL_LONGITUDE", referencedColumnName = "longitude" )
    } )
    private Location arrival;

    @ManyToOne( fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
    @JoinColumn( name = "TRAVEL_MEAN_USED" )
    private TravelMean meanUsed;

    public TravelComponent () {
    }

    public TravelComponent ( Instant startingTime, Instant endingTime, float length, Location departure, Location arrival, TravelMean meanUsed ) {
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.length = length;
        this.departure = departure;
        this.arrival = arrival;
        this.meanUsed = meanUsed;
    }

    public static TravelComponent load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( TravelComponent.class, key );
    }

    public Instant getStartingTime () {
        return startingTime;
    }

    public void setStartingTime ( Instant startingTime ) {
        this.startingTime = startingTime;
    }

    public Instant getEndingTime () {
        return endingTime;
    }

    public void setEndingTime ( Instant endingTime ) {
        this.endingTime = endingTime;
    }

    public float getLength () {
        return length;
    }

    public void setLength ( float length ) {
        this.length = length;
    }

    public Location getDeparture () {
        return departure;
    }

    public void setDeparture ( Location departure ) {
        this.departure = departure;
    }

    public Location getArrival () {
        return arrival;
    }

    public void setArrival ( Location arrival ) {
        this.arrival = arrival;
    }

    public TravelMean getMeanUsed () {
        return meanUsed;
    }

    public void setMeanUsed ( TravelMean meanUsed ) {
        this.meanUsed = meanUsed;
    }

    public long deltaTimeInSeconds () {
        return endingTime.getEpochSecond() - startingTime.getEpochSecond();
    }

    @Override
    public String toString () {
        return "TravelComponent{" +
                "startingTime=" + startingTime +
                ", endingTime=" + endingTime +
                ", length=" + length +
                ", departure=" + departure +
                ", arrival=" + arrival +
                ", meanUsed=" + meanUsed +
                '}';
    }

}
