package it.polimi.travlendarplus.retrofit.body;

import java.util.List;

import it.polimi.travlendarplus.ParamFirstPath;
import it.polimi.travlendarplus.Preference;
import it.polimi.travlendarplus.TravelMeanEnum;

/**
 * Body to be sent to server to add a preference.
 */
public class PreferenceBody {
    private long id;
    private String name;
    private ParamFirstPath paramFirstPath;
    private List < Preference.PeriodConstraint > limitedByPeriod;
    private List < Preference.DistanceConstraint > limitedByDistance;
    private List < TravelMeanEnum > deactivate;

    public PreferenceBody ( String name, ParamFirstPath paramFirstPath,
                            List < Preference.PeriodConstraint > limitedByPeriod,
                            List < Preference.DistanceConstraint > limitedByDistance,
                            List < TravelMeanEnum > deactivate ) {
        this.name = name;
        this.paramFirstPath = paramFirstPath;
        this.limitedByPeriod = limitedByPeriod;
        this.limitedByDistance = limitedByDistance;
        this.deactivate = deactivate;
    }

    public long getId () {
        return id;
    }

    public void setId ( long id ) {
        this.id = id;
    }
}
