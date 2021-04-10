package com.batch.GUI.MaterialsWindow;

import com.batch.Database.Entities.Material;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class MaterialsModel {

    private ObservableList<Material> list = FXCollections.observableArrayList();
    private BooleanProperty isShown = new SimpleBooleanProperty();
}
