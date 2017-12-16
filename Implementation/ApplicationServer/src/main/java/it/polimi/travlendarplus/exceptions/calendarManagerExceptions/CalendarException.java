package it.polimi.travlendarplus.exceptions.calendarManagerExceptions;

import it.polimi.travlendarplus.exceptions.TravlendarPlusException;

public class CalendarException extends TravlendarPlusException {

    private static final long serialVersionUID = 6099327543014472244L;

    public CalendarException () {
    }

    public CalendarException ( String message ) {
        super( message );
    }
}
