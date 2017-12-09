package com.shakk.travlendar.activity;

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
import android.widget.Toast;

import com.shakk.travlendar.MyFirebaseInstanceIdService;
import com.shakk.travlendar.R;
import com.shakk.travlendar.TravlendarRestClient;

import org.json.*;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

/**
 * A registration screen that offers registration to the server.
 */
public class RegistrationActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText email_editText;
    private EditText name_editText;
    private EditText surname_editText;
    private EditText password1_editText;
    private EditText password2_editText;
    private Button registration_button;
    private ProgressBar progressBar;

    private String email;
    private String name;
    private String surname;
    private String password1;
    private String password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Set up the registration form.
        email_editText = findViewById(R.id.email_editText);
        name_editText = findViewById(R.id.name_editText);
        surname_editText = findViewById(R.id.surname_editText);
        password1_editText = findViewById(R.id.password1_editText);
        password2_editText = findViewById(R.id.password2_editText);
        registration_button = findViewById(R.id.registration_button);
        progressBar = findViewById(R.id.progressBar);

        password1_editText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                signUp();
                return true;
            }
            return false;
        });

        registration_button.setOnClickListener(view -> signUp());
    }


    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void signUp() {

        /*if (mAuthTask != null) {
            return;
        }*/
        Log.d("TAG", "SignUp");

        // Store values at the time of the login attempt.
        email = email_editText.getText().toString();
        name = name_editText.getText().toString();
        surname = surname_editText.getText().toString();
        password1 = password1_editText.getText().toString();
        password2 = password2_editText.getText().toString();

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Something is wrong", Toast.LENGTH_LONG).show();
            return;
        }
        /*

        String token = new MyFirebaseInstanceIdService().getRefreshedToken();
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password1);
        params.put("idDevice", token);
        params.put("name", name);
        params.put("surname", surname);
        TravlendarRestClient.post("register", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                waitForServerResponse();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                JSONObject firstEvent = timeline.get(0);
                String tweetText = firstEvent.getString("text");

                // Do something with the response
                System.out.println(tweetText);
            }
        });
        */
    }

    /*
     * Checks the fields to assure the all the user's input are valid.
     * If not, user gets notified by a toast and errors are shown.
     */
    private boolean validate() {
        // Reset errors.
        email_editText.setError(null);
        password1_editText.setError(null);
        password2_editText.setError(null);

        boolean valid = true;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password1)) {
            password1_editText.setError(getString(R.string.error_invalid_password));
            focusView = password1_editText;
            valid = false;
        }

        // Check if the two password are equals, if the user entered them.
        if (isPasswordValid(password1) && !password1.equals(password2)) {
            password2_editText.setError(getString(R.string.error_different_passwords));
            focusView = password2_editText;
            valid = false;
        }

        // Check for a valid name.
        if (isNameValid(name)) {
            name_editText.setError(getString(R.string.error_invalid_name));
            focusView = name_editText;
            valid = false;
        }

        // Check for a valid surname.
        if (isNameValid(surname)) {
            surname_editText.setError(getString(R.string.error_invalid_surname));
            focusView = surname_editText;
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

    private boolean isNameValid(String name) {
        return name.length() > 1;
    }

    private void waitForServerResponse() {
        registration_button.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void resumeNormalMode() {
        registration_button.setEnabled(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}

