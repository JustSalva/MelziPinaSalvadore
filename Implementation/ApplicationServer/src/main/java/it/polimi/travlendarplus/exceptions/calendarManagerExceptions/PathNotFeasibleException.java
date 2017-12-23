package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

/**
 * Exception thrown when a path is identified as non feasible
 * (either duration is too much for the allotted time,
 * or it does't satisfy the user's preferences)
 */
public class PathNotFeasibleException extends CalendarException {

    private static final long serialVersionUID = -4998363330823504627L;

    public PathNotFeasibleException () {
        super( WrongFields.PATH_NOT_FEASIBLE );
    }

    public PathNotFeasibleException ( String message ) {
        super( message );
    }
}
