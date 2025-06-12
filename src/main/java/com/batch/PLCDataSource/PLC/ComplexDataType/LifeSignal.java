package com.batch.PLCDataSource.PLC.ComplexDataType;

import com.batch.PLCDataSource.PLC.ElementaryDefinitions.Address;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.Utilities.LogIdentefires;

public class LifeSignal extends RowDataDefinition {
    public LifeSignal(String name) {
        super(name, 2, 2);
    }

    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        this.addAttribute(Life.LifeSignal, EDT.Integer, new Address(InAddress, 0), new IntegerDataType(0), true, Alarming.Disable, LogIdentefires.Error, Logging.Enable);
        this.addAttribute(Life.LifeSignal, EDT.Integer, new Address(OutAddress, 0), new IntegerDataType(0), false, Alarming.Disable, LogIdentefires.Error, Logging.Enable);
    }
}
