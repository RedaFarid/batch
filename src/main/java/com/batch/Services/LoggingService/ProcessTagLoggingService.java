package com.batch.Services.LoggingService;

import com.batch.ApplicationContext;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProcessTagLoggingService {

    private Map<String, RowDataDefinition> allDevices;
    private final TagLogRepository tagLogRepository;
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;

    @EventListener
    public void atStart(ApplicationContext.GraphicsInitializerEvent event){
        allDevices = plcDataDefinitionFactory.getAllDevicesDataModel();
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        try {
            if (allDevices != null) {
                allDevices.values().stream().flatMap(item -> {
                    List<LogDataHolder> list = new ArrayList<>();
                    item.getEnableTagLogging().forEach((att, val) -> {
                        if (val.equals(Logging.Enable)) {
                            ValueObject value = item.getAllValues().get(att);
                            String name = item.getName();
                            EDT type = item.getTypes().get(att);
                            list.add(new LogDataHolder(name, att, value, type));
                        }
                    });
                    return list.stream();
                }).forEach(item -> {
                    tagLogRepository.save(new TagLog(item.getName(), item.getAttribute().toString(), getValue(item.getValue(), item.getType())));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private double getValue(ValueObject value, EDT type) {
        double returnValue = 0.0;
        switch (type) {
            case Boolean:
                if (((BooleanProperty) value).getValue()) {
                    returnValue = 1.0;
                } else {
                    returnValue = 0.0;
                }
                break;
            case Integer:
                returnValue = Double.parseDouble(String.valueOf(((IntegerProperty) value).getValue()));
                break;
            case Real:
                returnValue = Double.parseDouble(String.valueOf(((FloatProperty) value).getValue()));
                break;
            default:
                break;
        }
        return returnValue;
    }
}
