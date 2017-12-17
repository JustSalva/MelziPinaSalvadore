package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.encryptionExceptions.DecryptionFailedException;
import it.polimi.travlendarplus.exceptions.encryptionExceptions.EncryprionFailedException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.crypto.Cipher;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.security.*;
import java.time.Instant;
import java.util.Base64;

@Entity( name = "Encryption_keys" )
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

    public String decryptPassword( String encryptedPassword ) throws DecryptionFailedException {
        return decryptPassword( encryptedPassword, privateKey );
    }

    // Decrypt using RSA public key
    private String decryptPassword ( String encryptedPassword, PrivateKey privateKey ) throws DecryptionFailedException {
        try {
            Cipher cipher = setUpCypher( Cipher.DECRYPT_MODE, privateKey );
            return new String( cipher.doFinal( Base64.getDecoder().decode( encryptedPassword ) ) );
        } catch ( GeneralSecurityException e ) {
            throw new DecryptionFailedException();
        }
    }

    // Encrypt using RSA private key
    private static String encryptPassword ( String plainPassword, PublicKey publicKey ) throws EncryprionFailedException {
        try {
            Cipher cipher = setUpCypher( Cipher.ENCRYPT_MODE, publicKey );
            return Base64.getEncoder().encodeToString( cipher.doFinal( plainPassword.getBytes() ) );
        } catch ( GeneralSecurityException e ) {
            throw new EncryprionFailedException();
        }
    }

    private static Cipher setUpCypher ( int cipherMode, Key key ) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance( "RSA" );
        cipher.init( cipherMode, key );
        return cipher;
    }

    public static RSAEncryption load ( String idDevice ) throws EntityNotFoundException {
        return GenericEntity.load( RSAEncryption.class, idDevice );
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

    @Override
    public boolean isAlreadyInDb () {
        try {
            load( idDevice );
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
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
}
