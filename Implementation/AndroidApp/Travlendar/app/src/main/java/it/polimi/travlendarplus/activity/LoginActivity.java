package it.polimi.travlendarplus.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.entity.User;
import it.polimi.travlendarplus.retrofit.controller.LoginController;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // idDevice token.
    private String idDevice;

    // UI references.
    private EditText email_editText;
    private EditText password_editText;
    private Button login_button;
    private ProgressBar progressBar;

    // Strings to be read from input fields.
    private String email;
    private String password;

    // Handler for server responses.
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the link to registration button.
        TextView register_textView = findViewById(R.id.linkToRegister_textView);
        register_textView.setPaintFlags(register_textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        register_textView.setOnClickListener((event) ->
                startActivity(new Intent(this, RegistrationActivity.class)));

        // Set up the registration form.
        email_editText = findViewById(R.id.email_editText);
        password_editText = findViewById(R.id.password_editText);
        login_button = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);

        // Listener on the password field.
        password_editText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                logIn();
                return true;
            }
            return false;
        });

        // Listener on the login button.
        login_button.setOnClickListener(view -> logIn());

        // Handle server responses.
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Toast.makeText(getBaseContext(), "No internet connection available!", Toast.LENGTH_LONG).show();
                        break;
                    case 200:
                        // Retrieve data from bundle.
                        Bundle bundle = msg.getData();
                        String name = bundle.getString("name");
                        String surname = bundle.getString("surname");
                        String token = bundle.getString("token");
                        // Insert new User into the local DB.
                        User user = new User(email, name, surname, token);
                        new InsertUserTask(getApplicationContext()).execute(user);
                        goToCalendarActivity();
                        break;
                    case 400:
                        Toast.makeText(getBaseContext(), "Invalid fields sent to server!", Toast.LENGTH_LONG).show();
                        break;
                    case 401:
                        Toast.makeText(getBaseContext(), "This user is not registered!", Toast.LENGTH_LONG).show();
                        break;
                    case 403:
                        Toast.makeText(getBaseContext(), "Credentials inserted are not correct!", Toast.LENGTH_LONG).show();
                        password_editText.setError("Wrong password!");
                        password_editText.requestFocus();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "Unknown error.", Toast.LENGTH_LONG).show();
                        Log.d("ERROR_RESPONSE", msg.toString());
                        break;
                }
                resumeNormalMode();
            }
        };
    }


    /**
     * Attempts to login the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void logIn() {
        // Store values at the time of the login attempt.
        email = email_editText.getText().toString();
        password = password_editText.getText().toString();

        // Check if inputs are correct.
        if (!validate()) {
            Toast.makeText(getBaseContext(), "Something is wrong", Toast.LENGTH_LONG).show();
            return;
        }

        // Retrieve token representing device.
        idDevice = FirebaseInstanceId.getInstance().getToken();

        // Send request to server.
        waitForServerResponse();
        LoginController loginController = new LoginController(handler);
        loginController.start(email, password, idDevice);
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

