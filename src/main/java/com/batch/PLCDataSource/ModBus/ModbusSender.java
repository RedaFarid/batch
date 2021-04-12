package com.batch.PLCDataSource.ModBus;

import com.batch.Services.LoggingService.LoggingService;
import javafx.beans.property.BooleanProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModbusSender extends ModbusSystem {

    private BooleanProperty bufferSunchronized;
    private BooleanProperty connectionStatus;
    private Runnable dataMapperTask;
    private ModbusConnectionMonitor connectionMonitorTask;

    private final LoggingService loggingService;
    
    private byte blank = 0;

    public ModbusSender(Map<Integer, Byte> buffer, String connectionName, String IP, int Port, byte Identifier, BooleanProperty bufferSunchronized, BooleanProperty connectionStatus, Runnable dataMapperTask, ModbusConnectionMonitor connectionMonitorTask, LoggingService loggingService) {
        super(buffer, connectionName, IP, Port, Identifier);
        this.bufferSunchronized = bufferSunchronized;
        this.connectionStatus = connectionStatus;
        this.dataMapperTask = dataMapperTask;
        this.connectionMonitorTask = connectionMonitorTask;
        this.loggingService = loggingService;
    }

    @Override
    public void run() {
        try {
            connectionMonitorTask.checkConnection(modbusClient, IP);
            if (connectionStatus.getValue() && bufferSunchronized.getValue()) {
                dataMapperTask.run();
                for (j = 0; j < swap; j++) {
                    taskProcedure(j * uniteDataAddress, uniteDataAddress);
                }
            }
        } catch (Exception e) {
            loggingService.LogRecordForException("Modbus Sender 1", e);
        }
    }

    @Override
    protected void taskProcedure(int start, int quantity) throws Exception {
        List<Byte> tempBuffer = super.buffer.values().stream().collect(Collectors.toList());
        for (i = 0; i < quantity; i++) {
            int address = ((j * uniteDataAddress * 2) + (i * 2));
            intArray[i] = (bytesToInteger(tempBuffer.get(address), tempBuffer.get(address + 1), blank, blank));
        }
        modbusClient.WriteMultipleRegisters(start, intArray);
    }

    private int bytesToInteger(byte data1, byte data2, byte data3, byte data4) {
        return ((data4 & 0xFF) << 24)
                | ((data3 & 0xFF) << 16)
                | ((data1 & 0xFF) << 8)
                | ((data2 & 0xFF));
    }
}
