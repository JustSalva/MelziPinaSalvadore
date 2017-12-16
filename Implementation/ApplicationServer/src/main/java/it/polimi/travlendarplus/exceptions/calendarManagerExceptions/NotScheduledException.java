package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

public class NotScheduledException extends CalendarException {

    private static final long serialVersionUID = 6562594429309455377L;

    public NotScheduledException () {
    }

    public NotScheduledException ( String message ) {
        super( message );
    }
}
