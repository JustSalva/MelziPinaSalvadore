package it.polimi.travlendarplus.RESTful.messages.tripMessages;

import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

/**
 * This is an helper message class used to contain the name and the type of
 * the travel means that can be associated to a ticket
 */
public class AddPublicTravelMeanMessage extends TripMessage {

    private static final long serialVersionUID = 582651283252214691L;

    private String name;
    private TravelMeanEnum type;

    public AddPublicTravelMeanMessage () {
    }

    public AddPublicTravelMeanMessage ( String name, TravelMeanEnum type ) {
        this.name = name;
        this.type = type;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public TravelMeanEnum getType () {
        return type;
    }

    public void setType ( TravelMeanEnum type ) {
        this.type = type;
    }
}
