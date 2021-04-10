package com.batch.GUI.UnitsWindow;

import com.batch.Database.Entities.Unit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class UnitsModel {

    private final ObservableList<Unit> list = FXCollections.observableArrayList();
    private BooleanProperty isShown = new SimpleBooleanProperty();
}
