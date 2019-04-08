package com.example.dominator.smartparkinginterface.Entities;

/**
 * Parking Slot Status Entity
 *
 * Author: DangNHH - 20/02/2019
 */
public class ParkingSlotStatus {
    private Integer statusId;
    private String statusName;

    public ParkingSlotStatus() {
    }

    public ParkingSlotStatus(String statusName) {
        this.statusName = statusName;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }


    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
