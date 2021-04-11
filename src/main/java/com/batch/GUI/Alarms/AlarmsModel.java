package com.batch.GUI.Alarms;

import com.batch.Database.Entities.Log;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class AlarmsModel {
    private final ObservableList<Log> allAlarmsList = FXCollections.observableArrayList();
    private final BooleanProperty isShown = new SimpleBooleanProperty();

    private final StringProperty airPressureLoAlarm = new SimpleStringProperty();
    private final StringProperty airPressureHiAlarm = new SimpleStringProperty();
}
