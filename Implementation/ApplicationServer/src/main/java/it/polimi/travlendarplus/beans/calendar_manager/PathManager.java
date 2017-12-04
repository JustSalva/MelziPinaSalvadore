package it.polimi.travlendarplus.beans.calendar_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import it.polimi.travlendarplus.entities.*;
import it.polimi.travlendarplus.entities.calendar.DateOfCalendar;
import it.polimi.travlendarplus.entities.calendar.Event;
import org.joda.time.DateTime;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.net.HttpURLConnection;
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

    //the eventual path related to this event: origin is yet setted into the event object's field
    private GMapsURL baseCallPreviousPath(Event event) {
        return baseCall(event).addParam("origin", event.getDeparture().getAddress()).
                addParam("destination", event.getEventLocation().getAddress()).
                addParam("departure_time", ((Event)scheduleManager.getPossiblePreviousEvent(event)).getEndingTime().toString());
    }

    //the eventual path will be lnked to following event
    private GMapsURL baseCallFollowingPath(Event event) {
        return baseCall(event).addParam("origin", event.getEventLocation().getAddress()).
                addParam("destination", ((Event)scheduleManager.getPossibleFollowingEvent(event)).getEventLocation().getAddress()).
                addParam("departure_time", event.getEndingTime().toString());
    }

    private GMapsURL baseCall(Event event) {
        GMapsURL callURL = new GMapsURL();
        return callURL.addParam("key", "AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y").
                addParam("alternatives", "true").
                addParam("units", "metric");
    }


    class GMapsURL {
        private String callURL;

        public GMapsURL() {
            callURL = "https://maps.googleapis.com/maps/api/directions/json?";
        }

        public GMapsURL(String callURL) {
            this.callURL = callURL;
        }

        public String getCallURL() {
            return callURL;
        }

        GMapsURL addParam (String param, String address) {
            return getCallURL().charAt(getCallURL().length()-1) == '?' ?
                new GMapsURL (getCallURL() + param + "=" + address) :
                    new GMapsURL (getCallURL() + "&" + param + "=" + address);
        }
    }

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
