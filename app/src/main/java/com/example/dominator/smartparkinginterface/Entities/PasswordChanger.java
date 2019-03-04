package com.example.dominator.smartparkinginterface.Entities;

/**
 * Change Password DTO
 *
 * Author: DangNHH - 17/02/2019
 */
public class PasswordChanger {
    private Integer accountId;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public PasswordChanger() {
    }

    public PasswordChanger(Integer accountId, String oldPassword, String newPassword, String confirmPassword) {
        this.accountId = accountId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
