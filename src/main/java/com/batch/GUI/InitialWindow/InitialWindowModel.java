package com.batch.GUI.InitialWindow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class InitialWindowModel {

    private StringProperty connectionInfo = new SimpleStringProperty();
    private BooleanProperty connectionStatus = new SimpleBooleanProperty();

}
