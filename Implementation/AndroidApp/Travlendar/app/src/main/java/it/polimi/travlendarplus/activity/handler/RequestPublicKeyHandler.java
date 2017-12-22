package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
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
public class RequestPublicKeyHandler extends DefaultHandler {

    private PublicKeyActivity activity;

    public RequestPublicKeyHandler(Looper looper, Context context, PublicKeyActivity activity) {
        super(looper, context);
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                byte[] bytesPublicKey = bundle.getByteArray("bytesPublicKey");
                PublicKey publicKey = null;
                try {
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytesPublicKey);
                    publicKey = keyFactory.generatePublic(keySpec);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                activity.setPublicKey(publicKey);
                break;
            case 503:
                Toast.makeText(context, "Service unavailable!", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, LoginActivity.class));
                break;
            default:
                context.startActivity(new Intent(context, LoginActivity.class));
                break;
        }
        activity.resumeNormalMode();
    }
}
