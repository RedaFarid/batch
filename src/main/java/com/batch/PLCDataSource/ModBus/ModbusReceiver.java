package com.batch.PLCDataSource.ModBus;

import com.batch.Services.LoggingService.LoggingService;
import javafx.beans.property.BooleanProperty;

import java.util.Map;

public class ModbusReceiver extends ModbusSystem {

    private final BooleanProperty bufferSynchronized;
    private final BooleanProperty connectionStatus;
    private final Runnable dataMapperTask;
    private final ModbusConnectionMonitor connectionMonitorTask;

    private final LoggingService loggingService;
    
    public ModbusReceiver(Map<Integer, Byte> buffer, String connectionName, String IP, int Port, byte Identifier, BooleanProperty bufferSynchronized, BooleanProperty connectionStatus, Runnable dataMapperTask, ModbusConnectionMonitor connectionMonitorTask, LoggingService loggingService) {
        super(buffer, connectionName, IP, Port, Identifier);
        this.bufferSynchronized = bufferSynchronized;
        this.connectionStatus = connectionStatus;
        this.dataMapperTask = dataMapperTask;
        this.connectionMonitorTask = connectionMonitorTask;
        this.loggingService = loggingService;
    }

    @Override
    public void run() {
        try {
            connectionMonitorTask.checkConnection(modbusClient, IP);
            buffer.clear();
            if (connectionStatus.getValue() && bufferSynchronized.getValue()) {
                for (j = 0; j < swap; j++) {
                    taskProcedure(j * uniteDataAddress, uniteDataAddress);
                }
                dataMapperTask.run();
            }
        } catch (Exception e) {
            loggingService.LogRecordForException("Modbus Receiver 1", e);
        }
    }

    @Override
    protected void taskProcedure(int start, int quantity) throws Exception {
        int[] v = modbusClient.ReadHoldingRegisters(start, quantity);
        for (i = 0; i < quantity; i++) {
            buffer.put((j * uniteDataAddress * 2) + (i * 2), intToBytes(v[i])[0]);
            buffer.put((j * uniteDataAddress * 2) + (i * 2) + 1, intToBytes(v[i])[1]);
        }
    }

    private byte[] intToBytes(int data) {

        return new byte[]{
            (byte) ((data >> 8) & 0xff),
            (byte) ((data) & 0xff),};
    }

}
