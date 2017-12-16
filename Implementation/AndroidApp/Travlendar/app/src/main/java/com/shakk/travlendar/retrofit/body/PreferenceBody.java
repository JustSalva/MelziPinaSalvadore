package com.shakk.travlendar.retrofit.body;


import com.shakk.travlendar.Preference;

import java.util.List;

public class PreferenceBody {
    private String name;
    private Preference.ParamFirstPath paramFirstPath;
    private PeriodConstraint limitedByPeriod;
    private DistanceConstraint limitedByDistance;
    private List<Preference.TravelMeanEnum> deactivate;

    public PreferenceBody(
            String name,
            Preference.ParamFirstPath paramFirstPath,
            PeriodConstraint limitedByPeriod,
            DistanceConstraint limitedByDistance,
            List<Preference.TravelMeanEnum> deactivate) {
        this.name = name;
        this.paramFirstPath = paramFirstPath;
        this.limitedByPeriod = limitedByPeriod;
        this.limitedByDistance = limitedByDistance;
        this.deactivate = deactivate;
    }

    private class PeriodConstraint {
        private int minHour;
        private int maxHour;
        private Preference.TravelMeanEnum concerns;

        public PeriodConstraint(int minHour, int maxHour, Preference.TravelMeanEnum concerns) {
            this.minHour = minHour;
            this.maxHour = maxHour;
            this.concerns = concerns;
        }
    }

    private class DistanceConstraint {
        private int minLength;
        private int maxLength;
        private Preference.TravelMeanEnum concerns;

        public DistanceConstraint(int minLength, int maxLength, Preference.TravelMeanEnum concerns) {
            this.minLength = minLength;
            this.maxLength = maxLength;
            this.concerns = concerns;
        }
    }
}
