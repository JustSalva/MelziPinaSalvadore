package com.shakk.travlendar.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakk.travlendar.R;
import com.shakk.travlendar.TravlendarRestClient;
import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    //Database reference and idDevice token.
    private String token;

    // UI references.
    private EditText email_editText;
    private EditText password_editText;
    private Button login_button;
    private ProgressBar progressBar;

    //Strings to be read by input fields.
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView register_textView = findViewById(R.id.linkToRegister_textView);
        register_textView.setPaintFlags(register_textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        register_textView.setOnClickListener((event) -> {
            startActivity(new Intent(this, RegistrationActivity.class));
        });

        // Set up the registration form.
        email_editText = findViewById(R.id.email_editText);
        password_editText = findViewById(R.id.password_editText);
        login_button = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);

        password_editText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                logIn();
                return true;
            }
            return false;
        });

        login_button.setOnClickListener(view -> logIn());
    }


    /**
     * Attempts to login the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void logIn() {
        Log.d("TAG", "LogIn");

        // Store values at the time of the login attempt.
        email = email_editText.getText().toString();
        password = password_editText.getText().toString();

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Something is wrong", Toast.LENGTH_LONG).show();
            return;
        }

        //Retrieve token representing device.
        token = FirebaseInstanceId.getInstance().getToken();

        //Build JSON to be sent to server.
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("email", email);
            jsonParams.put("password", password);
            jsonParams.put("idDevice", token);
            Log.d("JSON", jsonParams.toString());
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Send JSON to server.
        TravlendarRestClient.post("login", entity, new JsonHttpResponseHandler() {
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

                //Insert new User into the local DB.
                User user = new User(email, name, surname, univocalCode);
                Log.d("INSERT USER", user.toString());
                new InsertUserTask(getApplicationContext()).execute(user);

                //Go to calendar activity.
                goToCalendarActivity();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("RESPONSE ERROR", responseString);
                //Makes UI responsive again.
                resumeNormalMode();
            }
        });
    }

    /**
     * Checks the fields to assure the all the user's inputs are valid.
     * If not, user gets notified by a toast and errors are shown.
     */
    private boolean validate() {
        // Reset errors.
        email_editText.setError(null);
        password_editText.setError(null);

        boolean valid = true;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            password_editText.setError(getString(R.string.error_invalid_password));
            focusView = password_editText;
            valid = false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            email_editText.setError(getString(R.string.error_field_required));
            focusView = email_editText;
            valid = false;
        } else if (!isEmailValid(email)) {
            email_editText.setError(getString(R.string.error_invalid_email));
            focusView = email_editText;
            valid = false;
        }

        if (!valid) {
            // There was an error; focus the first form field with an error.
            focusView.requestFocus();
        }

        return valid;
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4 && password.length() < 10;
    }

    /**
     * Disables user input fields.
     */
    private void waitForServerResponse() {
        login_button.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Enables user input fields.
     */
    private void resumeNormalMode() {
        login_button.setEnabled(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Launches calendar activity.
     */
    private void goToCalendarActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    /**
     * Performs an User input operation in the DB on a separated thread.
     */
    private static class InsertUserTask extends AsyncTask<User, Void, Void> {

        private AppDatabase database;

        InsertUserTask(Context context) {
            this.database = AppDatabase.getInstance(context);
        }

        protected Void doInBackground(User... users) {
            for (User user : users) {
                database.userDao().delete();
                database.userDao().insert(user);
            }
            return null;
        }
    }
}

