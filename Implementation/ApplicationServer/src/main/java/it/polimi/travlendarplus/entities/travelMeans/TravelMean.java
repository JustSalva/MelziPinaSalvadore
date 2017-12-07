package it.polimi.travlendarplus.entities.travelMeans;


import it.polimi.travlendarplus.entities.EntityWithLongKey;

import javax.persistence.*;

@Entity(name = "ABSTRACT_TRAVEL_MEAN")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE")
public abstract class TravelMean extends EntityWithLongKey {

    private static final long serialVersionUID = -2649047659064813632L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private TravelMeanEnum type;

    @Column(name = "ECO")
    private float eco;

    public TravelMean() {
    }

    public TravelMean(String name, TravelMeanEnum type, float eco) {
        this.name = name;
        this.type = type;
        this.eco = eco;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TravelMeanEnum getType() {
        return type;
    }

    public void setType(TravelMeanEnum type) {
        this.type = type;
    }

    public float getEco() {
        return eco;
    }

    public void setEco(float eco) {
        this.eco = eco;
    }

    @Override
    public String toString() {
        return "TravelMean{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", eco=" + eco +
                '}';
    }
}
