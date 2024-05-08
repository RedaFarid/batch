package com.batch.GUI.Reporting;

import com.batch.Database.Entities.Batch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportsModel {
    private final ObservableList<Batch> list = FXCollections.observableArrayList();
    private ObjectProperty<LocalDate> fromDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> toDate = new SimpleObjectProperty<>();
    private StringProperty filterString = new SimpleStringProperty("");
}
