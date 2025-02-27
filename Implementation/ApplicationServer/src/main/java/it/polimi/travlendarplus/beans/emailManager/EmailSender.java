package it.polimi.travlendarplus.beans.emailManager;

import it.polimi.travlendarplus.entities.User;
import it.polimi.travlendarplus.exceptions.authenticationExceptions.MailPasswordForwardingFailedException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * It provide functionalities to send emails from travlendar plus proprietary email
 */
@Startup
@Singleton
public class EmailSender implements EmailInterface {

    private static String SMTP = "smtp";
    private static String HOST = "smtp.gmail.com";
    private static String TRUE = "true";
    private static String PORT = "587";
    private static String SMTP_PORT = "mail.smtp.port";
    private static String SMTP_AUTH = "mail.smtp.auth";
    private static String SMTP_TLS_ENABLE = "mail.smtp.starttls.enable";
    private static String SMTP_SSL_TRUST = "mail.smtp.ssl.trust";

    private static String SUBJECT_RESET_PASSWORD = "Travlendar plus - Password Reset";
    private static String BODY_RESET_PASSWORD = "Hi, <br><br> this is your new password: <br>";
    private static String BODY_SIGNATURE = "<br><br> Regards, <br>Travlendar plus staff";
    private static String TEXT_HTML = "text/html";

    //TODO read mail and pws from file?
    private static String TRAVLENDAR_MAIL = "travlendarplus2018@gmail.com";
    private static String PWS = "melzipinasalva";

    private static Properties mailServerProperties;

    /**
     * Sets up the Travlendar's email properties
     */
    @PostConstruct
    public void setUP () {
        mailServerProperties = System.getProperties();
        mailServerProperties.put( SMTP_PORT, PORT );
        mailServerProperties.put( SMTP_AUTH, TRUE );
        mailServerProperties.put( SMTP_TLS_ENABLE, TRUE );
        mailServerProperties.put( SMTP_SSL_TRUST, HOST );
    }

    /**
     * It sends an email containing the new password of an user
     *
     * @param password       new password of the recipient user
     * @param emailRecipient email address of the user which password is to be sent
     * @throws MessagingException if the email forwarding process fails
     */
    private void sendNewPasswordEmail ( String password, String emailRecipient ) throws MessagingException {
        Session getMailSession;
        MimeMessage generateMailMessage;
        getMailSession = Session.getDefaultInstance( mailServerProperties, null );
        generateMailMessage = new MimeMessage( getMailSession );
        generateMailMessage.addRecipient( Message.RecipientType.TO, new InternetAddress( emailRecipient ) );
        generateMailMessage.setSubject( SUBJECT_RESET_PASSWORD );
        //TODO better-looking email?
        String emailBody = BODY_RESET_PASSWORD + password + BODY_SIGNATURE;
        generateMailMessage.setContent( emailBody, TEXT_HTML );
        Transport transport = getMailSession.getTransport( SMTP );

        transport.connect( HOST, TRAVLENDAR_MAIL, PWS );
        transport.sendMessage( generateMailMessage, generateMailMessage.getAllRecipients() );
        transport.close();
    }

    /**
     * Compute and send a new email to the specified user
     *
     * @param user user that wants to reset his password
     * @throws MailPasswordForwardingFailedException if the email forwarding process fails
     */
    public void sendNewCredentials ( User user ) throws MailPasswordForwardingFailedException {
        SecureRandom secureRandom = new SecureRandom();
        long longToken = Math.abs( secureRandom.nextLong() );
        String newPassword = Long.toString( longToken, 25 );
        user.setPassword( newPassword );

        try {
            sendNewPasswordEmail( newPassword, user.getEmail() );
        } catch ( MessagingException e ) {
            throw new MailPasswordForwardingFailedException();
        }

        user.save();
    }

    public static void main ( String[] args ) {
        EmailSender emailSender = new EmailSender();
        try {
            emailSender.sendNewPasswordEmail( "newPassword", "matteo.salvadore@gmail.com" );
        } catch ( MessagingException e ) {
            e.printStackTrace();
        }
    }
}
