package com.batch.GUI.InitialWindow;

import javafx.beans.property.*;
import lombok.Data;

@Data
public class InitialWindowModel {

    private StringProperty connectionInfo = new SimpleStringProperty();
    private BooleanProperty connectionStatus = new SimpleBooleanProperty();

    private StringProperty airPressureInfo = new SimpleStringProperty();
    private BooleanProperty airPressureStatus = new SimpleBooleanProperty();

    private StringProperty overUnderVoltageInfo = new SimpleStringProperty();
    private BooleanProperty overUnderVoltageStatus = new SimpleBooleanProperty();

    private StringProperty esdInfo = new SimpleStringProperty();
    private BooleanProperty esdStatus = new SimpleBooleanProperty();

    private DoubleProperty gauge1 = new SimpleDoubleProperty();
    private DoubleProperty gauge2 = new SimpleDoubleProperty();





}
