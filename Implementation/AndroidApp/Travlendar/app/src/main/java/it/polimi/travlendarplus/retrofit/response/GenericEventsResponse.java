package it.polimi.travlendarplus.retrofit.response;


import java.util.List;

public class GenericEventsResponse {

    private List<EventResponse> updatedEvents;
    private List<BreakEventResponse> updatedBreakEvents;

    public List<EventResponse> getUpdatedEvents() {
        return updatedEvents;
    }

    public List<BreakEventResponse> getUpdatedBreakEvents() {
        return updatedBreakEvents;
    }
}
