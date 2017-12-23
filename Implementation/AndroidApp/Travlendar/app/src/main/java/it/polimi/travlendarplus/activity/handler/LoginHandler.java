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
import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.tasks.InsertUserTask;
import it.polimi.travlendarplus.database.entity.User;
import it.polimi.travlendarplus.retrofit.response.ErrorResponse;

/**
 * Handler that handles the server response to the login request.
 * It is used by the LoginActivity.
 */
public class LoginHandler extends DefaultHandler {

    private LoginActivity loginActivity;

    public LoginHandler(Looper looper, Context context, LoginActivity loginActivity) {
        super(looper, context);
        this.loginActivity = loginActivity;
    }

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        switch (msg.what){
            case 200:
                // Retrieve data from bundle.
                Bundle bundle = msg.getData();
                String email = loginActivity.getEmail();
                String name = bundle.getString("name");
                String surname = bundle.getString("surname");
                String token = bundle.getString("token");
                // Insert new User into the local DB.
                User user = new User(email, name, surname, token);
                new InsertUserTask(context).execute(user);
                break;
            case 403:
                Toast.makeText(context, "Credentials inserted are not correct!", Toast.LENGTH_LONG).show();
                loginActivity.getPassword_editText().setError("Wrong password!");
                loginActivity.getPassword_editText().requestFocus();
                break;
            default:
                break;
        }
        loginActivity.resumeNormalMode();
    }
}
