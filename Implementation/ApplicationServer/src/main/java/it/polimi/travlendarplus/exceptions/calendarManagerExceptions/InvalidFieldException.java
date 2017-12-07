package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

public class InvalidFieldException extends CalendarException {

    private static final long serialVersionUID = 127086253559991638L;

    public InvalidFieldException() {
    }

    public InvalidFieldException( String message ) {
        super( message );
    }
}
