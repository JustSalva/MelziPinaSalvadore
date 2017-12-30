package it.polimi.travlendarplus;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.polimi.travlendarplus.database.AppDatabase;
import it.polimi.travlendarplus.database.dao.CalendarDao;
import it.polimi.travlendarplus.database.dao.TicketsDao;
import it.polimi.travlendarplus.database.entity.TravelComponent;
import it.polimi.travlendarplus.database.entity.event.Event;
import it.polimi.travlendarplus.database.entity.event.GenericEvent;
import it.polimi.travlendarplus.database.entity.ticket.DistanceTicket;
import it.polimi.travlendarplus.database.entity.ticket.Ticket;

@RunWith(AndroidJUnit4.class)
public class TicketsDaoTest {

    private TicketsDao ticketsDao;
    private CalendarDao calendarDao;
    private AppDatabase database;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        ticketsDao = database.ticketsDao();
        calendarDao = database.calendarDao();
    }

    @After
    public void closeDb() throws IOException {
        database.close();
    }

    @Test
    public void tickets() {
        Ticket ticket = new Ticket(2, 10);
        DistanceTicket distanceTicket = new DistanceTicket(50);
        ticket.setDistanceTicket(distanceTicket);
        ticket.setType(Ticket.TicketType.DISTANCE);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        ticketsDao.insert(tickets);
        assert Objects.equals(ticketsDao.getTickets().getValue().get(0), ticket);

        ticket.setCost(30);
        ticketsDao.update(ticket);
        assert Objects.equals(ticketsDao.getTickets().getValue().get(0), ticket);

        ticketsDao.delete(ticket);
        assert ticketsDao.getTickets().getValue().size() == 0;

        ticketsDao.insert(tickets);
        ticketsDao.deleteAll();
        assert ticketsDao.getTickets().getValue().size() == 0;

        ticketsDao.insert(tickets);
        ticketsDao.deleteFromId(2);
        assert ticketsDao.getTickets().getValue().size() == 0;
    }

    @Test
    public void deleteTicketFromTravelComponent() throws Exception {
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

        ticketsDao.removeTicketFromTravelComponent((int) ticket.getId());
        assert calendarDao.getAllTravelComponents().getValue().get(0).getTicketId() == 0;
    }
}
