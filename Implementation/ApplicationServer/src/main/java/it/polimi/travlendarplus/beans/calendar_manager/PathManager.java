package it.polimi.travlendarplus.beans.calendar_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsJSONReader;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsURL;
import it.polimi.travlendarplus.entities.*;
import it.polimi.travlendarplus.entities.calendar.DateOfCalendar;
import it.polimi.travlendarplus.entities.calendar.Event;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;

@Stateless
public class PathManager extends UserManager{

    @EJB
    ScheduleManager scheduleManager;

    URL url;
    HttpURLConnection connection;

    //TODO calculate path before and after
    //attention to first and last event of the schedule (only one array of paths)

    public static void main (String[] a) {
        GMapsURL gMapsURL = new GMapsURL();
        try {
            URL maps = new URL(gMapsURL.getBaseCallPreviousPath(null, null));
            HttpURLConnection connection = (HttpURLConnection) maps.openConnection();
            BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = read.readLine();
            String html = "";
            while(line!=null) {
                html += line + "\n";
                line = read.readLine();
            }
            GMapsJSONReader response = new GMapsJSONReader();

            JSONObject resp = new JSONObject(html);

            JSONArray routes = response.getRoutes(resp);

            System.out.println(response.getTotDurationInSeconds((JSONObject) routes.get(0)));
            /*JSONObject responseJSON = new JSONObject(html);
            JSONArray routes = (JSONArray) responseJSON.get("routes");
            for(int i=0; i<routes.length(); i++)
                System.out.println(((JSONObject)((JSONObject)((JSONArray)((JSONObject)routes.get(i)).get("legs")).get(0)).get("distance")).get("text"));
*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //((Event)scheduleManager.getPossibleFollowingEvent(event)) to use for second parameter in function: baseCallFollowingPath()


    /*private static GeoApiContext context = new GeoApiContext.Builder()
            .apiKey("AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y")
            .build();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void calculatePath(Event event) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getDate().getDate()), ZoneOffset.UTC);

        //GMaps APIs require an object of Joda Time -> DateTime
        DateTime dateTime = new DateTime(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), date.getHour(), date.getMinute());
        LatLng lat1 = new LatLng(event.getDeparture().getLatitude(),event.getDeparture().getLongitude());
        LatLng lat2 = new LatLng(event.getEventLocation().getLatitude(), event.getDeparture().getLongitude());
        try {
            DirectionsResult result = DirectionsApi.newRequest(context)
                .origin(event.getDeparture().getAddress())
                .destination(event.getEventLocation().getAddress())
                .arrivalTime(dateTime)
                .mode(TravelMode.TRANSIT)
                .alternatives(true)
                .await();

            String res = gson.toJson(result);
            System.out.println(res);
*/
            /*JSONObject jsonObj = new JSONObject(res);
            JSONArray routes = jsonObj.getJSONArray("routes");
            JSONArray legs = routes.getJSONArray(0);

            System.out.println(routes.length());
            System.out.println(gson.toJson(routes));
*/

        /*} catch (Exception e) {
            e.printStackTrace();
        }*/

/*    }


    public static void main (String args[]) {
        Instant time = Instant.now();
        LocalDate day = LocalDate.of(2018,1,1);
        DateOfCalendar date = new DateOfCalendar(time.getEpochSecond());
        Location departure = new Location(30, 30, "Como, Italy");
        Location arrival = new Location(31,31,"Lecco, Italy");
        Event e = new Event("", time, time, true, date, "", true, null, null, arrival, departure, null);
        calculatePath(e);
    }
    */
}
