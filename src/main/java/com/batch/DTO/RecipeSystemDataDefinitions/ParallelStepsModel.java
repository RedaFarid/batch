package com.batch.DTO.RecipeSystemDataDefinitions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParallelStepsModel {
    private List<StepModel> steps= new LinkedList<>() ;
    public void addStep(StepModel step) {
        steps.add(step);
    }
}
