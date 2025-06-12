package com.batch.GUI.BatchWindow;

public class BatchesModel {
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BatchesModel other)) {
            return false;
        } else {
            return other.canEqual(this);
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BatchesModel;
    }

    public int hashCode() {
        int result = 1;
        return 1;
    }

    public String toString() {
        return "BatchesModel()";
    }
}
