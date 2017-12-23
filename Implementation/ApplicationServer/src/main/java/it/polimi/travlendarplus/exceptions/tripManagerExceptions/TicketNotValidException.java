package it.polimi.travlendarplus.exceptions.tripManagerExceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This exception is thrown when a travel component is added to a ticket,
 * but the ticket is not compatible with such travel component;
 * the specific conflict is specified inside the Exception
 */
public class TicketNotValidException extends TripManagerException {

    private static final long serialVersionUID = 3115093008344891282L;

    /**
     * List of possible conflicts if the new travel component is added
     */
    private List < String > errors;

    public TicketNotValidException () {
        this.errors = new ArrayList <>();
    }

    public TicketNotValidException ( String exceptionDescription ) {
        this();
        errors.add( exceptionDescription );
    }

    public List < String > getErrors () {
        return errors;
    }

    public void setErrors ( List < String > errors ) {
        this.errors = errors;
    }

    public void addErrors ( List < String > errors ) {
        this.errors.addAll( errors );
    }
}
