package com.smarttravel.command;

import com.smarttravel.composite.ActivityComponent;
import com.smarttravel.composite.ActivityPlan;

public class AddActivityCommand implements Command {
    private ActivityPlan parentPlan;
    private ActivityComponent componentToAdd;

    public AddActivityCommand(ActivityPlan parentPlan, ActivityComponent componentToAdd) {
        this.parentPlan = parentPlan;
        this.componentToAdd = componentToAdd;
    }

    @Override
    public void execute() {
        parentPlan.add(componentToAdd);
    }

    @Override
    public void undo() {
        parentPlan.remove(componentToAdd);
    }
}
