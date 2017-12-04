package it.polimi.travlendarplus.entities.preferences;

import it.polimi.travlendarplus.entities.EntityWithLongKey;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;

import javax.persistence.*;

@Entity (name = "ABSTRACT_CONSTRAINT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CONSTRAINT_TYPE")
public abstract class Constraint extends EntityWithLongKey {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TravelMeanEnum concerns;

    public Constraint() {
    }

    public Constraint(TravelMeanEnum concerns) {
        this.concerns = concerns;
    }

    public TravelMeanEnum getConcerns() {
        return concerns;
    }

    public void setConcerns(TravelMeanEnum concerns) {
        this.concerns = concerns;
    }
}
