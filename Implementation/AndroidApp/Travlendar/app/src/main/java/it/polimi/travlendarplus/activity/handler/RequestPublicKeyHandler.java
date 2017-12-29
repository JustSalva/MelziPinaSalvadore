package it.polimi.travlendarplus.activity.handler;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.ActionMenuView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.PublicKeyActivity;

/**
 * Handler that handles the server response to the public key request.
 * It is used by the PublicKeyActivity.
 */
public class RequestPublicKeyHandler extends DefaultHandler<Activity> {

    //private PublicKeyActivity activity;

    public RequestPublicKeyHandler(Looper looper, PublicKeyActivity activity) {
        super(looper, (Activity)activity);
        //this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                //byte[] bytesPublicKey = bundle.getByteArray("bytesPublicKey");
                byte[] bytes = {48,-126,1,34,48,13,6,9,42,-122,72,-122,-9,13,1,1,1,5,0,3,-126,1,15,0,48,-126,1,10,2,-126,1,1,0,-64,69,103,114,28,19,126,-68,-26,-25,-65,127,-12,-32,43,5,-34,-96,-122,111,-59,53,9,-45,-23,-77,-98,64,-83,74,-97,77,114,-105,-113,-3,15,-36,87,-7,-9,-23,58,63,-34,-114,111,-42,8,122,-86,-118,-43,49,52,67,-121,41,127,-48,-111,-84,53,65,49,86,116,-20,-124,35,-66,127,99,54,34,-30,-36,-32,30,-84,-126,-105,-113,-44,116,64,-11,-116,-125,-7,54,96,-128,113,46,65,-102,127,63,64,37,-108,40,40,74,-111,51,-53,60,48,5,-63,-113,22,-97,-43,85,-64,72,53,39,108,127,-105,-107,-84,100,-123,125,93,-84,117,-102,40,-59,18,-84,48,92,27,-40,-58,64,95,-14,-55,28,2,90,16,32,62,-45,-93,1,35,117,-40,36,117,-9,-84,-127,65,-43,-95,42,112,33,112,-70,120,119,-1,-128,87,-40,86,-46,20,-56,-93,-50,-55,-97,6,113,-105,-37,112,-56,-48,-106,-44,-44,-111,-72,-53,-4,-24,102,-113,24,-28,-104,-48,13,28,80,61,-1,-97,-75,-106,105,-78,62,-122,1,85,-125,78,-103,25,60,65,-38,-97,34,-74,-40,-68,-47,52,-104,9,1,-86,102,44,65,107,-110,-39,110,-44,81,88,8,83,105,-78,12,-112,-122,115,2,3,1,0,1
                };

                PublicKey publicKey = null;
                try {
                    publicKey = KeyFactory.getInstance( "RSA" ).generatePublic( new
                            X509EncodedKeySpec( bytes ) );
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                ((PublicKeyActivity)activity).setPublicKey(publicKey);
                break;
            case 503:
                Toast.makeText(activity, "Service unavailable!", Toast.LENGTH_LONG).show();
                activity.startActivity(new Intent(activity, LoginActivity.class));
                break;
            default:
                activity.startActivity(new Intent(activity, LoginActivity.class));
                break;
        }
        ((PublicKeyActivity)activity).resumeNormalMode();
    }
}
