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
import java.security.spec.KeySpec;
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
    private static final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    private static final int RSA_KEY_LENGTH = 2048;
    private static final String ALGORITHM_NAME = "RSA";
    private static final String PADDING_SCHEME = "OAEPWITHSHA-512ANDMGF1PADDING";
    private static final String MODE_OF_OPERATION = "ECB"; // This essentially means none behind the scene

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
        KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance( ALGORITHM_NAME );
        rsaKeyGen.initialize( RSA_KEY_LENGTH );
        KeyPair rsaKeyPair = rsaKeyGen.generateKeyPair();
        this.privateKey = rsaKeyPair.getPrivate();
        this.publicKey = rsaKeyPair.getPublic();
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
            return byteArrayToHexString( cipher.doFinal( plainPassword.getBytes("UTF-8") ) );
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
        Cipher cipher = Cipher.getInstance( ALGORITHM_NAME + "/" + MODE_OF_OPERATION + "/" + PADDING_SCHEME );
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
            return new String( cipher.doFinal(  hexStringToByteArray( encryptedPassword ) ),"UTF-8" );
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

    /**
     * Converts the String of hexadecimals to an array of bytes
     *
     * @param s string to be converted
     * @return the requested array
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    /**
     * Converts the array of bytes into a String of hexadecimals
     *
     * @param bytes array to be converted
     * @return the requested String
     */
    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length*2];
        int v;

        for(int j=0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v>>>4];
            hexChars[j*2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    public static void main ( String[] args ) throws Exception {
        String plainText = "password";

        // Generate public and private keys using RSA
        //RSAEncryption rsaEncryption = new RSAEncryption( "pippo" );
        RSAEncryption rsaEncryption = RSAEncryption.load( "idDevice" );
        byte[] bytes = { 48,-126,1,34,48,13,6,9,42,-122,72,-122,-9,13,1,1,1,5,0,3,-126,1,15,0,48,-126,1,10,2,-126,1
                ,1,0,-93,-52,-101,-92,85,94,-22,-60,13,0,32,-88,21,-102,-29,-22,83,41,122,28,68,84,-50,98,-1,-27,
                -93,85,-11,-95,125,-100,50,-113,-96,-43,118,-8,25,-95,54,-11,-95,-44,30,11,7,-74,-60,113,97,108,
                -59,110,31,31,17,15,-59,82,127,7,5,-73,-68,56,-26,46,98,-108,-120,37,54,11,55,53,-122,-101,97,-50,
                -121,-27,-14,98,-50,102,-62,-97,-103,-89,87,9,40,-57,68,-42,-126,-90,-30,-8,-101,88,112,-122,-43,-3,
                -3,-66,55,95,-110,-87,-61,-120,-1,5,-3,-100,84,45,81,102,87,78,-104,61,106,27,-52,-92,-15,-112,8,-39,
                -4,-121,21,78,103,87,-120,-6,-97,40,-8,-41,67,20,-10,-110,11,-11,-75,53,-119,-107,-14,119,90,59,-1,43,
                -10,106,111,-13,42,-18,-48,-23,-26,95,32,-104,92,28,10,-108,8,-10,-68,-21,37,-89,53,-35,65,48,78,30,-17,
                -100,-51,-20,114,2,-105,-44,-124,63,-89,-53,-97,14,67,-46,72,-34,76,-25,-48,-66,20,55,56,-121,85,-81,-47,
                12,44,31,-78,3,-74,-31,22,53,11,27,67,13,-65,-3,-58,2,-110,-18,-99,86,-60,-115,67,-5,37,105,-40,83,-46,
                13,-8,-36,-14,-128,91,-97,2,3,1,0,1};
        PublicKey publicKey = KeyFactory.getInstance( "RSA" ).generatePublic( new X509EncodedKeySpec( bytes ) );
        String encryptedText = encryptPassword( plainText, publicKey );
        String decryptedText = rsaEncryption.decryptPassword( encryptedText );

        System.out.println( "input:" + plainText );

        System.out.println("encrypted:" + encryptedText);
        System.out.println( "decrypted:" + decryptedText );

    }
}
