package it.polimi.travlendarplus.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity (name = "ABSTRACT_CONSTRAINT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CONSTRAINT_TYPE")
public abstract class Constraint{

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int Id;

    public Constraint() {
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TravelMeanEnum concerns;

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
