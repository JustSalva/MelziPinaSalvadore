package it.polimi.travlendarplus.retrofit.body;


/**
 * Body to be sent to server to add a breakEvent.
 */
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
