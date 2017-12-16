package it.polimi.travlendarplus.entities.calendar;

import it.polimi.travlendarplus.entities.GenericEntity;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity( name = "DATE_OF_CALENDAR" )
public class DateOfCalendar extends GenericEntity {

    private static final long serialVersionUID = 4766678681363951004L;

    @Id
    private long date;

    //TODO private ArrayList<EnvironmentConditions> conditions;

    public DateOfCalendar () {
    }

    public DateOfCalendar ( long date ) {
        this.date = date;
    }

    public static DateOfCalendar load ( long key ) throws EntityNotFoundException {
        return GenericEntity.load( DateOfCalendar.class, key );
    }

    public long getDate () {
        return date;
    }

    public void setDate ( long date ) {
        this.date = date;
    }

    @Override
    public boolean isAlreadyInDb () {
        try {
            load( date );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}
