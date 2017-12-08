package it.polimi.travlendarplus.messages.calendarMessages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.travlendarplus.messages.GenericResponseMessage;

public class JsonBodyResponse extends GenericResponseMessage {

    private static final long serialVersionUID = 2515057482016649610L;

    private String jsonResponse;

    public JsonBodyResponse() {
    }

    public JsonBodyResponse( Object response ) {
        Gson gson = new GsonBuilder().create();
        this.jsonResponse = gson.toJson( response );
    }

    public String getResponseCodifiedInJson() {
        return jsonResponse;
    }

    public void setResponseCodifiedInJson( String responseCodifiedInJson ) {
        this.jsonResponse = responseCodifiedInJson;
    }
}
