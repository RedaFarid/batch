package com.batch.GUI.Reporting;

import com.batch.Database.Entities.Batch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class ReportsModel {
    private final ObservableList<Batch> list = FXCollections.observableArrayList();
    private ObjectProperty<LocalDate> fromDate = new SimpleObjectProperty();
    private ObjectProperty<LocalDate> toDate = new SimpleObjectProperty();
    private StringProperty filterString = new SimpleStringProperty("");

    public ObservableList<Batch> getList() {
        return this.list;
    }

    public ObjectProperty<LocalDate> getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(final ObjectProperty<LocalDate> fromDate) {
        this.fromDate = fromDate;
    }

    public ObjectProperty<LocalDate> getToDate() {
        return this.toDate;
    }

    public void setToDate(final ObjectProperty<LocalDate> toDate) {
        this.toDate = toDate;
    }

    public StringProperty getFilterString() {
        return this.filterString;
    }

    public void setFilterString(final StringProperty filterString) {
        this.filterString = filterString;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ReportsModel other)) {
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

                Object this$fromDate = this.getFromDate();
                Object other$fromDate = other.getFromDate();
                if (this$fromDate == null) {
                    if (other$fromDate != null) {
                        return false;
                    }
                } else if (!this$fromDate.equals(other$fromDate)) {
                    return false;
                }

                Object this$toDate = this.getToDate();
                Object other$toDate = other.getToDate();
                if (this$toDate == null) {
                    if (other$toDate != null) {
                        return false;
                    }
                } else if (!this$toDate.equals(other$toDate)) {
                    return false;
                }

                Object this$filterString = this.getFilterString();
                Object other$filterString = other.getFilterString();
                if (this$filterString == null) {
                    return other$filterString == null;
                } else return this$filterString.equals(other$filterString);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ReportsModel;
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
        Object $filterString = this.getFilterString();
        result = result * 59 + ($filterString == null ? 43 : $filterString.hashCode());
        return result;
    }

    public String toString() {
        ObservableList var10000 = this.getList();
        return "ReportsModel(list=" + var10000 + ", fromDate=" + this.getFromDate() + ", toDate=" + this.getToDate() + ", filterString=" + this.getFilterString() + ")";
    }
}
