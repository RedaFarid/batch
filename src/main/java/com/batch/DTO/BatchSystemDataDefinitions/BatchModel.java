package com.batch.DTO.BatchSystemDataDefinitions;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement
public class BatchModel {
    private List<BatchParallelStepsModel> parallelSteps = new LinkedList();

    public BatchModel(BatchParallelStepsModel... firstParallelStepsModel) {
        this.parallelSteps.addAll(Arrays.asList(firstParallelStepsModel));
    }

    public BatchModel(final List<BatchParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }

    public BatchModel() {
    }

    public List<BatchParallelStepsModel> getParallelSteps() {
        return this.parallelSteps;
    }

    public void setParallelSteps(final List<BatchParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BatchModel other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$parallelSteps = this.getParallelSteps();
                Object other$parallelSteps = other.getParallelSteps();
                if (this$parallelSteps == null) {
                    return other$parallelSteps == null;
                } else return this$parallelSteps.equals(other$parallelSteps);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BatchModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $parallelSteps = this.getParallelSteps();
        result = result * 59 + ($parallelSteps == null ? 43 : $parallelSteps.hashCode());
        return result;
    }

    public String toString() {
        return "BatchModel(parallelSteps=" + this.getParallelSteps() + ")";
    }
}
