package com.example.dominator.smartparkinginterface.Entities;


import java.io.Serializable;

public class InformationAccount implements Serializable {
    private Integer accountId;
    private String email;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private byte[] avatar;

    public InformationAccount() {
    }

    public InformationAccount(Integer accountId, String email, String password, String phoneNumber, String firstName, String lastName, byte[] avatar) {
        this.accountId = accountId;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
