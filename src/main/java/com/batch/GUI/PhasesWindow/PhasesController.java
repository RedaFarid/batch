package com.batch.GUI.PhasesWindow;

import com.batch.DTO.RecipeSystemDataDefinitions.PhaseInformationDTO;
import com.batch.Database.Entities.Parameter;
import com.batch.Database.Entities.Phase;
import com.batch.Database.Entities.Unit;
import com.batch.Database.Repositories.PhaseRepository;
import com.batch.Database.Repositories.UnitsRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PhasesController {

    private final PhasesModel model = new PhasesModel();
    private final PhaseRepository phaseRepository;
    private final UnitsRepository unitsRepository;


    public PhasesModel getModel() {
        return model;
    }

    public void refresh() {
        final TreeItem<PhaseInformationDTO> rootItem = model.getRootItem();
        rootItem.getChildren().clear();
        findAllPhases().forEach(phase -> {
            PhaseInformationDTO phaseitem = new PhaseInformationDTO(String.valueOf(phase.getId()), phase.getName(), phase.getUnit(), phase.getPhaseType(), "", "");
            TreeItem<PhaseInformationDTO> phaseTreeItem = new TreeItem<>(phaseitem);
            rootItem.getChildren().add(phaseTreeItem);
            phase.getParameters().forEach(parameter -> {
                PhaseInformationDTO parameteritem = new PhaseInformationDTO("", "", "", "", parameter.getName(), parameter.getType());
                TreeItem<PhaseInformationDTO> parameterTreeItem = new TreeItem<>(parameteritem);
                phaseTreeItem.getChildren().add(parameterTreeItem);
            });
        });
    }


    public ObservableList<String> getUnitsName() {
        return FXCollections.observableArrayList(unitsRepository.findAll().stream().map(Unit::getName).collect(Collectors.toList()));
    }
    public List<Phase> findAllPhases() {
        return phaseRepository.findAll();
    }



    @Async
    public void deletePhase(String id) {
        phaseRepository.deleteById(Long.parseLong(id));
        Platform.runLater(this::refresh);
    }

    @Async
    public void addParameterToPhase(String id, String name, String type) {
        phaseRepository.findById(Long.parseLong(id)).ifPresent(phase -> {
            phase.getParameters().add(new Parameter(name, type));
            phaseRepository.save(phase);
            Platform.runLater(this::refresh);
        });
    }

    @Async
    public void deleteParameterFromPhase(String id, String parameterName) {
        phaseRepository.findById(Long.parseLong(id)).ifPresent(phase -> {
            phase.getParameters().stream().filter(parameter -> parameter.getName().equals(parameterName)).findAny().ifPresent(parameter -> {
                phase.getParameters().remove(parameter);
                phaseRepository.save(phase);
                Platform.runLater(this::refresh);
            });
        });
    }

    @Async
    public void clearAllPhases() {
        phaseRepository.deleteAll();
    }

    public Optional<Phase> findPhaseById(String id) {
        return phaseRepository.findById(Long.parseLong(id));
    }

    public void createNewPhase(Phase phase) {
        phaseRepository.save(phase);
    }
}
