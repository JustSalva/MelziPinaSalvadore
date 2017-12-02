package it.polimi.travlendarplus.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;

public class GMapsAPIsTest {

    public static void main (String args[]) {

        try {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyDaLQb73k0f7P6dNAnA6yLbBdmfddYs-3Y")
                    .build();

            DirectionsResult result = DirectionsApi.getDirections(context, "Sydney, AU", "Melbourne, AU").await();

            //GeocodingResult[] results = GeocodingApi.geocode(context,
              //      "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(result));
        } catch (Exception e) {

        }
    }
}
