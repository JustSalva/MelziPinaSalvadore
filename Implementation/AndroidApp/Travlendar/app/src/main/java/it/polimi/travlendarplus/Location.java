package it.polimi.travlendarplus;


/**
 * Contains info regarding a location.
 */
public class Location {

    private String name;
    private Position location;

    public Location ( String name, Position location ) {
        this.name = name;
        this.location = location;
    }

    public String getName () {
        return name;
    }

    public Position getLocation () {
        return location;
    }
}
