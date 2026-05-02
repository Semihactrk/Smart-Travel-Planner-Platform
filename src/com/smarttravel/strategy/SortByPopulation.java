package com.smarttravel.strategy;

import com.smarttravel.City;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortByPopulation implements SortStrategy {
    @Override
    public void sort(List<City> cities) {
        Collections.sort(cities, Comparator.comparingInt(City::getPopulation).reversed()); // Descending
    }
}
