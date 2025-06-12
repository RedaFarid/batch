

package com.batch.PLCDataSource.ModBus;

import com.batch.Services.NotificationService.NotificationService;
import com.batch.Utilities.StringUtilsL;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModbusReceiver extends ModbusSystem {
    private static final Logger log = LogManager.getLogger(ModbusReceiver.class);
    private final BooleanProperty bufferSynchronized;
    private final BooleanProperty connectionStatus;
    private final Runnable dataMapperTask;
    private final ModbusConnectionMonitor connectionMonitorTask;
    private final NotificationService loggingService;

    public ModbusReceiver(Map<Integer, Byte> buffer, String connectionName, String IP, int Port, byte Identifier, BooleanProperty bufferSynchronized, BooleanProperty connectionStatus, Runnable dataMapperTask, ModbusConnectionMonitor connectionMonitorTask, NotificationService loggingService) {
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
            this.buffer.clear();
            if (this.connectionStatus.getValue() && this.bufferSynchronized.getValue()) {
                for(this.j = 0; this.j < 4; ++this.j) {
                    this.taskProcedure(this.j * 120, 120);
                }

                this.dataMapperTask.run();
            }
        } catch (Exception e) {
            this.loggingService.newErrorMessage("Modbus receiver", "Main run", StringUtilsL.textLimiter(e.getMessage(), 40));
        }

    }

    protected void taskProcedure(int start, int quantity) throws Exception {
        int[] v = this.modbusClient.ReadHoldingRegisters(start, quantity);

        for(this.i = 0; this.i < quantity; ++this.i) {
            this.buffer.put(this.j * 120 * 2 + this.i * 2, this.intToBytes(v[this.i])[0]);
            this.buffer.put(this.j * 120 * 2 + this.i * 2 + 1, this.intToBytes(v[this.i])[1]);
        }

    }

    private byte[] intToBytes(int data) {
        return new byte[]{(byte)(data >> 8 & 255), (byte)(data & 255)};
    }
}
