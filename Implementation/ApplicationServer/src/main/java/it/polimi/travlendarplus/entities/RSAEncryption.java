package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.encryptionExceptions.DecryptionFailedException;
import it.polimi.travlendarplus.exceptions.encryptionExceptions.EncryptionFailedException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.crypto.Cipher;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    public static String encryptPassword ( String plainPassword, PublicKey publicKey )
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
        byte[] bytes = { 48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 1, 15, 0, 48, -126, 1, 10, 2, -126, 1, 1,
                0, -110, 37, 38, -66, -118, 117, 30, 31, -117, 2, 68, -48, 119, -54, 110, 41, -2, 89, 52, 24, 60, 95, -114, -84, 121, -117, 68,
                101, 31, 33, -91, 19, 54, 36, -54, 28, -68, 27, 88, -13, -67, 11, -41, 39, 6, -58, -28, -110, 53, 29, -69, -127, -80, 118, 120, 20,
                84, 42, 2, -11, -89, -60, -84, -21, -123, -71, -18, -1, 1, -37, 60, -34, -56, 21, 2, 68, -13, 11, 1, -94, -7, 76, 78, -1, 113, -118,
                107, 47, 16, -85, -21, -43, 83, -30, 50, -12, 103, 11, -89, -46, 123, 6, -48, -51, -21, -26, 51, -123, 75, -11, 23, -85, 74, 28, -85,
                -77, -5, -21, -84, -84, 56, -27, -42, 70, -73, -28, 13, 86, -101, -53, -64, -4, -29, -17, 12, -110, 69, -128, 123, 32, 3, 108, 55, -73,
                85, -19, 66, 31, 118, -108, 125, 4, 19, 28, -25, -87, -34, 56, -4, 9, 125, 1, -40, 81, -77, 87, -82, 84, -11, 63, -17, -73, 45, 40, 53, -92,
                -3, -61, 76, 85, 121, 18, 108, -122, 89, 116, 78, 68, -3, 108, 17, 126, 20, -14, 50, 53, 11, -45, 13, -73, -5, -12, 57, -75, 8, -60, -36,
                3, 122, 123, 60, -14, -126, 81, 43, -38, 18, -14, 82, -109, -123, 56, -34, 53, -27, 73, 18, -102, -67, 106, -59, 58, -35, 92, 52, 41,
                -20, -10, 48, 99, -15, 30, -62, -20, -81, -116, 48, -32, 104, -5, -35, -67, 78, 102, 77, -47, 2, 3, 1, 0, 1 };
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
