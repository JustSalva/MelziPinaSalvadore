package com.shakk.travlendar.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.shakk.travlendar.R;
import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase.getAppDatabase(getApplicationContext()).userDao().insertUser(new User("ema", "nam", "sur"));
        System.out.println(AppDatabase.getAppDatabase(getApplicationContext()).userDao().getUserByEmail("ema").getEmail());
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
}
