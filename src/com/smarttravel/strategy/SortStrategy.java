package com.smarttravel.strategy;

import com.smarttravel.City;
import java.util.List;

public interface SortStrategy {
    void sort(List<City> cities);
}
