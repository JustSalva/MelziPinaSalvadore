package it.polimi.travlendarplus.exceptions;

public class UserNotRegisteredException extends TravlendarPlusException {

    private static final long serialVersionUID = 3675655546635692167L;

    public UserNotRegisteredException() {
    }

    public UserNotRegisteredException(String message) {
        super(message);
    }
}
