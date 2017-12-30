package it.polimi.travlendarplus.activity.handler;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.PublicKeyActivity;

/**
 * Handler that handles the server response to the public key request.
 * It is used by the PublicKeyActivity.
 */
public class RequestPublicKeyHandler extends DefaultHandler < Activity > {

    //private PublicKeyActivity activity;

    public RequestPublicKeyHandler ( Looper looper, PublicKeyActivity activity ) {
        super( looper, ( Activity ) activity );
        //this.activity = activity;
    }

    @Override
    public void handleMessage ( Message msg ) {
        super.handleMessage( msg );
        switch ( msg.what ) {
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                byte[] bytesPublicKey = bundle.getByteArray("bytesPublicKey");

                PublicKey publicKey = null;
                try {
                    publicKey = KeyFactory.getInstance( "RSA" ).generatePublic( new
                            X509EncodedKeySpec( bytesPublicKey ) );
                } catch ( InvalidKeySpecException | NoSuchAlgorithmException e ) {
                    e.printStackTrace();
                }
                ( ( PublicKeyActivity ) activity ).setPublicKey( publicKey );
                break;
            case 503:
                Toast.makeText( activity, "Service unavailable!", Toast.LENGTH_LONG ).show();
                activity.startActivity( new Intent( activity, LoginActivity.class ) );
                break;
            default:
                activity.startActivity( new Intent( activity, LoginActivity.class ) );
                break;
        }
        ( ( PublicKeyActivity ) activity ).resumeNormalMode();
    }
}
