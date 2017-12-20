package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.PublicKey;

import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.PublicKeyActivity;

public class RequestPublicKeyHandler extends Handler {

    private Context context;
    private PublicKeyActivity activity;

    public RequestPublicKeyHandler(Looper looper, Context context, PublicKeyActivity activity) {
        super(looper);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(context, "No internet connection available!", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, LoginActivity.class));
                break;
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String jsonPublicKey = bundle.getString("jsonPublicKey");
                PublicKey publicKey = new Gson().fromJson(jsonPublicKey, PublicKey.class);
                activity.setPublicKey(publicKey);
                break;
            case 503:
                Toast.makeText(context, "Service unavailable!", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, LoginActivity.class));
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                context.startActivity(new Intent(context, LoginActivity.class));
                break;
        }
        activity.resumeNormalMode();
    }
}
