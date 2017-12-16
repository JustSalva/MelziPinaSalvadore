package it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

public class AddPeriodConstraintMessage extends AddConstraintMessage {

    private static final long serialVersionUID = -6577930709987006843L;

    private long minHour; //In seconds from 00.00 of the day
    private long maxHour; //max value = 24 h

    public AddPeriodConstraintMessage () {
    }

    public AddPeriodConstraintMessage ( TravelMeanEnum concerns, long minHour, long maxHour ) {
        super( concerns );
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public long getMinHour () {
        return minHour;
    }

    public void setMinHour ( long minHour ) {
        this.minHour = minHour;
    }

    public long getMaxHour () {
        return maxHour;
    }

    public void setMaxHour ( long maxHour ) {
        this.maxHour = maxHour;
    }
}
