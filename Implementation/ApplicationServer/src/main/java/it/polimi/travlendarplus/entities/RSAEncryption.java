package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.encryptionExceptions.DecryptionFailedException;
import it.polimi.travlendarplus.exceptions.encryptionExceptions.EncryptionFailedException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.crypto.Cipher;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
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
        //keyPairGenerator.initialize( 2048 );
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
    public static String encryptPassword ( String plainPassword, PublicKey publicKey )
            throws EncryptionFailedException {

        try {
            Cipher cipher = setUpCypher( Cipher.ENCRYPT_MODE, publicKey );
            return Base64.getEncoder().encodeToString( cipher.doFinal( plainPassword.getBytes("UTF-8") ) );
        } catch ( GeneralSecurityException | UnsupportedEncodingException e ) {
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
        Cipher cipher = Cipher.getInstance( "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING" );
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
        byte[] bytes = {48,-127,-97,48,13,6,9,42,-122,72,-122,-9,13,1,1,1,5,0,3,-127,-115,0,48,-127,-119,2,-127,-127,0,
                -125,-60,-126,-67,53,10,22,29,74,73,-24,108,64,27,-24,-76,-86,-58,4,-38,-35,14,85,71,8,38,-114,76,40,-32,
                -12,126,119,19,29,-7,2,119,44,94,119,113,-6,107,73,-65,19,-104,39,-84,105,36,-107,35,-84,99,3,-127,-28,8,
                -48,-6,-27,-52,21,-23,118,126,-40,-100,-47,-82,17,-34,-4,-122,-98,40,63,63,-108,-95,100,70,-39,-78,126,-77,
                -105,-85,116,37,-99,-28,68,-55,120,-23,79,45,-63,7,63,-3,87,46,-6,-102,-88,-44,-86,-18,-34,37,40,13,-125,
                124,6,-11,-32,-29,122,31,-18,-108,-1,121,2,3,1,0,1};
        PublicKey publicKey = KeyFactory.getInstance( "RSA" ).generatePublic( new X509EncodedKeySpec( bytes ) );
        String encryptedText = encryptPassword( plainText, publicKey );
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
            return new String( cipher.doFinal( Base64.getDecoder().decode( encryptedPassword ) ),"UTF-8" );
        } catch ( GeneralSecurityException | UnsupportedEncodingException e ) {
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

    public Instant getTimestamp () {
        return timestamp;
    }

    public void setTimestamp ( Instant timestamp ) {
        this.timestamp = timestamp;
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
