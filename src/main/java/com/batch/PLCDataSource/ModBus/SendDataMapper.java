
package com.batch.PLCDataSource.ModBus;

import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.Address;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;
import com.batch.Services.NotificationService.NotificationService;
import com.batch.Utilities.StringUtilsL;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class SendDataMapper implements Runnable {
    private static final Logger log = LogManager.getLogger(SendDataMapper.class);
    private Map<String, RowDataDefinition> devices;
    private Map<Integer, Byte> buffer;
    private final NotificationService loggingService;

    public SendDataMapper(Map<String, RowDataDefinition> definitions, Map<Integer, Byte> buffer, NotificationService loggingService) {
        this.devices = definitions;
        this.buffer = buffer;
        this.loggingService = loggingService;
    }

    private void setValueToBuffer(EDT type, Address address, ValueObject value) {
        if (this.buffer.size() > address.getByteNumber()) {
            switch (type) {
                case Boolean: {
                    boolean val = ((BooleanDataType) value).getValue();
                    this.buffer.replace(address.getByteNumber(), this.setBit((Byte) this.buffer.get(address.getByteNumber()), address.getBitNumber(), val));
                    break;
                }
                case Integer: {
                    int val = ((IntegerDataType) value).getValue();
                    byte[] result = this.intToBytes(val);
                    this.buffer.replace(address.getByteNumber(), result[0]);
                    this.buffer.replace(address.getByteNumber() + 1, result[1]);
                    break;
                }
                case Real: {
                    float val = ((RealDataType) value).getValue();
                    byte[] result = this.floatToBytes(val);
                    this.buffer.replace(address.getByteNumber(), result[0]);
                    this.buffer.replace(address.getByteNumber() + 1, result[1]);
                    this.buffer.replace(address.getByteNumber() + 2, result[2]);
                    this.buffer.replace(address.getByteNumber() + 3, result[3]);
                }
            }
        }

    }

    private byte setBit(byte _byte, int bitPosition, boolean bitValue) {
        return bitValue ? (byte)(_byte | 1 << bitPosition) : (byte)(_byte & ~(1 << bitPosition));
    }

    private byte[] intToBytes(int data) {
        return new byte[]{(byte)(data >> 8 & 255), (byte)(data & 255)};
    }

    private byte[] floatToBytes(float data) {
        return ByteBuffer.allocate(4).putFloat(data).array();
    }

    public void run() {
        Thread.currentThread().setPriority(10);

        try {
            this.devices.forEach((name, device) -> device.getAddresses().forEach((k, v) -> {
                try {
                    if (!(Boolean)device.getInOutIndecation().get(k)) {
                        this.setValueToBuffer((EDT)device.getTypes().get(k), v, (ValueObject)device.getAllValues().get(k));
                    }
                } catch (Exception e) {
                    log.fatal(e, e);
                    this.loggingService.newErrorMessage("Modbus sender data mapper", "Main run inner", StringUtilsL.textLimiter(e.getMessage(), 40));
                }

            }));
        } catch (Exception e) {
            log.fatal(e, e);
            this.loggingService.newErrorMessage("Modbus sender data mapper", "Main run outer", StringUtilsL.textLimiter(e.getMessage(), 40));
        }

    }
}
