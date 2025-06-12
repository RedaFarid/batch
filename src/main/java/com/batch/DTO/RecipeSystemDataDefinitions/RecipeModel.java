package com.batch.DTO.RecipeSystemDataDefinitions;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement
public class RecipeModel {
    private List<ParallelStepsModel> parallelSteps = new LinkedList();

    public RecipeModel(ParallelStepsModel... firstParallelStepsModel) {
        Collections.addAll(this.parallelSteps, firstParallelStepsModel);
    }

    public RecipeModel(final List<ParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }

    public RecipeModel() {
    }

    public List<ParallelStepsModel> getParallelSteps() {
        return this.parallelSteps;
    }

    public void setParallelSteps(final List<ParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof RecipeModel other)) {
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
        return other instanceof RecipeModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $parallelSteps = this.getParallelSteps();
        result = result * 59 + ($parallelSteps == null ? 43 : $parallelSteps.hashCode());
        return result;
    }

    public String toString() {
        return "RecipeModel(parallelSteps=" + this.getParallelSteps() + ")";
    }
}
