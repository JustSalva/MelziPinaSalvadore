package it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities;

import it.polimi.travlendarplus.exceptions.googleMapsExceptions.GMapsUnavailableException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provide methods to perform HTML requests
 */
public class HTMLCallAndResponse {

    /**
     * Performs a request to Google Maps APIs
     *
     * @param urlString url at which the request must be performed
     * @return a Json object containing all the info received after the request
     * @throws GMapsUnavailableException if Google Maps APIs are unavailable
     */
    public static JSONObject performCall ( String urlString ) throws GMapsUnavailableException {
        String html = "";

        try {
            URL call = new URL( urlString );
            HttpURLConnection connection = ( HttpURLConnection ) call.openConnection();
            BufferedReader read = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line = read.readLine();
            while ( line != null ) {
                html += line + "\n";
                line = read.readLine();
            }
            read.close();

        } catch ( MalformedURLException e ) {
            Logger.getLogger( HTMLCallAndResponse.class.getName() ).log( Level.SEVERE, e.getMessage(), e );
        } catch ( IOException e ) {
            throw new GMapsUnavailableException( );
        }

        return new JSONObject( html );
    }
}
