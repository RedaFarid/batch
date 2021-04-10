package com.batch.PLCDataSource.ModBus;

import java.util.Map;

import com.batch.Services.LoggingService.LoggingService;
import javafx.beans.property.BooleanProperty;

public class SendBufferCreator extends ModbusSystem {

    private BooleanProperty bufferSunchronized;
    private BooleanProperty connectionStatus;
    private Runnable dataMapperTask;
    private ModbusConnectionMonitor connectionMonitorTask;

    private final LoggingService loggingService;

    public SendBufferCreator(Map<Integer, Byte> buffer, String connectionName, String IP, int Port, byte Identifier, BooleanProperty bufferSunchronized, BooleanProperty connectionStatus, Runnable dataMapperTask, ModbusConnectionMonitor connectionMonitorTask, LoggingService loggingService) {
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
            if (connectionStatus.getValue()) {
                if (!bufferSunchronized.getValue()) {
                    buffer.clear();
                    for (j = 0; j < swap; j++) {
                        taskprocedure(j * uniteDataAddress, uniteDataAddress);
                    }
                    bufferSunchronized.setValue(Boolean.TRUE);
                    dataMapperTask.run();
                }
            }
        } catch (Exception e) {
            loggingService.LogRecordForException("Creator : Disconnecting modbus connection because of error to get data", e);
        }
    }

    @Override
    protected void taskprocedure(int start, int quantity) throws Exception {
        int[] v = modbusClient.ReadHoldingRegisters(start, quantity);
        for (i = 0; i < quantity; i++) {
            buffer.put((j * uniteDataAddress * 2) + (i * 2), intToBytes(v[i])[0]);
            buffer.put((j * uniteDataAddress * 2) + (i * 2) + 1, intToBytes(v[i])[1]);
        }
        
        
    }

    private byte[] intToBytes(int data) {
        return new byte[]{
            (byte) ((data >> 8) & 0xff),
            (byte) ((data) & 0xff)};
    }
}