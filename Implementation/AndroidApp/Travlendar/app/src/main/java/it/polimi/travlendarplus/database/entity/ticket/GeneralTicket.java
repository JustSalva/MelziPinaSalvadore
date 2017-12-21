package it.polimi.travlendarplus.database.entity.ticket;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

/**
 * DB general ticket entity.
 */
public class GeneralTicket {
    @ColumnInfo(name = "line_name")
    private String lineName;

    @Embedded
    private PathTicket pathTicket;

    public GeneralTicket(String lineName, PathTicket pathTicket) {
        this.lineName = lineName;
        this.pathTicket = pathTicket;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public PathTicket getPathTicket() {
        return pathTicket;
    }

    public void setPathTicket(PathTicket pathTicket) {
        this.pathTicket = pathTicket;
    }
}
