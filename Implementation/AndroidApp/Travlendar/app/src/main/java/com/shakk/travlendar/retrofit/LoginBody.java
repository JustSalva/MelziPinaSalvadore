package com.shakk.travlendar.retrofit;


public class LoginBody {

    private String email;
    private String password;
    private String idDevice;

    public LoginBody(String email, String password, String idDevice) {
        this.email = email;
        this.password = password;
        this.idDevice = idDevice;
    }
}
