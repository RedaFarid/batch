
package com.batch.Database.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("PhasesTypesParameters")
public class Parameter {
    @Id
    private Long pid;
    @Column("ParameterName")
    private String name;
    @Column("ParameterType")
    private String type;
    private Long id;
    @Transient
    private Phase phase;

    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String toString() {
        return String.format("Parameter{id=%-10d, name='%-10s', type='%-5s}", this.pid, this.name, this.type);
    }

    public Long getPid() {
        return this.pid;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public Long getId() {
        return this.id;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public void setPid(final Long pid) {
        this.pid = pid;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setPhase(final Phase phase) {
        this.phase = phase;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Parameter)) {
            return false;
        } else {
            Parameter other = (Parameter)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$pid = this.getPid();
                Object other$pid = other.getPid();
                if (this$pid == null) {
                    if (other$pid != null) {
                        return false;
                    }
                } else if (!this$pid.equals(other$pid)) {
                    return false;
                }

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

                Object this$type = this.getType();
                Object other$type = other.getType();
                if (this$type == null) {
                    if (other$type != null) {
                        return false;
                    }
                } else if (!this$type.equals(other$type)) {
                    return false;
                }

                Object this$phase = this.getPhase();
                Object other$phase = other.getPhase();
                if (this$phase == null) {
                    if (other$phase != null) {
                        return false;
                    }
                } else if (!this$phase.equals(other$phase)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Parameter;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $pid = this.getPid();
        result = result * 59 + ($pid == null ? 43 : $pid.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $phase = this.getPhase();
        result = result * 59 + ($phase == null ? 43 : $phase.hashCode());
        return result;
    }

    public Parameter(final Long pid, final String name, final String type, final Long id, final Phase phase) {
        this.pid = pid;
        this.name = name;
        this.type = type;
        this.id = id;
        this.phase = phase;
    }

    public Parameter() {
    }
}
