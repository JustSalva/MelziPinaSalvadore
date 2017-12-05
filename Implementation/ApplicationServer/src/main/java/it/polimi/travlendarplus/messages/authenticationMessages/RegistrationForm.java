package it.polimi.travlendarplus.messages.authenticationMessages;

import it.polimi.travlendarplus.messages.authenticationMessages.Credentials;

/**
 * Message sent when an user want to register himself into the system
 */
public class RegistrationForm extends Credentials {

    private static final long serialVersionUID = -2803431881475572856L;

    private String name;
    private String surname;

    public RegistrationForm() {
    }

    public RegistrationForm( String email, String password, String idDevice,
                             String name, String surname ) {
        super( email, password, idDevice );
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname( String surname ) {
        this.surname = surname;
    }

}
