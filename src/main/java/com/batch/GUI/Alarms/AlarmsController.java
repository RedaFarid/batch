package com.batch.GUI.Alarms;

import com.batch.Database.Entities.Log;
import com.batch.PLCDataSource.PLC.ComplexDataType.GeneralOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Services.LoggingService.MessageLoggingService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class AlarmsController {
    @Getter
    private final AlarmsModel model = new AlarmsModel();
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;
    private final MessageLoggingService log;
    private Map<String, RowDataDefinition> allDevices;

    public AlarmsController(final PLCDataDefinitionFactory plcDataDefinitionFactory, final MessageLoggingService log) {
        this.plcDataDefinitionFactory = plcDataDefinitionFactory;
        this.log = log;
    }

    @Scheduled(fixedDelay = 2000L)
    public void update() {
        if (this.model.viewIsShown().getValue()) {
            LinkedList<Log> allLogs = this.log.getAllLogs();
            ObservableList<Log> tableList = this.model.getAllAlarmsList();

            tableList.removeIf(log -> !allLogs.contains(log));
            for (Log log : allLogs) {
                if (!tableList.contains(log)) {
                    tableList.add(log);
                }
            }
        }

    }

    @EventListener
    public void afterStarted(ContextStartedEvent event) {
        this.allDevices = this.plcDataDefinitionFactory.getAllDevicesDataModel();
        this.updateHiPressureValue();
        this.updateLoPressureValue();
        ((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.HI_Air_Pressure_Limit)).addListener((observable, oldValue, newValue) -> this.updateHiPressureValue());
        ((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.LO_Air_Pressure_Limit)).addListener((observable, oldValue, newValue) -> this.updateLoPressureValue());
    }

    private void updateLoPressureValue() {
        try {
            Platform.runLater(() -> this.model.getAirPressureLoAlarm().setValue(String.valueOf(((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.LO_Air_Pressure_Limit)).getValue())));
        } catch (Exception e) {
            log.logExcption("AlarmController [updateLoPressureValue]",e);
        }

    }

    private void updateHiPressureValue() {
        try {
            Platform.runLater(() -> this.model.getAirPressureHiAlarm().setValue(String.valueOf(((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.HI_Air_Pressure_Limit)).getValue())));
        } catch (Exception e) {
            log.logExcption("AlarmController [updateHiPressureValue]",e);
        }

    }

    @Async
    public void highPressureLimitCommit() {
        ((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.HI_Air_Pressure_Limit)).setValue(Float.parseFloat(this.model.getAirPressureHiAlarm().getValue()));
    }

    @Async
    public void lowPressureLimitCommit() {
        ((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.LO_Air_Pressure_Limit)).setValue(Float.parseFloat(this.model.getAirPressureLoAlarm().getValue()));
    }

    public void refresh() {
        this.model.getAllAlarmsList().clear();
    }
}
