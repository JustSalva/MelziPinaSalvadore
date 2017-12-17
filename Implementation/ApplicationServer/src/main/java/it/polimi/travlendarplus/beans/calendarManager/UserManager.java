package it.polimi.travlendarplus.beans.calendarManager;

import it.polimi.travlendarplus.entities.User;

import javax.ejb.Stateless;

/**
 * This is a generic manager class, to be extended by all managers in order to receive an injected user
 */
@Stateless
public class UserManager {

    protected User currentUser;

    public User getCurrentUser () {
        return currentUser;
    }

    public void setCurrentUser ( User currentUser ) {
        this.currentUser = currentUser;
    }
}
