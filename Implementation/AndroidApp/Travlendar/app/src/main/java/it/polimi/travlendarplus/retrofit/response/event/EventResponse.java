package it.polimi.travlendarplus.retrofit.response.event;

import it.polimi.travlendarplus.FeasiblePath;
import it.polimi.travlendarplus.Position;
import it.polimi.travlendarplus.Timestamp;

/**
 * Event parsed from the JSON returned by the server.
 */
public class EventResponse {

    private String name;
    private String description;
    private long id;
    private EventType type;
    private Position eventLocation;
    private Position departure;
    private FeasiblePath feasiblePath;
    private Timestamp startingTime;
    private Timestamp endingTime;
    private boolean isScheduled;
    private boolean prevLocChoice;
    private boolean travelAtLastChoice;

    public String getName () {
        return name;
    }

    public String getDescription () {
        return description;
    }

    public long getId () {
        return id;
    }

    public EventType getType () {
        return type;
    }

    public Position getEventLocation () {
        return eventLocation;
    }

    public Position getDeparture () {
        return departure;
    }

    public FeasiblePath getFeasiblePath () {
        return feasiblePath;
    }

    public Timestamp getStartingTime () {
        return startingTime;
    }

    public Timestamp getEndingTime () {
        return endingTime;
    }

    public boolean isScheduled () {
        return isScheduled;
    }

    public boolean isPrevLocChoice () {
        return prevLocChoice;
    }

    public boolean isTravelAtLastChoice () {
        return travelAtLastChoice;
    }

    public class EventType {
        private long id;

        public long getId () {
            return id;
        }
    }
}
