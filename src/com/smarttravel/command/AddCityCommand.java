package com.smarttravel.command;

import com.smarttravel.City;
import java.util.List;

public class AddCityCommand implements Command {
    private List<City> tripCities;
    private City cityToAdd;

    public AddCityCommand(List<City> tripCities, City cityToAdd) {
        this.tripCities = tripCities;
        this.cityToAdd = cityToAdd;
    }

    @Override
    public void execute() {
        if (!tripCities.contains(cityToAdd)) {
            tripCities.add(cityToAdd);
        }
    }

    @Override
    public void undo() {
        tripCities.remove(cityToAdd);
    }
}
