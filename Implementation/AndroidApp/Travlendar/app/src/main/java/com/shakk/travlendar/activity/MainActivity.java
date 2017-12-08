package com.shakk.travlendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shakk.travlendar.R;
import com.shakk.travlendar.database.entity.TravelComponent;
import com.shakk.travlendar.database.entity.event.Event;
import com.shakk.travlendar.database.entity.event.GenericEvent;
import com.shakk.travlendar.database.entity.ticket.DistanceTicket;
import com.shakk.travlendar.database.entity.ticket.PeriodTicket;
import com.shakk.travlendar.database.entity.ticket.Ticket;
import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getAppDatabase(getApplicationContext());
        addUser();
/*
        TextView textView = findViewById(R.id.test_view);
        textView.setOnClickListener(view -> {
            addUser();
            //userViewModel.getCurrentName().setValue("John Doe");
        });

        /* Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activity receive the same MyViewModel instance created by the first activity.
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

    public void addUser() {
        new Thread(() -> {

            database.userDao().delete();
            database.ticketsDao().deleteAll();
            database.calendarDao().deleteAll();
            database.calendarDao().deleteTravelComponents();

            database.userDao().insert(new User("10486221@polimi.it", "Alessandro", "Pina"));

            Ticket ticket;
            ticket = new Ticket(30, new PeriodTicket("JAN", 20170101, 20170131));
            database.ticketsDao().insert(ticket);
            ticket = new Ticket(30, new DistanceTicket(30));
            database.ticketsDao().insert(ticket);

            GenericEvent genericEvent = new GenericEvent("name",
                    20171207, 1100, 1200, false,
                    new Event("de", "meeting", "qua",
                            false, null));
            database.calendarDao().insert(genericEvent);

            List<TravelComponent> list = new ArrayList<>();
            list.add(new TravelComponent(21, 1, 2, "tram",
                    "here", "there", "0800", "0900"));
            list.add(new TravelComponent(12, 1, 1, "foot",
                    "here", "there", "0800", "0900"));
            database.calendarDao().insert(list);

            Log.d("TAG", Integer.toString(database.userDao().countUsers()));
        }).start();
    }
}
