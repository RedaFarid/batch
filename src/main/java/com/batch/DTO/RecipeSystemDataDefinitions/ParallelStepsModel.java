package com.batch.DTO.RecipeSystemDataDefinitions;

import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@ToString
public class ParallelStepsModel {

    private List<StepModel> steps= new LinkedList() ;

    public ParallelStepsModel(List<StepModel> steps) {
        this.steps = steps;
    }

    public ParallelStepsModel() {
    }

    public List<StepModel> getSteps() {
        return steps;
    }

    public void setSteps(List<StepModel> steps) {
        this.steps = steps;
    }

    public void addStep(StepModel step) {
        steps.add(step);
    }

}
