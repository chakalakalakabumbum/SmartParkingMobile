package com.example.dominator.smartparkinginterface.Entities;

/**
 * Role Entity
 *
 * Author: DangNHH - 20/02/2019
 */
public class Role {
    private Integer roleId;
    private String roleName;

    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }


}
