package com.shakk.travlendar;


import java.util.ArrayList;
import java.util.List;

public class Preference {
    private int id;
    private String name;
    private ParamFirstPath paramFirstPath;
    private List<Constraint> limitedBy;
    private List<TravelMeanEnum> deactivate;

    public Preference() {
        this.id = 0;
        this.name = "Normal";
        this.paramFirstPath = ParamFirstPath.MIN_TIME;
        this.limitedBy = new ArrayList<>();
        this.deactivate = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ParamFirstPath getParamFirstPath() {
        return paramFirstPath;
    }

    public List<Constraint> getLimitedBy(String travelMean) {
        List<Constraint> constraints = new ArrayList<>();
        for (Constraint constraint : limitedBy) {
            if (constraint.concerns.getTravelMean().equals(travelMean)) {
                constraints.add(constraint);
            }
        }
        return constraints;
    }

    public boolean isActivated(TravelMeanEnum travelMean) {
        return !deactivate.contains(travelMean);
    }

    public static class Constraint {
        private int id;
        private TravelMeanEnum concerns;
        private int minHour;
        private int maxHour;
        private int minLength;
        private int maxLength;

        public int getId() {
            return id;
        }

        public TravelMeanEnum getConcerns() {
            return concerns;
        }

        public int getMinHour() {
            return minHour;
        }

        public int getMaxHour() {
            return maxHour;
        }

        public int getMinLength() {
            return minLength;
        }

        public int getMaxLength() {
            return maxLength;
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
    }
}
