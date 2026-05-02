package com.smarttravel.observer;

import com.smarttravel.City;
import java.util.List;

public interface WeatherObserver {
    void update(List<City> cities);
}
