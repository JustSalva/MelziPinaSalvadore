package it.polimi.travlendarplus.messages.calendarMessages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.travlendarplus.messages.GenericResponseMessage;

public class JsonBodyResponse extends GenericResponseMessage {

    private static final long serialVersionUID = 2515057482016649610L;

    private String responseCodifiedInJson;

    public JsonBodyResponse() {
    }

    public JsonBodyResponse( Object response ) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.responseCodifiedInJson = gson.toJson( response );
    }

    public String getResponseCodifiedInJson() {
        return responseCodifiedInJson;
    }

    public void setResponseCodifiedInJson( String responseCodifiedInJson ) {
        this.responseCodifiedInJson = responseCodifiedInJson;
    }
}
