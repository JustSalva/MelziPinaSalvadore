package it.polimi.travlendarplus.RESTful.messages.authenticationMessages;

import it.polimi.travlendarplus.exceptions.encryptionExceptions.DecryptionFailedException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

/**
 * Message to be sent by a client to perform a login
 */
public class Credentials extends AuthenticationMessage {

    private static final long serialVersionUID = 4386269294697577939L;

    private String email;
    private String password;
    private String idDevice;

    public Credentials () {
    }

    public Credentials ( String email, String password, String idDevice ) {
        this.email = email;
        this.password = password;
        this.idDevice = idDevice;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail ( String email ) {
        this.email = email;
    }

    /**
     * Automatically decrypts a password and return the plain password
     *
     * @return the plain password
     * @throws EntityNotFoundException   if the keyPair needed to decrypt
     *                                   the password has exceeded
     * @throws DecryptionFailedException if the password format is inconsistent
     */
    public String getPassword () throws EntityNotFoundException, DecryptionFailedException {
        //TODO
        /*RSAEncryption encryption = RSAEncryption.load( idDevice );
        return encryption.decryptPassword( password );*/
        return password;

    }

    public void setPassword ( String password ) {
        this.password = password;
    }

    public boolean isPasswordConsistent () {
        return password != null;
    }

    public String getIdDevice () {
        return idDevice;
    }

    public void setIdDevice ( String idDevice ) {
        this.idDevice = idDevice;
    }

    @Override
    public String toString () {
        return "Credentials{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idDevice='" + idDevice + '\'' +
                '}';
    }
}