package com.batch.GUI.Alarms;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Log;
import com.batch.PLCDataSource.PLC.ComplexDataType.GeneralOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Services.LoggingService.LoggingService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class AlarmsController {

    private Map<String, RowDataDefinition> allDevices;
    private final AlarmsModel model = new AlarmsModel();

    private final LoggingService loggingService;

    public AlarmsModel getModel() {
        return model;
    }


    @Scheduled(fixedDelay = 2000)
    public void update() {
        if (model.getIsShown().getValue()) {
            final List<Log> allLogs = loggingService.getAllLogs();
            final ObservableList<Log> tableList = model.getAllAlarmsList();

            tableList.removeAll(allLogs.stream()
                    .filter(item -> !tableList.contains(item))
                    .collect(() -> tableList, ObservableList::add, ObservableList::addAll)
                    .stream()
                    .filter(tableListItem -> allLogs.stream().noneMatch(dataBaseItem -> dataBaseItem.equals(tableListItem)))
                    .collect(Collectors.toList()));
        }
    }

    @EventListener
    public void afterRefreshed(ApplicationContext.ApplicationListener event) {
        allDevices = PLCDataDefinitionFactory.getSystem().getAllDevicesDataModel();
    }

    @EventListener
    public void afterStarted(ContextStartedEvent event) {

        updateHiPressureValue();
        updateLoPressureValue();
        ((RealDataType) allDevices.get("General").getAllValues().get(GeneralOutput.HI_Air_Pressure_Limit)).addListener((observable, oldValue, newValue) -> updateHiPressureValue());
        ((RealDataType) allDevices.get("General").getAllValues().get(GeneralOutput.LO_Air_Pressure_Limit)).addListener((observable, oldValue, newValue) -> updateLoPressureValue());
    }
    private void updateLoPressureValue() {
        try {
            Platform.runLater(() -> {
                model.getAirPressureLoAlarm().setValue(String.valueOf(((RealDataType) allDevices.get("General").getAllValues().get(GeneralOutput.LO_Air_Pressure_Limit)).getValue()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateHiPressureValue() {
        try {
            Platform.runLater(() -> {
                model.getAirPressureHiAlarm().setValue(String.valueOf(((RealDataType) allDevices.get("General").getAllValues().get(GeneralOutput.HI_Air_Pressure_Limit)).getValue()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void highPressureLimitCommit() {
        ((RealDataType) allDevices.get("General").getAllValues().get(GeneralOutput.HI_Air_Pressure_Limit)).setValue(Float.parseFloat(model.getAirPressureHiAlarm().getValue()));
    }
    @Async
    public void lowPressureLimitCommit() {
        ((RealDataType) allDevices.get("General").getAllValues().get(GeneralOutput.LO_Air_Pressure_Limit)).setValue(Float.parseFloat(model.getAirPressureLoAlarm().getValue()));
    }
}
