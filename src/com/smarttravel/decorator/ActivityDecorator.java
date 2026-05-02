package com.smarttravel.decorator;

public abstract class ActivityDecorator implements CityActivity {
    protected CityActivity customActivity;

    public ActivityDecorator(CityActivity customActivity) {
        this.customActivity = customActivity;
    }

    @Override
    public String getDescription() {
        return customActivity.getDescription();
    }

    @Override
    public double getCost() {
        return customActivity.getCost();
    }

    @Override
    public double getRequiredTime() {
        return customActivity.getRequiredTime();
    }
}
