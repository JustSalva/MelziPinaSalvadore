package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

/**
 * This exception is thrown when a swap request is performed on an already scheduled generic event
 */
public class AlreadyScheduledException extends CalendarException {

    private static final long serialVersionUID = 2411808805635557880L;

    public AlreadyScheduledException () {
        super( "event already inside the schedule" );
    }

    public AlreadyScheduledException ( String message ) {
        super( message );
    }
}
