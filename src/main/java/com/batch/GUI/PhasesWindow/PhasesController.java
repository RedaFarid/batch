package com.batch.GUI.PhasesWindow;

import com.batch.DTO.RecipeSystemDataDefinitions.PhaseInformationDTO;
import com.batch.Database.Entities.Parameter;
import com.batch.Database.Entities.Phase;
import com.batch.Database.Entities.Unit;
import com.batch.Database.Repositories.PhaseRepository;
import com.batch.Database.Repositories.UnitsRepository;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PhasesController {
    private final PhasesModel model = new PhasesModel();
    private final PhaseRepository phaseRepository;
    private final UnitsRepository unitsRepository;

    public PhasesController(final PhaseRepository phaseRepository, final UnitsRepository unitsRepository) {
        this.phaseRepository = phaseRepository;
        this.unitsRepository = unitsRepository;
    }

    public PhasesModel getModel() {
        return this.model;
    }

    public void refresh() {
        TreeItem<PhaseInformationDTO> rootItem = this.model.getRootItem();
        rootItem.getChildren().clear();
        this.findAllPhases().forEach((phase) -> {
            PhaseInformationDTO phaseitem = new PhaseInformationDTO(String.valueOf(phase.getId()), phase.getName(), phase.getUnit(), phase.getPhaseType(), "", "");
            TreeItem<PhaseInformationDTO> phaseTreeItem = new TreeItem(phaseitem);
            rootItem.getChildren().add(phaseTreeItem);
            phase.getParameters().forEach((parameter) -> {
                PhaseInformationDTO parameteritem = new PhaseInformationDTO("", "", "", "", parameter.getName(), parameter.getType());
                TreeItem<PhaseInformationDTO> parameterTreeItem = new TreeItem(parameteritem);
                phaseTreeItem.getChildren().add(parameterTreeItem);
            });
        });
    }

    public ObservableList<String> getUnitsName() {
        return FXCollections.observableArrayList((Collection) Lists.newArrayList(this.unitsRepository.findAll()).stream().map(Unit::getName).collect(Collectors.toList()));
    }

    public List<Phase> findAllPhases() {
        return Lists.newArrayList(this.phaseRepository.findAll());
    }

    @Async
    public void deletePhase(String id) {
        this.phaseRepository.deleteById(Long.parseLong(id));
        Platform.runLater(this::refresh);
    }

    @Async
    public void addParameterToPhase(String id, String name, String type) {
        this.phaseRepository.findById(Long.parseLong(id)).ifPresent((phase) -> {
            phase.getParameters().add(new Parameter(name, type));
            this.phaseRepository.save(phase);
            Platform.runLater(this::refresh);
        });
    }

    @Async
    public void deleteParameterFromPhase(String id, String parameterName) {
        this.phaseRepository.findById(Long.parseLong(id)).ifPresent((phase) -> phase.getParameters().stream().filter((parameter) -> parameter.getName().equals(parameterName)).findAny().ifPresent((parameter) -> {
            phase.getParameters().remove(parameter);
            this.phaseRepository.save(phase);
            Platform.runLater(this::refresh);
        }));
    }

    @Async
    public void clearAllPhases() {
        this.phaseRepository.deleteAll();
    }

    public Optional<Phase> findPhaseById(String id) {
        return this.phaseRepository.findById(Long.parseLong(id));
    }

    public void createNewPhase(Phase phase) {
        this.phaseRepository.save(phase);
    }
}
