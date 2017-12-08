package it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

import java.util.List;

public class ModifyTypeOfEventMessage extends AddTypeOfEventMessage {

    private static final long serialVersionUID = 1759339803772382125L;

    private long id;

    public ModifyTypeOfEventMessage() {
    }

    public ModifyTypeOfEventMessage( String name, ParamFirstPath paramFirstPath, List< AddPeriodConstraintMessage > limitedByPeriod, List< AddDistanceConstraintMessage > limitedByDistance, List< TravelMeanEnum > deactivate, long id ) {
        super( name, paramFirstPath, limitedByPeriod, limitedByDistance, deactivate );
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }
}
