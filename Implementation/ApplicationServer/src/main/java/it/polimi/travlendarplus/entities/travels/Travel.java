package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.Timestamp;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This JPA class represent a travel that is attached to a scheduled event
 */
@Entity( name = "TRAVEL" )
public class Travel extends EntityWithLongKey {

    private static final long serialVersionUID = 3515840069172744899L;

    /**
     * List of travel segment that compose the overall travel
     */
    @JoinTable( name = "TRAVEL_COMPONENTS" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < TravelComponent > miniTravels;

    /**
     * Timestamp in Unix time that memorize the last update of an event
     */
    @Embedded
    private Timestamp lastUpdate;

    public Travel () {
        this.lastUpdate = new Timestamp();
        this.miniTravels = new ArrayList <>();
    }

    public Travel ( ArrayList < TravelComponent > miniTravels ) {
        this();
        this.miniTravels = miniTravels;
    }

    /**
     * Allows to load a Travel class from the database
     *
     * @param key primary key of the travel tuple
     * @return the requested tuple as a Travel class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static Travel load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( Travel.class, key );
    }

    public List < TravelComponent > getMiniTravels () {
        return Collections.unmodifiableList( miniTravels );
    }

    public void setMiniTravels ( ArrayList < TravelComponent > miniTravels ) {
        this.miniTravels = miniTravels;
    }

    public void setMiniTravels ( List < TravelComponent > miniTravels ) {
        this.miniTravels = miniTravels;
    }

    /**
     * Provide the amount of time (in seconds) that are to be spent to perform a travel
     *
     * @return the requested travel time
     */
    public long getTravelTime () {
        long totalSeconds = 0;
        for ( TravelComponent component : miniTravels )
            totalSeconds += component.deltaTimeInSeconds();
        return totalSeconds;
    }

    /**
     * Provide the overall amount of time (in seconds) between departure and arrival time
     *
     * @return the requested travel time
     */
    public long getTotalTime () {
        return getEndingTime().getEpochSecond() - getStartingTime().getEpochSecond();
    }

    /**
     * Provide the total length ot a travel
     *
     * @return the total length in Km
     */
    public float getTotalLength () {
        float length = 0;
        for ( TravelComponent comp : miniTravels )
            length += comp.getLength();
        return length;
    }

    public Instant getStartingTime () {
        return ( miniTravels.get( 0 ).getStartingTime().getEpochSecond() > 0 )
                ? miniTravels.get( 0 ).getStartingTime()
                : correctStartingTime();
    }

    private Instant correctStartingTime () {
        int i = 0;
        long timeToSub = 0;
        while ( miniTravels.get( i ).getStartingTime().getEpochSecond() == 0 ) {
            timeToSub += miniTravels.get( i ).getEndingTime().getEpochSecond();
            i++;
        }
        long startingTime = miniTravels.get( i ).getStartingTime().getEpochSecond() - timeToSub;
        return Instant.ofEpochSecond( startingTime );
    }

    public Instant getEndingTime () {
        return ( miniTravels.get( miniTravels.size() - 1 ).getStartingTime().getEpochSecond() > 0 ) ?
                miniTravels.get( miniTravels.size() - 1 ).getEndingTime() : correctEndingTime();
    }

    private Instant correctEndingTime () {
        int i = miniTravels.size() - 1;
        long timeToAdd = 0;
        while ( miniTravels.get( i ).getStartingTime().getEpochSecond() == 0 ) {
            timeToAdd += miniTravels.get( i ).getEndingTime().getEpochSecond();
            i--;
        }
        long endingTime = miniTravels.get( i ).getEndingTime().getEpochSecond() + timeToAdd;
        return Instant.ofEpochSecond( endingTime );
    }

    private Location getStartingLocation () {
        return miniTravels.get( 0 ).getDeparture();
    }

    private Location getEndingLocation () {
        return miniTravels.get( miniTravels.size() - 1 ).getArrival();
    }

    public Timestamp getLastUpdate () {
        return lastUpdate;
    }

    public void setLastUpdate ( Timestamp lastUpdate ) {
        this.lastUpdate = lastUpdate;
    }

    public int numberOfChanges () {
        return miniTravels.size() - 1;
    }

    public void fixTimes () {
        int i = 0;
        // It goes into the while if the path starts with walking components.
        while ( miniTravels.get( i ).getStartingTime().getEpochSecond() == 0 ) {
            i++;
        }
        // Adjusting starting and ending time for paths starting with walking components.
        while ( i > 0 ) {
            long stTimeFoll = miniTravels.get( i ).getStartingTime().getEpochSecond();
            long walkingDuration = miniTravels.get( i - 1 ).getEndingTime().getEpochSecond();
            miniTravels.get( i - 1 ).setStartingTime( Instant.ofEpochSecond( stTimeFoll - walkingDuration ) );
            miniTravels.get( i - 1 ).setEndingTime( Instant.ofEpochSecond( stTimeFoll ) );
            i--;
        }
        // Adjusting walking component planned after a component related to public mean.
        while ( i < miniTravels.size() ) {
            if ( miniTravels.get( i ).getStartingTime().getEpochSecond() == 0 ) {
                long endTimePrev = miniTravels.get( i - 1 ).getEndingTime().getEpochSecond();
                long walkingDuration = miniTravels.get( i ).getEndingTime().getEpochSecond();
                miniTravels.get( i ).setStartingTime( Instant.ofEpochSecond( endTimePrev ) );
                miniTravels.get( i ).setEndingTime( Instant.ofEpochSecond( endTimePrev + walkingDuration ) );
            }
            i++;
        }
    }

    @Override
    public String toString () {
        return "Travel{" +
                ", miniTravels=" + miniTravels +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
