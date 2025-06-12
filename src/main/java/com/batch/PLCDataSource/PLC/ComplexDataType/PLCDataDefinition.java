package com.batch.PLCDataSource.PLC.ComplexDataType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PLCDataDefinition {
    private Map<String, RowDataDefinition> allDevices = Collections.synchronizedMap(new LinkedHashMap());
    private int InLastAddress;
    private int OutLastAddress = 0;

    public PLCDataDefinition(final Map<String, RowDataDefinition> allDevices, final int InLastAddress, final int OutLastAddress) {
        this.allDevices = allDevices;
        this.InLastAddress = InLastAddress;
        this.OutLastAddress = OutLastAddress;
    }

    public PLCDataDefinition() {
    }

    public void addDevice(String name, RowDataDefinition device) {
        this.allDevices.put(name, device);
    }

    public int getInLastAddress() {
        return this.InLastAddress;
    }

    public void setInLastAddress(int InLastAddress) {
        this.InLastAddress = InLastAddress;
    }

    public int getOutLastAddress() {
        return this.OutLastAddress;
    }

    public void setOutLastAddress(int OutLastAddress) {
        this.OutLastAddress = OutLastAddress;
    }

    public Map<String, RowDataDefinition> getAllDevices() {
        return this.allDevices;
    }

    public void setAllDevices(final Map<String, RowDataDefinition> allDevices) {
        this.allDevices = allDevices;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PLCDataDefinition other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getInLastAddress() != other.getInLastAddress()) {
                return false;
            } else if (this.getOutLastAddress() != other.getOutLastAddress()) {
                return false;
            } else {
                Object this$allDevices = this.getAllDevices();
                Object other$allDevices = other.getAllDevices();
                if (this$allDevices == null) {
                    return other$allDevices == null;
                } else return this$allDevices.equals(other$allDevices);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PLCDataDefinition;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getInLastAddress();
        result = result * 59 + this.getOutLastAddress();
        Object $allDevices = this.getAllDevices();
        result = result * 59 + ($allDevices == null ? 43 : $allDevices.hashCode());
        return result;
    }

    public String toString() {
        Map var10000 = this.getAllDevices();
        return "PLCDataDefinition(allDevices=" + var10000 + ", InLastAddress=" + this.getInLastAddress() + ", OutLastAddress=" + this.getOutLastAddress() + ")";
    }
}
