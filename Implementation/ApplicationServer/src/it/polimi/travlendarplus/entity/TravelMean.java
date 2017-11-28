package it.polimi.travlendarplus.entity;

public abstract class TravelMean {
    private String name;
    private int speed; //TODO unità di misura?
    private float eco;

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
