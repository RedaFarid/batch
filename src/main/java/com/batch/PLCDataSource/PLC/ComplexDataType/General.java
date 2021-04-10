package com.batch.PLCDataSource.PLC.ComplexDataType;


import com.batch.PLCDataSource.PLC.ElementaryDefinitions.Address;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Utilities.LogIdentefires;

public class General extends RowDataDefinition {

    public General(String name) {
        super(name, 10, 10);
    }

    @Override
    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        addAttribute(GeneralInput.Air_Pressure, EDT.Real, new Address(InAddress, 0), new RealDataType(0.0f), In, Alarming.Disable, LogIdentefires.Warning, Logging.Enable);
        addAttribute(GeneralInput.Water_Pressure, EDT.Real, new Address(InAddress + 4, 0), new RealDataType(0.0f), In, Alarming.Disable, LogIdentefires.Error, Logging.Enable);
        addAttribute(GeneralInput.Mixer_1_Manual_Add_Message_Request, EDT.Boolean, new Address(InAddress + 8, 0), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralInput.Mixer_2_Manual_Add_Message_Request, EDT.Boolean, new Address(InAddress + 8, 1), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request, EDT.Boolean, new Address(InAddress + 8, 2), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request, EDT.Boolean, new Address(InAddress + 8, 3), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralInput.IPC_Fill_From_Tank_1_Message_Request, EDT.Boolean, new Address(InAddress + 8, 4), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralInput.IPC_Fill_From_Tank_2_Message_Request, EDT.Boolean, new Address(InAddress + 8, 5), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralInput.IPC_Fill_From_Tank_3_Message_Request, EDT.Boolean, new Address(InAddress + 8, 6), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralInput.HI_Air_Pressure_Alarm, EDT.Boolean, new Address(InAddress + 8, 7), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        addAttribute(GeneralInput.Lo_Air_Pressure_Alarm, EDT.Boolean, new Address(InAddress + 9, 0), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        addAttribute(GeneralInput.Over_Under_Voltage_Alarm, EDT.Boolean, new Address(InAddress + 9, 1), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        addAttribute(GeneralInput.ESD_Alarm, EDT.Boolean, new Address(InAddress + 9, 2), new BooleanDataType(false), In, Alarming.Enable, LogIdentefires.Error, Logging.Disable);
        
        addAttribute(GeneralOutput.HI_Air_Pressure_Limit, EDT.Real, new Address(OutAddress, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Enable);
        addAttribute(GeneralOutput.LO_Air_Pressure_Limit, EDT.Real, new Address(OutAddress + 4, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Warning, Logging.Enable);
        addAttribute(GeneralOutput.Make_All_Devices_In_Automatic, EDT.Boolean, new Address(OutAddress + 8, 0), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralOutput.Start_WaterTank_Fill_To_HiAlarm, EDT.Boolean, new Address(OutAddress + 8, 1), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralOutput.Mixer_1_Manual_Add_Confirmation, EDT.Boolean, new Address(OutAddress + 8, 2), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralOutput.Mixer_2_Manual_Add_Confirmation, EDT.Boolean, new Address(OutAddress + 8, 3), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralOutput.IPC_Fill_From_Mixer_1_Message_Confirmation, EDT.Boolean, new Address(OutAddress + 8, 4), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralOutput.IPC_Fill_From_Mixer_2_Message_Confirmation, EDT.Boolean, new Address(OutAddress + 8, 5), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralOutput.IPC_Fill_From_Tank_1_Message_Confirmation, EDT.Boolean, new Address(OutAddress + 8, 6), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralOutput.IPC_Fill_From_Tank_2_Message_Confirmation, EDT.Boolean, new Address(OutAddress + 8, 7), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
        addAttribute(GeneralOutput.IPC_Fill_From_Tank_3_Message_Confirmation, EDT.Boolean, new Address(OutAddress + 9, 0), new BooleanDataType(false), Out, Alarming.Enable, LogIdentefires.Info, Logging.Disable);
    }
}