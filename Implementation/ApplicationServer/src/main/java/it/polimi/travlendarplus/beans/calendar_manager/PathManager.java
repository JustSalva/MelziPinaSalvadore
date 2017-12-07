package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException.GMapsGeneralException;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsJSONReader;
import it.polimi.travlendarplus.beans.calendar_manager.support.GMapsDirectionsHandler;
import it.polimi.travlendarplus.beans.calendar_manager.support.HTMLCallAndResponse;
import it.polimi.travlendarplus.entities.travelMeans.TravelMeanEnum;
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

@Stateless
public class PathManager extends UserManager{

    @EJB
    ScheduleManager scheduleManager;

    //TODO calculate path before and after
    //attention to first and last event of the schedule (only one array of paths)

    public static void main (String[] a) {
        GMapsDirectionsHandler gMapsURL = new GMapsDirectionsHandler();
        JSONObject response = HTMLCallAndResponse.performCall(gMapsURL.getBaseCallPreviousPath(null, null));
        GMapsJSONReader responseReader = new GMapsJSONReader();
        //System.out.println(responseReader.getTravelNoTransitMeans(response, TravelMeanEnum.CAR, 1512558191, null, null));
        try {
            System.out.println(responseReader.getTravelWithTransitMeans(response));
        } catch (GMapsGeneralException e) {
            e.printStackTrace();
        }
        //System.out.println(response);
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
