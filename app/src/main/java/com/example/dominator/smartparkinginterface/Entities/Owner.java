package com.example.dominator.smartparkinginterface.Entities;

import java.util.Date;

public class Owner {
    private Integer ownerId;
    private String firstName;
    private String lastName;
    private String sex;
    private String address;
    private String phoneNumber;
    private Date yearOfBirth;

    public Owner() {
    }

    public Owner(Integer ownerId, String firstName, String lastName, String sex, String address, String phoneNumber, Date yearOfBirth) {
        this.ownerId = ownerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.yearOfBirth = yearOfBirth;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Date yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
