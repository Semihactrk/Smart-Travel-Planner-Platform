package com.smarttravel.command;

import com.smarttravel.composite.ActivityComponent;
import com.smarttravel.composite.ActivityPlan;

import java.util.ArrayList;
import java.util.List;

public class ClearPlanCommand implements Command {
    private ActivityPlan plan;
    private List<ActivityComponent> previousComponents;

    public ClearPlanCommand(ActivityPlan plan) {
        this.plan = plan;
    }

    @Override
    public void execute() {
        previousComponents = new ArrayList<>();
        // Assuming we add a getChildren() to ActivityPlan to save state,
        // but for now let's just clear it. We need a way to restore.
        // We need to implement getChildren in ActivityPlan or something similar.
        for (int i = 0;; i++) {
            try {
                previousComponents.add(plan.getChild(i));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        for (ActivityComponent component : previousComponents) {
            plan.remove(component);
        }
    }

    @Override
    public void undo() {
        for (ActivityComponent component : previousComponents) {
            plan.add(component);
        }
    }
}
