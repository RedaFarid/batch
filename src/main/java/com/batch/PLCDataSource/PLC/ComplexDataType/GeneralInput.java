
package com.batch.PLCDataSource.PLC.ComplexDataType;


public enum GeneralInput implements RowAttripute{
    Water_Pressure,
    Air_Pressure,
    Mixer_1_Manual_Add_Message_Request,
    Mixer_2_Manual_Add_Message_Request,
    IPC_Fill_From_Mixer_1_Message_Request,
    IPC_Fill_From_Mixer_2_Message_Request,
    IPC_Fill_From_Tank_1_Message_Request,
    IPC_Fill_From_Tank_2_Message_Request,
    IPC_Fill_From_Tank_3_Message_Request,
    HI_Air_Pressure_Alarm,
    Lo_Air_Pressure_Alarm,
    Over_Under_Voltage_Alarm,
    ESD_Alarm;
}
