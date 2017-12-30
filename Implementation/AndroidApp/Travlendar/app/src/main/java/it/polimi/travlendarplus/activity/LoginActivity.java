package it.polimi.travlendarplus.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.security.PublicKey;

import it.polimi.travlendarplus.R;
import it.polimi.travlendarplus.activity.handler.LoginHandler;
import it.polimi.travlendarplus.activity.handler.RequestPublicKeyHandler;
import it.polimi.travlendarplus.retrofit.controller.LoginController;

/**
 * Activity that allows the user to perform a login or to reach the registration activity.
 * Password encryption to be implemented.
 */
public class LoginActivity extends AppCompatActivity implements PublicKeyActivity {
    private static final int RSA_KEY_LENGTH = 2048;
    private static final String ALGORITHM_NAME = "RSA";
    private static final String PADDING_SCHEME = "OAEPWITHSHA-512ANDMGF1PADDING";
    private static final String MODE_OF_OPERATION = "ECB";

    // idDevice token.
    private String idDevice;
    // PUBLIC KEY: to be removed when encryption works.
    private PublicKey publicKey;
    // UI references.
    private EditText email_editText;
    private EditText password_editText;
    private Button login_button;
    // Strings to be read from input fields.
    private String email;
    private String password;
    // Handler for server responses.
    private Handler loginHandler;
    // PUBLIC KEY HANDLER: to be removed when encryption works.
    private Handler requestPublicKeyHandler;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        // Set up the link to registration button.
        TextView register_textView = findViewById( R.id.linkToRegister_textView );
        register_textView.setPaintFlags( register_textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG );
        register_textView.setOnClickListener( ( event ) ->
                startActivity( new Intent( this, RegistrationActivity.class ) ) );

        // Set up the registration form.
        email_editText = findViewById( R.id.email_editText );
        password_editText = findViewById( R.id.password_editText );
        login_button = findViewById( R.id.login_button );

        // Retrieve token representing device.
        idDevice = FirebaseInstanceId.getInstance().getToken();

        // Public key handler.
        requestPublicKeyHandler = new RequestPublicKeyHandler( Looper.getMainLooper(), this );

        // Handle server responses.
        loginHandler = new LoginHandler( Looper.getMainLooper(), this );

        /* PUBLIC KEY REQUEST: to be removed when encryption works.
        if (publicKey == null) {
            // Send public key request to server.
            waitForServerResponse();
            RequestPublicKeyController requestPublicKeyController =
                new RequestPublicKeyController(requestPublicKeyHandler);
            requestPublicKeyController.start(idDevice);
        }*/

        // Listener on the login button.
        login_button.setOnClickListener( view -> logIn() );
    }


    /**
     * Attempts to login the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void logIn () {
        // Store values at the time of the login attempt.
        email = email_editText.getText().toString();
        password = password_editText.getText().toString();
        // Check if inputs are correct.
        if ( !validate() ) {
            Toast.makeText( getBaseContext(), "Something is wrong", Toast.LENGTH_LONG ).show();
            return;
        }

        /* PASSWORD ENCRYPTION: to be removed when encryption works.
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM_NAME + "/" + MODE_OF_OPERATION + "/" + PADDING_SCHEME);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String cryptoPassword = null;
        try {
            cryptoPassword = DateUtility.byteArrayToHexString( cipher.doFinal( "password".getBytes("UTF-8") ) );
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        // Send request to server.
        waitForServerResponse();
        LoginController loginController = new LoginController( loginHandler );
        loginController.start( email, password, email );
    }

    /**
     * Checks the fields to assure the all the user's inputs are valid.
     * If not, user gets notified by a toast and errors are shown.
     */
    private boolean validate () {
        // Reset errors.
        email_editText.setError( null );
        password_editText.setError( null );

        boolean valid = true;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if ( !isPasswordValid( password ) ) {
            password_editText.setError( getString( R.string.error_invalid_password ) );
            focusView = password_editText;
            valid = false;
        }

        // Check for a valid email address.
        if ( TextUtils.isEmpty( email ) ) {
            email_editText.setError( getString( R.string.error_field_required ) );
            focusView = email_editText;
            valid = false;
        } else if ( !isEmailValid( email ) ) {
            email_editText.setError( getString( R.string.error_invalid_email ) );
            focusView = email_editText;
            valid = false;
        }

        if ( !valid ) {
            // There was an error; focus the first form field with an error.
            focusView.requestFocus();
        }

        return valid;
    }

    private boolean isEmailValid ( String email ) {
        return Patterns.EMAIL_ADDRESS.matcher( email ).matches();
    }

    private boolean isPasswordValid ( String password ) {
        return password.length() > 4 && password.length() < 10;
    }

    /**
     * Disables user input fields.
     */
    private void waitForServerResponse () {
        login_button.setEnabled( false );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE );
        findViewById( R.id.progressBar ).setVisibility( View.VISIBLE );
    }

    /**
     * Enables user input fields.
     */
    public void resumeNormalMode () {
        login_button.setEnabled( true );
        getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE );
        findViewById( R.id.progressBar ).setVisibility( View.GONE );
    }

    public EditText getPassword_editText () {
        return password_editText;
    }

    public String getEmail () {
        return email;
    }

    @Override
    public void setPublicKey ( PublicKey publicKey ) {
        this.publicKey = publicKey;
    }
}

