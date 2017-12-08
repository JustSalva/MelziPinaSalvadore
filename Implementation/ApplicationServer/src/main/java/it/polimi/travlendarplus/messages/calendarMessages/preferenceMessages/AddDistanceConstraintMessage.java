package it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

public class AddDistanceConstraintMessage extends AddConstraintMessage {

    private static final long serialVersionUID = 3192731780629920451L;

    private int minLength;
    private int maxLength;

    public AddDistanceConstraintMessage() {
    }

    public AddDistanceConstraintMessage( TravelMeanEnum concerns, int minLength, int maxLength ) {
        super( concerns );
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength( int minLength ) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength( int maxLength ) {
        this.maxLength = maxLength;
    }
}
