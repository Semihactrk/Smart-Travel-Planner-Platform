package com.smarttravel.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityPlan implements ActivityComponent {
    private List<ActivityComponent> components = new ArrayList<>();
    private String planName;

    public ActivityPlan(String planName) {
        this.planName = planName;
    }

    @Override
    public void add(ActivityComponent component) {
        components.add(component);
    }

    @Override
    public void remove(ActivityComponent component) {
        components.remove(component);
    }

    @Override
    public ActivityComponent getChild(int index) {
        return components.get(index);
    }
    
    @Override
    public int getComponentIndex(ActivityComponent component) {
        return components.indexOf(component);
    }
    
    @Override
    public int getComponentCount() {
        return components.size();
    }
    
    @Override
    public void moveComponentUp(ActivityComponent component) {
        int index = components.indexOf(component);
        if (index > 0) {
            Collections.swap(components, index, index - 1);
        }
    }
    
    @Override
    public void moveComponentDown(ActivityComponent component) {
        int index = components.indexOf(component);
        if (index >= 0 && index < components.size() - 1) {
            Collections.swap(components, index, index + 1);
        }
    }

    @Override
    public String getName() {
        return planName;
    }

    @Override
    public double getCost() {
        return components.stream().mapToDouble(ActivityComponent::getCost).sum();
    }

    @Override
    public double getRequiredTime() {
        return components.stream().mapToDouble(ActivityComponent::getRequiredTime).sum();
    }

    @Override
    public void print() {
        System.out.println(
                "Plan: " + planName + " [Total Cost: $" + getCost() + ", Total Time: " + getRequiredTime() + "h]");
        for (ActivityComponent component : components) {
            component.print();
        }
    }
}
