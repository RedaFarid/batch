

package com.batch.PLCDataSource.ModBus;

import com.batch.Services.NotificationService.NotificationService;
import com.batch.Utilities.StringUtilsL;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javafx.beans.property.BooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModbusConnectionMonitor {
    private static final Logger log = LogManager.getLogger(ModbusConnectionMonitor.class);
    private static volatile ModbusConnectionMonitor singleton = null;
    private final BooleanProperty connectionStatus;
    private final BooleanProperty bufferSynchronized;
    private final AtomicInteger life = new AtomicInteger(1);
    private final NotificationService loggingService;

    private ModbusConnectionMonitor(BooleanProperty connectionStatus, BooleanProperty bufferSynchronized, NotificationService loggingService) {
        this.connectionStatus = connectionStatus;
        this.bufferSynchronized = bufferSynchronized;
        this.loggingService = loggingService;
    }

    public static ModbusConnectionMonitor getService(String IP, BooleanProperty connectionStatus, BooleanProperty bufferSynchronized, NotificationService loggingService) {
        synchronized(ModbusConnectionMonitor.class) {
            if (singleton == null) {
                singleton = new ModbusConnectionMonitor(connectionStatus, bufferSynchronized, loggingService);
            }
        }

        return singleton;
    }

    public synchronized void checkConnection(ModbusClientUpdated connection, String IP) throws Exception {
        try {
            if (InetAddress.getByName(IP).isReachable(1000)) {
                if (!connection.isConnected()) {
                    connection.Connect();
                    this.connectionStatus.setValue(Boolean.FALSE);
                    this.bufferSynchronized.setValue(Boolean.FALSE);
                } else {
                    this.writeToLifeSignalOnPLC(connection, this.life.getAndAdd(1));
                    this.connectionStatus.setValue(Boolean.TRUE);
                }
            } else {
                connection.Disconnect();
                this.connectionStatus.setValue(Boolean.FALSE);
                this.bufferSynchronized.setValue(Boolean.FALSE);
                this.loggingService.newErrorMessage("Modbus connection monitor", "Check connection", "Can not ping the contgroller\nCheck the ip of the controlller, the ip of the server, cables, plug, controller is Up and the port configurations");
            }
        } catch (Exception e) {
            this.connectionStatus.setValue(Boolean.FALSE);
            this.bufferSynchronized.setValue(Boolean.FALSE);
            this.DisconnectConnection(connection);
            this.loggingService.newErrorMessage("Modbus connection monitor", "Check connection", StringUtilsL.textLimiter(e.getMessage(), 40));
        }

    }

    private void writeToLifeSignalOnPLC(ModbusClientUpdated connection, int var) throws Exception {
        connection.WriteSingleRegister(0, var);
    }

    private void DisconnectConnection(ModbusClientUpdated connection) {
        try {
            connection.Disconnect();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ModbusConnectionMonitor.class.getName()).log(Level.SEVERE, (String)null, ex);
        }

    }
}
