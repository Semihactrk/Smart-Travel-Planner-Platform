package com.smarttravel.command;

import com.smarttravel.City;
import java.util.List;

public class RemoveCityCommand implements Command {
    private List<City> tripCities;
    private City cityToRemove;
    private int previousIndex;

    public RemoveCityCommand(List<City> tripCities, City cityToRemove) {
        this.tripCities = tripCities;
        this.cityToRemove = cityToRemove;
        this.previousIndex = tripCities.indexOf(cityToRemove);
    }

    @Override
    public void execute() {
        tripCities.remove(cityToRemove);
    }

    @Override
    public void undo() {
        if (previousIndex >= 0 && previousIndex <= tripCities.size()) {
            tripCities.add(previousIndex, cityToRemove);
        } else {
            tripCities.add(cityToRemove);
        }
    }
}
