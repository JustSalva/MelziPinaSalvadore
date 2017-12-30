package it.polimi.travlendarplus.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.database.view_model.UserViewModel;

/**
 * First activity started by the application, checks if there is already a user logged in.
 * If true, redirects to the calendar activity. Otherwise, to the login activity.
 */
public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Stetho.initialize( Stetho.newInitializerBuilder( this )
                .enableDumpapp( Stetho.defaultDumperPluginsProvider( this ) )
                .enableWebKitInspector( Stetho.defaultInspectorModulesProvider( this ) )
                .build() );

        // Check if there is already a user in the DB.
        userViewModel = ViewModelProviders.of( this ).get( UserViewModel.class );
        userViewModel.getUser().observe( this, user -> {
            if ( user == null ) {
                // There is not a user, go to login activity.
                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
            } else {
                // There is a user, go to calendar activity.
                startActivity( new Intent( getApplicationContext(), CalendarActivity.class ) );
            }
        } );
    }
}
