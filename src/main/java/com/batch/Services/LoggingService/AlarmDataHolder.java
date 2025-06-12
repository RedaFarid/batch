//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.batch.Services.LoggingService;

import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;
import com.batch.Utilities.LogIdentefires;

public class AlarmDataHolder {
    private String name;
    private RowAttripute attribute;
    private ValueObject value;
    private EDT type;
    private LogIdentefires identifier;

    public AlarmDataHolder(final String name, final RowAttripute attribute, final ValueObject value, final EDT type, final LogIdentefires identifier) {
        this.name = name;
        this.attribute = attribute;
        this.value = value;
        this.type = type;
        this.identifier = identifier;
    }

    public AlarmDataHolder() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public RowAttripute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(final RowAttripute attribute) {
        this.attribute = attribute;
    }

    public ValueObject getValue() {
        return this.value;
    }

    public void setValue(final ValueObject value) {
        this.value = value;
    }

    public EDT getType() {
        return this.type;
    }

    public void setType(final EDT type) {
        this.type = type;
    }

    public LogIdentefires getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(final LogIdentefires identifier) {
        this.identifier = identifier;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AlarmDataHolder other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$attribute = this.getAttribute();
                Object other$attribute = other.getAttribute();
                if (this$attribute == null) {
                    if (other$attribute != null) {
                        return false;
                    }
                } else if (!this$attribute.equals(other$attribute)) {
                    return false;
                }

                Object this$value = this.getValue();
                Object other$value = other.getValue();
                if (this$value == null) {
                    if (other$value != null) {
                        return false;
                    }
                } else if (!this$value.equals(other$value)) {
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

                Object this$identifier = this.getIdentifier();
                Object other$identifier = other.getIdentifier();
                if (this$identifier == null) {
                    return other$identifier == null;
                } else return this$identifier.equals(other$identifier);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AlarmDataHolder;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $attribute = this.getAttribute();
        result = result * 59 + ($attribute == null ? 43 : $attribute.hashCode());
        Object $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $identifier = this.getIdentifier();
        result = result * 59 + ($identifier == null ? 43 : $identifier.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getName();
        return "AlarmDataHolder(name=" + var10000 + ", attribute=" + this.getAttribute() + ", value=" + this.getValue() + ", type=" + this.getType() + ", identifier=" + this.getIdentifier() + ")";
    }
}
