package it.polimi.travlendarplus.messages.calendarMessages.preferenceMessages;

import it.polimi.travlendarplus.messages.calendarMessages.CalendarResponse;

public class PreferredLocationResponse extends CalendarResponse {

    private static final long serialVersionUID = -8395557542049928652L;

    private String name;
    private String address;

    public PreferredLocationResponse() {
    }

    public PreferredLocationResponse( String name, String address ) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }
}
