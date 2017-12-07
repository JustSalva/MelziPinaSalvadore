package it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.messages.calendarMessages.CalendarResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListPreferredLocationsResponse extends CalendarResponse {

    private static final long serialVersionUID = 7287235658936119083L;

    private List<PreferredLocationResponse> locations;

    public ListPreferredLocationsResponse() {
    }

    public ListPreferredLocationsResponse( Map<Location, String> locations ) {
        this.locations = new ArrayList<>();
        for (Map.Entry<Location, String> entry : locations.entrySet()) {
            this.locations.add( new PreferredLocationResponse( entry.getValue(), entry.getKey().getAddress()) );
        }
    }

    public List< PreferredLocationResponse > getLocations() {
        return locations;
    }

    public void setLocations( List< PreferredLocationResponse > locations ) {
        this.locations = locations;
    }
}
