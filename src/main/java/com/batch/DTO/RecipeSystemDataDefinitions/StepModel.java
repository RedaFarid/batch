package com.batch.DTO.RecipeSystemDataDefinitions;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Parameter;
import com.batch.Database.Repositories.PhaseRepository;
import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class StepModel {

    private long PhaseID;
    private String PhaseType = "";
    private String PhaseName = "";
    private long MaterialID = 0;

    private List<Parameter> parametersType = new LinkedList<>();
    private Map<String, Double> ValueParametersData = new LinkedHashMap<>();
    private Map<String, Boolean> CheckParametersData = new LinkedHashMap<>();

    private final PhaseRepository phaseRepository;

    public StepModel(String PhaseType, String PhaseName, List<Parameter> parametersType, Map<String, Double> ValueParametersData, Map<String, Boolean> CheckParametersData) {
        this.PhaseType = PhaseType;
        this.PhaseName = PhaseName;
        this.parametersType = parametersType;
        this.ValueParametersData = ValueParametersData;
        this.CheckParametersData = CheckParametersData;
        this.phaseRepository = ApplicationContext.applicationContext.getBean(PhaseRepository.class);
        phaseRepository.findAll()
                .stream()
                .filter(phase -> phase.getName().equals(PhaseName))
                .findAny()
                .ifPresent(phase -> {
                    this.PhaseType = phase.getPhaseType();
                    this.PhaseID = phase.getId();
                    phase.getParameters().forEach(parameter -> {
                        parametersType.add(new Parameter(parameter.getName(), parameter.getType()));
                        ValueParametersData.put(parameter.getName(), 0.0);
                        CheckParametersData.put(parameter.getName(), false);
                    });
                });
    }
    public StepModel(String PhaseName) {
        this.PhaseName = PhaseName;
        this.phaseRepository = ApplicationContext.applicationContext.getBean(PhaseRepository.class);
        phaseRepository.findAll().stream().filter(phase -> phase.getName().equals(PhaseName)).findAny().ifPresent(phase -> {
            this.PhaseType = phase.getPhaseType();
            this.PhaseID = phase.getId();
            phase.getParameters().forEach(parameter -> {
                parametersType.add(new Parameter(parameter.getName(), parameter.getType()));
                ValueParametersData.put(parameter.getName(), 0.0);
                CheckParametersData.put(parameter.getName(), false);
            });
        });
    }
    public StepModel() {
        this.phaseRepository = ApplicationContext.applicationContext.getBean(PhaseRepository.class);
        phaseRepository.findAll().stream().filter(phase -> phase.getName().equals(PhaseName)).findAny().ifPresent(phase -> {
            this.PhaseType = phase.getPhaseType();
            this.PhaseID = phase.getId();
            phase.getParameters().forEach(parameter -> {
                parametersType.add(new Parameter(parameter.getName(), parameter.getType()));
                ValueParametersData.put(parameter.getName(), 0.0);
                CheckParametersData.put(parameter.getName(), false);
            });
        });
    }

}
