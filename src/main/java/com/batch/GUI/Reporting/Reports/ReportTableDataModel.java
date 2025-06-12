

package com.batch.GUI.Reporting.Reports;

public class ReportTableDataModel {
    private int number;
    private String materialName;
    private double required;
    private double loaded;
    private double error;
    private double requiredPercent;
    private double actualPercent;

    public int getNumber() {
        return this.number;
    }

    public String getMaterialName() {
        return this.materialName;
    }

    public double getRequired() {
        return this.required;
    }

    public double getLoaded() {
        return this.loaded;
    }

    public double getError() {
        return this.error;
    }

    public double getRequiredPercent() {
        return this.requiredPercent;
    }

    public double getActualPercent() {
        return this.actualPercent;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public void setMaterialName(final String materialName) {
        this.materialName = materialName;
    }

    public void setRequired(final double required) {
        this.required = required;
    }

    public void setLoaded(final double loaded) {
        this.loaded = loaded;
    }

    public void setError(final double error) {
        this.error = error;
    }

    public void setRequiredPercent(final double requiredPercent) {
        this.requiredPercent = requiredPercent;
    }

    public void setActualPercent(final double actualPercent) {
        this.actualPercent = actualPercent;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ReportTableDataModel)) {
            return false;
        } else {
            ReportTableDataModel other = (ReportTableDataModel)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getNumber() != other.getNumber()) {
                return false;
            } else if (Double.compare(this.getRequired(), other.getRequired()) != 0) {
                return false;
            } else if (Double.compare(this.getLoaded(), other.getLoaded()) != 0) {
                return false;
            } else if (Double.compare(this.getError(), other.getError()) != 0) {
                return false;
            } else if (Double.compare(this.getRequiredPercent(), other.getRequiredPercent()) != 0) {
                return false;
            } else if (Double.compare(this.getActualPercent(), other.getActualPercent()) != 0) {
                return false;
            } else {
                Object this$materialName = this.getMaterialName();
                Object other$materialName = other.getMaterialName();
                if (this$materialName == null) {
                    if (other$materialName != null) {
                        return false;
                    }
                } else if (!this$materialName.equals(other$materialName)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ReportTableDataModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getNumber();
        long $required = Double.doubleToLongBits(this.getRequired());
        result = result * 59 + (int)($required >>> 32 ^ $required);
        long $loaded = Double.doubleToLongBits(this.getLoaded());
        result = result * 59 + (int)($loaded >>> 32 ^ $loaded);
        long $error = Double.doubleToLongBits(this.getError());
        result = result * 59 + (int)($error >>> 32 ^ $error);
        long $requiredPercent = Double.doubleToLongBits(this.getRequiredPercent());
        result = result * 59 + (int)($requiredPercent >>> 32 ^ $requiredPercent);
        long $actualPercent = Double.doubleToLongBits(this.getActualPercent());
        result = result * 59 + (int)($actualPercent >>> 32 ^ $actualPercent);
        Object $materialName = this.getMaterialName();
        result = result * 59 + ($materialName == null ? 43 : $materialName.hashCode());
        return result;
    }

    public String toString() {
        int var10000 = this.getNumber();
        return "ReportTableDataModel(number=" + var10000 + ", materialName=" + this.getMaterialName() + ", required=" + this.getRequired() + ", loaded=" + this.getLoaded() + ", error=" + this.getError() + ", requiredPercent=" + this.getRequiredPercent() + ", actualPercent=" + this.getActualPercent() + ")";
    }

    public ReportTableDataModel() {
    }

    public ReportTableDataModel(final int number, final String materialName, final double required, final double loaded, final double error, final double requiredPercent, final double actualPercent) {
        this.number = number;
        this.materialName = materialName;
        this.required = required;
        this.loaded = loaded;
        this.error = error;
        this.requiredPercent = requiredPercent;
        this.actualPercent = actualPercent;
    }
}
