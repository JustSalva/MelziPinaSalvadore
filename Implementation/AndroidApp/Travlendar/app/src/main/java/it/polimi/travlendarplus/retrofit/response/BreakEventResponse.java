package it.polimi.travlendarplus.retrofit.response;


import it.polimi.travlendarplus.Timestamp;

public class BreakEventResponse {

    private long id;
    private String name;
    private long minimumTime;
    private Timestamp startingTime;
    private Timestamp endingTime;
    private boolean isScheduled;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getMinimumTime() {
        return minimumTime;
    }

    public Timestamp getStartingTime() {
        return startingTime;
    }

    public Timestamp getEndingTime() {
        return endingTime;
    }

    public boolean isScheduled() {
        return isScheduled;
    }
}
