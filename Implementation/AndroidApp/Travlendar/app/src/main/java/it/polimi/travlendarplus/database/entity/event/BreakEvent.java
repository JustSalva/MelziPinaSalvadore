package it.polimi.travlendarplus.database.entity.event;

import android.arch.persistence.room.ColumnInfo;

import it.polimi.travlendarplus.DateUtility;

/**
 * DB break event entity.
 */
public class BreakEvent {

    @ColumnInfo(name = "minimum_time")
    private long minimumTime;

    public BreakEvent(long minimumTime) {
        this.minimumTime = minimumTime;
    }

    public long getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(long minimumTime) {
        this.minimumTime = minimumTime;
    }

    @Override
    public String toString() {
        return "Minimum duration: " + DateUtility.getHHmmFromSeconds(minimumTime) + "\n";
    }
}
