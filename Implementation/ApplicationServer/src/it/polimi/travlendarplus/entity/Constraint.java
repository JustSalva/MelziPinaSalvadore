package it.polimi.travlendarplus.entity;

public abstract class Constraint {
    private TravelMean concerns;

    public Constraint(TravelMean concerns) {
        this.concerns = concerns;
    }

    public TravelMean getConcerns() {
        return concerns;
    }

    public void setConcerns(TravelMean concerns) {
        this.concerns = concerns;
    }
}
