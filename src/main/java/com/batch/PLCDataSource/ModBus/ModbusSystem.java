
package com.batch.PLCDataSource.ModBus;

import java.util.Map;

public abstract class ModbusSystem implements Runnable {
    protected volatile ModbusClientUpdated modbusClient;
    protected Map<Integer, Byte> buffer;
    protected String connectionName;
    protected String IP;
    protected int Port;
    protected byte Identifier;
    protected int i;
    protected int j;
    protected int totalRefreshment = 0;
    protected final int uniteDataAddress = 120;
    protected final int totalRequiredData = 480;
    protected final int swap = 4;
    protected int[] intArray = new int[120];

    public ModbusSystem(Map<Integer, Byte> buffer, String connectionName, String IP, int Port, byte Identifier) {
        this.buffer = buffer;
        this.connectionName = connectionName;
        this.IP = IP;
        this.Port = Port;
        this.Identifier = Identifier;
        this.modbusClient = new ModbusClientUpdated(IP, Port);
        this.modbusClient.setUnitIdentifier(Identifier);
    }

    public ModbusClientUpdated getConnection() {
        return this.modbusClient;
    }

    protected abstract void taskProcedure(int start, int quantity) throws Exception;
}
