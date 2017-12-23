package it.polimi.travlendarplus.exceptions.tripManagerExceptions;

/**
 * This helper class provide static constants to be used to communicate
 * why a ticket is not applicable to a travel component
 */
public class TicketNotValidCauses {

    public static final String OUT_OF_VALIDITY_PERIOD =
            "The travel component is out of the validity period of the ticket ";
    public static final String WRONG_LINE_NAME = "Ticket's line name " +
            "different from the travel component's line name";
    public static final String MAX_DISTANCE_EXCEEDED =
            "The maximum distance of the ticket would be exceeded";
}
