package com.example.dominator.smartparkinginterface.Entities;

import java.util.Comparator;
import java.util.List;

public class CarSlotCounter implements Comparator<CarSlotCounter> {
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

    @Override
    public int compare(CarSlotCounter o1, CarSlotCounter o2) {
        return o1.getLaneCharacter().compareTo(o2.getLaneCharacter());
    }
}
