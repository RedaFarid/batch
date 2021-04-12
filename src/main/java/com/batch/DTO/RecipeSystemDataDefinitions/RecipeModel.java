package com.batch.DTO.RecipeSystemDataDefinitions;

import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@ToString
@XmlRootElement
public class RecipeModel {

    private List<ParallelStepsModel> parallelSteps = new LinkedList();

    public RecipeModel(List<ParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }

    public RecipeModel(ParallelStepsModel ... firstParallelStepsModel ) {
        
        for (ParallelStepsModel step : firstParallelStepsModel) {
            parallelSteps.add(step);
        }
        
    }

    public RecipeModel() {
    }

    public List<ParallelStepsModel> getParallelSteps() {
        return parallelSteps;
    }

    public void setParallelSteps(List<ParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }


    
}
