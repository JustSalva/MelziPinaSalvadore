package com.shakk.travlendar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shakk.travlendar.R;
import com.shakk.travlendar.database.UserViewModel;
import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.User;

public class MainActivity extends AppCompatActivity {

    UserViewModel userViewModel;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getAppDatabase(getApplicationContext());
        addUser();

        TextView textView = findViewById(R.id.test_view);
        textView.setOnClickListener(view -> {
            addUser();
            //userViewModel.getCurrentName().setValue("John Doe");
        });

        /* Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user -> {
            textView.setText(user.getEmail());
        });*/

    }

    public void goToLogin(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToRegistration(View view) {
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void goToCalendar(View view) {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    public void goToSchedule(View view) {
        Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }

    public void addUser() {
        new Thread(() -> {
            database.userDao().deleteUser();
            database.userDao().insertUser(new User("10486221@polimi.it", "Alessandro", "Pina"));
            Log.d("TAG", Integer.toString(database.userDao().countUsers()));
        }).start();
    }
}
