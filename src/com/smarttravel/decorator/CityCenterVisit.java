package com.smarttravel.decorator;

public class CityCenterVisit extends ActivityDecorator {
    public CityCenterVisit(CityActivity customActivity) {
        super(customActivity);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Historic City Center Visit";
    }

    @Override
    public double getCost() {
        return super.getCost() + 12.0; // Guided tour cost
    }

    @Override
    public double getRequiredTime() {
        return super.getRequiredTime() + 2.5; // 2.5 hours
    }
}
