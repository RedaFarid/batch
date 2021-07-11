package com.batch.DTO.BatchSystemDataDefinitions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BatchModel {

    private List<BatchParallelStepsModel> parallelSteps = new LinkedList<>();

    public BatchModel(List<BatchParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }

    public BatchModel(BatchParallelStepsModel ... firstParallelStepsModel ) {
        
        parallelSteps.addAll(Arrays.asList(firstParallelStepsModel));
        
    }

    public BatchModel() {
    }

    public List<BatchParallelStepsModel> getParallelSteps() {
        return parallelSteps;
    }

    public void setParallelSteps(List<BatchParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }
    
}
