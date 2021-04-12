package com.batch.PLCDataSource.PLC.ComplexDataType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PLCDataDefinition {

    private Map<String, RowDataDefinition> allDevices = Collections.synchronizedMap(new LinkedHashMap<>());
    private int InLastAddress, OutLastAddress = 0;
    
    public void addDevice(String name, RowDataDefinition device) {
        allDevices.put(name, device);
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