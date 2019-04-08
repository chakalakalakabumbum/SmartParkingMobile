package com.example.dominator.smartparkinginterface.Entities;

/**
 * Supervision Entity
 *
 * Author: DangNHH - 19/02/2019
 */
public class Supervision {
    private Integer supervisionId;
    private ParkingLot parkingLot;
    private Account supervisor;

    public Supervision() {
    }

    /**
     * Constructor full arguments
     */
    public Supervision(ParkingLot parkingLot, Account supervisor) {
        this.parkingLot = parkingLot;
        this.supervisor = supervisor;
    }

    public Integer getSupervisionId() {
        return supervisionId;
    }

    public void setSupervisionId(Integer supervisionId) {
        this.supervisionId = supervisionId;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public Account getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Account supervisor) {
        this.supervisor = supervisor;
    }
}
