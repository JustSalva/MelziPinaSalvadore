package it.polimi.travlendarplus.RESTful.messages.calendarMessages.eventMessages;

import it.polimi.travlendarplus.RESTful.messages.calendarMessages.CalendarResponse;

import java.util.List;

/**
 * This is a message class sent to reply to the client with a list of errors
 * ( invalid fields or not satisfied conditions ) that are present in the request body
 */
public class ErrorListResponse extends CalendarResponse {

    private static final long serialVersionUID = 73176428865424620L;

    private List < String > messages;

    public ErrorListResponse () {
    }

    public ErrorListResponse ( List < String > messages ) {
        this.messages = messages;
    }

    public List < String > getMessages () {
        return messages;
    }

    public void setMessages ( List < String > messages ) {
        this.messages = messages;
    }
}
