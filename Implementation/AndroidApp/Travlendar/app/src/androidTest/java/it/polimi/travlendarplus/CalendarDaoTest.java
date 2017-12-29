package it.polimi.travlendarplus;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.dao.CalendarDao;
import it.polimi.travlendarplus.database.dao.TicketsDao;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.entity.event.BreakEvent;
import it.polimi.travlendarplus.database.entity.event.Event;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.database.entity.ticket.DistanceTicket;
import it.polimi.travlendarplus.database.entity.ticket.GenericTicket;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class CalendarDaoTest {

    private CalendarDao calendarDao;
    private TicketsDao ticketsDao;
    private AppDatabase database;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        calendarDao = database.calendarDao();
        ticketsDao = database.ticketsDao();
    }

    @After
    public void closeDb() throws IOException {
        database.close();
    }

    @Test
    public void addUpdateRemoveEvent() throws Exception {
        GenericEvent genericEvent = new GenericEvent(0, "name", 0, 1000, true);
        Event event = new Event("", 0, "Home", false, true, "Poli");
        genericEvent.setEvent(event);
        calendarDao.insertGenericEvent(genericEvent);
        assert calendarDao.countGenericEvents() == 1;
        assert calendarDao.getAllGenericEvents().getValue().size() == 1;
        assert calendarDao.getGenericEventsByDate(0).getValue().size() == 1;
        assert calendarDao.getScheduledEventsByDate(0).getValue().size() == 1;

        genericEvent.setName("name2");
        calendarDao.updateGenericEvent(genericEvent);
        List<GenericEvent> events = calendarDao.getAllGenericEvents().getValue();
        assert Objects.equals(events.get(0), genericEvent);

        calendarDao.deleteGenericEvent(genericEvent);
        assert calendarDao.countGenericEvents() == 0;

        calendarDao.insertGenericEvent(genericEvent);
        assert calendarDao.countGenericEvents() == 1;
        calendarDao.deleteGenericEventFromId(0);
        assert calendarDao.countGenericEvents() == 0;

        calendarDao.deleteAllGenericEvents();
        assert calendarDao.countGenericEvents() == 0;
    }

    @Test
    public void breakEvents() throws Exception {
        GenericEvent genericEvent = new GenericEvent(0, "name", 0, 1000, true);
        BreakEvent breakEvent = new BreakEvent(3600);
        genericEvent.setBreakEvent(breakEvent);
        calendarDao.insertGenericEvent(genericEvent);

        List<GenericEvent> breakEvents = calendarDao.getBreakEventsByDate(0).getValue();
        assert Objects.equals(breakEvents.get(0), genericEvent);
    }

    @Test
    public void travelComponents() throws Exception {
        GenericEvent genericEvent = new GenericEvent(0, "name", 0, 1000, true);
        Event event = new Event("", 0, "Home", false, true, "Poli");
        genericEvent.setEvent(event);
        calendarDao.insertGenericEvent(genericEvent);

        Ticket ticket = new Ticket(2, 10);
        DistanceTicket distanceTicket = new DistanceTicket(50);
        ticket.setDistanceTicket(distanceTicket);

        TravelComponent travelComponent = new TravelComponent(1, 10, 0, "BIKE", "Home", "Poli", 0, 3600);

        calendarDao.insertTravelComponent(travelComponent);
        assert Objects.equals(calendarDao.getTravelComponentsByEventId(0).getValue().get(0), travelComponent);
        assert Objects.equals(calendarDao.getAllTravelComponents().getValue().get(0), travelComponent);

        calendarDao.deleteAllTravelComponents();
        assert calendarDao.getAllTravelComponents().getValue().isEmpty();

        calendarDao.insertTravelComponent(travelComponent);
        assert calendarDao.getAllTravelComponents().getValue().size() == 1;
        calendarDao.deleteEventTravelComponents(0);
        assert calendarDao.getAllTravelComponents().getValue().isEmpty();
    }

    @Test
    public void selectTicket() throws Exception {
        GenericEvent genericEvent = new GenericEvent(0, "name", 0, 1000, true);
        Event event = new Event("", 0, "Home", false, true, "Poli");
        genericEvent.setEvent(event);
        calendarDao.insertGenericEvent(genericEvent);

        Ticket ticket = new Ticket(2, 10);
        DistanceTicket distanceTicket = new DistanceTicket(50);
        ticket.setDistanceTicket(distanceTicket);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        ticketsDao.insert(tickets);

        TravelComponent travelComponent = new TravelComponent(1, 10, 0, "BIKE", "Home", "Poli", 0, 3600);
        calendarDao.insertTravelComponent(travelComponent);

        calendarDao.selectTicket((int)ticket.getId(), (int)travelComponent.getId());
        assert calendarDao.getAllTravelComponents().getValue().get(0).getTicketId() == 2;

        calendarDao.deselectTicket((int) travelComponent.getId());
        assert calendarDao.getAllTravelComponents().getValue().get(0).getTicketId() == 0;
    }
}