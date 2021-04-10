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

    @Override
    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        addAttribute(MixerInput.Running, EDT.Boolean, new Address(InAddress, 0), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(MixerInput.QControl, EDT.Boolean, new Address(InAddress, 1), new BooleanDataType(false), In, Alarming.Disable, LogIdentefires.Warning, Logging.Disable);
        addAttribute(MixerInput.Fault, EDT.Boolean, new Address(InAddress, 2), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        addAttribute(MixerInput.Output_Speed, EDT.Real, new Address(InAddress + 2, 0), new RealDataType(0.0f), In, Alarming.Disable, LogIdentefires.Error, Logging.Enable);
        addAttribute(MixerInput.Ampere_Reading, EDT.Real, new Address(InAddress + 6, 0), new RealDataType(0.0f), In, Alarming.Disable, LogIdentefires.Error, Logging.Enable);

        addAttribute(MixerOutput.Start, EDT.Boolean, new Address(OutAddress, 0), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(MixerOutput.Stop, EDT.Boolean, new Address(OutAddress, 1), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(MixerOutput.Mode, EDT.Boolean, new Address(OutAddress, 2), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(MixerOutput.Source, EDT.Boolean, new Address(OutAddress, 3), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(MixerOutput.Reset, EDT.Boolean, new Address(OutAddress, 4), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(MixerOutput.Speed_Setpoint, EDT.Real, new Address(OutAddress + 2, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Error, Logging.Enable);
    }
}
