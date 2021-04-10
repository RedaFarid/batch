package com.batch.PLCDataSource.PLC.ComplexDataType;

import com.batch.PLCDataSource.PLC.ElementaryDefinitions.*;
import com.batch.Utilities.LogIdentefires;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class RowDataDefinition {

    protected String name;

    private int InAddressOffset, OutAddressOffset;
    protected final boolean In = true;
    protected final boolean Out = false;

    private Map<RowAttripute, EDT> elementsDataType = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<RowAttripute, Address> elementsAddress = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<RowAttripute, ValueObject> elementsValue = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<RowAttripute, Boolean> inOutIndecator = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<RowAttripute, TagQuality> quality = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<RowAttripute, Alarming> enableAlarmLogging = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<RowAttripute, Logging> enableTagLogging = Collections.synchronizedMap(new LinkedHashMap<>());
    private Map<RowAttripute, LogIdentefires> alarmingClass = Collections.synchronizedMap(new LinkedHashMap<>());

    public RowDataDefinition(String name, int InAddressOffset, int OutAddressOffset) {
        this.name = name;
        this.InAddressOffset = InAddressOffset;
        this.OutAddressOffset = OutAddressOffset;
    }

    protected synchronized void addAttribute(RowAttripute name, EDT dataType, Address address, ValueObject value, boolean sendRecieve, Alarming enableAlarming, LogIdentefires alaClass, Logging enableLogging) {
        elementsAddress.put(name, address);
        elementsDataType.put(name, dataType);
        elementsValue.put(name, value);
        inOutIndecator.put(name, sendRecieve);
        quality.put(name, TagQuality.Bad);
        enableAlarmLogging.put(name, enableAlarming);
        enableTagLogging.put(name, enableLogging);
        alarmingClass.put(name, alaClass);
    }

    public synchronized void setValue(RowAttripute name, ValueObject value) {
        try {
            switch (elementsDataType.get(name)) {
                case Boolean:
                    ((BooleanDataType) elementsValue.get(name)).setValue(((BooleanDataType) value).getValue());
                    break;
                case Integer:
                    ((IntegerDataType) elementsValue.get(name)).setValue(((IntegerDataType) value).getValue());
                    break;
                case Real:
                    ((RealDataType) elementsValue.get(name)).setValue(((RealDataType) value).getValue());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Map<RowAttripute, EDT> getTypes() {
        Map<RowAttripute, EDT> map = new LinkedHashMap<>();
        elementsDataType.keySet().forEach(i -> {
            map.put(i, elementsDataType.get(i));
        });
        return map;
    }

    public synchronized Map<RowAttripute, ValueObject> getAllValues() {
        Map<RowAttripute, ValueObject> values = new LinkedHashMap<>();

        elementsValue.forEach((k, v) -> {
            values.put(k, v);
        });
        return values;
    }

    public synchronized Map<RowAttripute, Address> getAddresses() {
        Map<RowAttripute, Address> map = new LinkedHashMap<>();
        elementsDataType.keySet().forEach(i -> {
            map.put(i, elementsAddress.get(i));
        });
        return map;
    }

    public synchronized Map<RowAttripute, Boolean> getInOutIndecation() {
        Map<RowAttripute, Boolean> map = new LinkedHashMap<>();
        elementsDataType.keySet().forEach(i -> {
            map.put(i, inOutIndecator.get(i));
        });
        return map;
    }

    public Map<RowAttripute, Alarming> getEnableAlarmLogging() {
        return enableAlarmLogging;
    }

    public void setEnableAlarmLogging(Map<RowAttripute, Alarming> enableAlarmLogging) {
        this.enableAlarmLogging = enableAlarmLogging;
    }

    public Map<RowAttripute, Logging> getEnableTagLogging() {
        return enableTagLogging;
    }

    public void setEnableTagLogging(Map<RowAttripute, Logging> enableTagLogging) {
        this.enableTagLogging = enableTagLogging;
    }

    public Map<RowAttripute, LogIdentefires> getAlarmingClass() {
        return alarmingClass;
    }

    public void setAlarmingClass(Map<RowAttripute, LogIdentefires> alarmingClass) {
        this.alarmingClass = alarmingClass;
    }

    public Map<RowAttripute, TagQuality> getQuality() {
        return quality;
    }

    public void setQuality(Map<RowAttripute, TagQuality> quality) {
        this.quality = quality;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized int getInAddress() {
        return InAddressOffset;
    }

    public synchronized void setInAddress(int InAddress) {
        this.InAddressOffset = InAddress;
    }

    public synchronized int getOutAddress() {
        return OutAddressOffset;
    }

    public synchronized void setOutAddress(int OutAddress) {
        this.OutAddressOffset = OutAddress;
    }

    public abstract void createNewDeviceDataModel(int InAddress, int OutAddress);
}
