package it.polimi.travlendarplus;


/**
 * Calls representing a type of travel mean used.
 */
public class TravelMeanUsed {

    private TravelMeanEnum type;
    private String name;

    public TravelMeanUsed ( TravelMeanEnum type ) {
        this.type = type;
    }

    public TravelMeanUsed ( TravelMeanEnum type, String name ) {
        this.type = type;
        this.name = name;
    }

    public TravelMeanEnum getType () {
        return type;
    }
}
