package com.smarttravel.decorator;

public class ParkVisit extends ActivityDecorator {
    public ParkVisit(CityActivity customActivity) {
        super(customActivity);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Walk in the Park";
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.0; // Parks are usually free
    }

    @Override
    public double getRequiredTime() {
        return super.getRequiredTime() + 1.5;
    }
}