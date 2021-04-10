package com.batch.PLCDataSource.ModBus;


import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.Services.LoggingService.LoggingService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ModBusService {


    private final BooleanProperty connectionStatus = new SimpleBooleanProperty(Boolean.FALSE);
    private final BooleanProperty bufferSynchronized = new SimpleBooleanProperty(Boolean.FALSE);

    private Map<String, RowDataDefinition> devices;

    @Autowired
    private LoggingService loggingService;

    @Autowired(required = false)
    @Qualifier("ModbusScheduler")
    private TaskScheduler scheduler;

    @EventListener
    public void initializeAndStartService(ContextStartedEvent event) {
        devices = PLCDataDefinitionFactory.getSystem().getAllDevicesDataModel();

        Map<Integer, Byte> sendBuffer = new LinkedHashMap<>();
        Map<Integer, Byte> receiveBuffer = new LinkedHashMap<>();

        Map<Integer, Byte> concurrentSendBuffer = Collections.synchronizedMap(sendBuffer);
        Map<Integer, Byte> concurrentReceiveBuffer = Collections.synchronizedMap(receiveBuffer);

        SendDataMapper sendMapper = new SendDataMapper(devices, concurrentSendBuffer, loggingService);
        RecieveDataMapper receiveMapper = new RecieveDataMapper(devices, concurrentReceiveBuffer, loggingService);
        SendBufferDataMapper sendBufferDataMapper = new SendBufferDataMapper(devices, concurrentSendBuffer, loggingService);

        byte senderID = 10;
        byte receiverID = 11;
        byte SenderReceiveID = 12;

        ModbusConnectionMonitor connectionMonitor = ModbusConnectionMonitor.getService("192.168.0.1", connectionStatus, bufferSynchronized, loggingService);
        ModbusSender sender = new ModbusSender(concurrentSendBuffer, "Sender_Connection", "192.168.0.1", 503, senderID, bufferSynchronized, connectionStatus, sendMapper, connectionMonitor, loggingService);
        ModbusReciever receiver = new ModbusReciever(concurrentReceiveBuffer, "Receiver_Connection", "192.168.0.1", 502, receiverID, bufferSynchronized, connectionStatus, receiveMapper, connectionMonitor, loggingService);
        SendBufferCreator creator = new SendBufferCreator(concurrentSendBuffer, "SenderReceive_Connection", "192.168.0.1", 504, SenderReceiveID, bufferSynchronized, connectionStatus, sendBufferDataMapper, connectionMonitor, loggingService);

        try {
            int sendingCycle = 100;
            int receivingCycle = 100;

            scheduler.scheduleWithFixedDelay(creator, 10);
            scheduler.scheduleWithFixedDelay(sender, sendingCycle);
            scheduler.scheduleWithFixedDelay(receiver, receivingCycle);

        } catch (Exception e) {
            e.printStackTrace();
            loggingService.LogRecordForException("Modbus Service", e);
        }
    }

    public BooleanProperty getConnectionStatus() {
        return connectionStatus;
    }
    public BooleanProperty getBufferSynchronized() {
        return bufferSynchronized;
    }
}
