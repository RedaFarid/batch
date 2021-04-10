package com.batch.PLCDataSource.PLC.ComplexDataType;


import com.batch.PLCDataSource.PLC.ElementaryDefinitions.Address;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Utilities.LogIdentefires;

public class Weight extends RowDataDefinition {

    

    public Weight(String name) {
        super(name, 6, 34);
    }

    @Override
    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        addAttribute(WeightInput.Low_Warning, EDT.Boolean, new Address(InAddress, 0), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightInput.Low_Alarm, EDT.Boolean, new Address(InAddress, 1), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        addAttribute(WeightInput.High_Warning, EDT.Boolean, new Address(InAddress, 2), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightInput.High_Alarm, EDT.Boolean, new Address(InAddress, 3), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        addAttribute(WeightInput.Weight, EDT.Real, new Address(InAddress + 2, 0), new RealDataType(0.0f), In, Alarming.Disable, LogIdentefires.Warning, Logging.Enable);

        addAttribute(WeightOutput.Latch_Alarms_Till_Reset, EDT.Boolean, new Address(OutAddress, 0), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(WeightOutput.Enable_Simulation, EDT.Boolean, new Address(OutAddress, 1), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(WeightOutput.Enable_Low_Pass_Filter, EDT.Boolean, new Address(OutAddress, 2), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(WeightOutput.Simulation_Value, EDT.Real, new Address(OutAddress + 2, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightOutput.Low_Pass_Coeffecient, EDT.Real, new Address(OutAddress + 6, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightOutput.Low_Warning_SP, EDT.Real, new Address(OutAddress + 10, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightOutput.Low_Alarm_Sp, EDT.Real, new Address(OutAddress + 14, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightOutput.High_Warning_SP, EDT.Real, new Address(OutAddress + 18, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightOutput.High_Alarm_SP, EDT.Real, new Address(OutAddress + 22, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightOutput.Zero, EDT.Real, new Address(OutAddress + 26, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(WeightOutput.Span, EDT.Real, new Address(OutAddress + 30, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
    }
}
