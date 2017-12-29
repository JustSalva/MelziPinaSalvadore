package it.polimi.travlendarplus.activity.handler;


import android.app.Activity;
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
import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.MainActivity;
import it.polimi.travlendarplus.activity.tasks.InsertUserTask;
import it.polimi.travlendarplus.activity.tasks.RemoveUserTask;
import it.polimi.travlendarplus.database.entity.User;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;

/**
 * Handler that handles the server response to the login request.
 * It is used by the LoginActivity.
 */
public class LoginHandler extends DefaultHandler<LoginActivity> {

    //private LoginActivity loginActivity;

    public LoginHandler(Looper looper, LoginActivity loginActivity) {
        super(looper, loginActivity);
        //this.loginActivity = loginActivity;
    }

    @Override
    public void handleMessage(Message msg){
        switch (msg.what){
            case 0:
                Toast.makeText(activity, "No internet connection available!", Toast.LENGTH_LONG).show();
                break;
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String email = activity.getEmail();
                String name = bundle.getString("name");
                String surname = bundle.getString("surname");
                String token = bundle.getString("token");
                // Insert new User into the local DB.
                User user = new User(email, name, surname, token);
                new InsertUserTask(activity).execute(user);
                break;
            case 401:
                Toast.makeText(activity, "This user does not exist!", Toast.LENGTH_LONG).show();
                break;
            case 403:
                Toast.makeText(activity, "Credentials inserted are not correct!", Toast.LENGTH_LONG).show();
                activity.getPassword_editText().setError("Wrong password!");
                activity.getPassword_editText().requestFocus();
                break;
            default:
                break;
        }
        activity.resumeNormalMode();
    }
}
