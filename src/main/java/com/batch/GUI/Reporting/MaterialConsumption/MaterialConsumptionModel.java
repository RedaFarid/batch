package com.batch.GUI.Reporting.MaterialConsumption;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialConsumptionModel {

    private final ObservableList<Item> list = FXCollections.observableArrayList();
    private ObjectProperty<LocalDate> fromDate = new SimpleObjectProperty();
    private ObjectProperty<LocalDate> toDate = new SimpleObjectProperty();

    @Data
    @AllArgsConstructor
    public static class Item{
        private String materialName;
        private Double required;
        private Double actual;
        private Double error;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MaterialConsumptionModel that = (MaterialConsumptionModel) o;
        return Objects.equals(list, that.list) && Objects.equals(fromDate, that.fromDate) && Objects.equals(toDate, that.toDate);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MaterialConsumptionModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $list = this.getList();
        result = result * 59 + ($list == null ? 43 : $list.hashCode());
        Object $fromDate = this.getFromDate();
        result = result * 59 + ($fromDate == null ? 43 : $fromDate.hashCode());
        Object $toDate = this.getToDate();
        result = result * 59 + ($toDate == null ? 43 : $toDate.hashCode());
        return result;
    }

    public String toString() {
        ObservableList var10000 = this.getList();
        return "MaterialConsumptionModel(list=" + var10000 + ", fromDate=" + this.getFromDate() + ", toDate=" + this.getToDate()  + ")";
    }
}
