package it.polimi.travlendarplus.beans.calendarManager.support;

import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import java.util.List;

/**
 * Helper class used to represent the combination of two paths: the path of
 * a specific event and the path of the event that follows that event
 * This is used during path computation cause these two paths are strictly
 * correlated since the user proposed travels must guarantee that he will be able
 * to reach an event and then from the event location he must also be able
 * to reach also the following one
 */
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

    /**
     * Provide the total travel length of previous and following travels
     *
     * @return the total length
     */
    public float getTotalLength () {
        float prev = ( prevPath != null ) ? prevPath.getTotalLength() : 0;
        float foll = ( follPath != null ) ? follPath.getTotalLength() : 0;
        return prev + foll;
    }

    /**
     * Provide the total travel time of previous and following travels
     *
     * @return the total travel time
     */
    public long getTotalTime () {
        long prev = ( prevPath != null ) ? prevPath.getTotalTime() : 0;
        long foll = ( follPath != null ) ? follPath.getTotalTime() : 0;
        return prev + foll;
    }

    /**
     * Saves all the locations contained in all the travel components in the database
     */
    public void saveLocationsOnDB () {
        if ( prevPath != null ) {
            saveTravelLocations( prevPath.getMiniTravels() );
        }
        if ( follPath != null ) {
            saveTravelLocations( follPath.getMiniTravels() );
        }
    }

    /**
     * Since walking travel traits does not have start and end time this method
     * set them
     */
    public void fixTimes () {
        if ( prevPath != null ) {
            prevPath.fixTimes();
        }
        if ( follPath != null ) {
            follPath.fixTimes();
        }
    }

    /**
     * Saves all the locations contained in the specified list of travel components
     *
     * @param travel list of travel components that contains the locations to be saved
     */
    private void saveTravelLocations ( List < TravelComponent > travel ) {
        for ( TravelComponent comb : travel ) {
            comb.getDeparture().save();
            comb.getArrival().save();
        }
    }
}
