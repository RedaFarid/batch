
package com.batch.Database.Entities;

import java.util.LinkedList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("PhasesTypesName")
public class Phase {
    @Id
    private Long id;
    private String name;
    private String unit;
    @Column("Type")
    private String phaseType;
    @MappedCollection(
            idColumn = "ID"
    )
    private List<Parameter> Parameters = new LinkedList();

    public String toString() {
        return String.format("Phase{id=%-5d, name='%-40s', unit='%-15s', phaseType='%-30s', Parameters=%-10s}", this.id, this.name, this.unit, this.phaseType, this.Parameters);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getPhaseType() {
        return this.phaseType;
    }

    public List<Parameter> getParameters() {
        return this.Parameters;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public void setPhaseType(final String phaseType) {
        this.phaseType = phaseType;
    }

    public void setParameters(final List<Parameter> Parameters) {
        this.Parameters = Parameters;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Phase)) {
            return false;
        } else {
            Phase other = (Phase)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$unit = this.getUnit();
                Object other$unit = other.getUnit();
                if (this$unit == null) {
                    if (other$unit != null) {
                        return false;
                    }
                } else if (!this$unit.equals(other$unit)) {
                    return false;
                }

                Object this$phaseType = this.getPhaseType();
                Object other$phaseType = other.getPhaseType();
                if (this$phaseType == null) {
                    if (other$phaseType != null) {
                        return false;
                    }
                } else if (!this$phaseType.equals(other$phaseType)) {
                    return false;
                }

                Object this$Parameters = this.getParameters();
                Object other$Parameters = other.getParameters();
                if (this$Parameters == null) {
                    if (other$Parameters != null) {
                        return false;
                    }
                } else if (!this$Parameters.equals(other$Parameters)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Phase;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $unit = this.getUnit();
        result = result * 59 + ($unit == null ? 43 : $unit.hashCode());
        Object $phaseType = this.getPhaseType();
        result = result * 59 + ($phaseType == null ? 43 : $phaseType.hashCode());
        Object $Parameters = this.getParameters();
        result = result * 59 + ($Parameters == null ? 43 : $Parameters.hashCode());
        return result;
    }

    public Phase(final Long id, final String name, final String unit, final String phaseType, final List<Parameter> Parameters) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.phaseType = phaseType;
        this.Parameters = Parameters;
    }

    public Phase() {
    }
}
