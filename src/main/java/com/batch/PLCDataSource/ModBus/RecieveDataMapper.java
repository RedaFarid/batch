package com.batch.PLCDataSource.ModBus;

import com.batch.Database.Entities.Log;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.*;
import com.batch.Services.LoggingService.LoggingService;
import com.batch.Utilities.LogIdentefires;

import java.nio.ByteBuffer;
import java.util.Map;

class RecieveDataMapper implements Runnable {

    private Map<String, RowDataDefinition> devices;
    private Map<Integer, Byte> buffer;

    private final LoggingService loggingService;

    public RecieveDataMapper(Map<String, RowDataDefinition> definitions, Map<Integer, Byte> buffer, LoggingService loggingService) {
        this.devices = definitions;
        this.buffer = buffer;
        this.loggingService = loggingService;
    }

    private ValueObject getValueFromBuffer(EDT type, Address address) {
        try {
            if (buffer.size() > address.getByteNumber()) {
                switch (type) {
                    case Boolean: {
                        byte x = buffer.get(address.getByteNumber());
                        boolean val = getBit(x, address.getBitNumber());
                        return new BooleanDataType(val);
                    }
                    case Integer: {
                        int val = getInteger(buffer.get(address.getByteNumber()), buffer.get(address.getByteNumber() + 1));
                        return new IntegerDataType(val);
                    }
                    case Real: {
                        float val = getReal(buffer.get(address.getByteNumber()), buffer.get(address.getByteNumber() + 1), buffer.get(address.getByteNumber() + 2), buffer.get(address.getByteNumber() + 3));
                        return new RealDataType(val);
                    }
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean getBit(byte n, int k) {
        return (((n >> k) & 1) == 1);
    }

    private int getInteger(byte byte0, byte byte1) {
        return ((byte0 & 0xff) << 8) | (byte1 & 0xff);
    }

    private float getReal(byte byte0, byte byte1, byte byte2, byte byte3) {
        byte[] bytes = {byte0, byte1, byte2, byte3};
        return ByteBuffer.wrap(bytes).getFloat();
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        try {
            devices.forEach((name, device) -> {
                device.getAddresses().forEach((k, v) -> {
                    if (device.getInOutIndecation().get(k)) {
                        ValueObject value = getValueFromBuffer(device.getTypes().get(k), v);
                        if (value == null) {
                            Log log = new Log(LogIdentefires.System.name(), "Recieve data mapper : Value = null ");
                            log.setSource("Recieve data mapper");
                            loggingService.LogRecord(log);
                        } else {
                            device.setValue(k, value);
                        }
                    }
                });
            });
        } catch (Exception e) {
            loggingService.LogRecordForException("Modbus reciever data mapper", e);
        }
    }
}
