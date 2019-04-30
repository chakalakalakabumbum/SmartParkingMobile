package com.example.dominator.smartparkinginterface.Entities;

public class UserLogin {
    private Integer id;
    private String email;
    private String password;
    private int system;

    public UserLogin() {
    }

    public UserLogin(Integer id, String email, String password, int system) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.system = system;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSystem() {
        return system;
    }

    public void setSystem(int system) {
        this.system = system;
    }
}
