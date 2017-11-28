package it.polimi.travlendarplus.entity;

public class DistanceConstraint extends Constraint{
    private int minLenght;
    private int maxLenght;

    public DistanceConstraint(TravelMean concerns, int minLenght, int maxLenght) {
        super(concerns);
        this.minLenght = minLenght;
        this.maxLenght = maxLenght;
    }

    public int getMinLenght() {
        return minLenght;
    }

    public void setMinLenght(int minLenght) {
        this.minLenght = minLenght;
    }

    public int getMaxLenght() {
        return maxLenght;
    }

    public void setMaxLenght(int maxLenght) {
        this.maxLenght = maxLenght;
    }
}
