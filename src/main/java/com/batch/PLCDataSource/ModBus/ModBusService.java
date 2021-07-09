package com.batch.PLCDataSource.ModBus;


import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.Services.LoggingService.LoggingService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private PLCDataDefinitionFactory plcDataDefinitionFactory;

    @Autowired
    private LoggingService loggingService;

    @Autowired(required = false)
    @Qualifier("ModbusScheduler")
    private TaskScheduler scheduler;

    @Value("${modbus.receivingTime}")
    private final int receivingCycle = 100;

    @Value("${modbus.sendingTime}")
    private final int sendingCycle = 100;

    @Value("${modbus.monitoringTime}")
    private final int monitorCycle = 100;

    @EventListener
    public void initializeAndStartService(ContextStartedEvent event) {
        Map<String, RowDataDefinition> devices = plcDataDefinitionFactory.getAllDevicesDataModel();

        Map<Integer, Byte> sendBuffer = new LinkedHashMap<>();
        Map<Integer, Byte> receiveBuffer = new LinkedHashMap<>();

        Map<Integer, Byte> concurrentSendBuffer = Collections.synchronizedMap(sendBuffer);
        Map<Integer, Byte> concurrentReceiveBuffer = Collections.synchronizedMap(receiveBuffer);

        SendDataMapper sendMapper = new SendDataMapper(devices, concurrentSendBuffer, loggingService);
        ReceiveDataMapper receiveMapper = new ReceiveDataMapper(devices, concurrentReceiveBuffer, loggingService);
        SendBufferDataMapper sendBufferDataMapper = new SendBufferDataMapper(devices, concurrentSendBuffer, loggingService);

        byte senderID = 10;
        byte receiverID = 11;
        byte SenderReceiveID = 12;

        ModbusConnectionMonitor connectionMonitor = ModbusConnectionMonitor.getService("192.168.0.1", connectionStatus, bufferSynchronized, loggingService);
        ModbusSender sender = new ModbusSender(concurrentSendBuffer, "Sender_Connection", "192.168.0.1", 503, senderID, bufferSynchronized, connectionStatus, sendMapper, connectionMonitor, loggingService);
        ModbusReceiver receiver = new ModbusReceiver(concurrentReceiveBuffer, "Receiver_Connection", "192.168.0.1", 502, receiverID, bufferSynchronized, connectionStatus, receiveMapper, connectionMonitor, loggingService);
        SendBufferCreator creator = new SendBufferCreator(concurrentSendBuffer, "SenderReceive_Connection", "192.168.0.1", 504, SenderReceiveID, bufferSynchronized, connectionStatus, sendBufferDataMapper, connectionMonitor, loggingService);

        try {
            scheduler.scheduleWithFixedDelay(creator, monitorCycle);
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
