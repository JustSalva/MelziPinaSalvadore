package it.polimi.travlendarplus;


/**
 * Contains info regarding a travel component.
 */
public class MiniTravel {

    private long id;
    private Timestamp startingTime;
    private Timestamp endingTime;
    private float length;
    private Position departure;
    private Position arrival;
    private TravelMeanUsed meanUsed;

    public long getId () {
        return id;
    }

    public Timestamp getStartingTime () {
        return startingTime;
    }

    public Timestamp getEndingTime () {
        return endingTime;
    }

    public float getLength () {
        return length;
    }

    public Position getDeparture () {
        return departure;
    }

    public Position getArrival () {
        return arrival;
    }

    public TravelMeanUsed getMeanUsed () {
        return meanUsed;
    }
}
