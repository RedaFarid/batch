

package com.batch.DTO.RecipeSystemDataDefinitions;

import java.util.LinkedList;
import java.util.List;

public class ParallelStepsModel {
    private List<StepModel> steps = new LinkedList();

    public void addStep(StepModel step) {
        this.steps.add(step);
    }

    public List<StepModel> getSteps() {
        return this.steps;
    }

    public void setSteps(final List<StepModel> steps) {
        this.steps = steps;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ParallelStepsModel)) {
            return false;
        } else {
            ParallelStepsModel other = (ParallelStepsModel)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$steps = this.getSteps();
                Object other$steps = other.getSteps();
                if (this$steps == null) {
                    if (other$steps != null) {
                        return false;
                    }
                } else if (!this$steps.equals(other$steps)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ParallelStepsModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $steps = this.getSteps();
        result = result * 59 + ($steps == null ? 43 : $steps.hashCode());
        return result;
    }

    public String toString() {
        return "ParallelStepsModel(steps=" + this.getSteps() + ")";
    }

    public ParallelStepsModel() {
    }

    public ParallelStepsModel(final List<StepModel> steps) {
        this.steps = steps;
    }
}
