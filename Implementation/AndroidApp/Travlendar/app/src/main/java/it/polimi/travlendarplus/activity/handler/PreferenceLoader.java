package it.polimi.travlendarplus.activity.handler;

import java.util.Map;

import it.polimi.travlendarplus.Preference;

/**
 * Interface to be implemented by an activity that downloads preferences from the server.
 */
public interface PreferenceLoader {
    void updatePreferences ( Map < String, Preference > preferenceMap );
}
