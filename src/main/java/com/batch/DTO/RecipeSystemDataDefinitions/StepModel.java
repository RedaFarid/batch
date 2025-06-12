package com.batch.DTO.RecipeSystemDataDefinitions;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Parameter;
import com.batch.Database.Repositories.PhaseRepository;
import com.google.common.collect.Lists;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StepModel {
    private final PhaseRepository phaseRepository;
    private Long PhaseID = 0L;
    private String PhaseType = "";
    private String PhaseName = "";
    private Long MaterialID = 0L;
    private List<Parameter> parametersType = new LinkedList();
    private Map<String, Double> ValueParametersData = new LinkedHashMap();
    private Map<String, Boolean> CheckParametersData = new LinkedHashMap();

    public StepModel(String PhaseType, String PhaseName, List<Parameter> parametersType, Map<String, Double> ValueParametersData, Map<String, Boolean> CheckParametersData) {
        this.PhaseType = PhaseType;
        this.PhaseName = PhaseName;
        this.parametersType = parametersType;
        this.ValueParametersData = ValueParametersData;
        this.CheckParametersData = CheckParametersData;
        this.phaseRepository = ApplicationContext.applicationContext.getBean(PhaseRepository.class);
        Lists.newArrayList(this.phaseRepository.findAll()).stream().filter((phase) -> phase.getName().equals(PhaseName)).findAny().ifPresent((phase) -> {
            this.PhaseType = phase.getPhaseType();
            this.PhaseID = phase.getId();
            phase.getParameters().forEach((parameter) -> {
                parametersType.add(new Parameter(parameter.getName(), parameter.getType()));
                ValueParametersData.put(parameter.getName(), (double) 0.0F);
                CheckParametersData.put(parameter.getName(), false);
            });
        });
    }

    public StepModel(String PhaseName) {
        this.PhaseName = PhaseName;
        this.phaseRepository = ApplicationContext.applicationContext.getBean(PhaseRepository.class);
        Lists.newArrayList(this.phaseRepository.findAll()).stream().filter((phase) -> phase.getName().equals(PhaseName)).findAny().ifPresent((phase) -> {
            this.PhaseType = phase.getPhaseType();
            this.PhaseID = phase.getId();
            phase.getParameters().forEach((parameter) -> {
                this.parametersType.add(new Parameter(parameter.getName(), parameter.getType()));
                this.ValueParametersData.put(parameter.getName(), (double) 0.0F);
                this.CheckParametersData.put(parameter.getName(), false);
            });
        });
    }

    public StepModel() {
        this.phaseRepository = ApplicationContext.applicationContext.getBean(PhaseRepository.class);
        Lists.newArrayList(this.phaseRepository.findAll()).stream().filter((phase) -> phase.getName().equals(this.PhaseName)).findAny().ifPresent((phase) -> {
            this.PhaseType = phase.getPhaseType();
            this.PhaseID = phase.getId();
            phase.getParameters().forEach((parameter) -> {
                this.parametersType.add(new Parameter(parameter.getName(), parameter.getType()));
                this.ValueParametersData.put(parameter.getName(), (double) 0.0F);
                this.CheckParametersData.put(parameter.getName(), false);
            });
        });
    }

    public Long getPhaseID() {
        return this.PhaseID;
    }

    public void setPhaseID(final Long PhaseID) {
        this.PhaseID = PhaseID;
    }

    public String getPhaseType() {
        return this.PhaseType;
    }

    public void setPhaseType(final String PhaseType) {
        this.PhaseType = PhaseType;
    }

    public String getPhaseName() {
        return this.PhaseName;
    }

    public void setPhaseName(final String PhaseName) {
        this.PhaseName = PhaseName;
    }

    public Long getMaterialID() {
        return this.MaterialID;
    }

    public void setMaterialID(final Long MaterialID) {
        this.MaterialID = MaterialID;
    }

    public List<Parameter> getParametersType() {
        return this.parametersType;
    }

    public void setParametersType(final List<Parameter> parametersType) {
        this.parametersType = parametersType;
    }

    public Map<String, Double> getValueParametersData() {
        return this.ValueParametersData;
    }

    public void setValueParametersData(final Map<String, Double> ValueParametersData) {
        this.ValueParametersData = ValueParametersData;
    }

    public Map<String, Boolean> getCheckParametersData() {
        return this.CheckParametersData;
    }

    public void setCheckParametersData(final Map<String, Boolean> CheckParametersData) {
        this.CheckParametersData = CheckParametersData;
    }

    public PhaseRepository getPhaseRepository() {
        return this.phaseRepository;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof StepModel other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$PhaseID = this.getPhaseID();
                Object other$PhaseID = other.getPhaseID();
                if (this$PhaseID == null) {
                    if (other$PhaseID != null) {
                        return false;
                    }
                } else if (!this$PhaseID.equals(other$PhaseID)) {
                    return false;
                }

                Object this$MaterialID = this.getMaterialID();
                Object other$MaterialID = other.getMaterialID();
                if (this$MaterialID == null) {
                    if (other$MaterialID != null) {
                        return false;
                    }
                } else if (!this$MaterialID.equals(other$MaterialID)) {
                    return false;
                }

                Object this$PhaseType = this.getPhaseType();
                Object other$PhaseType = other.getPhaseType();
                if (this$PhaseType == null) {
                    if (other$PhaseType != null) {
                        return false;
                    }
                } else if (!this$PhaseType.equals(other$PhaseType)) {
                    return false;
                }

                Object this$PhaseName = this.getPhaseName();
                Object other$PhaseName = other.getPhaseName();
                if (this$PhaseName == null) {
                    if (other$PhaseName != null) {
                        return false;
                    }
                } else if (!this$PhaseName.equals(other$PhaseName)) {
                    return false;
                }

                Object this$parametersType = this.getParametersType();
                Object other$parametersType = other.getParametersType();
                if (this$parametersType == null) {
                    if (other$parametersType != null) {
                        return false;
                    }
                } else if (!this$parametersType.equals(other$parametersType)) {
                    return false;
                }

                Object this$ValueParametersData = this.getValueParametersData();
                Object other$ValueParametersData = other.getValueParametersData();
                if (this$ValueParametersData == null) {
                    if (other$ValueParametersData != null) {
                        return false;
                    }
                } else if (!this$ValueParametersData.equals(other$ValueParametersData)) {
                    return false;
                }

                Object this$CheckParametersData = this.getCheckParametersData();
                Object other$CheckParametersData = other.getCheckParametersData();
                if (this$CheckParametersData == null) {
                    if (other$CheckParametersData != null) {
                        return false;
                    }
                } else if (!this$CheckParametersData.equals(other$CheckParametersData)) {
                    return false;
                }

                Object this$phaseRepository = this.getPhaseRepository();
                Object other$phaseRepository = other.getPhaseRepository();
                if (this$phaseRepository == null) {
                    return other$phaseRepository == null;
                } else return this$phaseRepository.equals(other$phaseRepository);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof StepModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $PhaseID = this.getPhaseID();
        result = result * 59 + ($PhaseID == null ? 43 : $PhaseID.hashCode());
        Object $MaterialID = this.getMaterialID();
        result = result * 59 + ($MaterialID == null ? 43 : $MaterialID.hashCode());
        Object $PhaseType = this.getPhaseType();
        result = result * 59 + ($PhaseType == null ? 43 : $PhaseType.hashCode());
        Object $PhaseName = this.getPhaseName();
        result = result * 59 + ($PhaseName == null ? 43 : $PhaseName.hashCode());
        Object $parametersType = this.getParametersType();
        result = result * 59 + ($parametersType == null ? 43 : $parametersType.hashCode());
        Object $ValueParametersData = this.getValueParametersData();
        result = result * 59 + ($ValueParametersData == null ? 43 : $ValueParametersData.hashCode());
        Object $CheckParametersData = this.getCheckParametersData();
        result = result * 59 + ($CheckParametersData == null ? 43 : $CheckParametersData.hashCode());
        Object $phaseRepository = this.getPhaseRepository();
        result = result * 59 + ($phaseRepository == null ? 43 : $phaseRepository.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getPhaseID();
        return "StepModel(PhaseID=" + var10000 + ", PhaseType=" + this.getPhaseType() + ", PhaseName=" + this.getPhaseName() + ", MaterialID=" + this.getMaterialID() + ", parametersType=" + this.getParametersType() + ", ValueParametersData=" + this.getValueParametersData() + ", CheckParametersData=" + this.getCheckParametersData() + ", phaseRepository=" + this.getPhaseRepository() + ")";
    }
}
