package it.polimi.travlendarplus;


import java.util.List;

/**
 * Contains info regarding a feasible path.
 */
public class FeasiblePath {

    private long id;
    private List < MiniTravel > miniTravels;

    public long getId () {
        return id;
    }

    public List < MiniTravel > getMiniTravels () {
        return miniTravels;
    }
}
