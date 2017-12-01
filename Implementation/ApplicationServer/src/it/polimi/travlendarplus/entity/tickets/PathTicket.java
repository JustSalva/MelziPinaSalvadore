package it.polimi.travlendarplus.entity.tickets;

import it.polimi.travlendarplus.entity.Location;
import it.polimi.travlendarplus.entity.travelMeans.PublicTravelMean;

import javax.persistence.*;
import java.util.ArrayList;

@Entity(name="PATH_TICKET")
@DiscriminatorValue("PATH")
public class PathTicket extends GeneralTicket {

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="DEPARTURE_LATITUDE", referencedColumnName="latitude"),
            @JoinColumn(name="DEPARTURE_LONGITUDE", referencedColumnName="longitude")
    })
    private Location startingLocation;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="ARRIVAL_LATITUDE", referencedColumnName="latitude"),
            @JoinColumn(name="ARRIVAL_LONGITUDE", referencedColumnName="longitude")
    })
    private Location endingLocation;

    public PathTicket() {
    }

    public PathTicket(float cost, ArrayList<PublicTravelMean> relatedTo, String lineName, Location startingLocation, Location endingLocation) {
        super(cost, relatedTo, lineName);
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
    }

    public Location getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(Location startingLocation) {
        this.startingLocation = startingLocation;
    }

    public Location getEndingLocation() {
        return endingLocation;
    }

    public void setEndingLocation(Location endingLocation) {
        this.endingLocation = endingLocation;
    }
}
