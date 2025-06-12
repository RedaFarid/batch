
package com.batch.GUI.PhasesWindow;

import com.batch.DTO.RecipeSystemDataDefinitions.PhaseInformationDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class PhasesModel {
    private TreeItem<PhaseInformationDTO> rootItem;
    private final ObservableList<PhaseInformationDTO> list = FXCollections.observableArrayList();

    public TreeItem<PhaseInformationDTO> getRootItem() {
        return this.rootItem;
    }

    public ObservableList<PhaseInformationDTO> getList() {
        return this.list;
    }

    public void setRootItem(final TreeItem<PhaseInformationDTO> rootItem) {
        this.rootItem = rootItem;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PhasesModel)) {
            return false;
        } else {
            PhasesModel other = (PhasesModel)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$rootItem = this.getRootItem();
                Object other$rootItem = other.getRootItem();
                if (this$rootItem == null) {
                    if (other$rootItem != null) {
                        return false;
                    }
                } else if (!this$rootItem.equals(other$rootItem)) {
                    return false;
                }

                Object this$list = this.getList();
                Object other$list = other.getList();
                if (this$list == null) {
                    if (other$list != null) {
                        return false;
                    }
                } else if (!this$list.equals(other$list)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PhasesModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $rootItem = this.getRootItem();
        result = result * 59 + ($rootItem == null ? 43 : $rootItem.hashCode());
        Object $list = this.getList();
        result = result * 59 + ($list == null ? 43 : $list.hashCode());
        return result;
    }

    public String toString() {
        TreeItem var10000 = this.getRootItem();
        return "PhasesModel(rootItem=" + var10000 + ", list=" + this.getList() + ")";
    }
}
