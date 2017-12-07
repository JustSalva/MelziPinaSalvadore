package it.polimi.travlendarplus.entities;

import it.polimi.travlendarplus.exceptions.authenticationExceptions.InvalidTokenException;
import it.polimi.travlendarplus.exceptions.persistenceExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.security.SecureRandom;
import java.util.Random;

@Entity(name = "USER_DEVICES")
public class UserDevice extends GenericEntity {

    private static final long serialVersionUID = -3483428076400625921L;

    @Id
    private String idDevice;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(name = "USER", nullable = false)
    private User user;

    @Column(name = "UNIVOCAL_CODE", nullable = false)
    private String univocalCode;

    public UserDevice() {
    }

    public UserDevice(String idDevice, User user) {
        this.idDevice = idDevice;
        this.user = user;
        this.univocalCode = user.getEmail().concat("@"+generateToken());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }

    public String getUnivocalCode() {
        return univocalCode;
    }

    public void setUnivocalCode(String univocalCode) {
        this.univocalCode = univocalCode;
    }

    private String generateToken(){
        SecureRandom secureRandom = new SecureRandom();
        long longToken;
        String token = "";
        for(int i=0; i<3; i++){
            longToken = Math.abs( secureRandom.nextLong() );
            token = token.concat(Long.toString( longToken, 34+i));//max value of radix is 36
        }
        return token;
    }

    public static UserDevice load(String idDevice) throws EntityNotFoundException {
        return GenericEntity.load( UserDevice.class, idDevice );
    }

    @Override
    public boolean isAlreadyInDb() {
        try {
            load(idDevice);
        } catch ( EntityNotFoundException e ) {
            return false;
        }
        return true;
    }


    public static User findUserRelativeToToken ( String token ) throws InvalidTokenException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("TravlendarDB");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        UserDevice userDevice;
        TypedQuery<UserDevice> query = entityManager.createQuery( "" +
                "SELECT userDevice " +
                "FROM USER_DEVICES userDevice " +
                "WHERE userDevice.univocalCode = :token",
                UserDevice.class);
        query.setParameter( "token", token );
        try{
            userDevice = query.getSingleResult();
        }catch ( NoResultException e ){
            throw new InvalidTokenException();
        }finally {
            entityManager.close();
            entityManagerFactory.close();
        }
        return userDevice.getUser();
    }
}
