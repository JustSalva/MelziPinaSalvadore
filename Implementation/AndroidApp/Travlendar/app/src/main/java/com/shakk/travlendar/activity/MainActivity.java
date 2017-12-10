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
        if (userViewModel.getUser().getValue() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
        }
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

    /**
     * Performs an User search operation in the DB on a separated thread.
     */
    private static class SearchUserTask extends AsyncTask<Void, Void, User> {

        private AppDatabase database;

        private WeakReference<Context> weakContext;

        SearchUserTask(Context context) {
            this.database = AppDatabase.getInstance(context);
            this.weakContext = new WeakReference<>(context);
        }

        protected User doInBackground(Void... voids) {
            LiveData<User> user = database.userDao().getUser();
            return user.getValue();
        }

        protected void onPostExecute(User user) {
            Context context = weakContext.get();
            if (user == null) {
                context.startActivity(new Intent(context, LoginActivity.class));
            }
            context.startActivity(new Intent(context, CalendarActivity.class));
        }
    }
}
