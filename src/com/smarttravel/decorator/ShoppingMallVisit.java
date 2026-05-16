package com.smarttravel.decorator;

public class ShoppingMallVisit extends ActivityDecorator {
    public ShoppingMallVisit(CityActivity customActivity) {
        super(customActivity);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Shopping Mall Visit";
    }

    @Override
    public double getCost() {
        return super.getCost() + 25.0; // Average shopping spend
    }

    @Override
    public double getRequiredTime() {
        return super.getRequiredTime() + 3.0; // 3 hours
    }
}
