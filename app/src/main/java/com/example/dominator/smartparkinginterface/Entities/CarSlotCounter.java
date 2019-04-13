package com.example.dominator.smartparkinginterface.Entities;

import java.util.List;

public class CarSlotCounter {
    private String laneCharacter;
    private List<ParkingSlot> slotInLane;

    public CarSlotCounter(String laneCharacter, List<ParkingSlot> slotInLane) {
        this.laneCharacter = laneCharacter;
        this.slotInLane = slotInLane;
    }

    public CarSlotCounter() {
    }

    public String getLaneCharacter() {
        return laneCharacter;
    }

    public void setLaneCharacter(String laneCharacter) {
        this.laneCharacter = laneCharacter;
    }

    public List<ParkingSlot> getSlotInLane() {
        return slotInLane;
    }

    public void setSlotInLane(List<ParkingSlot> slotInLane) {
        this.slotInLane = slotInLane;
    }
}
