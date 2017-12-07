package it.polimi.travlendarplus.beans.calendar_manager.support.GMapsException;

public class BadRequestException extends GMapsGeneralException {

    public BadRequestException() {
        super("Sorry! This request can't be performed.");
    }

    public BadRequestException(String message) {
        super(message);
    }

}
