package com.batch.DTO.BatchSystemDataDefinitions;

import java.util.LinkedList;
import java.util.List;

public class BatchParallelStepsModel {
    private List<BatchStepModel> steps = new LinkedList();

    public BatchParallelStepsModel() {
    }

    public BatchParallelStepsModel(final List<BatchStepModel> steps) {
        this.steps = steps;
    }

    public void addStep(BatchStepModel step) {
        this.steps.add(step);
    }

    public List<BatchStepModel> getSteps() {
        return this.steps;
    }

    public void setSteps(final List<BatchStepModel> steps) {
        this.steps = steps;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BatchParallelStepsModel other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$steps = this.getSteps();
                Object other$steps = other.getSteps();
                if (this$steps == null) {
                    return other$steps == null;
                } else return this$steps.equals(other$steps);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BatchParallelStepsModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $steps = this.getSteps();
        result = result * 59 + ($steps == null ? 43 : $steps.hashCode());
        return result;
    }

    public String toString() {
        return "BatchParallelStepsModel(steps=" + this.getSteps() + ")";
    }
}
