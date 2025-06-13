package com.batch.PLCDataSource.ModBus;

import com.batch.Services.NotificationService.NotificationService;
import com.batch.Utilities.StringUtilsL;
import javafx.beans.property.BooleanProperty;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModbusSender extends ModbusSystem {
    private final BooleanProperty bufferSynchronized;
    private final BooleanProperty connectionStatus;
    private final Runnable dataMapperTask;
    private final ModbusConnectionMonitor connectionMonitorTask;
    private final NotificationService loggingService;
    private final byte blank = 0;

    public ModbusSender(Map<Integer, Byte> buffer, String connectionName, String IP, int Port, byte Identifier, BooleanProperty bufferSynchronized, BooleanProperty connectionStatus, Runnable dataMapperTask, ModbusConnectionMonitor connectionMonitorTask, NotificationService loggingService) {
        super(buffer, connectionName, IP, Port, Identifier);
        this.bufferSynchronized = bufferSynchronized;
        this.connectionStatus = connectionStatus;
        this.dataMapperTask = dataMapperTask;
        this.connectionMonitorTask = connectionMonitorTask;
        this.loggingService = loggingService;
    }

    public void run() {
        try {
            this.connectionMonitorTask.checkConnection(this.modbusClient, this.IP);
            if (this.connectionStatus.getValue() && this.bufferSynchronized.getValue()) {
                this.dataMapperTask.run();

                for (this.j = 0; this.j < 4; ++this.j) {
                    this.taskProcedure(this.j * 120, 120);
                }
            }
        } catch (Exception e) {
            this.loggingService.newErrorMessage("Modbus Sender 1", "Main run", StringUtilsL.textLimiter(e.getMessage(), 40));
        }

    }

    protected void taskProcedure(int start, int quantity) throws Exception {
        List<Byte> tempBuffer = new ArrayList<>(super.buffer.values());

        for (this.i = 0; this.i < quantity; ++this.i) {
            int address = this.j * 120 * 2 + this.i * 2;
            this.intArray[this.i] = this.bytesToInteger(tempBuffer.get(address), tempBuffer.get(address + 1), this.blank, this.blank);
        }

        this.modbusClient.WriteMultipleRegisters(start, this.intArray);
    }

    private int bytesToInteger(byte data1, byte data2, byte data3, byte data4) {
        return (data4 & 255) << 24 | (data3 & 255) << 16 | (data1 & 255) << 8 | data2 & 255;
    }
}
