
package com.batch.Services.LoggingService;


import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;

public class LogDataHolder {
    String name;
    RowAttripute attribute;
    ValueObject value;
    EDT type;

    public LogDataHolder(String name, RowAttripute attribute, ValueObject value, EDT type) {
        this.name = name;
        this.attribute = attribute;
        this.value = value;
        this.type = type;
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
    
}
