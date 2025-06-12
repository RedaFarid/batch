
package com.batch.Database.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("BatchControllerData")
public class BatchControllerData {
    @Id
    private String unit;
    private long runningBatchID;
    private int CurrentParallelStepsNo;
    private boolean controlBit;
    private boolean lockGeneralControl;

    public String getUnit() {
        return this.unit;
    }

    public long getRunningBatchID() {
        return this.runningBatchID;
    }

    public int getCurrentParallelStepsNo() {
        return this.CurrentParallelStepsNo;
    }

    public boolean isControlBit() {
        return this.controlBit;
    }

    public boolean isLockGeneralControl() {
        return this.lockGeneralControl;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public void setRunningBatchID(final long runningBatchID) {
        this.runningBatchID = runningBatchID;
    }

    public void setCurrentParallelStepsNo(final int CurrentParallelStepsNo) {
        this.CurrentParallelStepsNo = CurrentParallelStepsNo;
    }

    public void setControlBit(final boolean controlBit) {
        this.controlBit = controlBit;
    }

    public void setLockGeneralControl(final boolean lockGeneralControl) {
        this.lockGeneralControl = lockGeneralControl;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BatchControllerData)) {
            return false;
        } else {
            BatchControllerData other = (BatchControllerData)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getRunningBatchID() != other.getRunningBatchID()) {
                return false;
            } else if (this.getCurrentParallelStepsNo() != other.getCurrentParallelStepsNo()) {
                return false;
            } else if (this.isControlBit() != other.isControlBit()) {
                return false;
            } else if (this.isLockGeneralControl() != other.isLockGeneralControl()) {
                return false;
            } else {
                Object this$unit = this.getUnit();
                Object other$unit = other.getUnit();
                if (this$unit == null) {
                    if (other$unit != null) {
                        return false;
                    }
                } else if (!this$unit.equals(other$unit)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BatchControllerData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $runningBatchID = this.getRunningBatchID();
        result = result * 59 + (int)($runningBatchID >>> 32 ^ $runningBatchID);
        result = result * 59 + this.getCurrentParallelStepsNo();
        result = result * 59 + (this.isControlBit() ? 79 : 97);
        result = result * 59 + (this.isLockGeneralControl() ? 79 : 97);
        Object $unit = this.getUnit();
        result = result * 59 + ($unit == null ? 43 : $unit.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getUnit();
        return "BatchControllerData(unit=" + var10000 + ", runningBatchID=" + this.getRunningBatchID() + ", CurrentParallelStepsNo=" + this.getCurrentParallelStepsNo() + ", controlBit=" + this.isControlBit() + ", lockGeneralControl=" + this.isLockGeneralControl() + ")";
    }

    public BatchControllerData(final String unit, final long runningBatchID, final int CurrentParallelStepsNo, final boolean controlBit, final boolean lockGeneralControl) {
        this.unit = unit;
        this.runningBatchID = runningBatchID;
        this.CurrentParallelStepsNo = CurrentParallelStepsNo;
        this.controlBit = controlBit;
        this.lockGeneralControl = lockGeneralControl;
    }

    public BatchControllerData() {
    }
}
