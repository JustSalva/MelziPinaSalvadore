package it.polimi.travlendarplus.activity.handler;


import java.util.Map;

import it.polimi.travlendarplus.Location;

/**
 * Interface to be implemented by an activity that downloads locations from the server.
 */
public interface LocationLoader {
    void updateLocations(Map<String, Location> locationMap);
}
