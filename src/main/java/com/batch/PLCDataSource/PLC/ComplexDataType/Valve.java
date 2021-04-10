package com.batch.PLCDataSource.PLC.ComplexDataType;

import com.batch.PLCDataSource.PLC.ElementaryDefinitions.Address;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.Utilities.LogIdentefires;

public class Valve extends RowDataDefinition {

    
    public Valve(String name) {
        super(name, 2, 4);
    }

    @Override
    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        addAttribute(ValveInput.Opened_Closed, EDT.Boolean, new Address(InAddress, 0), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(ValveInput.QOpen, EDT.Boolean, new Address(InAddress, 1), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(ValveInput.QClose, EDT.Boolean, new Address(InAddress , 2), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(ValveInput.FB_Open, EDT.Boolean, new Address(InAddress , 3), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(ValveInput.FB_Close, EDT.Boolean, new Address(InAddress , 4), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(ValveInput.Fault, EDT.Boolean, new Address(InAddress , 5), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);

        addAttribute(ValveOutput.Open, EDT.Boolean, new Address(OutAddress, 0), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(ValveOutput.Close, EDT.Boolean, new Address(OutAddress, 1), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(ValveOutput.Mode, EDT.Boolean, new Address(OutAddress, 2), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(ValveOutput.Reset, EDT.Boolean, new Address(OutAddress, 3), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(ValveOutput.Source, EDT.Boolean, new Address(OutAddress, 4), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(ValveOutput.Enable_Monitoring, EDT.Boolean, new Address(OutAddress, 5), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(ValveOutput.Monitoring_Time, EDT.Integer, new Address(OutAddress + 2, 0), new IntegerDataType(0), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
    }
}
