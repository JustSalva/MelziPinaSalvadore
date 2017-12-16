package it.polimi.travlendarplus.beans.calendarManager.googleMapsUtilities;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTMLCallAndResponse {

    public static JSONObject performCall ( String urlString ) {
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
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return new JSONObject( html );
    }
}
