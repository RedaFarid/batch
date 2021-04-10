package com.batch.PLCDataSource.PLC.ComplexDataType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PLCDataDefinition {

    private Map<String, RowDataDefinition> allDevices = Collections.synchronizedMap(new LinkedHashMap<>());
    private int InLastAddress, OutLastAddress = 0;

    public PLCDataDefinition() {
    }
    
    public void addDevice(String name, RowDataDefinition device) {
        allDevices.put(name, device);
    }

    public Map<String, RowDataDefinition> getAllDevices() {
        return allDevices;
    }

    public void setAllDevices(Map<String, RowDataDefinition> allDevices) {
        this.allDevices = allDevices;
    }

    public int getInLastAddress() {
        return InLastAddress;
    }

    public void setInLastAddress(int InLastAddress) {
        this.InLastAddress = InLastAddress;
    }

    public int getOutLastAddress() {
        return OutLastAddress;
    }

    public void setOutLastAddress(int OutLastAddress) {
        this.OutLastAddress = OutLastAddress;
    }
    
}