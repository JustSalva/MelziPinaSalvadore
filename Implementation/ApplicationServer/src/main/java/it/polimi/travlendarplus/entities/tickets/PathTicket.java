package it.polimi.travlendarplus.entities.tickets;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.travelMeans.PublicTravelMean;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.util.ArrayList;

@Entity( name = "PATH_TICKET" )
@DiscriminatorValue( "PATH" )
public class PathTicket extends GenericTicket {

    private static final long serialVersionUID = 958692352206870450L;

    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    @JoinColumns( {
            @JoinColumn( name = "DEPARTURE_LATITUDE", referencedColumnName = "latitude" ),
            @JoinColumn( name = "DEPARTURE_LONGITUDE", referencedColumnName = "longitude" )
    } )
    private Location startingLocation;

    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.PERSIST )
    @JoinColumns( {
            @JoinColumn( name = "ARRIVAL_LATITUDE", referencedColumnName = "latitude" ),
            @JoinColumn( name = "ARRIVAL_LONGITUDE", referencedColumnName = "longitude" )
    } )
    private Location endingLocation;

    public PathTicket() {
    }

    public PathTicket( float cost, ArrayList< PublicTravelMean > relatedTo, String lineName, Location startingLocation, Location endingLocation ) {
        super( cost, relatedTo, lineName );
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
    }

    public Location getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation( Location startingLocation ) {
        this.startingLocation = startingLocation;
    }

    public Location getEndingLocation() {
        return endingLocation;
    }

    public void setEndingLocation( Location endingLocation ) {
        this.endingLocation = endingLocation;
    }

    public static PathTicket load( long key ) throws EntityNotFoundException {
        return GenericEntity.load( PathTicket.class, key );
    }
}
