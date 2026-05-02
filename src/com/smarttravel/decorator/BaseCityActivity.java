package com.smarttravel.decorator;

import com.smarttravel.City;

public class BaseCityActivity implements CityActivity {
    private City city;

    public BaseCityActivity(City city) {
        this.city = city;
    }

    @Override
    public String getDescription() {
        return "Visit " + city.getName();
    }

    @Override
    public double getCost() {
        return 0.0;
    }

    @Override
    public double getRequiredTime() {
        return 0.0;
    }
}
