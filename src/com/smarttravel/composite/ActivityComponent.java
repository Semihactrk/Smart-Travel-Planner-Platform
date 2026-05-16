package com.smarttravel.composite;

public interface ActivityComponent {
    void add(ActivityComponent component);

    void remove(ActivityComponent component);

    ActivityComponent getChild(int index);
    
    int getComponentIndex(ActivityComponent component);
    
    int getComponentCount();
    
    void moveComponentUp(ActivityComponent component);
    
    void moveComponentDown(ActivityComponent component);

    String getName();

    double getCost();

    double getRequiredTime();

    void print();
}
