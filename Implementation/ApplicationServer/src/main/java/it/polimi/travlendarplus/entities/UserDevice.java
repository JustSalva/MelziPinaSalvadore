package it.polimi.travlendarplus.entities;

import javax.persistence.*;
import java.security.SecureRandom;
import java.util.Random;

@Entity(name = "USER_DEVICES")
public class UserDevice extends GenericEntity {

    @Id
    private String idDevice;

    @ManyToOne
    @JoinColumn(name = "USER", nullable = false)
    private User user;

    @Column(name = "UNIVOCAL_CODE", nullable = false)
    private String univocalCode;

    public UserDevice() {
    }

    public UserDevice(String idDevice, User user) {
        this.idDevice = idDevice;
        this.user = user;
        this.univocalCode = user.getEmail().concat(":"+generateToken());
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

    public static UserDevice load(String idDevice){
        return GenericEntity.load( UserDevice.class, idDevice );
    }

    @Override
    public boolean isAlreadyInDb() {
        return load(idDevice) != null;
    }
}
