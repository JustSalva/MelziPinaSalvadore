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

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.RegistrationActivity;
import it.polimi.travlendarplus.activity.tasks.InsertUserTask;
import it.polimi.travlendarplus.database.entity.User;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;

/**
 * Handler that handles the server response to the registration request.
 * It is used by the RegistrationActivity.
 */
public class RegistrationHandler extends DefaultHandler {

    private RegistrationActivity registrationActivity;

    public RegistrationHandler(Looper looper, Context context, RegistrationActivity registrationActivity) {
        super(looper, context);
        this.registrationActivity = registrationActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String token = bundle.getString("token");
                Log.d("UNICODE", token);
                // Insert new User into the local DB.
                String email = registrationActivity.getEmail();
                String name = registrationActivity.getName();
                String surname = registrationActivity.getSurname();
                User user = new User(email, name, surname, token);
                Log.d("INSERT_USER", user.toString());
                new InsertUserTask(context).execute(user);
                context.startActivity(new Intent(context, CalendarActivity.class));
                break;
            default:
                break;
        }
        registrationActivity.resumeNormalMode();
    }
}
