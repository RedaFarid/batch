
package com.batch.PLCDataSource.PLC.ComplexDataType;


public enum GeneralOutput implements RowAttripute {
    HI_Air_Pressure_Limit,
    LO_Air_Pressure_Limit,
    Make_All_Devices_In_Automatic,
    Start_WaterTank_Fill_To_HiAlarm,
    Mixer_1_Manual_Add_Confirmation,
    Mixer_2_Manual_Add_Confirmation,
    IPC_Fill_From_Mixer_1_Message_Confirmation,
    IPC_Fill_From_Mixer_2_Message_Confirmation,
    IPC_Fill_From_Tank_1_Message_Confirmation,
    IPC_Fill_From_Tank_2_Message_Confirmation,
    IPC_Fill_From_Tank_3_Message_Confirmation;
}
