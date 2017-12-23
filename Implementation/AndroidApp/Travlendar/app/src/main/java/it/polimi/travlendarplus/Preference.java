package it.polimi.travlendarplus;


import java.util.ArrayList;
import java.util.List;

/**
 * Contains info regarding a preference.
 */
public class Preference {
    private long id;
    private String name;
    private ParamFirstPath paramFirstPath;
    private List<PeriodConstraint> periodOfDayConstraints;
    private List<DistanceConstraint> distanceConstraints;
    private List<TravelMeanEnum> deactivate;

    /**
     * Constructor to create the standard preference with id = 0;
     */
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

    /**
     * @param travelMean travel mean related to the constraint.
     * @return periodConstraint related to the travel mean.
     */
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

    /**
     * @param travelMean travel mean related to the constraint.
     * @return distanceConstraint relateed to the travel mean.
     */
    public DistanceConstraint getDistanceConstraint(String travelMean) {
        for (DistanceConstraint constraint : distanceConstraints) {
            if (constraint.getConcerns().equals(TravelMeanEnum.getEnumFromString(travelMean))) {
                return constraint;
            }
        }
        return null;
    }

    /**
     * @param travelMean travel mean related to the constraint.
     * @return true if the travel mean is activated in a constraint, false otherwise.
     */
    public boolean isActivated(TravelMeanEnum travelMean) {
        return !deactivate.contains(travelMean);
    }

    public List<TravelMeanEnum> getDeactivate() {
        return deactivate;
    }

    /**
     * Constraint regarding the duration of an event.
     */
    public static class PeriodConstraint {
        private long id;
        private TravelMeanEnum concerns;
        private long minHour;
        private long maxHour;

        public PeriodConstraint(long id, TravelMeanEnum concerns, long minHour, long maxHour) {
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

        public long getMinHour() {
            return minHour;
        }

        public void setMinHour(long minHour) {
            this.minHour = minHour;
        }

        public long getMaxHour() {
            return maxHour;
        }

        public void setMaxHour(long maxHour) {
            this.maxHour = maxHour;
        }
    }

    /**
     * Constraint regarding the distance of an event.
     */
    public static class DistanceConstraint {
        private long id;
        private TravelMeanEnum concerns;
        private float minLength;
        private float maxLength;

        public DistanceConstraint(long id, TravelMeanEnum concerns, float minLength, float maxLength) {
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

        public float getMinLength() {
            return minLength;
        }

        public void setMinLength(float minLength) {
            this.minLength = minLength;
        }

        public float getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(float maxLength) {
            this.maxLength = maxLength;
        }
    }

    /**
     * Enum containing values indicating the type of path preferred by the user.
     */
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

    /**
     * Enum containing travel means that can be used by the user.
     */
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
