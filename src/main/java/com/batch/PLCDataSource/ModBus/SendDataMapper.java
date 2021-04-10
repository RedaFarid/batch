package com.batch.PLCDataSource.ModBus;


import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.*;
import com.batch.Services.LoggingService.LoggingService;

import java.nio.ByteBuffer;
import java.util.Map;

class SendDataMapper implements Runnable {

    private Map<String, RowDataDefinition> devices;
    private Map<Integer, Byte> buffer;

    private final LoggingService loggingService;
    

    public SendDataMapper(Map<String, RowDataDefinition> definitions, Map<Integer, Byte> buffer, LoggingService loggingService) {
        this.devices = definitions;
        this.buffer = buffer;
        this.loggingService = loggingService;
    }

    private void setValueToBuffer(EDT type, Address address, ValueObject value) {
        
        if (buffer.size() > address.getByteNumber()) {
            switch (type) {
                case Boolean:
                    {
                        boolean val = ((BooleanDataType) value).getValue();
                        buffer.replace(address.getByteNumber(), setBit(buffer.get(address.getByteNumber()), address.getBitNumber(), val));
                        break;
                    }
                case Integer:
                    {
                        int val = ((IntegerDataType) value).getValue();
                        byte[] result = intToBytes(val);
                        buffer.replace(address.getByteNumber(), result[0]);
                        buffer.replace(address.getByteNumber() + 1, result[1]);
                        break;
                    }
                case Real:
                    {
                        float val = ((RealDataType) value).getValue();
                        byte[] result = floatToBytes(val);
                        buffer.replace(address.getByteNumber(), result[0]);
                        buffer.replace(address.getByteNumber() + 1, result[1]);
                        buffer.replace(address.getByteNumber() + 2, result[2]);
                        buffer.replace(address.getByteNumber() + 3, result[3]);
                        break;
                    }
                default:
                    break;
            }
        }
    }

    private byte setBit(byte _byte, int bitPosition, boolean bitValue) {
        if (bitValue) {
            return (byte) (_byte | (1 << bitPosition));
        }
        return (byte) (_byte & ~(1 << bitPosition));
    }
    
    private byte[] intToBytes(int data) {
        return new byte[]{
            (byte) ((data >> 8) & 0xff),
            (byte) ((data) & 0xff)};
    }
    
    private byte[] floatToBytes(float data) {
        return ByteBuffer.allocate(4).putFloat(data).array();
    }
    
    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        try {
            devices.forEach((name, device) -> {
                device.getAddresses().forEach((k, v) -> {
                    try {
                        if (!device.getInOutIndecation().get(k)) {
                            setValueToBuffer(device.getTypes().get(k), v, device.getAllValues().get(k));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.LogRecordForException("Modbus sender data mapper", e);
        }
    }
}
