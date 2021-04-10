package com.batch.PLCDataSource.ModBus;

import com.batch.Services.LoggingService.LoggingService;
import javafx.beans.property.BooleanProperty;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModbusConnectionMonitor {

    private static volatile ModbusConnectionMonitor singelton = null;

    private BooleanProperty connectionStatus;
    private BooleanProperty bufferSunchronized;

    private AtomicInteger life = new AtomicInteger(1);

    private final LoggingService loggingService;

    private ModbusConnectionMonitor(BooleanProperty connectionStatus, BooleanProperty bufferSunchronized, LoggingService loggingService) {
        this.connectionStatus = connectionStatus;
        this.bufferSunchronized = bufferSunchronized;
        this.loggingService = loggingService;
    }

    public static ModbusConnectionMonitor getService(String IP, BooleanProperty connectionStatus, BooleanProperty bufferSunchronized, LoggingService loggingService) {
        synchronized (ModbusConnectionMonitor.class) {
            if (singelton == null) {
                singelton = new ModbusConnectionMonitor(connectionStatus, bufferSunchronized, loggingService);
            }
        }
        return singelton;
    }

    public synchronized void checkConnection(ModbusClientUpdated connection, String IP) throws Exception {
        try {
            if (InetAddress.getByName(IP).isReachable(1000)) {
                if (!connection.isConnected()) {
                    connection.Connect();
                    connectionStatus.setValue(Boolean.FALSE);
                    bufferSunchronized.setValue(Boolean.FALSE);
                } else {
                    writeToLifeSignalOnPLC(connection, life.getAndAdd(1));
                    connectionStatus.setValue(Boolean.TRUE);
                }
            } else {
                connection.Disconnect();
                connectionStatus.setValue(Boolean.FALSE);
                bufferSunchronized.setValue(Boolean.FALSE);
            }
        } catch (Exception e) {
            connectionStatus.setValue(Boolean.FALSE);
            bufferSunchronized.setValue(Boolean.FALSE);
            DisconnectConnection(connection);
            loggingService.LogRecordForException("Modbus Connection monitor", e);
        }
    }

    private void writeToLifeSignalOnPLC(ModbusClientUpdated connection, int var) throws Exception {
        connection.WriteSingleRegister(0, var);
    }

    private void DisconnectConnection(ModbusClientUpdated connection) {
        try {
            connection.Disconnect();
        } catch (IOException ex) {
            Logger.getLogger(ModbusConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
