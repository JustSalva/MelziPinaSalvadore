package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.TypeOfEventResponse;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This JPA class represent a constraint on the time of the day at which a travel mean should occur
 */
@Entity( name = "PERIOD_OF_DAY_CONSTRAINT" )
@DiscriminatorValue( "PERIOD_OF_DAY" )
public class PeriodOfDayConstraint extends Constraint {

    private static final long serialVersionUID = -4287946262554067696L;

    private static final long SECONDS_IN_A_DAY = 24 * 60 * 60;

    /**
     * Minimum value (in seconds) from 00.00 of the day at which a travel mean can be used
     * (max value = 86400)
     */
    @Column( name = "MIN_HOUR" )
    private long minHour;

    /**
     * Maximum value (in seconds) from 00.00 of the day at which a travel mean can be used
     * (max value = 86400)
     */
    @Column( name = "MAX_HOUR" )
    private long maxHour;

    public PeriodOfDayConstraint ( TravelMeanEnum concerns, long minHour, long maxHour ) {
        super( concerns );
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public PeriodOfDayConstraint () {
    }

    /**
     * Allows to load a PeriodOfDayConstraint class from the database
     *
     * @param key primary key of the periodOfDayConstraint tuple
     * @return the requested tuple as a PeriodOfDayConstraint class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean respectConstraint ( TravelComponent travelComponent ) {
        long startingTimeInDay = travelComponent.getStartingTime().getEpochSecond() % ( SECONDS_IN_A_DAY );
        long endingTimeInDay = travelComponent.getEndingTime().getEpochSecond() % ( SECONDS_IN_A_DAY );
        return startingTimeInDay >= minHour &&
                endingTimeInDay <= maxHour;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serializeResponse ( TypeOfEventResponse typeOfEventResponse ) {
        typeOfEventResponse.addPeriodOfDayConstraint( this );
    }

}
