package it.polimi.travlendarplus.beans.calendarManager.support;

import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import java.util.List;

public class PathCombination {
    private Travel prevPath;
    private Travel follPath;

    public PathCombination ( Travel prevPath, Travel follPath ) {
        this.prevPath = prevPath;
        this.follPath = follPath;
    }

    public Travel getPrevPath () {
        return prevPath;
    }

    public Travel getFollPath () {
        return follPath;
    }

    @Override
    public String toString () {
        return "PathCombination{" +
                "prevPath=" + prevPath +
                ", follPath=" + follPath +
                '}';
    }

    public float getTotalLength () {
        float prev = ( prevPath != null ) ? prevPath.getTotalLength() : 0;
        float foll = ( follPath != null ) ? follPath.getTotalLength() : 0;
        return prev + foll;
    }

    public long getTotalTime () {
        long prev = ( prevPath != null ) ? prevPath.getTotalTime() : 0;
        long foll = ( follPath != null ) ? follPath.getTotalTime() : 0;
        return prev + foll;
    }

    public void saveLocationsOnDB () {
        if ( prevPath != null ) {
            saveTravelLocations( prevPath.getMiniTravels() );
        }
        if ( follPath != null ) {
            saveTravelLocations( follPath.getMiniTravels() );
        }
    }

    public void fixTimes () {
        if ( prevPath != null ) {
            prevPath.fixTimes();
        }
        if ( follPath != null ) {
            follPath.fixTimes();
        }
    }

    private void saveTravelLocations ( List < TravelComponent > travel ) {
        for ( TravelComponent comb : travel ) {
            comb.getDeparture().save();
            comb.getArrival().save();
        }
    }
}
