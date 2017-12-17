package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.TypeOfEventResponse;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity( name = "PERIOD_OF_DAY_CONSTRAINT" )
@DiscriminatorValue( "PERIOD_OF_DAY" )
public class PeriodOfDayConstraint extends Constraint {

    private static final long serialVersionUID = -4287946262554067696L;

    private static final long SECONDS_IN_A_DAY = 24 * 60 * 60;

    @Column( name = "MIN_HOUR" )
    private long minHour; //In seconds from 00.00 of the day

    @Column( name = "MAX_HOUR" )
    private long maxHour; //max value = 24 h

    public PeriodOfDayConstraint ( TravelMeanEnum concerns, long minHour, long maxHour ) {
        super( concerns );
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public PeriodOfDayConstraint () {
    }

    public static PeriodOfDayConstraint load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( PeriodOfDayConstraint.class, key );
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

    @Override
    public boolean respectConstraint ( TravelComponent travelComponent ) {
        long startingTimeInDay = travelComponent.getStartingTime().getEpochSecond() % ( SECONDS_IN_A_DAY );
        long endingTimeInDay = travelComponent.getEndingTime().getEpochSecond() % ( SECONDS_IN_A_DAY );
        return startingTimeInDay >= minHour &&
                endingTimeInDay <= maxHour;
    }

    @Override
    public void serializeResponse ( TypeOfEventResponse typeOfEventResponse ) {
        typeOfEventResponse.addPeriodOfDayConstraint( this );
    }

}
