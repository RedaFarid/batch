package com.batch.PLCDataSource.ModBus;

import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.Services.LoggingService.MessageLoggingService;
import com.batch.Services.NotificationService.BackGroundServices;
import com.batch.Services.NotificationService.NotificationService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ModBusService {
    @Getter
    private final BooleanProperty connectionStatus;
    private final BooleanProperty bufferSynchronized;
    @Value("${modbus.receivingTime}")
    private final int receivingCycle;
    @Value("${modbus.sendingTime}")
    private final int sendingCycle;
    @Value("${modbus.monitoringTime}")
    private final int monitorCycle;
    @Autowired
    private PLCDataDefinitionFactory plcDataDefinitionFactory;
    @Autowired
    @BackGroundServices
    private NotificationService notificationService;
    @Autowired(required = false)
    @Qualifier("ModbusScheduler")
    private TaskScheduler scheduler;

    @Autowired
    private MessageLoggingService log;

    public ModBusService() {
        this.connectionStatus = new SimpleBooleanProperty(Boolean.FALSE);
        this.bufferSynchronized = new SimpleBooleanProperty(Boolean.FALSE);
        this.receivingCycle = 100;
        this.sendingCycle = 100;
        this.monitorCycle = 100;
    }

    @Async
    @EventListener
    public void initializeAndStartService(ContextRefreshedEvent event) {
        Map<String, RowDataDefinition> devices = this.plcDataDefinitionFactory.getAllDevicesDataModel();
        Map<Integer, Byte> sendBuffer = new LinkedHashMap<>();
        Map<Integer, Byte> receiveBuffer = new LinkedHashMap<>();
        Map<Integer, Byte> concurrentSendBuffer = Collections.synchronizedMap(sendBuffer);
        Map<Integer, Byte> concurrentReceiveBuffer = Collections.synchronizedMap(receiveBuffer);
        SendDataMapper sendMapper = new SendDataMapper(devices, concurrentSendBuffer, this.notificationService);
        ReceiveDataMapper receiveMapper = new ReceiveDataMapper(devices, concurrentReceiveBuffer, this.notificationService);
        SendBufferDataMapper sendBufferDataMapper = new SendBufferDataMapper(devices, concurrentSendBuffer, this.notificationService);
        byte senderID = 10;
        byte receiverID = 11;
        byte SenderReceiveID = 12;
        ModbusConnectionMonitor connectionMonitor = ModbusConnectionMonitor.getService("192.168.0.1", this.connectionStatus, this.bufferSynchronized, this.notificationService);
        ModbusSender sender = new ModbusSender(concurrentSendBuffer, "Sender_Connection", "192.168.0.1", 503, senderID, this.bufferSynchronized, this.connectionStatus, sendMapper, connectionMonitor, this.notificationService);
        ModbusReceiver receiver = new ModbusReceiver(concurrentReceiveBuffer, "Receiver_Connection", "192.168.0.1", 502, receiverID, this.bufferSynchronized, this.connectionStatus, receiveMapper, connectionMonitor, this.notificationService);
        SendBufferCreator creator = new SendBufferCreator(concurrentSendBuffer, "SenderReceive_Connection", "192.168.0.1", 504, SenderReceiveID, this.bufferSynchronized, this.connectionStatus, sendBufferDataMapper, connectionMonitor, this.notificationService);

        try {
            this.scheduler.scheduleWithFixedDelay(creator, 100L);
            this.scheduler.scheduleWithFixedDelay(sender, 100L);
            this.scheduler.scheduleWithFixedDelay(receiver, 100L);
        } catch (Exception e) {
            log.logExcption("ModbusService [initializeAndStartService] ", e);
//            this.notificationService.newErrorMessage("Modbus Service", "Main run", StringUtilsL.textLimiter(e.getMessage(), 40));
        }

        this.bufferSynchronized.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                log.system("Buffer is synchronized");
//                this.notificationService.newErrorMessage("Modbus connection monitor", "Check connection", "Buffer is synchronized");
            } else {
                log.system("Buffer is synchronized");
//                this.notificationService.newErrorMessage("Modbus connection monitor", "Check connection", "Buffer is not synchronized");
            }

        });
    }
}
