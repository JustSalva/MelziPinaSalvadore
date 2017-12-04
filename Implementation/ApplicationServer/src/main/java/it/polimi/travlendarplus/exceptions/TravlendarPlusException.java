package it.polimi.travlendarplus.exceptions;

/**
 * This is the main exception class of travlendar plus project.
 * It is to be extended by all others project exceptions
 */
public class TravlendarPlusException extends Exception {

    private static final long serialVersionUID = -1603342441878689900L;

    public TravlendarPlusException() {
    }

    public TravlendarPlusException(String message) {
        super(message);
    }
}
