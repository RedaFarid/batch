package com.batch.GUI.UnitsWindow;

import com.batch.Database.Entities.Unit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UnitsModel {
    private final ObservableList<Unit> list = FXCollections.observableArrayList();
    private BooleanProperty isShown = new SimpleBooleanProperty();

    public ObservableList<Unit> getList() {
        return this.list;
    }

    public BooleanProperty getIsShown() {
        return this.isShown;
    }

    public void setIsShown(final BooleanProperty isShown) {
        this.isShown = isShown;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof UnitsModel other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$list = this.getList();
                Object other$list = other.getList();
                if (this$list == null) {
                    if (other$list != null) {
                        return false;
                    }
                } else if (!this$list.equals(other$list)) {
                    return false;
                }

                Object this$isShown = this.getIsShown();
                Object other$isShown = other.getIsShown();
                if (this$isShown == null) {
                    return other$isShown == null;
                } else return this$isShown.equals(other$isShown);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UnitsModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $list = this.getList();
        result = result * 59 + ($list == null ? 43 : $list.hashCode());
        Object $isShown = this.getIsShown();
        result = result * 59 + ($isShown == null ? 43 : $isShown.hashCode());
        return result;
    }

    public String toString() {
        ObservableList var10000 = this.getList();
        return "UnitsModel(list=" + var10000 + ", isShown=" + this.getIsShown() + ")";
    }
}
