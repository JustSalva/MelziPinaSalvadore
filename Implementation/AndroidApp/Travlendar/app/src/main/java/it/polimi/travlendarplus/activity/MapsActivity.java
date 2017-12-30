package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.travlendarplus.Location;
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.handler.LocationLoader;
import it.polimi.travlendarplus.activity.handler.location.GetLocationsHandler;
import it.polimi.travlendarplus.database.view_model.UserViewModel;
import it.polimi.travlendarplus.retrofit.controller.location.GetLocationsController;


/**
 * Activity that shows a map containing the locations added by the user.
 **/
// extends MenuActivity
public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, LocationLoader, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private UserViewModel userViewModel;
    private boolean locationsLoaded = false;
    private Map<String, Location> locationMap;
    private Map<String, Marker> markerMap;
    private String token;
    private GetLocationsHandler getLocationsHandler;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map_fragment );
        mapFragment.getMapAsync( this );

        this.getLocationsHandler = new GetLocationsHandler(Looper.getMainLooper(), this);

        userViewModel = ViewModelProviders.of( this ).get( UserViewModel.class );
        userViewModel.getUser().observe( this, user -> {
            token = user != null ? user.getToken() : "";
            if ( token != null && !locationsLoaded ) {
                // Server request for locations.
                GetLocationsController getLocationsController = new GetLocationsController(getLocationsHandler);
                getLocationsController.start(token);
            }
        } );
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady ( GoogleMap googleMap ) {
        mMap = googleMap;

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Toast.makeText(this,
                    marker.getTitle(),
                    Toast.LENGTH_SHORT).show();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onBackPressed () {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
            startActivity( new Intent( this, CalendarActivity.class ) );
        }
    }

    @Override
    public void updateLocations(Map<String, Location> locationMap) {
        this.locationMap = locationMap;
        this.markerMap = new HashMap<>();

        // Add some markers to the map, and add a data object to each marker.
        for (Location location : locationMap.values()) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position( new LatLng(
                            location.getLocation().getLatitude(),
                            location.getLocation().getLongitude()
                    ) )
                    .title(location.getName()));
            marker.setTag(location.getName());
            markerMap.put(location.getName(), marker);
        }
    }
}
