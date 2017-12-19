package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.preferenceMessages.TypeOfEventResponse;
import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import javax.persistence.*;

/**
 * This JPA class represent a generic constraint, is to be extended by all constraints classes
 */
@Entity( name = "ABSTRACT_CONSTRAINT" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "CONSTRAINT_TYPE" )
public abstract class Constraint extends EntityWithLongKey {

    private static final long serialVersionUID = -8843367479749790229L;

    /**
     * Represent which travel mean a constraint is relative to
     */
    @Column( nullable = false )
    @Enumerated( EnumType.STRING )
    private TravelMeanEnum concerns;

    public Constraint () {
    }

    public Constraint ( TravelMeanEnum concerns ) {
        this.concerns = concerns;
    }

    public TravelMeanEnum getConcerns () {
        return concerns;
    }

    public void setConcerns ( TravelMeanEnum concerns ) {
        this.concerns = concerns;
    }

    /**
     * Verifies if a travel component respect this constraint
     *
     * @param travelComponent component to be checked
     * @return true it the travel component respect this constraint, false otherwise
     */
    public abstract boolean respectConstraint ( TravelComponent travelComponent );

    /**
     * Visitor method used to serialize a correct response to the client,
     * useful in order to handle constraint forwarding transparently
     *
     * @param typeOfEventResponse message that is to be sent to the client
     */
    public abstract void serializeResponse ( TypeOfEventResponse typeOfEventResponse );
}
