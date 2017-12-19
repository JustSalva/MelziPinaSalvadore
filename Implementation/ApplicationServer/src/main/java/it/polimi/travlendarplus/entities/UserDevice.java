package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.authenticationExceptions.InvalidTokenException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.security.SecureRandom;

/**
 * This JPA class represent a user device
 */
@Entity( name = "USER_DEVICES" )
public class UserDevice extends GenericEntity {

    private static final long serialVersionUID = -3483428076400625921L;

    /**
     * Identifier of the device
     */
    @Id
    private String idDevice;

    /**
     * User which owns the device
     */
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "USER_MAIL", nullable = false )
    private User user;

    /**
     * Token associated to this device and used by such device to identify
     * himself when it is performing requests
     */
    @Column( name = "UNIVOCAL_CODE", nullable = false )
    private String univocalCode;

    public UserDevice () {
    }

    public UserDevice ( String idDevice, User user ) {
        this.idDevice = idDevice;
        this.user = user;
        this.univocalCode = user.getEmail().concat( "@" + generateToken() );
    }

    /**
     * Allows to load a UserDevice class from the database
     *
     * @param idDevice primary key of the userDevice tuple
     * @return the requested tuple as a UserDevice class instance
     * @throws EntityNotFoundException if the requested tuple does not exist
     */
    public static UserDevice load ( String idDevice ) throws EntityNotFoundException {
        return GenericEntity.load( UserDevice.class, idDevice );
    }

    /**
     * Loads from the database the user associated with a specific token
     *
     * @param token token whose user id to be retrieved
     * @return the requested user
     * @throws InvalidTokenException if the token does not exist
     */
    public static User findUserRelativeToToken ( String token ) throws InvalidTokenException {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory( "TravlendarDB" );
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        UserDevice userDevice;
        TypedQuery < UserDevice > query = entityManager.createQuery( "" +
                        "SELECT userDevice " +
                        "FROM USER_DEVICES userDevice " +
                        "WHERE userDevice.univocalCode = :token",
                UserDevice.class );
        query.setParameter( "token", token );
        try {
            userDevice = query.getSingleResult();
        } catch ( NoResultException e ) {
            throw new InvalidTokenException();
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
        return userDevice.getUser();
    }

    public User getUser () {
        return user;
    }

    public void setUser ( User user ) {
        this.user = user;
    }

    public String getIdDevice () {
        return idDevice;
    }

    public void setIdDevice ( String idDevice ) {
        this.idDevice = idDevice;
    }

    public String getUnivocalCode () {
        return univocalCode;
    }

    public void setUnivocalCode ( String univocalCode ) {
        this.univocalCode = univocalCode;
    }

    /**
     * Generates a random token, concatenating 3 random Strings
     *
     * @return the requested token
     */
    private String generateToken () {
        SecureRandom secureRandom = new SecureRandom();
        long longToken;
        String token = "";
        for ( int i = 0; i < 3; i++ ) {
            longToken = Math.abs( secureRandom.nextLong() );
            token = token.concat( Long.toString( longToken, 34 + i ) );//max value of radix is 36
        }
        return token;
    }

    /**
     * Checks if an userDevice is already present in the database
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
