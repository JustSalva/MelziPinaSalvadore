package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

public class AlreadyScheduledException extends CalendarException {

    private static final long serialVersionUID = 2411808805635557880L;

    public AlreadyScheduledException () {
        super( "event already inside the schedule" );
    }

    public AlreadyScheduledException ( String message ) {
        super( message );
    }
}
