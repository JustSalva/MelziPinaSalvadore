package it.polimi.travlendarplus.beans.calendar_manager;

import it.polimi.travlendarplus.entities.User;

import javax.ejb.Stateless;

@Stateless
public class UserManager {

    protected User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
