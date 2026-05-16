package com.smarttravel.command;

import com.smarttravel.composite.ActivityComponent;
import com.smarttravel.composite.ActivityPlan;

public class MoveCommand implements Command {
    private ActivityPlan plan;
    private ActivityComponent component;
    private int direction; // -1 Yukarı (Up), 1 Aşağı (Down)

    public MoveCommand(ActivityPlan plan, ActivityComponent component, int direction) {
        this.plan = plan;
        this.component = component;
        this.direction = direction;
    }

    @Override
    public void execute() {
        plan.moveComponent(component, direction);
    }

    @Override
    public void undo() {
        plan.moveComponent(component, -direction);
    }
}