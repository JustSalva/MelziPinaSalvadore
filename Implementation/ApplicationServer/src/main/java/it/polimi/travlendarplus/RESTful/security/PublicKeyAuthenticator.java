package it.polimi.travlendarplus.RESTful.security;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.entities.RSAEncryption;
import it.polimi.travlendarplus.messages.authenticationMessages.PublicKeyResponse;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

@Path("/")
public class PublicKeyAuthenticator {
    @EJB
    PublicKeyTimerInterface publicKeyTimerInterface;

    @Path( "/security/{ idDevice }" )
    @GET
    @Produces( MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response requestPublicKey( @PathParam("idDevice") String idDevice ){
        RSAEncryption encryption = null;
        try {
            encryption = new RSAEncryption( idDevice );
        } catch ( NoSuchAlgorithmException e ) {
            return HttpResponseBuilder.notAvaiable();
        }
        encryption.save();
        PublicKey publicKey = encryption.getPublicKey();
        publicKeyTimerInterface.scheduleSingleTimer( encryption );
        return HttpResponseBuilder.buildOkResponse( new PublicKeyResponse( publicKey ) );
    }
}
