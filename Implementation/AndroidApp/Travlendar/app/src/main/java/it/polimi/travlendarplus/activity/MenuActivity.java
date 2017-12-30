package it.polimi.travlendarplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.tasks.RemoveUserTask;

/**
 * Activity to be extended by all the activities that need a toolbar and a side menu.
 * Contains a navigationView with the five main activities and a toolbar that shows the name
 * of the current activity and allows the user to log out.
 */
public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    /**
     * Links the component to the activity.
     */
    public void setupMenuToolbar () {
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
    }

    @Override
    public void onBackPressed () {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected ( @NonNull MenuItem item ) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if ( id == R.id.nav_calendar ) {
            Intent intent = new Intent( this, CalendarActivity.class );
            startActivity( intent );
        } else if ( id == R.id.nav_map ) {
            Intent intent = new Intent( this, MapsActivity.class );
            startActivity( intent );
        } else if ( id == R.id.nav_account ) {
            Intent intent = new Intent( this, AccountActivity.class );
            startActivity( intent );
        } else if ( id == R.id.nav_preferences ) {
            Intent intent = new Intent( this, PreferencesActivity.class );
            startActivity( intent );
        } else if ( id == R.id.nav_tickets ) {
            Intent intent = new Intent( this, TicketsViewerActivity.class );
            startActivity( intent );
        }

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        int id = item.getItemId();

        if ( id == R.id.action_logout ) {
            new RemoveUserTask( getApplicationContext() ).execute();
            Intent intent = new Intent( this, LoginActivity.class );
            startActivity( intent );
        }

        return super.onOptionsItemSelected( item );
    }

    /**
     * Disables user input fields.
     */
    public void waitForServerResponse () {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE );
        findViewById( R.id.progressBar ).setVisibility( View.VISIBLE );
    }

    /**
     * Enables user input fields.
     */
    public void resumeNormalMode () {
        getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE );
        findViewById( R.id.progressBar ).setVisibility( View.GONE );
    }
}
