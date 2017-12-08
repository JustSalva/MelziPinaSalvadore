package it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

public class AddDistanceConstraintMessage extends AddConstraintMessage {

    private static final long serialVersionUID = 3192731780629920451L;

    private int minLenght;
    private int maxLenght;

    public AddDistanceConstraintMessage() {
    }

    public AddDistanceConstraintMessage( TravelMeanEnum concerns, int minLenght, int maxLenght ) {
        super( concerns );
        this.minLenght = minLenght;
        this.maxLenght = maxLenght;
    }

    public int getMinLenght() {
        return minLenght;
    }

    public void setMinLenght( int minLenght ) {
        this.minLenght = minLenght;
    }

    public int getMaxLenght() {
        return maxLenght;
    }

    public void setMaxLenght( int maxLenght ) {
        this.maxLenght = maxLenght;
    }
}
