package com.smarttravel.decorator;

public class CityCenterVisit extends ActivityDecorator {
    public CityCenterVisit(CityActivity customActivity) {
        super(customActivity);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Historic City Center Tour";
    }

    @Override
    public double getCost() {
        return super.getCost() + 15.0;
    }

    @Override
    public double getRequiredTime() {
        return super.getRequiredTime() + 2.5;
    }
}