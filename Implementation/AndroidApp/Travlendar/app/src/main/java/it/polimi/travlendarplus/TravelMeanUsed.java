package it.polimi.travlendarplus;


/**
 * Calls representing a type of travel mean used.
 */
public class TravelMeanUsed {

    private TravelMeanEnum type;

    public TravelMeanUsed(TravelMeanEnum type) {
        this.type = type;
    }

    public TravelMeanEnum getType() {
        return type;
    }
}
