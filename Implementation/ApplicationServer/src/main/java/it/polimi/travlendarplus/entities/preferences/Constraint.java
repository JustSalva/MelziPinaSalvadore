package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
import it.polimi.travlendarplus.entities.travels.TravelComponent;

import javax.persistence.*;

@Entity( name = "ABSTRACT_CONSTRAINT" )
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn( name = "CONSTRAINT_TYPE" )
public abstract class Constraint extends EntityWithLongKey {

    private static final long serialVersionUID = -8843367479749790229L;

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

    public abstract boolean respectConstraint ( TravelComponent travelComponent );
}
