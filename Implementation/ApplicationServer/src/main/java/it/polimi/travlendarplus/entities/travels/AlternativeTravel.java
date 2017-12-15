package it.polimi.travlendarplus.entities.travels;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.util.List;

@Entity(name = "ALTERNATIVE_TRAVELS")
public class AlternativeTravel extends EntityWithLongKey {

    private static final long serialVersionUID = 6240399376599177285L;

    @JoinTable( name = "ALTERNATIVE_TRAVEL" )
    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List<Travel> alternativeTravels;

    @OneToOne( fetch=FetchType.LAZY )
    @JoinColumn(name="RELATED_EVENT", nullable = false)
    private Event eventRelated;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(name = "TRAVEL_OWNER", nullable = false)
    private User user;

    public AlternativeTravel() {
    }

    public AlternativeTravel( List< Travel > alternativeTravels, Event eventRelated, User user ) {
        this.alternativeTravels = alternativeTravels;
        this.eventRelated = eventRelated;
        this.user = user;
    }

    public Travel getSelectedTravel( long id ) throws EntityNotFoundException{
        Travel selectedTravel = alternativeTravels.stream()
                .filter( travel -> travel.getId() == id )
                .findFirst().orElse( null );
        if ( selectedTravel == null){
            throw new EntityNotFoundException();
        }
        return selectedTravel;
    }

    public List< Travel > getAlternativeTravels() {
        return alternativeTravels;
    }

    public void setAlternativeTravels( List< Travel > alternativeTravels ) {
        this.alternativeTravels = alternativeTravels;
    }

    public Event getEventRelated() {
        return eventRelated;
    }

    public void setEventRelated( Event eventRelated ) {
        this.eventRelated = eventRelated;
    }

    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }
}
