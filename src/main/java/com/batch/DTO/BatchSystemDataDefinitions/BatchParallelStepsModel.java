package com.batch.DTO.BatchSystemDataDefinitions;

import java.util.LinkedList;
import java.util.List;

public class BatchParallelStepsModel {

    private List<BatchStepModel> steps= new LinkedList() ;

    public BatchParallelStepsModel(List<BatchStepModel> steps) {
        this.steps = steps;
    }

    public BatchParallelStepsModel() {
    }

    public List<BatchStepModel> getSteps() {
        return steps;
    }

    public void setSteps(List<BatchStepModel> steps) {
        this.steps = steps;
    }

    public void addStep(BatchStepModel step) {
        steps.add(step);
    }

}
