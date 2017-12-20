package it.polimi.travlendarplus;

import it.polimi.travlendarplus.RESTful.HttpResponseBuilder;
import it.polimi.travlendarplus.RESTful.authenticationManager.AuthenticatedUser;
import it.polimi.travlendarplus.RESTful.authenticationManager.Secured;
import it.polimi.travlendarplus.RESTful.messages.authenticationMessages.PublicKeyResponse;
import it.polimi.travlendarplus.entities.Location;
import it.polimi.travlendarplus.entities.RSAEncryption;
import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.entities.calendar.Event;
import it.polimi.travlendarplus.entities.travels.Travel;
import it.polimi.travlendarplus.exceptions.encryptionExceptions.EncryptionFailedException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;

// The Java class will be hosted at the URI path "/prova"
@Path( "/prova" )
public class HelloWorld {

    @Inject
    @AuthenticatedUser
    User authenticatedUser;

    @GET
    @Secured
    @Path( "/a" )
    @Produces( "text/plain" )
    public String prova () {
        return authenticatedUser.getEmail();
    }

    @Path( "IdTest/{id}" )
    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces( MediaType.APPLICATION_JSON )
    public Response getClichedMessage ( @PathParam( "id" ) Integer id ) {

        RSAEncryption rsaEncryption = null;
        try {
            rsaEncryption = RSAEncryption.load( "idDevice" );
        } catch ( EntityNotFoundException e ) {
            e.printStackTrace();
        }

        return HttpResponseBuilder.buildOkResponse( rsaEncryption.getPublicKey().getEncoded() );
        /*ogg.setAddress("prova");
        ogg.save();*/
        //ogg.remove();
        //return ogg;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response function ( PublicKeyResponse publicKeyResponse ) {
        PublicKey publicKey = null;
        String encryptedText = null;
        try {
            publicKey = KeyFactory.getInstance( "RSA" ).generatePublic( new X509EncodedKeySpec( publicKeyResponse.getPublicKey() ) );
            try {
                encryptedText = RSAEncryption.encryptPassword( "password", publicKey );
            } catch ( EncryptionFailedException e ) {
                e.printStackTrace();
            }
        } catch ( InvalidKeySpecException e ) {
            e.printStackTrace();
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        return HttpResponseBuilder.buildOkResponse( encryptedText );
    }

}