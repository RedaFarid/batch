
package com.batch.DTO.BatchSystemDataDefinitions;


import com.batch.DTO.RecipeSystemDataDefinitions.StepModel;
import com.batch.Database.Entities.Parameter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class BatchStepModel {
    
    private long PhaseID;
    private String PhaseType;
    private String PhaseName;
    private String state;
    private String order;
    private long MaterialID;
    
    private List<Parameter> parametersType = new LinkedList<>();
    private Map<String, Double> ValueParametersData = new LinkedHashMap<>();
    private Map<String, Boolean> CheckParametersData = new LinkedHashMap<>();
    private Map<String, Double> ActualvalueParametersData = new LinkedHashMap<>();
    private Map<String, Boolean> ActualCheckParametersData = new LinkedHashMap<>();

    public BatchStepModel(StepModel model) {
        this.state = BatchStates.Created.name();
        this.order = BatchOrders.Create.name();
        this.PhaseType = model.getPhaseType();
        this.PhaseName = model.getPhaseName();
        System.err.println(model.getPhaseID());
        this.PhaseID = model.getPhaseID();
        this.MaterialID = model.getMaterialID();
        
        model.getParametersType().forEach(item -> {
            this.parametersType.add(new Parameter(item.getName(), item.getType()));
        });
        
        model.getCheckParametersData().forEach((a, b) -> {
            this.CheckParametersData.put(a, b);
        });
        
        model.getValueParametersData().forEach((a, b) -> {
            this.ValueParametersData.put(a, b);
        });
        
        parametersType.forEach(parameter -> {
            ActualvalueParametersData.put(parameter.getName(), 0.0);
            ActualCheckParametersData.put(parameter.getName(), false);
        });
    }

    public BatchStepModel() {
    }

    public long getPhaseID() {
        return PhaseID;
    }

    public void setPhaseID(int PhaseID) {
        this.PhaseID = PhaseID;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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

    public Map<String, Double> getActualvalueParametersData() {
        return ActualvalueParametersData;
    }

    public void setActualvalueParametersData(Map<String, Double> ActualvalueParametersData) {
        this.ActualvalueParametersData = ActualvalueParametersData;
    }

    public Map<String, Boolean> getActualCheckParametersData() {
        return ActualCheckParametersData;
    }

    public void setActualCheckParametersData(Map<String, Boolean> ActualCheckParametersData) {
        this.ActualCheckParametersData = ActualCheckParametersData;
    }

    public long getMaterialID() {
        return MaterialID;
    }

    public void setMaterialID(long MaterialID) {
        this.MaterialID = MaterialID;
    }
    

    @Override
    public String toString() {
        return "PhaseID=" + PhaseID + ", PhaseType=" + PhaseType + ", PhaseName=" + PhaseName + ", state=" + state + ", order=" + order ;
    }
    
}
