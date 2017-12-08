package it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.entities.preferences.ParamFirstPath;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.messages.calendarMessages.CalendarMessage;

import java.util.List;

public class AddTypeOfEventMessage extends CalendarMessage {

    private static final long serialVersionUID = -5859779994509147722L;

    private String name;
    private ParamFirstPath paramFirstPath;
    private List<AddPeriodConstraintMessage> limitedByPeriod;
    private List<AddDistanceConstraintMessage> limitedByDistance;
    private List<TravelMeanEnum > deactivate;

    public AddTypeOfEventMessage() {
    }

    public AddTypeOfEventMessage( String name, ParamFirstPath paramFirstPath,
                                  List< AddPeriodConstraintMessage > limitedByPeriod,
                                  List< AddDistanceConstraintMessage > limitedByDistance,
                                  List< TravelMeanEnum > deactivate ) {
        this.name = name;
        this.paramFirstPath = paramFirstPath;
        this.limitedByPeriod = limitedByPeriod;
        this.limitedByDistance = limitedByDistance;
        this.deactivate = deactivate;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public ParamFirstPath getParamFirstPath() {
        return paramFirstPath;
    }

    public void setParamFirstPath( ParamFirstPath paramFirstPath ) {
        this.paramFirstPath = paramFirstPath;
    }

    public List< AddPeriodConstraintMessage > getLimitedByPeriod() {
        return limitedByPeriod;
    }

    public void setLimitedByPeriod( List< AddPeriodConstraintMessage > limitedByPeriod ) {
        this.limitedByPeriod = limitedByPeriod;
    }

    public List< AddDistanceConstraintMessage > getLimitedByDistance() {
        return limitedByDistance;
    }

    public void setLimitedByDistance( List< AddDistanceConstraintMessage > limitedByDistance ) {
        this.limitedByDistance = limitedByDistance;
    }

    public List< TravelMeanEnum > getDeactivate() {
        return deactivate;
    }

    public void setDeactivate( List< TravelMeanEnum > deactivate ) {
        this.deactivate = deactivate;
    }
}
