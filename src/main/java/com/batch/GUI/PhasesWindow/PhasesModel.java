package com.batch.GUI.PhasesWindow;

import com.batch.DTO.RecipeSystemDataDefinitions.PhaseInformationDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.Data;

@Data
public class PhasesModel {

    private TreeItem<PhaseInformationDTO> rootItem;
    private final ObservableList<PhaseInformationDTO> list = FXCollections.observableArrayList();

}
