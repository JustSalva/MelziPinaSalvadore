package it.polimi.travlendarplus.RESTful.messages.authenticationMessages;

import it.polimi.travlendarplus.RESTful.messages.GenericMessage;


/**
 * Message sent when the user want to reset his password and he have to specify his email
 */
public class EmailMessage extends GenericMessage {

    private static final long serialVersionUID = 7433287640010660407L;

    private String email;

    public EmailMessage () {
    }

    public EmailMessage ( String email ) {
        this.email = email;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail ( String email ) {
        this.email = email;
    }
}
