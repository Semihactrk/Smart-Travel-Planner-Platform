package com.smarttravel.decorator;

public class MuseumVisit extends ActivityDecorator {
    public MuseumVisit(CityActivity customActivity) {
        super(customActivity);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Visit Museum";
    }

    @Override
    public double getCost() {
        return super.getCost() + 18.0;
    }

    @Override
    public double getRequiredTime() {
        return super.getRequiredTime() + 2.0;
    }
}
