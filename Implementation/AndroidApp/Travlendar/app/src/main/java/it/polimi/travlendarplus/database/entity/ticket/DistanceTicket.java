package it.polimi.travlendarplus.database.entity.ticket;

/**
 * DB distance ticket entity.
 */
public class DistanceTicket {

    private float distance;

    public DistanceTicket ( float distance ) {
        this.distance = distance;
    }

    public float getDistance () {
        return distance;
    }

    public void setDistance ( float distance ) {
        this.distance = distance;
    }
}
