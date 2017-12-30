package it.polimi.travlendarplus.database.converters;

import android.arch.persistence.room.TypeConverter;

import java.util.Objects;

import it.polimi.travlendarplus.database.entity.ticket.Ticket;

import static it.polimi.travlendarplus.database.entity.ticket.Ticket.TicketType.DISTANCE;
import static it.polimi.travlendarplus.database.entity.ticket.Ticket.TicketType.GENERIC;
import static it.polimi.travlendarplus.database.entity.ticket.Ticket.TicketType.PATH;
import static it.polimi.travlendarplus.database.entity.ticket.Ticket.TicketType.PERIOD;

/**
 * Class that contains type converters for saving ticket objects in the DB.
 */
public class TicketTypeConverters {
    @TypeConverter
    public static Ticket.TicketType toTicketType ( String type ) {
        if ( Objects.equals( type, GENERIC.getType() ) ) {
            return GENERIC;
        } else if ( Objects.equals( type, PERIOD.getType() ) ) {
            return PERIOD;
        } else if ( Objects.equals( type, DISTANCE.getType() ) ) {
            return DISTANCE;
        } else if ( Objects.equals( type, PATH.getType() ) ) {
            return PATH;
        } else {
            throw new IllegalArgumentException( "Could not recognize type" );
        }
    }

    @TypeConverter
    public static String toString ( Ticket.TicketType type ) {
        return type.getType();
    }
}
