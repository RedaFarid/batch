//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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

    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        this.addAttribute(WeightInput.Low_Warning, EDT.Boolean, new Address(InAddress, 0), new BooleanDataType(false), true, Alarming.Enable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightInput.Low_Alarm, EDT.Boolean, new Address(InAddress, 1), new BooleanDataType(false), true, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        this.addAttribute(WeightInput.High_Warning, EDT.Boolean, new Address(InAddress, 2), new BooleanDataType(false), true, Alarming.Enable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightInput.High_Alarm, EDT.Boolean, new Address(InAddress, 3), new BooleanDataType(false), true, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        this.addAttribute(WeightInput.Weight, EDT.Real, new Address(InAddress + 2, 0), new RealDataType(0.0F), true, Alarming.Disable, LogIdentefires.Warning, Logging.Enable);
        this.addAttribute(WeightOutput.Latch_Alarms_Till_Reset, EDT.Boolean, new Address(OutAddress, 0), new BooleanDataType(false), false, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(WeightOutput.Enable_Simulation, EDT.Boolean, new Address(OutAddress, 1), new BooleanDataType(false), false, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(WeightOutput.Enable_Low_Pass_Filter, EDT.Boolean, new Address(OutAddress, 2), new BooleanDataType(false), false, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(WeightOutput.Simulation_Value, EDT.Real, new Address(OutAddress + 2, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightOutput.Low_Pass_Coeffecient, EDT.Real, new Address(OutAddress + 6, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightOutput.Low_Warning_SP, EDT.Real, new Address(OutAddress + 10, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightOutput.Low_Alarm_Sp, EDT.Real, new Address(OutAddress + 14, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightOutput.High_Warning_SP, EDT.Real, new Address(OutAddress + 18, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightOutput.High_Alarm_SP, EDT.Real, new Address(OutAddress + 22, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightOutput.Zero, EDT.Real, new Address(OutAddress + 26, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(WeightOutput.Span, EDT.Real, new Address(OutAddress + 30, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
    }
}
