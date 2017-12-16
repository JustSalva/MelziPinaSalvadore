package it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarMessage;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

public abstract class AddConstraintMessage extends CalendarMessage {

    private static final long serialVersionUID = -2248207969199151746L;

    private TravelMeanEnum concerns;

    public AddConstraintMessage() {
    }

    public AddConstraintMessage( TravelMeanEnum concerns ) {
        this.concerns = concerns;
    }

    public TravelMeanEnum getConcerns() {
        return concerns;
    }

    public void setConcerns( TravelMeanEnum concerns ) {
        this.concerns = concerns;
    }
}
