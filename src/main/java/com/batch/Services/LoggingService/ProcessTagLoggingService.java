package com.batch.Services.LoggingService;

import com.batch.Database.Entities.TagLog;
import com.batch.Database.Repositories.TagLogRepository;
import com.batch.PLCDataSource.PLC.ComplexDataType.Logging;
import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProcessTagLoggingService {
    private final TagLogRepository tagLogRepository;
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;
    private Map<String, RowDataDefinition> allDevices;

    public ProcessTagLoggingService(final TagLogRepository tagLogRepository, final PLCDataDefinitionFactory plcDataDefinitionFactory) {
        this.tagLogRepository = tagLogRepository;
        this.plcDataDefinitionFactory = plcDataDefinitionFactory;
    }

    @EventListener
    public void atAppReady(ApplicationReadyEvent event) {
        this.allDevices = this.plcDataDefinitionFactory.getAllDevicesDataModel();
    }

    @Scheduled(
            fixedDelay = 1000L
    )
    public void run() {
        try {
            if (this.allDevices != null) {
                this.allDevices.values().stream().flatMap((item) -> {
                    List<LogDataHolder> list = new ArrayList();
                    item.getEnableTagLogging().forEach((att, val) -> {
                        if (val.equals(Logging.Enable)) {
                            ValueObject value = item.getAllValues().get(att);
                            String name = item.getName();
                            EDT type = item.getTypes().get(att);
                            list.add(new LogDataHolder(name, att, value, type));
                        }

                    });
                    return list.stream();
                }).forEach((item) -> this.tagLogRepository.save(new TagLog(item.getName(), item.getAttribute().toString(), this.getValue(item.getValue(), item.getType()))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private double getValue(ValueObject value, EDT type) {
        double returnValue = 0.0F;
        switch (type) {
            case Boolean:
                if (((BooleanProperty) value).getValue()) {
                    returnValue = 1.0F;
                } else {
                    returnValue = 0.0F;
                }
                break;
            case Integer:
                returnValue = Double.parseDouble(String.valueOf(((IntegerProperty) value).getValue()));
                break;
            case Real:
                returnValue = Double.parseDouble(String.valueOf(((FloatProperty) value).getValue()));
        }

        return returnValue;
    }
}
