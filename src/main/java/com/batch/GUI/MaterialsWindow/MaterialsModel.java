

package com.batch.GUI.MaterialsWindow;

import com.batch.Database.Entities.Material;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MaterialsModel {
    private ObservableList<Material> list = FXCollections.observableArrayList();
    private BooleanProperty isShown = new SimpleBooleanProperty();

    public ObservableList<Material> getList() {
        return this.list;
    }

    public BooleanProperty getIsShown() {
        return this.isShown;
    }

    public void setList(final ObservableList<Material> list) {
        this.list = list;
    }

    public void setIsShown(final BooleanProperty isShown) {
        this.isShown = isShown;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof MaterialsModel)) {
            return false;
        } else {
            MaterialsModel other = (MaterialsModel)o;
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
                    if (other$isShown != null) {
                        return false;
                    }
                } else if (!this$isShown.equals(other$isShown)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MaterialsModel;
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
        return "MaterialsModel(list=" + var10000 + ", isShown=" + this.getIsShown() + ")";
    }
}
