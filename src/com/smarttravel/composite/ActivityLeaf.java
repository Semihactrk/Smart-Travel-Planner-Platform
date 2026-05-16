package com.smarttravel.composite;

import com.smarttravel.decorator.CityActivity;

public class ActivityLeaf implements ActivityComponent {
    private String name;
    private double cost;
    private double requiredTime;

    // Could also take a CityActivity decorator
    public ActivityLeaf(String name, double cost, double requiredTime) {
        this.name = name;
        this.cost = cost;
        this.requiredTime = requiredTime;
    }

    public ActivityLeaf(CityActivity cityActivity) {
        this.name = cityActivity.getDescription();
        this.cost = cityActivity.getCost();
        this.requiredTime = cityActivity.getRequiredTime();
    }

    @Override
    public void add(ActivityComponent component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(ActivityComponent component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActivityComponent getChild(int index) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getComponentIndex(ActivityComponent component) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getComponentCount() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void moveComponentUp(ActivityComponent component) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void moveComponentDown(ActivityComponent component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public double getRequiredTime() {
        return requiredTime;
    }

    @Override
    public void print() {
        System.out.println(" - " + name + " [Cost: $" + cost + ", Time: " + requiredTime + "h]");
    }
}
