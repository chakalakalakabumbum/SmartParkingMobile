package com.example.dominator.smartparkinginterface.Entities;

import java.util.Date;

/**
 * Account DTO
 *
 * Author: DangNHH - 16/02/2019
 */
public class Account {
    private Integer accountId;
    private String email;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private Date createdDate;
    private Integer roleId;
    private boolean active;
    private String avatar;

    public Account() {
    }

    /**
     * Constructor full arguments
     * @param accountId
     * @param email
     * @param password
     * @param phoneNumber
     * @param firstName
     * @param lastName
     * @param createdDate
     * @param roleId
     * @param isActive
     */
    public Account(Integer accountId, String email, String password, String phoneNumber, String firstName,
                   String lastName, Date createdDate, Integer roleId, boolean isActive, String avatar) {
        this.accountId = accountId;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdDate = createdDate;
        this.roleId = roleId;
        this.active = active;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        active = active;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
