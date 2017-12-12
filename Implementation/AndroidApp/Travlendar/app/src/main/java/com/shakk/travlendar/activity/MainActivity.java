package com.shakk.travlendar.activity;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.shakk.travlendar.R;
import com.shakk.travlendar.database.AppDatabase;
import com.shakk.travlendar.database.entity.User;
import com.shakk.travlendar.database.view_model.UserViewModel;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        userViewModel = new UserViewModel(getApplication());
        if (userViewModel.getUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
        }
    }
/*
            database.userDao().delete();
            database.userDao().insert(new User(1, "10486221@polimi.it", "Alessandro", "Pina"));

            database.ticketsDao().deleteAll();
            Ticket ticket;
            ticket = new Ticket(1, 30);
            PeriodTicket periodTicket = new PeriodTicket("JAN", 20170101, 20170131);
            ticket.setType(Ticket.TicketType.PERIOD);
            ticket.setPeriodTicket(periodTicket);
            database.ticketsDao().insert(ticket);
            ticket = new Ticket(2, 30);
            DistanceTicket distanceTicket = new DistanceTicket(30);
            ticket.setType(Ticket.TicketType.DISTANCE);
            ticket.setDistanceTicket(distanceTicket);
            database.ticketsDao().insert(ticket);

            database.calendarDao().deleteAll();
            GenericEvent genericEvent = new GenericEvent(1, "name",
                    20171207, 1100, 1200, false);
            Event event = new Event("de", "meeting", "qua",
                            false, null);
            genericEvent.setType(GenericEvent.EventType.EVENT);
            genericEvent.setEvent(event);
            database.calendarDao().insert(genericEvent);

            database.calendarDao().deleteTravelComponents();
            List<TravelComponent> list = new ArrayList<>();
            list.add(new TravelComponent(1, 21, 1, 1, "tram",
                    "here", "there", "0800", "0900"));
            list.add(new TravelComponent(2, 12, 1, 1, "foot",
                    "here", "there", "0800", "0900"));
            database.calendarDao().insert(list);
*/
}
