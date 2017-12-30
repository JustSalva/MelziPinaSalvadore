package it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarResponse;
import it.polimi.travlendarplus.entities.preferences.*;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a message class sent to reply with all needed info
 * to a typeOfEvent - related response (add or modify typeOfEvent request)
 */
public class TypeOfEventResponse extends CalendarResponse {

    private static final long serialVersionUID = 4793495605267399032L;

    private long id;
    private String name;
    private ParamFirstPath paramFirstPath;
    private List < PeriodOfDayConstraint > periodOfDayConstraints;
    private List < DistanceConstraint > distanceConstraints;
    private List < TravelMeanEnum > deactivate;

    public TypeOfEventResponse () {
    }

    public TypeOfEventResponse ( TypeOfEvent typeOfEvent ) {
        this.id = typeOfEvent.getId();
        this.name = typeOfEvent.getName();
        this.paramFirstPath = typeOfEvent.getParamFirstPath();
        this.deactivate = typeOfEvent.getDeactivate();
        this.distanceConstraints = new ArrayList <>();
        this.periodOfDayConstraints = new ArrayList <>();
        for ( Constraint constraint : typeOfEvent.getLimitedBy() ) {
            constraint.serializeResponse( this );
        }

    }

    public long getId () {
        return id;
    }

    public void setId ( long id ) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public ParamFirstPath getParamFirstPath () {
        return paramFirstPath;
    }

    public void setParamFirstPath ( ParamFirstPath paramFirstPath ) {
        this.paramFirstPath = paramFirstPath;
    }

    public List < PeriodOfDayConstraint > getperiodOfDayConstraints () {
        return periodOfDayConstraints;
    }

    public void setperiodOfDayConstraints ( List < PeriodOfDayConstraint > periodOfDayConstraints ) {
        this.periodOfDayConstraints = periodOfDayConstraints;
    }

    public void addPeriodOfDayConstraint ( PeriodOfDayConstraint periodOfDayConstraint ) {
        this.periodOfDayConstraints.add( periodOfDayConstraint );
    }

    public List < DistanceConstraint > getdistanceConstraints () {
        return distanceConstraints;
    }

    public void setdistanceConstraints ( List < DistanceConstraint > distanceConstraints ) {
        this.distanceConstraints = distanceConstraints;
    }

    public void addDistanceConstraint ( DistanceConstraint distanceConstraint ) {
        this.distanceConstraints.add( distanceConstraint );
    }

    public List < TravelMeanEnum > getDeactivate () {
        return deactivate;
    }

    public void setDeactivate ( List < TravelMeanEnum > deactivate ) {
        this.deactivate = deactivate;
    }
}
