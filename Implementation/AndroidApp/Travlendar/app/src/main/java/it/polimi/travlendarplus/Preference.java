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
    private List < PeriodConstraint > periodOfDayConstraints;
    private List < DistanceConstraint > distanceConstraints;
    private List < TravelMeanEnum > deactivate;

    /**
     * Constructor to create the standard preference with id = 0;
     */
    public Preference () {
        this.id = 0;
        this.name = "Normal";
        this.paramFirstPath = ParamFirstPath.MIN_TIME;
        this.periodOfDayConstraints = new ArrayList <>();
        this.distanceConstraints = new ArrayList <>();
        this.deactivate = new ArrayList <>();
    }

    public long getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public ParamFirstPath getParamFirstPath () {
        return paramFirstPath;
    }

    public void setParamFirstPath ( ParamFirstPath paramFirstPath ) {
        this.paramFirstPath = paramFirstPath;
    }

    public List < PeriodConstraint > getPeriodOfDayConstraints () {
        return periodOfDayConstraints;
    }

    /**
     * @param travelMean travel mean related to the constraint.
     * @return periodConstraint related to the travel mean.
     */
    public PeriodConstraint getPeriodOfDayConstraint ( String travelMean ) {
        for ( PeriodConstraint constraint : periodOfDayConstraints ) {
            if ( constraint.getConcerns().equals( TravelMeanEnum.getEnumFromString( travelMean ) ) ) {
                return constraint;
            }
        }
        return null;
    }

    public List < DistanceConstraint > getDistanceConstraints () {
        return distanceConstraints;
    }

    /**
     * @param travelMean travel mean related to the constraint.
     * @return distanceConstraint relateed to the travel mean.
     */
    public DistanceConstraint getDistanceConstraint ( String travelMean ) {
        for ( DistanceConstraint constraint : distanceConstraints ) {
            if ( constraint.getConcerns().equals( TravelMeanEnum.getEnumFromString( travelMean ) ) ) {
                return constraint;
            }
        }
        return null;
    }

    /**
     * @param travelMean travel mean related to the constraint.
     * @return true if the travel mean is activated in a constraint, false otherwise.
     */
    public boolean isActivated ( TravelMeanEnum travelMean ) {
        return !deactivate.contains( travelMean );
    }

    public List < TravelMeanEnum > getDeactivate () {
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

        public PeriodConstraint ( long id, TravelMeanEnum concerns, long minHour, long maxHour ) {
            this.id = id;
            this.concerns = concerns;
            this.minHour = minHour;
            this.maxHour = maxHour;
        }

        public long getId () {
            return id;
        }

        public TravelMeanEnum getConcerns () {
            return concerns;
        }

        public long getMinHour () {
            return minHour;
        }

        public void setMinHour ( long minHour ) {
            this.minHour = minHour;
        }

        public long getMaxHour () {
            return maxHour;
        }

        public void setMaxHour ( long maxHour ) {
            this.maxHour = maxHour;
        }
    }

    /**
     * Constraint regarding the distance of an event.
     */
    public static class DistanceConstraint {
        private long id;
        private TravelMeanEnum concerns;
        private int minLength;
        private int maxLength;

        public DistanceConstraint ( long id, TravelMeanEnum concerns, int minLength, int maxLength ) {
            this.id = id;
            this.concerns = concerns;
            this.minLength = minLength;
            this.maxLength = maxLength;
        }

        public long getId () {
            return id;
        }

        public TravelMeanEnum getConcerns () {
            return concerns;
        }

        public int getMinLength () {
            return minLength;
        }

        public void setMinLength ( int minLength ) {
            this.minLength = minLength;
        }

        public int getMaxLength () {
            return maxLength;
        }

        public void setMaxLength ( int maxLength ) {
            this.maxLength = maxLength;
        }
    }
}
