package com.shakk.travlendar.retrofit.body;


public class BreakEventBody {

    private String name;
    private String startingTime;
    private String endingTime;
    private long minimumTime;

    public BreakEventBody(String name, String startingTime, String endingTime, long minimumTime) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.minimumTime = minimumTime;
    }
}
