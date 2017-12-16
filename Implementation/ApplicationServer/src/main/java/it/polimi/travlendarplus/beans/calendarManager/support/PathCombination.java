package it.polimi.travlendarplus.beans.calendarManager.support;

import it.polimi.travlendarplus.entities.travels.Travel;

public class PathCombination {
    private Travel prevPath;
    private Travel follPath;

    public PathCombination( Travel prevPath, Travel follPath ) {
        this.prevPath = prevPath;
        this.follPath = follPath;
    }

    public Travel getPrevPath() {
        return prevPath;
    }

    public Travel getFollPath() {
        return follPath;
    }

    @Override
    public String toString() {
        return "PathCombination{" +
                "prevPath=" + prevPath +
                ", follPath=" + follPath +
                '}';
    }

    public float getTotalLength() {
        float prev = ( prevPath != null ) ? prevPath.getTotalLength() : 0;
        float foll = ( follPath != null ) ? follPath.getTotalLength() : 0;
        return prev + foll;
    }

    public long getTotalTime() {
        long prev = ( prevPath != null ) ? prevPath.getTotalTime() : 0;
        long foll = ( follPath != null ) ? follPath.getTotalTime() : 0;
        return prev + foll;
    }
}
