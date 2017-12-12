package com.shakk.travlendar.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakk.travlendar.R;
import com.shakk.travlendar.TravlendarRestClient;
import com.shakk.travlendar.database.entity.User;
import com.shakk.travlendar.database.view_model.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class AccountActivity extends MenuActivity {

    private TextView name_textView;
    private TextView surname_textView;
    private TextView email_textView;
    private EditText locationName_editText;
    private EditText locationAddress_editText;
    private Button addLocation_button;
    private ProgressBar progressBar;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        super.setupMenuToolbar();

        //Find layout views.
        name_textView = findViewById(R.id.name);
        surname_textView = findViewById(R.id.surname);
        email_textView = findViewById(R.id.email);
        locationName_editText = findViewById(R.id.locationName_editText);
        locationAddress_editText = findViewById(R.id.locationAddress_editText);
        addLocation_button = findViewById(R.id.addLocation_button);
        progressBar = findViewById(R.id.progressBar);

        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activity receive the same MyViewModel instance created by the first activity.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            String name = user != null ? user.getName() : "";
            name_textView.setText(name);
            String surname = user != null ? user.getSurname() : "";
            surname_textView.setText(surname);
            String email = user != null ? user.getEmail() : "";
            email_textView.setText(email);
        });

        addLocation_button.setOnClickListener(view -> addLocation());
    }

    private void addLocation() {
        // Store values at the time of the sending attempt.
        String locationName = locationName_editText.getText().toString();
        String locationAddress = locationAddress_editText.getText().toString();

        // Retrieve token representing device.
        String token = FirebaseInstanceId.getInstance().getToken();

        // Retrieve inserted address latitude and longitude.
        String latitude;
        String longitude;

        // Build JSON to be sent to server.
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("name", locationName);
            jsonParams.put("address", locationAddress);
            //jsonParams.put("latitude", latitude);
            //jsonParams.put("longitude", longitude);
            Log.d("JSON", jsonParams.toString());
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Send JSON to server.
        TravlendarRestClient.post("ApplicationServer/preference/location", entity, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                //Makes UI unresponsive.
                waitForServerResponse();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("JSON REPLY", response.toString());
                String univocalCode = "";
                String name = "";
                String surname = "";
                //Get univocalCode from JSON response.
                try {
                    univocalCode = response.getString("token");
                    name = response.getString("name");
                    surname = response.getString("surname");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
/*
                //Insert new User into the local DB.
                User user = new User(email, name, surname, univocalCode);
                Log.d("INSERT USER", user.toString());
                new LoginActivity.InsertUserTask(getApplicationContext()).execute(user);

                //Go to calendar activity.
                goToCalendarActivity();
                */
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 403) {
                    Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_LONG).show();
                } else if (statusCode == 400) {
                    Toast.makeText(getBaseContext(), "Bad response", Toast.LENGTH_LONG).show();
                }
                Log.d("RESPONSE ERROR", responseString);
                //Makes UI responsive again.
                resumeNormalMode();
            }
        });
    }

    /**
     * Disables user input fields.
     */
    private void waitForServerResponse() {
        addLocation_button.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Enables user input fields.
     */
    private void resumeNormalMode() {
        addLocation_button.setEnabled(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
    }
}
