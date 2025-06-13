package com.batch.Services.LoggingService;

import com.batch.Database.Entities.Log;
import com.batch.PLCDataSource.PLC.ComplexDataType.Alarming;
import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;
import com.batch.Utilities.LogIdentefires;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessAlarmLoggingService {
    private final MessageLoggingService messageLoggingService;
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;

    public ProcessAlarmLoggingService(final MessageLoggingService messageLoggingService, final PLCDataDefinitionFactory plcDataDefinitionFactory) {
        this.messageLoggingService = messageLoggingService;
        this.plcDataDefinitionFactory = plcDataDefinitionFactory;
    }

    @EventListener
    private void initialization(ContextStartedEvent event) {
        this.plcDataDefinitionFactory.getAllDevicesDataModel().values().stream().flatMap((item) -> {
            List<AlarmDataHolder> list = new ArrayList();
            item.getEnableAlarmLogging().forEach((att, val) -> {
                if (val.equals(Alarming.Enable)) {
                    ValueObject value = item.getAllValues().get(att);
                    String name = item.getName();
                    EDT type = item.getTypes().get(att);
                    LogIdentefires identifier = item.getAlarmingClass().get(att);
                    list.add(new AlarmDataHolder(name, att, value, type, identifier));
                }

            });
            return list.stream();
        }).forEach((element) -> {
            switch (element.getType()) {
                case Boolean -> ((BooleanProperty) element.getValue()).addListener((observable, oldValue, newValue) -> {
                    Log record;
                    if (newValue) {
                        record = new Log(element.getIdentifier().name(), element.getName(), "Alarm : -->" + element.getAttribute().toString() + "<-- was activated [changed from 0 to 1]");
                    } else {
                        record = new Log(element.getIdentifier().name(), element.getName(), "Alarm : -->" + element.getAttribute().toString() + "<-- was deactivated [changed from 1 to 0]");
                    }

                    this.messageLoggingService.logEvent(record);
                });
                case Integer -> ((IntegerProperty) element.getValue()).addListener((observable, oldValue, newValue) -> {
                    Log record = new Log(element.getIdentifier().name(), element.getName(), " Alarm : -->" + element.getAttribute().toString() + "<-- Value changed from " + oldValue + " to " + newValue);
                    this.messageLoggingService.logEvent(record);
                });
                case Real -> ((FloatProperty) element.getValue()).addListener((observable, oldValue, newValue) -> {
                    Log record = new Log(element.getIdentifier().name(), element.getName(), " Alarm : -->" + element.getAttribute().toString() + "<-- Value changed from " + oldValue + " to " + newValue);
                    this.messageLoggingService.logEvent(record);
                });
            }

        });
    }
}
