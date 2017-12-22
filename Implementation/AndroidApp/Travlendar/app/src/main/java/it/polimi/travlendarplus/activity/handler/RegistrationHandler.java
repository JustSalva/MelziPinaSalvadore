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
public class RegistrationHandler extends Handler {

    private Context context;
    private RegistrationActivity registrationActivity;

    public RegistrationHandler(Looper looper, Context context, RegistrationActivity registrationActivity) {
        super(looper);
        this.context = context;
        this.registrationActivity = registrationActivity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(context, "No internet connection available!", Toast.LENGTH_LONG).show();
                break;
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
            case 400:
                // Shows the user which invalid fields have been sent to server.
                Toast.makeText(context, "Invalid fields sent to server!", Toast.LENGTH_LONG).show();
                ErrorResponse errorResponse = new Gson()
                        .fromJson(msg.getData().getString("errorResponse"), ErrorResponse.class);
                // Shows a toast for each error message.
                for (String message : errorResponse.getMessages()) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
                break;
            case 401:
                Toast.makeText(context, "This email is already taken!", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                break;
        }
        registrationActivity.resumeNormalMode();
    }
}
