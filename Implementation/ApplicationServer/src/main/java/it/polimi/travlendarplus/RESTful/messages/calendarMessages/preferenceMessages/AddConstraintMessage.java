package it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarMessage;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

/**
 * This is the message abstract class sent in the body of an HTTP request to add a
 * constraint to be contained in a user's preference  profile, it is to be extended
 * by all constraint message classes
 */
public abstract class AddConstraintMessage extends CalendarMessage {

    private static final long serialVersionUID = -2248207969199151746L;

    private TravelMeanEnum concerns;

    public AddConstraintMessage () {
    }

    public AddConstraintMessage ( TravelMeanEnum concerns ) {
        this.concerns = concerns;
    }

    public TravelMeanEnum getConcerns () {
        return concerns;
    }

    public void setConcerns ( TravelMeanEnum concerns ) {
        this.concerns = concerns;
    }
}
