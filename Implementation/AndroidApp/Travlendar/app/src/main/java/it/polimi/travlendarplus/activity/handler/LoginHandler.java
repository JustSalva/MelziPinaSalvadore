package it.polimi.travlendarplus.activity.handler;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import it.polimi.travlendarplus.activity.CalendarActivity;
import it.polimi.travlendarplus.activity.LoginActivity;
import it.polimi.travlendarplus.activity.tasks.InsertUserTask;
import it.polimi.travlendarplus.database.entity.User;

/**
 * Handler that handles the server response to the login request.
 * It is used by the LoginActivity.
 */
public class LoginHandler extends Handler {

    private Context context;
    private LoginActivity loginActivity;

    public LoginHandler(Looper looper, Context context, LoginActivity loginActivity) {
        super(looper);
        this.context = context;
        this.loginActivity = loginActivity;
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
                String email = loginActivity.getEmail();
                String name = bundle.getString("name");
                String surname = bundle.getString("surname");
                String token = bundle.getString("token");
                // Insert new User into the local DB.
                User user = new User(email, name, surname, token);
                new InsertUserTask(context).execute(user);
                context.startActivity(new Intent(context, CalendarActivity.class));
                break;
            case 400:
                Toast.makeText(context, "Invalid fields sent to server!", Toast.LENGTH_LONG).show();
                break;
            case 401:
                Toast.makeText(context, "This user is not registered!", Toast.LENGTH_LONG).show();
                break;
            case 403:
                Toast.makeText(context, "Credentials inserted are not correct!", Toast.LENGTH_LONG).show();
                loginActivity.getPassword_editText().setError("Wrong password!");
                loginActivity.getPassword_editText().requestFocus();
                break;
            default:
                Toast.makeText(context, "Unknown error.", Toast.LENGTH_LONG).show();
                Log.d("ERROR_RESPONSE", msg.toString());
                break;
        }
        loginActivity.resumeNormalMode();
    }
}
