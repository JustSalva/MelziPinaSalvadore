package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.util.List;

/**
 * This JPA class is used to store in the database all the alternative travels
 * requested by an user for a specific event, those travels are to be deleted after the user choose one
 * or after a given amount of time
 */
@Entity( name = "ALTERNATIVE_TRAVELS" )
public class AlternativeTravel extends EntityWithLongKey {

    private static final long serialVersionUID = 6240399376599177285L;

    /**
     * List of feasible travel proposed as an alternative to the selected one
     */
    @JoinTable( name = "ALTERNATIVE_TRAVEL" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List < Travel > alternativeTravels;

    /**
     * Event the proposed travel are related with
     */
    @OneToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "RELATED_EVENT", nullable = false )
    private Event eventRelated;

    /**
     * Owner of the event
     */
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "TRAVEL_OWNER", nullable = false )
    private User user;

    public AlternativeTravel () {
    }

    public AlternativeTravel ( List < Travel > alternativeTravels,
                               Event eventRelated, User user ) {
        this.alternativeTravels = alternativeTravels;
        this.eventRelated = eventRelated;
        this.user = user;
    }

    /**
     * Provide a selected travel, from the list of proposed ones
     *
     * @param id identifier of the selected travel
     * @return the requested travel
     * @throws EntityNotFoundException if a non existent travel is requested
     */
    public Travel getSelectedTravel ( long id ) throws EntityNotFoundException {
        Travel selectedTravel = alternativeTravels.stream()
                .filter( travel -> travel.getId() == id )
                .findFirst().orElse( null );
        if ( selectedTravel == null ) {
            throw new EntityNotFoundException();
        }
        return selectedTravel;
    }

    public List < Travel > getAlternativeTravels () {
        return alternativeTravels;
    }

    public void setAlternativeTravels ( List < Travel > alternativeTravels ) {
        this.alternativeTravels = alternativeTravels;
    }

    public Event getEventRelated () {
        return eventRelated;
    }

    public void setEventRelated ( Event eventRelated ) {
        this.eventRelated = eventRelated;
    }

    public User getUser () {
        return user;
    }

    public void setUser ( User user ) {
        this.user = user;
    }
}
