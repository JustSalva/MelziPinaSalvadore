package com.shakk.travlendar.retrofit.body;

import com.shakk.travlendar.Preference;

import java.util.List;

public class PreferenceBody {
    private long id;
    private String name;
    private Preference.ParamFirstPath paramFirstPath;
    private List<Preference.PeriodConstraint> limitedByPeriod;
    private List<Preference.DistanceConstraint> limitedByDistance;
    private List<Preference.TravelMeanEnum> deactivate;

    public PreferenceBody(
            String name,
            Preference.ParamFirstPath paramFirstPath,
            List<Preference.PeriodConstraint> limitedByPeriod,
            List<Preference.DistanceConstraint> limitedByDistance,
            List<Preference.TravelMeanEnum> deactivate) {
        this.name = name;
        this.paramFirstPath = paramFirstPath;
        this.limitedByPeriod = limitedByPeriod;
        this.limitedByDistance = limitedByDistance;
        this.deactivate = deactivate;
    }

    public void setId(long id) {
        this.id = id;
    }
}
