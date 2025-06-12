
package com.batch.PLCDataSource.PLC.ComplexDataType;

import com.batch.PLCDataSource.PLC.ElementaryDefinitions.Address;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;
import com.batch.Utilities.LogIdentefires;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class RowDataDefinition {
    protected String name;
    private int InAddressOffset;
    private int OutAddressOffset;
    protected final boolean In = true;
    protected final boolean Out = false;
    private Map<RowAttripute, EDT> elementsDataType = Collections.synchronizedMap(new LinkedHashMap());
    private Map<RowAttripute, Address> elementsAddress = Collections.synchronizedMap(new LinkedHashMap());
    private Map<RowAttripute, ValueObject> elementsValue = Collections.synchronizedMap(new LinkedHashMap());
    private Map<RowAttripute, Boolean> inOutIndecator = Collections.synchronizedMap(new LinkedHashMap());
    private Map<RowAttripute, TagQuality> quality = Collections.synchronizedMap(new LinkedHashMap());
    private Map<RowAttripute, Alarming> enableAlarmLogging = Collections.synchronizedMap(new LinkedHashMap());
    private Map<RowAttripute, Logging> enableTagLogging = Collections.synchronizedMap(new LinkedHashMap());
    private Map<RowAttripute, LogIdentefires> alarmingClass = Collections.synchronizedMap(new LinkedHashMap());

    public RowDataDefinition(String name, int InAddressOffset, int OutAddressOffset) {
        this.name = name;
        this.InAddressOffset = InAddressOffset;
        this.OutAddressOffset = OutAddressOffset;
    }

    protected synchronized void addAttribute(RowAttripute name, EDT dataType, Address address, ValueObject value, boolean sendRecieve, Alarming enableAlarming, LogIdentefires alaClass, Logging enableLogging) {
        this.elementsAddress.put(name, address);
        this.elementsDataType.put(name, dataType);
        this.elementsValue.put(name, value);
        this.inOutIndecator.put(name, sendRecieve);
        this.quality.put(name, TagQuality.Bad);
        this.enableAlarmLogging.put(name, enableAlarming);
        this.enableTagLogging.put(name, enableLogging);
        this.alarmingClass.put(name, alaClass);
    }

    public synchronized void setValue(RowAttripute name, ValueObject value) {
        try {
            switch ((EDT)this.elementsDataType.get(name)) {
                case Boolean -> ((BooleanDataType)this.elementsValue.get(name)).setValue(((BooleanDataType)value).getValue());
                case Integer -> ((IntegerDataType)this.elementsValue.get(name)).setValue(((IntegerDataType)value).getValue());
                case Real -> ((RealDataType)this.elementsValue.get(name)).setValue(((RealDataType)value).getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized Map<RowAttripute, EDT> getTypes() {
        Map<RowAttripute, EDT> map = new LinkedHashMap();
        this.elementsDataType.keySet().forEach((i) -> map.put(i, (EDT)this.elementsDataType.get(i)));
        return map;
    }

    public synchronized Map<RowAttripute, ValueObject> getAllValues() {
        Map<RowAttripute, ValueObject> values = new LinkedHashMap();
        this.elementsValue.forEach((k, v) -> values.put(k, v));
        return values;
    }

    public synchronized Map<RowAttripute, Address> getAddresses() {
        Map<RowAttripute, Address> map = new LinkedHashMap();
        this.elementsDataType.keySet().forEach((i) -> map.put(i, (Address)this.elementsAddress.get(i)));
        return map;
    }

    public synchronized Map<RowAttripute, Boolean> getInOutIndecation() {
        Map<RowAttripute, Boolean> map = new LinkedHashMap();
        this.elementsDataType.keySet().forEach((i) -> map.put(i, (Boolean)this.inOutIndecator.get(i)));
        return map;
    }

    public Map<RowAttripute, Alarming> getEnableAlarmLogging() {
        return this.enableAlarmLogging;
    }

    public void setEnableAlarmLogging(Map<RowAttripute, Alarming> enableAlarmLogging) {
        this.enableAlarmLogging = enableAlarmLogging;
    }

    public Map<RowAttripute, Logging> getEnableTagLogging() {
        return this.enableTagLogging;
    }

    public void setEnableTagLogging(Map<RowAttripute, Logging> enableTagLogging) {
        this.enableTagLogging = enableTagLogging;
    }

    public Map<RowAttripute, LogIdentefires> getAlarmingClass() {
        return this.alarmingClass;
    }

    public void setAlarmingClass(Map<RowAttripute, LogIdentefires> alarmingClass) {
        this.alarmingClass = alarmingClass;
    }

    public Map<RowAttripute, TagQuality> getQuality() {
        return this.quality;
    }

    public void setQuality(Map<RowAttripute, TagQuality> quality) {
        this.quality = quality;
    }

    public synchronized String getName() {
        return this.name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized int getInAddress() {
        return this.InAddressOffset;
    }

    public synchronized void setInAddress(int InAddress) {
        this.InAddressOffset = InAddress;
    }

    public synchronized int getOutAddress() {
        return this.OutAddressOffset;
    }

    public synchronized void setOutAddress(int OutAddress) {
        this.OutAddressOffset = OutAddress;
    }

    public abstract void createNewDeviceDataModel(int InAddress, int OutAddress);
}
