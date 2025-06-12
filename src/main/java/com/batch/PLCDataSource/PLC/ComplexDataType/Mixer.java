package com.batch.PLCDataSource.PLC.ComplexDataType;

import com.batch.PLCDataSource.PLC.ElementaryDefinitions.Address;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Utilities.LogIdentefires;

public class Mixer extends RowDataDefinition {
    public Mixer(String name) {
        super(name, 10, 6);
    }

    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        this.addAttribute(MixerInput.Running, EDT.Boolean, new Address(InAddress, 0), new BooleanDataType(false), true, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(MixerInput.QControl, EDT.Boolean, new Address(InAddress, 1), new BooleanDataType(false), true, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        this.addAttribute(MixerInput.Fault, EDT.Boolean, new Address(InAddress, 2), new BooleanDataType(false), true, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        this.addAttribute(MixerInput.Output_Speed, EDT.Real, new Address(InAddress + 2, 0), new RealDataType(0.0F), true, Alarming.Disable, LogIdentefires.Error, Logging.Enable);
        this.addAttribute(MixerInput.Ampere_Reading, EDT.Real, new Address(InAddress + 6, 0), new RealDataType(0.0F), true, Alarming.Disable, LogIdentefires.Error, Logging.Enable);
        this.addAttribute(MixerOutput.Start, EDT.Boolean, new Address(OutAddress, 0), new BooleanDataType(false), false, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(MixerOutput.Stop, EDT.Boolean, new Address(OutAddress, 1), new BooleanDataType(false), false, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(MixerOutput.Mode, EDT.Boolean, new Address(OutAddress, 2), new BooleanDataType(false), false, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(MixerOutput.Source, EDT.Boolean, new Address(OutAddress, 3), new BooleanDataType(false), false, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(MixerOutput.Reset, EDT.Boolean, new Address(OutAddress, 4), new BooleanDataType(false), false, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(MixerOutput.Speed_Setpoint, EDT.Real, new Address(OutAddress + 2, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Error, Logging.Enable);
    }
}
