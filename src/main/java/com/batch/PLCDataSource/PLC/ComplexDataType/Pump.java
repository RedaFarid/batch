package com.batch.PLCDataSource.PLC.ComplexDataType;

import com.batch.PLCDataSource.PLC.ElementaryDefinitions.Address;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.Utilities.LogIdentefires;

public class Pump extends RowDataDefinition {

    

    public Pump(String name) {
        super(name, 2, 4);
    }

    @Override
    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        addAttribute(PumpInput.Running, EDT.Boolean, new Address(InAddress, 0), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(PumpInput.QControl, EDT.Boolean, new Address(InAddress, 1), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(PumpInput.Feedback, EDT.Boolean, new Address(InAddress, 2), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(PumpInput.Fault, EDT.Boolean, new Address(InAddress, 3), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);

        addAttribute(PumpOutput.Start, EDT.Boolean, new Address(OutAddress, 0), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(PumpOutput.Stop, EDT.Boolean, new Address(OutAddress, 1), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(PumpOutput.Mode, EDT.Boolean, new Address(OutAddress, 2), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(PumpOutput.Source, EDT.Boolean, new Address(OutAddress, 3), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(PumpOutput.Reset, EDT.Boolean, new Address(OutAddress, 4), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(PumpOutput.Enable_Monitoring, EDT.Boolean, new Address(OutAddress, 5), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(PumpOutput.Monitoring_Time, EDT.Integer, new Address(OutAddress + 2, 0), new IntegerDataType(0), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
    }
}
