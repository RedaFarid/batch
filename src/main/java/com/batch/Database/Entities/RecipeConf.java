package com.batch.Database.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("RecipeConfiguration")
public class RecipeConf {
    @Id
    @Column("MaxNumberOfParallelSteps")
    private int maxParallelSteps;
    @Column("AcceptedError")
    private int acceptedErrorInDosePhases;
    @Column("MaxBatchSize")
    private double maxBatchSize;

    public RecipeConf(final int maxParallelSteps, final int acceptedErrorInDosePhases, final double maxBatchSize) {
        this.maxParallelSteps = maxParallelSteps;
        this.acceptedErrorInDosePhases = acceptedErrorInDosePhases;
        this.maxBatchSize = maxBatchSize;
    }

    public RecipeConf() {
    }

    public int getMaxParallelSteps() {
        return this.maxParallelSteps;
    }

    public void setMaxParallelSteps(final int maxParallelSteps) {
        this.maxParallelSteps = maxParallelSteps;
    }

    public int getAcceptedErrorInDosePhases() {
        return this.acceptedErrorInDosePhases;
    }

    public void setAcceptedErrorInDosePhases(final int acceptedErrorInDosePhases) {
        this.acceptedErrorInDosePhases = acceptedErrorInDosePhases;
    }

    public double getMaxBatchSize() {
        return this.maxBatchSize;
    }

    public void setMaxBatchSize(final double maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof RecipeConf other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getMaxParallelSteps() != other.getMaxParallelSteps()) {
                return false;
            } else if (this.getAcceptedErrorInDosePhases() != other.getAcceptedErrorInDosePhases()) {
                return false;
            } else {
                return Double.compare(this.getMaxBatchSize(), other.getMaxBatchSize()) == 0;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof RecipeConf;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getMaxParallelSteps();
        result = result * 59 + this.getAcceptedErrorInDosePhases();
        long $maxBatchSize = Double.doubleToLongBits(this.getMaxBatchSize());
        result = result * 59 + (int) ($maxBatchSize >>> 32 ^ $maxBatchSize);
        return result;
    }

    public String toString() {
        int var10000 = this.getMaxParallelSteps();
        return "RecipeConf(maxParallelSteps=" + var10000 + ", acceptedErrorInDosePhases=" + this.getAcceptedErrorInDosePhases() + ", maxBatchSize=" + this.getMaxBatchSize() + ")";
    }
}
