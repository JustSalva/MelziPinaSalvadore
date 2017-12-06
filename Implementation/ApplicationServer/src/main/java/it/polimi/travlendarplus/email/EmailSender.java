package it.polimi.travlendarplus.email;

import it.polimi.travlendarplus.entities.User;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import java.security.SecureRandom;

public class EmailSender {

    private static String EMAIL = "email";
    private static String PWS = "password";

    public static void sendEmail (String subject, String body, String emailRecipient){
        //TODO, it doesn't work
        MultiPartEmail email = new MultiPartEmail();
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(EMAIL, PWS));
        email.setDebug(true);
        email.setHostName("smtp.gmail.com");
        try {
            //TODO better email-looking?
            email.setFrom(EMAIL);
            email.setSubject(subject);
            email.setMsg(body);
            email.addTo(emailRecipient);
            email.setSSLOnConnect(true);
            email.send();
            System.out.println("Mail sent!");
        } catch ( EmailException e ) {
            //TODO
            e.printStackTrace();
        }
    }
    public static void sendNewCredentials ( User user ){

        SecureRandom secureRandom = new SecureRandom();
        long longToken = Math.abs( secureRandom.nextLong() );
        String newPassword = Long.toString( longToken, 25);

        user.setPassword( newPassword );
        //TODO send the new password
    }
}
