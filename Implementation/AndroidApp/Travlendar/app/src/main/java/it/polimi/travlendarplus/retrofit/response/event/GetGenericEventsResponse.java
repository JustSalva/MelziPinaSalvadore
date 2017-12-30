package it.polimi.travlendarplus.retrofit.response.event;


import java.util.List;

/**
 * Generic event parsed from the JSON returned by the server.
 */
public class GetGenericEventsResponse {

    private List < EventResponse > updatedEvents;
    private List < BreakEventResponse > updatedBreakEvents;

    public List < EventResponse > getUpdatedEvents () {
        return updatedEvents;
    }

    public List < BreakEventResponse > getUpdatedBreakEvents () {
        return updatedBreakEvents;
    }
}
