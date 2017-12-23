package it.polimi.travlendarplus.RESTful.messages;

import java.io.Serializable;

/**
 * This is the main message class to be sent and to be received through
 * the network connectivity; it is to be extended by all messages classes.
 */
public abstract class GenericMessage implements Serializable {
    private static final long serialVersionUID = -2544036463356969106L;
}
