package com.shakk.travlendar.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.shakk.travlendar.database.entity.ticket.Ticket;

import java.util.Objects;

import static com.shakk.travlendar.database.entity.ticket.Ticket.TicketType.DISTANCE;
import static com.shakk.travlendar.database.entity.ticket.Ticket.TicketType.GENERAL;
import static com.shakk.travlendar.database.entity.ticket.Ticket.TicketType.PATH;
import static com.shakk.travlendar.database.entity.ticket.Ticket.TicketType.PERIOD;

public class TicketTypeConverters {
    @TypeConverter
    public static Ticket.TicketType toTicketType(String type) {
        if (Objects.equals(type, GENERAL.getType())) {
            return GENERAL;
        } else if (Objects.equals(type, PERIOD.getType())) {
            return PERIOD;
        } else if (Objects.equals(type, DISTANCE.getType())) {
            return DISTANCE;
        } else if (Objects.equals(type, PATH.getType())) {
            return PATH;
        } else {
            throw new IllegalArgumentException("Could not recognize type");
        }
    }

    @TypeConverter
    public static String toString(Ticket.TicketType type) {
        return type.getType();
    }
}
