package com.smarttravel;

public class City {
    private String name;
    private int population;
    private double area;
    private double currentTemperature;
    private WeatherState currentWeatherState;

    public City(String name, int population, double area, double currentTemperature, WeatherState currentWeatherState) {
        this.name = name;
        this.population = population;
        this.area = area;
        this.currentTemperature = currentTemperature;
        this.currentWeatherState = currentWeatherState;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public double getArea() {
        return area;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public WeatherState getCurrentWeatherState() {
        return currentWeatherState;
    }

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public void setCurrentWeatherState(WeatherState currentWeatherState) {
        this.currentWeatherState = currentWeatherState;
    }

    @Override
    public String toString() {
        // Sayıları (örn. sıcaklığı) .1f ile sadece 1 ondalık basamak gösterecek şekilde kısalttık
        return String.format("%s (Pop: %d, Area: %.1f km², Temp: %.1f°C, %s)", 
                name, population, area, currentTemperature, currentWeatherState);
    }
}
