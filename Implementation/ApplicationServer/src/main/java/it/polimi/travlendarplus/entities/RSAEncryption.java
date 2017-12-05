package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.encryptionExceptions.DecryprionFailedException;
import it.polimi.travlendarplus.exceptions.encryptionExceptions.EncryprionFailedException;

import java.security.*;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.persistence.*;

@Entity(name = "Encryption keys")
public class RSAEncryption extends EntityWithLongKey {

    private static final long serialVersionUID = 8890765076763091319L;

    @Column(name = "PUBLIC_KEY")
    private PublicKey publicKey;

    @Column(name = "PRIVATE_KEY")
    private PrivateKey privateKey;

    @OneToOne
    @JoinColumn(name = "USER_DEVICE")
    private UserDevice userDevice;


    public RSAEncryption() {
    }

    public RSAEncryption( UserDevice userDevice) {
        this.userDevice = userDevice;
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();//TODO
        }

    }

    // Decrypt using RSA public key
    private static String decryptPassword(String encryptedPassword, PublicKey publicKey) throws DecryprionFailedException{
        try {
            Cipher cipher = setUpCypher( Cipher.DECRYPT_MODE, publicKey );
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedPassword)));
        }catch ( GeneralSecurityException e ){
            throw new DecryprionFailedException();
        }
    }

    // Encrypt using RSA private key
    private static String encryptPassword(String plainPassword, PrivateKey privateKey) throws EncryprionFailedException{
        try {
            Cipher cipher = setUpCypher( Cipher.ENCRYPT_MODE, privateKey );
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainPassword.getBytes()));
        }catch ( GeneralSecurityException e ){
            throw new EncryprionFailedException();
        }
    }

    private static Cipher setUpCypher (int cipherMode, Key key) throws GeneralSecurityException{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(cipherMode, key);
        return cipher;
    }

    public static void main(String[] args) throws Exception {
        String plainText = "Hello World!";

        // Generate public and private keys using RSA
        RSAEncryption rsaEncryption = new RSAEncryption( new UserDevice( ) );

        String encryptedText = encryptPassword(plainText, rsaEncryption.privateKey);
        String descryptedText = decryptPassword(encryptedText, rsaEncryption.publicKey);

        System.out.println("input:" + plainText);
        System.out.println("encrypted:" + encryptedText);
        System.out.println("decrypted:" + descryptedText);

    }

    public UserDevice getUserDevice() {
        return userDevice;
    }

    public void setUserDevice( UserDevice userDevice ) {
        this.userDevice = userDevice;
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

    public static RSAEncryption load(UserDevice userDevice){
        return GenericEntity.load( RSAEncryption.class, userDevice );
    }

    @Override
    public boolean isAlreadyInDb() {
        return false;
    }
}
