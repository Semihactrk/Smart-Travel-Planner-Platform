package com.smarttravel.decorator;

public class ShoppingMallVisit extends ActivityDecorator {
    public ShoppingMallVisit(CityActivity customActivity) {
        super(customActivity);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Visit Shopping Mall";
    }

    @Override
    public double getCost() {
        return super.getCost() + 40.0;
    }

    @Override
    public double getRequiredTime() {
        return super.getRequiredTime() + 3.0;
    }
}
