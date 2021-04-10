package com.batch.Services.LoggingService;


import com.batch.ApplicationContext;
import com.batch.Database.Entities.Log;
import com.batch.PLCDataSource.PLC.ComplexDataType.Alarming;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;
import com.batch.Utilities.LogIdentefires;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrrocessAlarmLoggingService {

    private static PrrocessAlarmLoggingService singelton = null;
    private static Map<String, RowDataDefinition> allDevices;

    private final LoggingService loggingService;

    public PrrocessAlarmLoggingService(Map<String, RowDataDefinition> allDevices) {
        this.loggingService = ApplicationContext.applicationContext.getBean(LoggingService.class);
        PrrocessAlarmLoggingService.allDevices = allDevices;
        intializaition();
    }

    public static PrrocessAlarmLoggingService getSystem(Map<String, RowDataDefinition> allDevices) {
        synchronized (PrrocessAlarmLoggingService.class) {
            if (singelton == null) {
                singelton = new PrrocessAlarmLoggingService(allDevices);
            }
        }
        return singelton;
    }

    private void intializaition() {
        allDevices.values().stream().flatMap(item -> {
            List<AlarmDataHolder> list = new ArrayList();
            item.getEnableAlarmLogging().forEach((att, val) -> {
                if (val.equals(Alarming.Enable)) {
                    ValueObject value = item.getAllValues().get(att);
                    String name = item.getName();
                    EDT type = item.getTypes().get(att);
                    LogIdentefires identefire = item.getAlarmingClass().get(att);
                    list.add(new AlarmDataHolder(name, att, value, type, identefire));
                }
            });
            return list.stream();
        }).forEach(element -> {
            switch (element.getType()) {
                case Boolean:
                    ((BooleanProperty) element.getValue()).addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (newValue) {
                                Log record = new Log(element.getIdentefire().name(), element.getName(), "Alarm : -->" + element.getAttribute().toString() + "<-- was activated [changed from 0 to 1]");
                                loggingService.LogRecord(record);
                            } else {
                                Log record = new Log(element.getIdentefire().name(), element.getName(), "Alarm : -->" + element.getAttribute().toString() + "<-- was deactivated [changed from 1 to 0]");
                                loggingService.LogRecord(record);
                            }
                        }
                    });
                    break;
                case Integer:
                    ((IntegerProperty) element.getValue()).addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            Log record = new Log(element.getIdentefire().name(), element.getName(), " Alarm : -->" + element.getAttribute().toString() + "<-- Value changed from " + oldValue + " to " + newValue);
                            loggingService.LogRecord(record);
                        }
                    });
                    break;
                case Real:
                    ((FloatProperty) element.getValue()).addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            Log record = new Log(element.getIdentefire().name(), element.getName(), " Alarm : -->" + element.getAttribute().toString() + "<-- Value changed from " + oldValue + " to " + newValue);
                            loggingService.LogRecord(record);
                        }
                    });
                    break;
                default:
                    break;
            }
        });

    }
}
