package it.polimi.travlendarplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.polimi.travlendarplus.R;


/**
 * Activity that shows a map containing the travel to be made by the user.
 * To be implemented, only a map is showed for now.
 **/
// extends FragmentActivity
public class MapsActivity extends MenuActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        super.setupMenuToolbar();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map_fragment );
        mapFragment.getMapAsync( this );
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady ( GoogleMap googleMap ) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng( -34, 151 );
        mMap.addMarker( new MarkerOptions().position( sydney ).title( "Marker in Sydney" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( sydney ) );
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
}
