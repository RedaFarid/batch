

package com.batch.DTO.RecipeSystemDataDefinitions;

public class PhaseInformationDTO {
    private String id;
    private String name;
    private String unit;
    private String phaseType;
    private String ParameterName;
    private String ParameterType;

    public PhaseInformationDTO(String id, String name, String unit, String phaseType, String ParameterName, String ParameterType) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.phaseType = phaseType;
        this.ParameterName = ParameterName;
        this.ParameterType = ParameterType;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPhaseType() {
        return this.phaseType;
    }

    public void setPhaseType(String phaseType) {
        this.phaseType = phaseType;
    }

    public String getParameterName() {
        return this.ParameterName;
    }

    public void setParameterName(String ParameterName) {
        this.ParameterName = ParameterName;
    }

    public String getParameterType() {
        return this.ParameterType;
    }

    public void setParameterType(String ParameterType) {
        this.ParameterType = ParameterType;
    }
}
