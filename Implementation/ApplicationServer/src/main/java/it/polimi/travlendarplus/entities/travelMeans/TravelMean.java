package it.polimi.travlendarplus.entities.travelMeans;


import it.polimi.travlendarplus.entities.GeneralEntity;

import javax.persistence.*;

@Entity(name = "ABSTRACT_TRAVEL_MEAN")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE")
public abstract class TravelMean extends GeneralEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SPEED")
    private int speed; //TODO unità di misura?

    @Column(name = "ECO")
    private float eco;

    public TravelMean() {
    }

    public TravelMean(String name, int speed, float eco) {
        this.name = name;
        this.speed = speed;
        this.eco = eco;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public float getEco() {
        return eco;
    }

    public void setEco(float eco) {
        this.eco = eco;
    }
}
