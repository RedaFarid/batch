
package com.batch.Services.LoggingService;


import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;
import com.batch.Utilities.LogIdentefires;

public class AlarmDataHolder {
    String name;
    RowAttripute attribute;
    ValueObject value;
    EDT type;
    LogIdentefires identefire;

    public AlarmDataHolder(String name, RowAttripute attribute, ValueObject value, EDT type, LogIdentefires identefire) {
        this.name = name;
        this.attribute = attribute;
        this.value = value;
        this.type = type;
        this.identefire = identefire;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RowAttripute getAttribute() {
        return attribute;
    }

    public void setAttribute(RowAttripute attribute) {
        this.attribute = attribute;
    }

    public ValueObject getValue() {
        return value;
    }

    public void setValue(ValueObject value) {
        this.value = value;
    }

    public EDT getType() {
        return type;
    }

    public void setType(EDT type) {
        this.type = type;
    }

    public LogIdentefires getIdentefire() {
        return identefire;
    }

    public void setIdentefire(LogIdentefires identefire) {
        this.identefire = identefire;
    }

}
