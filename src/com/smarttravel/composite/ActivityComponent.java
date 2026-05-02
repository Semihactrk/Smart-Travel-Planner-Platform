package com.smarttravel.composite;

public interface ActivityComponent {
    void add(ActivityComponent component);

    void remove(ActivityComponent component);

    ActivityComponent getChild(int index);

    String getName();

    double getCost();

    double getRequiredTime();

    void print();
}
