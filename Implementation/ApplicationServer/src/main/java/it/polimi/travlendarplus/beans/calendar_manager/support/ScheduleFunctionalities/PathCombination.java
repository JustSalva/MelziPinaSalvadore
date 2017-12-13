package it.polimi.travlendarplus.beans.calendar_manager.support.ScheduleFunctionalities;

import it.polimi.travlendarplus.entities.travels.Travel;

public class PathCombination {
    private Travel prevPath;
    private Travel follPath;

    public PathCombination(Travel prevPath, Travel follPath) {
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
        return prevPath.getTotalLength() + follPath.getTotalLength();
    }

    public long getTotalTime() {
        return prevPath.getTotalTime() + follPath.getTotalTime();
    }
}
