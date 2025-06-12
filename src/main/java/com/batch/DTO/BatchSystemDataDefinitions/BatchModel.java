
package com.batch.DTO.BatchSystemDataDefinitions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BatchModel {
    private List<BatchParallelStepsModel> parallelSteps = new LinkedList();

    public BatchModel(BatchParallelStepsModel... firstParallelStepsModel) {
        this.parallelSteps.addAll(Arrays.asList(firstParallelStepsModel));
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
        } else if (!(o instanceof BatchModel)) {
            return false;
        } else {
            BatchModel other = (BatchModel)o;
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

    public BatchModel(final List<BatchParallelStepsModel> parallelSteps) {
        this.parallelSteps = parallelSteps;
    }

    public BatchModel() {
    }
}
