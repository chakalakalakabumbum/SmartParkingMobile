package com.example.dominator.smartparkinginterface.Entities;

/**
 * Parking sLot DTO
 *
 * Author: DangNHH - 17/02/2019
 */
public class ParkingSlot {
    private Integer slotId;
    private String lane;
    private String row;
    private String status;
    private Integer parkingLotId;

    public ParkingSlot() {
    }

    /**
     * Constructor full arguments
     * @param slotId
     * @param lane
     * @param row
     * @param status
     * @param parkingLotId
     */
    public ParkingSlot(Integer slotId, String lane, String row, String status, Integer parkingLotId) {
        this.slotId = slotId;
        this.lane = lane;
        this.row = row;
        this.status = status;
        this.parkingLotId = parkingLotId;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(Integer parkingLotId) {
        this.parkingLotId = parkingLotId;
    }
}
