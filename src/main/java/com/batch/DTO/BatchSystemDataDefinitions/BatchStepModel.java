//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.batch.DTO.BatchSystemDataDefinitions;

import com.batch.DTO.RecipeSystemDataDefinitions.StepModel;
import com.batch.Database.Entities.Parameter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BatchStepModel {
    private Long PhaseID = 0L;
    private String PhaseType;
    private String PhaseName;
    private String state;
    private String order;
    private Long MaterialID = 0L;
    private List<Parameter> parametersType = new LinkedList();
    private Map<String, Double> ValueParametersData = new LinkedHashMap();
    private Map<String, Boolean> CheckParametersData = new LinkedHashMap();
    private Map<String, Double> actualvalueParametersData = new LinkedHashMap();
    private Map<String, Boolean> ActualCheckParametersData = new LinkedHashMap();

    public BatchStepModel(StepModel model) {
        this.state = BatchStates.Created.name();
        this.order = BatchOrders.Create.name();
        this.PhaseType = model.getPhaseType();
        this.PhaseName = model.getPhaseName();
        this.PhaseID = model.getPhaseID();
        this.MaterialID = model.getMaterialID();
        model.getParametersType().forEach((item) -> this.parametersType.add(new Parameter(item.getPid(), item.getName(), item.getType(), item.getId(), item.getPhase())));
        this.CheckParametersData.putAll(model.getCheckParametersData());
        this.ValueParametersData.putAll(model.getValueParametersData());
        this.parametersType.forEach((parameter) -> {
            this.actualvalueParametersData.put(parameter.getName(), (double) 0.0F);
            this.ActualCheckParametersData.put(parameter.getName(), false);
        });
    }

    public BatchStepModel() {
    }

    public BatchStepModel(final Long PhaseID, final String PhaseType, final String PhaseName, final String state, final String order, final Long MaterialID, final List<Parameter> parametersType, final Map<String, Double> ValueParametersData, final Map<String, Boolean> CheckParametersData, final Map<String, Double> actualvalueParametersData, final Map<String, Boolean> ActualCheckParametersData) {
        this.PhaseID = PhaseID;
        this.PhaseType = PhaseType;
        this.PhaseName = PhaseName;
        this.state = state;
        this.order = order;
        this.MaterialID = MaterialID;
        this.parametersType = parametersType;
        this.ValueParametersData = ValueParametersData;
        this.CheckParametersData = CheckParametersData;
        this.actualvalueParametersData = actualvalueParametersData;
        this.ActualCheckParametersData = ActualCheckParametersData;
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

    public String getState() {
        return this.state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(final String order) {
        this.order = order;
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

    public Map<String, Double> getActualvalueParametersData() {
        return this.actualvalueParametersData;
    }

    public void setActualvalueParametersData(final Map<String, Double> actualvalueParametersData) {
        this.actualvalueParametersData = actualvalueParametersData;
    }

    public Map<String, Boolean> getActualCheckParametersData() {
        return this.ActualCheckParametersData;
    }

    public void setActualCheckParametersData(final Map<String, Boolean> ActualCheckParametersData) {
        this.ActualCheckParametersData = ActualCheckParametersData;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BatchStepModel other)) {
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

                Object this$state = this.getState();
                Object other$state = other.getState();
                if (this$state == null) {
                    if (other$state != null) {
                        return false;
                    }
                } else if (!this$state.equals(other$state)) {
                    return false;
                }

                Object this$order = this.getOrder();
                Object other$order = other.getOrder();
                if (this$order == null) {
                    if (other$order != null) {
                        return false;
                    }
                } else if (!this$order.equals(other$order)) {
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

                Object this$actualvalueParametersData = this.getActualvalueParametersData();
                Object other$actualvalueParametersData = other.getActualvalueParametersData();
                if (this$actualvalueParametersData == null) {
                    if (other$actualvalueParametersData != null) {
                        return false;
                    }
                } else if (!this$actualvalueParametersData.equals(other$actualvalueParametersData)) {
                    return false;
                }

                Object this$ActualCheckParametersData = this.getActualCheckParametersData();
                Object other$ActualCheckParametersData = other.getActualCheckParametersData();
                if (this$ActualCheckParametersData == null) {
                    return other$ActualCheckParametersData == null;
                } else return this$ActualCheckParametersData.equals(other$ActualCheckParametersData);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BatchStepModel;
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
        Object $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        Object $order = this.getOrder();
        result = result * 59 + ($order == null ? 43 : $order.hashCode());
        Object $parametersType = this.getParametersType();
        result = result * 59 + ($parametersType == null ? 43 : $parametersType.hashCode());
        Object $ValueParametersData = this.getValueParametersData();
        result = result * 59 + ($ValueParametersData == null ? 43 : $ValueParametersData.hashCode());
        Object $CheckParametersData = this.getCheckParametersData();
        result = result * 59 + ($CheckParametersData == null ? 43 : $CheckParametersData.hashCode());
        Object $actualvalueParametersData = this.getActualvalueParametersData();
        result = result * 59 + ($actualvalueParametersData == null ? 43 : $actualvalueParametersData.hashCode());
        Object $ActualCheckParametersData = this.getActualCheckParametersData();
        result = result * 59 + ($ActualCheckParametersData == null ? 43 : $ActualCheckParametersData.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getPhaseID();
        return "BatchStepModel(PhaseID=" + var10000 + ", PhaseType=" + this.getPhaseType() + ", PhaseName=" + this.getPhaseName() + ", state=" + this.getState() + ", order=" + this.getOrder() + ", MaterialID=" + this.getMaterialID() + ", parametersType=" + this.getParametersType() + ", ValueParametersData=" + this.getValueParametersData() + ", CheckParametersData=" + this.getCheckParametersData() + ", actualvalueParametersData=" + this.getActualvalueParametersData() + ", ActualCheckParametersData=" + this.getActualCheckParametersData() + ")";
    }
}
