package com.smarttravel.command;

import com.smarttravel.composite.ActivityComponent;
import com.smarttravel.composite.ActivityPlan;

public class RemoveActivityCommand implements Command {
    private ActivityPlan parentPlan;
    private ActivityComponent componentToRemove;

    public RemoveActivityCommand(ActivityPlan parentPlan, ActivityComponent componentToRemove) {
        this.parentPlan = parentPlan;
        this.componentToRemove = componentToRemove;
    }

    @Override
    public void execute() {
        parentPlan.remove(componentToRemove);
    }

    @Override
    public void undo() {
        parentPlan.add(componentToRemove);
    }
}