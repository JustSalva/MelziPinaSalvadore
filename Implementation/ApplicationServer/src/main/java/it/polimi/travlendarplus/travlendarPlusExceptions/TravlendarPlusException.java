package it.polimi.travlendarplus.travlendarPlusExceptions;

/**
 * This is the main exception class of travlendar plus project.
 * It is to be extended by all others project exceptions
 */
public class TravlendarPlusException extends Exception {

    public TravlendarPlusException(String message) {
        super(message);
    }
}
