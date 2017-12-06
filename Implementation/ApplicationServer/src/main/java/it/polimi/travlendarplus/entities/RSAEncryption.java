package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.encryptionExceptions.DecryprionFailedException;
import it.polimi.travlendarplus.exceptions.encryptionExceptions.EncryprionFailedException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import java.security.*;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.persistence.*;

@Entity( name = "Encryption keys" )
public class RSAEncryption extends GenericEntity {

    private static final long serialVersionUID = 8890765076763091319L;

    @Id
    private String idDevice;

    @Column( name = "PUBLIC_KEY" )
    private PublicKey publicKey;

    @Column( name = "PRIVATE_KEY" )
    private PrivateKey privateKey;

    @Column( name = "UNIX_TIMESTAMP" )
    private Instant timestamp;

    public RSAEncryption() {
    }

    public RSAEncryption( String idDevice ) {
        this.idDevice = idDevice;
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance( "RSA" );
            keyPairGenerator.initialize( 2048 );
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            this.timestamp = Instant.now();
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();//TODO
        }

    }

    // Decrypt using RSA public key
    private static String decryptPassword( String encryptedPassword, PublicKey publicKey ) throws DecryprionFailedException {
        try {
            Cipher cipher = setUpCypher( Cipher.DECRYPT_MODE, publicKey );
            return new String( cipher.doFinal( Base64.getDecoder().decode( encryptedPassword ) ) );
        } catch ( GeneralSecurityException e ) {
            throw new DecryprionFailedException();
        }
    }

    // Encrypt using RSA private key
    private static String encryptPassword( String plainPassword, PrivateKey privateKey ) throws EncryprionFailedException {
        try {
            Cipher cipher = setUpCypher( Cipher.ENCRYPT_MODE, privateKey );
            return Base64.getEncoder().encodeToString( cipher.doFinal( plainPassword.getBytes() ) );
        } catch ( GeneralSecurityException e ) {
            throw new EncryprionFailedException();
        }
    }

    private static Cipher setUpCypher( int cipherMode, Key key ) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance( "RSA" );
        cipher.init( cipherMode, key );
        return cipher;
    }

    public static void main( String[] args ) throws Exception {
        String plainText = "Hello World!";

        // Generate public and private keys using RSA
        RSAEncryption rsaEncryption = new RSAEncryption( "pippo" );

        String encryptedText = encryptPassword( plainText, rsaEncryption.privateKey );
        String descryptedText = decryptPassword( encryptedText, rsaEncryption.publicKey );

        System.out.println( "input:" + plainText );
        System.out.println( "encrypted:" + encryptedText );
        System.out.println( "decrypted:" + descryptedText );

    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice( String idDevice ) {
        this.idDevice = idDevice;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey( PublicKey publicKey ) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey( PrivateKey privateKey ) {
        this.privateKey = privateKey;
    }

    public static RSAEncryption load( String idDevice ) throws EntityNotFoundException {
        return GenericEntity.load( RSAEncryption.class, idDevice );
    }

    @Override
    public boolean isAlreadyInDb() {
        try {
            load( idDevice );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }
}
