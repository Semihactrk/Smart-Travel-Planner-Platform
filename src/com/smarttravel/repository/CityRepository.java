package com.smarttravel.repository;

import com.smarttravel.City;
import com.smarttravel.WeatherState;
import java.util.ArrayList;
import java.util.List;

public class CityRepository {
    private static CityRepository instance;
    private List<City> cities;

    private CityRepository() {
        cities = new ArrayList<>();
        loadCitiesFromJson("cities.json");
    }

    public static synchronized CityRepository getInstance() {
        if (instance == null) {
            instance = new CityRepository();
        }
        return instance;
    }

    public List<City> getCities() {
        return cities;
    }

    private void loadCitiesFromJson(String filename) {
        // Mocking the JSON reading as requested
        cities.add(new City("Ankara", 5317215, 25615.0, 15.0, WeatherState.SUNNY));
        cities.add(new City("Buhara", 280000, 143.1, 22.2, WeatherState.CLOUDY));
        cities.add(new City("Gaziantep", 1835508, 6778.9, -4.7, WeatherState.SUNNY));
        cities.add(new City("Gazze", 2180400, 70.9, 40.0, WeatherState.SNOWY));
        cities.add(new City("Istanbul", 15719600, 5343.0, 34.3, WeatherState.SNOWY));
        cities.add(new City("Kudus", 981711, 126.0, 26.2, WeatherState.CLOUDY));
        cities.add(new City("Saraybosna", 347000, 141.6, 36.8, WeatherState.RAINY));
    }
}
