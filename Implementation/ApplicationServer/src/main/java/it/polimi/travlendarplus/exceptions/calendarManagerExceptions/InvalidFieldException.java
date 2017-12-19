package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

import java.util.List;

/**
 * This exception is thrown when a message received is checked and some fields are invalid,
 * which ones is specified into the list contained in this class
 */
public class InvalidFieldException extends CalendarException {

    private static final long serialVersionUID = 127086253559991638L;

    List < String > invalidFields;

    public InvalidFieldException () {
    }

    public InvalidFieldException ( List < String > invalidFields ) {
        this.invalidFields = invalidFields;
    }

    public List < String > getInvalidFields () {
        return invalidFields;
    }
}
