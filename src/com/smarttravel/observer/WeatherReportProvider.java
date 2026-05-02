package com.smarttravel.observer;

import com.smarttravel.City;
import com.smarttravel.WeatherState;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeatherReportProvider implements Runnable {
    private List<WeatherObserver> observers = new ArrayList<>();
    private List<City> cities;
    private boolean running = true;
    private Random random = new Random();

    public WeatherReportProvider(List<City> cities) {
        this.cities = cities;
    }

    public void addObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WeatherObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (WeatherObserver observer : observers) {
            observer.update(cities);
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(3000);
                for (City city : cities) {
                    // Update weather randomly
                    int weatherIndex = random.nextInt(WeatherState.values().length);
                    city.setCurrentWeatherState(WeatherState.values()[weatherIndex]);

                    // Update temp randomly
                    double tempChange = (random.nextDouble() * 10) - 5; // -5 to +5
                    city.setCurrentTemperature(city.getCurrentTemperature() + tempChange);
                }
                notifyObservers();
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }
}
