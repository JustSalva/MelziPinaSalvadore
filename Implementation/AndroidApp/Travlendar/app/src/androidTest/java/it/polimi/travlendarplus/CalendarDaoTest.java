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
        genericEvent.setType(GenericEvent.EventType.EVENT);
        calendarDao.insert(genericEvent);
        assert calendarDao.getAllEvents().getValue().size() == 1;
        assert calendarDao.getEvents(0).getValue().size() == 1;
        assert calendarDao.getScheduledEvents(0).getValue().size() == 1;

        genericEvent.setName("name2");
        calendarDao.update(genericEvent);
        List<GenericEvent> events = calendarDao.getAllEvents().getValue();
        assert Objects.equals(events.get(0), genericEvent);

        calendarDao.delete(genericEvent);
        assert calendarDao.getAllEvents().getValue().size() == 0;

        calendarDao.insert(genericEvent);
        assert calendarDao.getAllEvents().getValue().size() == 1;
        calendarDao.deleteEventFromId(0);
        assert calendarDao.getAllEvents().getValue().size() == 0;

        calendarDao.deleteAll();
        assert calendarDao.getAllEvents().getValue().size() == 0;
    }

    @Test
    public void breakEvents() throws Exception {
        GenericEvent genericEvent = new GenericEvent(0, "name", 0, 1000, true);
        BreakEvent breakEvent = new BreakEvent(3600);
        genericEvent.setBreakEvent(breakEvent);
        genericEvent.setType(GenericEvent.EventType.BREAK);
        calendarDao.insert(genericEvent);

        List<GenericEvent> breakEvents = calendarDao.getBreakEvents(0).getValue();
        assert Objects.equals(breakEvents.get(0), genericEvent);
    }

    @Test
    public void travelComponents() throws Exception {
        GenericEvent genericEvent = new GenericEvent(0, "name", 0, 1000, true);
        Event event = new Event("", 0, "Home", false, true, "Poli");
        genericEvent.setEvent(event);
        genericEvent.setType(GenericEvent.EventType.EVENT);
        calendarDao.insert(genericEvent);

        Ticket ticket = new Ticket(2, 10);
        DistanceTicket distanceTicket = new DistanceTicket(50);
        ticket.setDistanceTicket(distanceTicket);
        ticket.setType(Ticket.TicketType.DISTANCE);

        TravelComponent travelComponent = new TravelComponent(1, 10, 0, "BIKE", "Home", "Poli", 0, 3600);

        calendarDao.insert(travelComponent);
        assert Objects.equals(calendarDao.getTravelComponents(0).getValue().get(0), travelComponent);
        assert Objects.equals(calendarDao.getAllTravelComponents().getValue().get(0), travelComponent);

        calendarDao.deleteTravelComponents();
        assert calendarDao.getAllTravelComponents().getValue().isEmpty();

        calendarDao.insert(travelComponent);
        assert calendarDao.getAllTravelComponents().getValue().size() == 1;
        calendarDao.deleteEventTravelComponents(0);
        assert calendarDao.getAllTravelComponents().getValue().isEmpty();
    }

    @Test
    public void selectTicket() throws Exception {
        GenericEvent genericEvent = new GenericEvent(0, "name", 0, 1000, true);
        Event event = new Event("", 0, "Home", false, true, "Poli");
        genericEvent.setEvent(event);
        genericEvent.setType(GenericEvent.EventType.EVENT);
        calendarDao.insert(genericEvent);

        Ticket ticket = new Ticket(2, 10);
        DistanceTicket distanceTicket = new DistanceTicket(50);
        ticket.setDistanceTicket(distanceTicket);
        ticket.setType(Ticket.TicketType.DISTANCE);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        ticketsDao.insert(tickets);

        TravelComponent travelComponent = new TravelComponent(1, 10, 0, "BIKE", "Home", "Poli", 0, 3600);
        calendarDao.insert(travelComponent);

        calendarDao.selectTicket((int)ticket.getId(), (int)travelComponent.getId());
        assert calendarDao.getAllTravelComponents().getValue().get(0).getTicketId() == 2;

        calendarDao.deselectTicket((int) travelComponent.getId());
        assert calendarDao.getAllTravelComponents().getValue().get(0).getTicketId() == 0;
    }
}
