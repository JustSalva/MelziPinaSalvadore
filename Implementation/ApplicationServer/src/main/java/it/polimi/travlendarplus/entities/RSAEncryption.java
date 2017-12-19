package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.encryptionExceptions.DecryptionFailedException;
import it.polimi.travlendarplus.exceptions.encryptionExceptions.EncryptionFailedException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.crypto.Cipher;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.security.*;
import java.time.Instant;
import java.util.Base64;

/**
 * This JPA class represent a RSA key pair, associated to a specific device
 * in order to encrypt and decrypt sensible data sent through internet connectivity
 */
@Entity( name = "Encryption_keys" )
public class RSAEncryption extends GenericEntity {

    private static final long serialVersionUID = 8890765076763091319L;

    /**
     * Identifier of the device the credentials are associated to
     */
    @Id
    private String idDevice;

    /**
     * RSA Public key that is sent to the client
     */
    @Column( name = "PUBLIC_KEY" )
    private PublicKey publicKey;

    /**
     * RSA Private key that remains in server database and is never exposed
     */
    @Column( name = "PRIVATE_KEY" )
    private PrivateKey privateKey;

    /**
     * Timestamp in Unix time that memorize the last update of an RSA key pair
     */
    @Column( name = "UNIX_TIMESTAMP" )
    private Instant timestamp;

    public RSAEncryption () {
    }

    public RSAEncryption ( String idDevice ) throws NoSuchAlgorithmException {
        this.idDevice = idDevice;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance( "RSA" );
        keyPairGenerator.initialize( 2048 );
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        this.timestamp = Instant.now();
    }

    /**
     * Encrypts a password using RSA public key
     *
     * @param plainPassword password to be encrypted
     * @param publicKey     public key used in order to encrypt the password
     * @return the encrypted password
     * @throws EncryptionFailedException if the given encryptedPassword can't be encrypted
     */
    //
    private static String encryptPassword ( String plainPassword, PublicKey publicKey )
            throws EncryptionFailedException {

        try {
            Cipher cipher = setUpCypher( Cipher.ENCRYPT_MODE, publicKey );
            return Base64.getEncoder().encodeToString( cipher.doFinal( plainPassword.getBytes() ) );
        } catch ( GeneralSecurityException e ) {
            throw new EncryptionFailedException();
        }
    }

    /**
     * Sets up a cypher that allows either to encrypt or decrypt something
     * given either a public or private key
     *
     * @param cipherMode = 1 if ENCRYPT_MODE, = 2 DECRYPT_MODE;
     * @param key        key do be used in the process
     * @return the requested cypher
     * @throws GeneralSecurityException if an error occurs while cypher is built
     * @see Cipher
     */
    private static Cipher setUpCypher ( int cipherMode, Key key ) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance( "RSA" );
        cipher.init( cipherMode, key );
        return cipher;
    }

    /**
     * Allows to load a RSAEncryption class from the database
     *
     * @param idDevice primary key of the RSAEncryption tuple
     * @return the requested tuple as a RSAEncryption class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static RSAEncryption load ( String idDevice ) throws EntityNotFoundException {
        return GenericEntity.load( RSAEncryption.class, idDevice );
    }

    public static void main ( String[] args ) throws Exception {
        String plainText = "password";

        // Generate public and private keys using RSA
        //RSAEncryption rsaEncryption = new RSAEncryption( "pippo" );
        RSAEncryption rsaEncryption = RSAEncryption.load( "idDevice" );

        String encryptedText = encryptPassword( plainText, rsaEncryption.publicKey );
        String decryptedText = rsaEncryption.decryptPassword( encryptedText );

        System.out.println( "input:" + plainText );
        System.out.println( "encrypted:" + encryptedText );
        System.out.println( "decrypted:" + decryptedText );

    }

    /**
     * Decrypts a given password using RSA
     *
     * @param encryptedPassword password to be decrypted
     * @return the decrypted password
     * @throws DecryptionFailedException if the given encryptedPassword is not valid
     */
    public String decryptPassword ( String encryptedPassword ) throws DecryptionFailedException {
        return decryptPassword( encryptedPassword, privateKey );
    }

    /**
     * Decrypts a given password using RSA private key
     *
     * @param encryptedPassword password to be decrypted
     * @param privateKey        private key used in order to decrypt the password
     * @return the decrypted password
     * @throws DecryptionFailedException if the given encryptedPassword is not valid
     */
    private String decryptPassword ( String encryptedPassword, PrivateKey privateKey )
            throws DecryptionFailedException {

        try {
            Cipher cipher = setUpCypher( Cipher.DECRYPT_MODE, privateKey );
            return new String( cipher.doFinal( Base64.getDecoder().decode( encryptedPassword ) ) );
        } catch ( GeneralSecurityException e ) {
            throw new DecryptionFailedException();
        }
    }

    public String getIdDevice () {
        return idDevice;
    }

    public void setIdDevice ( String idDevice ) {
        this.idDevice = idDevice;
    }

    public PublicKey getPublicKey () {
        return publicKey;
    }

    public void setPublicKey ( PublicKey publicKey ) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey () {
        return privateKey;
    }

    public void setPrivateKey ( PrivateKey privateKey ) {
        this.privateKey = privateKey;
    }

    /**
     * Checks if a RSAEncryption instance is already present in the database
     *
     * @return true if present, false otherwise
     */
    @Override
    public boolean isAlreadyInDb () {
        try {
            load( idDevice );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}
