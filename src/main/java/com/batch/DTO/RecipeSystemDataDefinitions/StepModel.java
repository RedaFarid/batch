
package com.batch.DTO.RecipeSystemDataDefinitions;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Parameter;
import com.batch.Database.Repositories.PhaseRepository;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class StepModel {
    
    private Long PhaseID;
    private String PhaseType = "";
    private String PhaseName = "";
    private long MaterialID = 0;
    

    private List<Parameter> parametersType = new LinkedList<>();
    private Map<String, Double> ValueParametersData = new LinkedHashMap<>();
    private Map<String, Boolean> CheckParametersData = new LinkedHashMap<>();

    private PhaseRepository phaseRepository ;

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

    public String getPhaseType() {
        return PhaseType;
    }

    public void setPhaseType(String PhaseType) {
        this.PhaseType = PhaseType;
    }

    public String getPhaseName() {
        return PhaseName;
    }

    public void setPhaseName(String PhaseName) {
        this.PhaseName = PhaseName;
    }

    public List<Parameter> getParametersType() {
        return parametersType;
    }

    public void setParametersType(List<Parameter> parametersType) {
        this.parametersType = parametersType;
    }


    public Map<String, Double> getValueParametersData() {
        return ValueParametersData;
    }

    public void setValueParametersData(Map<String, Double> ValueParametersData) {
        this.ValueParametersData = ValueParametersData;
    }

    public Map<String, Boolean> getCheckParametersData() {
        return CheckParametersData;
    }

    public void setCheckParametersData(Map<String, Boolean> CheckParametersData) {
        this.CheckParametersData = CheckParametersData;
    }

    public long getPhaseID() {
        return PhaseID;
    }

    public void setPhaseID(long PhaseID) {
        this.PhaseID = PhaseID;
    }

    public long getMaterialID() {
        return MaterialID;
    }

    public void setMaterialID(long MaterialID) {
        this.MaterialID = MaterialID;
    }
    

    @Override
    public String toString() {
        return "StepModel{" + "PhaseID=" + PhaseID + ", PhaseType=" + PhaseType + ", PhaseName=" + PhaseName + ", parametersType=" + parametersType + ", ValueParametersData=" + ValueParametersData + ", CheckParametersData=" + CheckParametersData + '}';
    }
    
    
}
