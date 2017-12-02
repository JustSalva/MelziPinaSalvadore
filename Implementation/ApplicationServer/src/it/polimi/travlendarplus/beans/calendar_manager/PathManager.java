package it.polimi.travlendarplus.beans.calendar_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import it.polimi.travlendarplus.entity.Location;
import it.polimi.travlendarplus.entity.calendar.DateOfCalendar;
import it.polimi.travlendarplus.entity.calendar.Event;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;

public class PathManager {
    private static GeoApiContext context = new GeoApiContext.Builder()
            .apiKey("AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y")
            .build();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void calculatePath(Event event) {
        LocalDate date = event.getDate().getDate();
        LocalTime hour = event.getStartingTime();

        //GMaps APIs require an object of Joda Time -> DateTime
        DateTime dateTime = new DateTime(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour.getHour(), hour.getMinute());
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

            /*JSONObject jsonObj = new JSONObject(res);
            JSONArray routes = jsonObj.getJSONArray("routes");
            JSONArray legs = routes.getJSONArray(0);

            System.out.println(routes.length());
            System.out.println(gson.toJson(routes));
*/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main (String args[]) {
        LocalTime time = LocalTime.of(12, 30);
        LocalDate day = LocalDate.of(2018,1,1);
        DateOfCalendar date = new DateOfCalendar(day);
        Location departure = new Location(30, 30, "Como, Italy");
        Location arrival = new Location(31,31,"Lecco, Italy");
        Event e = new Event("", time, time, true, date, "", true, null, null, arrival, departure, null);
        calculatePath(e);
    }
}
