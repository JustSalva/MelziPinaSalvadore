package it.polimi.travlendarplus;


import java.util.ArrayList;
import java.util.List;

public class Preference {
    private long id;
    private String name;
    private ParamFirstPath paramFirstPath;
    private List<PeriodConstraint> periodOfDayConstraints;
    private List<DistanceConstraint> distanceConstraints;
    private List<TravelMeanEnum> deactivate;

    public Preference() {
        this.id = 0;
        this.name = "Normal";
        this.paramFirstPath = ParamFirstPath.MIN_TIME;
        this.periodOfDayConstraints = new ArrayList<>();
        this.distanceConstraints = new ArrayList<>();
        this.deactivate = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ParamFirstPath getParamFirstPath() {
        return paramFirstPath;
    }

    public void setParamFirstPath(ParamFirstPath paramFirstPath) {
        this.paramFirstPath = paramFirstPath;
    }

    public List<PeriodConstraint> getPeriodOfDayConstraints() {
        return periodOfDayConstraints;
    }

    public PeriodConstraint getPeriodOfDayConstraint(String travelMean) {
        for (PeriodConstraint constraint : periodOfDayConstraints) {
            if (constraint.getConcerns().equals(TravelMeanEnum.getEnumFromString(travelMean))) {
                return constraint;
            }
        }
        return null;
    }

    public List<DistanceConstraint> getDistanceConstraints() {
        return distanceConstraints;
    }

    public DistanceConstraint getDistanceConstraint(String travelMean) {
        for (DistanceConstraint constraint : distanceConstraints) {
            if (constraint.getConcerns().equals(TravelMeanEnum.getEnumFromString(travelMean))) {
                return constraint;
            }
        }
        return null;
    }

    public boolean isActivated(TravelMeanEnum travelMean) {
        return !deactivate.contains(travelMean);
    }

    public List<TravelMeanEnum> getDeactivate() {
        return deactivate;
    }

    public static class PeriodConstraint {
        private long id;
        private TravelMeanEnum concerns;
        private int minHour;
        private int maxHour;

        public PeriodConstraint(long id, TravelMeanEnum concerns, int minHour, int maxHour) {
            this.id = id;
            this.concerns = concerns;
            this.minHour = minHour;
            this.maxHour = maxHour;
        }

        public long getId() {
            return id;
        }

        public TravelMeanEnum getConcerns() {
            return concerns;
        }

        public int getMinHour() {
            return minHour;
        }

        public void setMinHour(int minHour) {
            this.minHour = minHour;
        }

        public int getMaxHour() {
            return maxHour;
        }

        public void setMaxHour(int maxHour) {
            this.maxHour = maxHour;
        }
    }

    public static class DistanceConstraint {
        private long id;
        private TravelMeanEnum concerns;
        private int minLength;
        private int maxLength;

        public DistanceConstraint(long id, TravelMeanEnum concerns, int minLength, int maxLength) {
            this.id = id;
            this.concerns = concerns;
            this.minLength = minLength;
            this.maxLength = maxLength;
        }

        public long getId() {
            return id;
        }

        public TravelMeanEnum getConcerns() {
            return concerns;
        }

        public int getMinLength() {
            return minLength;
        }

        public void setMinLength(int minLength) {
            this.minLength = minLength;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public enum ParamFirstPath {
        MIN_COST ("Minimum cost"),
        MIN_LENGTH ("Minimum length"),
        MIN_TIME ("Minimum time"),
        ECO_PATH ("Eco-friendly");

        private String text;

        ParamFirstPath(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static ParamFirstPath getEnumFromString(String text) {
            switch (text) {
                case "Minimum cost":
                    return MIN_COST;
                case "Minimum length":
                    return MIN_LENGTH;
                case "Minimum time":
                    return MIN_TIME;
                default:
                    return ECO_PATH;
            }
        }
    }

    public enum TravelMeanEnum {
        BIKE("Bike"),
        BUS("Bus"),
        BY_FOOT("By Foot"),
        CAR("Car"),
        SUBWAY("Subway"),
        TRAIN("Train"),
        TRAM("Tram"),
        SHARING_BIKE("Sharing Bike"),
        SHARING_CAR("Sharing Car");

        private final String travelMean;

        TravelMeanEnum(String travelMean) {
            this.travelMean = travelMean;
        }

        public String getTravelMean() {
            return travelMean;
        }

        public static TravelMeanEnum getEnumFromString(String text) {
            switch (text) {
                case "Bike":
                    return BIKE;
                case "Bus":
                    return BUS;
                case "By Foot":
                    return BY_FOOT;
                case "Car":
                    return CAR;
                case "Subway":
                    return SUBWAY;
                case "Train":
                    return TRAIN;
                case "Tram":
                    return TRAM;
                case "Sharing Bike":
                    return SHARING_BIKE;
                default:
                    return SHARING_CAR;
            }
        }
    }
}
