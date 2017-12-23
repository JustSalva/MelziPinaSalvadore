package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

/**
 * This exception is thrown when the info of a path are requested form a non scheduled event
 * (a non scheduled event  doesn't have a path)
 */
public class NotScheduledException extends CalendarException {

    private static final long serialVersionUID = 6562594429309455377L;

    public NotScheduledException () {
    }

    public NotScheduledException ( String message ) {
        super( message );
    }
}
