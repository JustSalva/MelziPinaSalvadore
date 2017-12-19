package it.polimi.travlendarplus.database.converters;

import android.arch.persistence.room.TypeConverter;

import it.polimi.travlendarplus.database.entity.event.GenericEvent;

import java.util.Objects;

import static it.polimi.travlendarplus.database.entity.event.GenericEvent.EventType.BREAK;
import static it.polimi.travlendarplus.database.entity.event.GenericEvent.EventType.EVENT;

public class EventTypeConverters {
    @TypeConverter
    public static GenericEvent.EventType toEventType(String type) {
        if (Objects.equals(type, EVENT.getType())) {
            return EVENT;
        } else if (Objects.equals(type, BREAK.getType())) {
            return BREAK;
        } else {
            throw new IllegalArgumentException("Could not recognize type");
        }
    }

    @TypeConverter
    public static String toString(GenericEvent.EventType type) {
        return type.getType();
    }
}
