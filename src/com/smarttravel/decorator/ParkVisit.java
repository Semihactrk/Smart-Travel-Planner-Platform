package com.smarttravel.decorator;

public class ParkVisit extends ActivityDecorator {
    public ParkVisit(CityActivity customActivity) {
        super(customActivity);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Park Visit";
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.0; // Free
    }

    @Override
    public double getRequiredTime() {
        return super.getRequiredTime() + 1.5; // 1.5 hours
    }
}
