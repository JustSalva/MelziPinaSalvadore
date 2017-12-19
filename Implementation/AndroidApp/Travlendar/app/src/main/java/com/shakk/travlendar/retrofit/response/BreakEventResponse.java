package com.shakk.travlendar.retrofit.response;


import com.shakk.travlendar.Timestamp;

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
