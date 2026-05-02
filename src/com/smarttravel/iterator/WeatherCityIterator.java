package com.smarttravel.iterator;

import com.smarttravel.City;
import com.smarttravel.WeatherState;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class WeatherCityIterator implements Iterator<City> {
    private List<City> cities;
    private WeatherState targetState;
    private int currentIndex = 0;

    public WeatherCityIterator(List<City> cities, WeatherState targetState) {
        this.cities = cities;
        this.targetState = targetState;
    }

    @Override
    public boolean hasNext() {
        while (currentIndex < cities.size()) {
            if (cities.get(currentIndex).getCurrentWeatherState() == targetState) {
                return true;
            }
            currentIndex++;
        }
        return false;
    }

    @Override
    public City next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return cities.get(currentIndex++);
    }
}
