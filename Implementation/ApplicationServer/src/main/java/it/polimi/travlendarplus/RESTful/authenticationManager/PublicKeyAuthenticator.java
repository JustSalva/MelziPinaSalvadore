package it.polimi.travlendarplus.RESTful.authenticationManager;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.messages.authenticationMessages.PublicKeyResponse;
import it.polimi.travlendarplus.entities.RSAEncryption;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

@Path( "/" )
public class PublicKeyAuthenticator {

    /**
     * EJB that provide a timer that will delete the generated Public key after a certain duration
     */
    @EJB
    private PublicKeyTimerInterface publicKeyTimerInterface;

    /**
     * Allows the user to obtain a public key
     *
     * @param idDevice identifier of the device that perform the request
     * @return an HTTP 200 OK success status response code and the requested public key ( in the message body )
     * or an HTTP 503 Service Unavailable response status code if something goes wrong
     */
    @Path( "/security/{ idDevice }" )
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response requestPublicKey ( @PathParam( "idDevice" ) String idDevice ) {
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
