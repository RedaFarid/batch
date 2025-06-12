package com.batch.PLCDataSource.ModBus;

import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.*;
import com.batch.Services.NotificationService.NotificationService;
import com.batch.Utilities.StringUtilsL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Map;

class SendBufferDataMapper implements Runnable {
    private static final Logger log = LogManager.getLogger(SendBufferDataMapper.class);
    private final Map<String, RowDataDefinition> devices;
    private final Map<Integer, Byte> buffer;
    private final NotificationService loggingService;

    public SendBufferDataMapper(Map<String, RowDataDefinition> definitions, Map<Integer, Byte> buffer, NotificationService loggingService) {
        this.devices = definitions;
        this.buffer = buffer;
        this.loggingService = loggingService;
    }

    private ValueObject getValueFromBuffer(EDT type, Address address) {
        if (this.buffer.size() > address.getByteNumber()) {
            switch (type) {
                case Boolean: {
                    byte x = this.buffer.get(address.getByteNumber());
                    boolean val = this.getBit(x, address.getBitNumber());
                    return new BooleanDataType(val);
                }
                case Integer: {
                    int val = this.getInteger(this.buffer.get(address.getByteNumber()), this.buffer.get(address.getByteNumber() + 1));
                    return new IntegerDataType(val);
                }
                case Real: {
                    float val = this.getReal(this.buffer.get(address.getByteNumber()), this.buffer.get(address.getByteNumber() + 1), this.buffer.get(address.getByteNumber() + 2), this.buffer.get(address.getByteNumber() + 3));
                    return new RealDataType(val);
                }
            }
        }


        return null;
    }

    private boolean getBit(byte n, int k) {
        return (n >> k & 1) == 1;
    }

    private int getInteger(byte byte0, byte byte1) {
        return (byte0 & 255) << 8 | byte1 & 255;
    }

    private float getReal(byte byte0, byte byte1, byte byte2, byte byte3) {
        byte[] bytes = new byte[]{byte0, byte1, byte2, byte3};
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public void run() {
        try {
            this.devices.forEach((name, device) -> device.getAddresses().forEach((k, v) -> {
                if (!(Boolean) device.getInOutIndecation().get(k)) {
                    ValueObject value = this.getValueFromBuffer(device.getTypes().get(k), v);
                    if (value == null) {
                        this.loggingService.newErrorMessage("Modbus send buffer data mapper", "Main run", "Receive data mapper : Value = null " + name + " \n" + device);
                    } else {
                        device.setValue(k, value);
                    }
                }

            }));
        } catch (Exception e) {
            log.fatal(e, e);
            this.loggingService.newErrorMessage("Modbus send buffer data mapper", "Main run", StringUtilsL.textLimiter(e.getMessage(), 40));
        }

    }
}
