package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

public class PathNotFeasibleException extends CalendarException{

    public PathNotFeasibleException() {
        super( "path not feasible");
    }

    public PathNotFeasibleException( String message ) {
        super( message );
    }
}
