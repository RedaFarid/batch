
package com.batch.PLCDataSource.ModBus;

import java.util.Map;


public abstract class ModbusSystem implements Runnable{
    
    protected volatile ModbusClientUpdated modbusClient;
    
    protected Map<Integer, Byte> buffer;
    protected String connectionName;
    protected String IP;
    protected int Port;
    protected byte Identifier;
    
    protected int i, j;
    
    protected int totalRefreshment = 0;

    protected final int uniteDataAddress = 120; // Means the double --> byte 240 in PLC
    protected final int totalRequiredData = 480;
    protected final int swap = totalRequiredData / uniteDataAddress;
    protected int[] intArray = new int[uniteDataAddress];

    public ModbusSystem(Map<Integer, Byte> buffer, String connectionName, String IP, int Port, byte Identifier) {
        this.buffer = buffer;
        this.connectionName = connectionName;
        this.IP = IP;
        this.Port = Port;
        this.Identifier = Identifier;
        
        modbusClient = new ModbusClientUpdated(IP, Port);
        modbusClient.setUnitIdentifier(Identifier);
    }
    
    public ModbusClientUpdated getConnection(){
        return modbusClient;
    }
    protected abstract void taskprocedure(int start, int quantity) throws Exception;
}
