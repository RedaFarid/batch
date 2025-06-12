
package com.batch.DTO.RecipeSystemDataDefinitions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecipeModel {
    private List<ParallelStepsModel> parallelSteps = new LinkedList();

    public RecipeModel(ParallelStepsModel... firstParallelStepsModel) {
        Collections.addAll(this.parallelSteps, firstParallelStepsModel);
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
        } else if (!(o instanceof RecipeModel)) {
            return false;
        } else {
            RecipeModel other = (RecipeModel)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$parallelSteps = this.getParallelSteps();
                Object other$parallelSteps = other.getParallelSteps();
                if (this$parallelSteps == null) {
                    if (other$parallelSteps != null) {
                        return false;
                    }
                } else if (!this$parallelSteps.equals(other$parallelSteps)) {
                    return false;
                }

                return true;
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

    public RecipeModel(final List<ParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }

    public RecipeModel() {
    }
}
