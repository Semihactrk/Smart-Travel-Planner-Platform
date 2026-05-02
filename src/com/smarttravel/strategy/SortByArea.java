package com.smarttravel.strategy;

import com.smarttravel.City;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortByArea implements SortStrategy {
    @Override
    public void sort(List<City> cities) {
        Collections.sort(cities, Comparator.comparingDouble(City::getArea).reversed()); // Descending
    }
}
