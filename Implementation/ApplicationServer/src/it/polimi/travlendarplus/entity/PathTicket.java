package it.polimi.travlendarplus.entity;

public class PathTicket extends GeneralTicket {
    private Location startingLocation;
    private Location endingLocation;

    public PathTicket(float cost, String lineName, Location startingLocation, Location endingLocation) {
        super(cost, lineName);
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
