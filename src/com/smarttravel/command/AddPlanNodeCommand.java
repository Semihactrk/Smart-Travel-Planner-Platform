package com.smarttravel.command;

import com.smarttravel.composite.ActivityPlan;

public class AddPlanNodeCommand implements Command {
    private ActivityPlan parentPlan;
    private ActivityPlan newPlanNode;

    public AddPlanNodeCommand(ActivityPlan parentPlan, ActivityPlan newPlanNode) {
        this.parentPlan = parentPlan;
        this.newPlanNode = newPlanNode;
    }

    @Override
    public void execute() {
        parentPlan.add(newPlanNode);
    }

    @Override
    public void undo() {
        parentPlan.remove(newPlanNode);
    }
}
