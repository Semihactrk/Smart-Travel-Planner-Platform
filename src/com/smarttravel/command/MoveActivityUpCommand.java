package com.smarttravel.command;

import com.smarttravel.composite.ActivityComponent;
import com.smarttravel.composite.ActivityPlan;

public class MoveActivityUpCommand implements Command {
    private ActivityPlan parentPlan;
    private ActivityComponent component;
    private int currentIndex;

    public MoveActivityUpCommand(ActivityPlan parentPlan, ActivityComponent component) {
        this.parentPlan = parentPlan;
        this.component = component;
    }

    @Override
    public void execute() {
        this.currentIndex = parentPlan.getComponentIndex(component);
        if (currentIndex > 0) {
            parentPlan.moveComponentUp(component);
        }
    }

    @Override
    public void undo() {
        if (currentIndex >= 0 && currentIndex < parentPlan.getComponentCount()) {
            parentPlan.moveComponentDown(component);
        }
    }
}
