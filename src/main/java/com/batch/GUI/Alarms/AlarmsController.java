package com.batch.GUI.Alarms;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Log;
import com.batch.PLCDataSource.PLC.ComplexDataType.GeneralOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Services.LoggingService.LoggingService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AlarmsController {
    private static final Logger log = LogManager.getLogger(AlarmsController.class);
    private final AlarmsModel model = new AlarmsModel();
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;
    private final LoggingService loggingService;
    private Map<String, RowDataDefinition> allDevices;

    public AlarmsController(final PLCDataDefinitionFactory plcDataDefinitionFactory, final LoggingService loggingService) {
        this.plcDataDefinitionFactory = plcDataDefinitionFactory;
        this.loggingService = loggingService;
    }

    public AlarmsModel getModel() {
        return this.model;
    }

    @Scheduled(
            fixedDelay = 2000L
    )
    public void update() {
        if (this.model.getIsShown().getValue()) {
            List<Log> allLogs = this.loggingService.getAllLogs();
            ObservableList<Log> tableList = this.model.getAllAlarmsList();
            tableList.removeAll((Collection) ((ObservableList) allLogs.stream().filter((item) -> !tableList.contains(item)).collect(() -> tableList, List::add, List::addAll)).stream().filter((tableListItem) -> allLogs.stream().noneMatch((dataBaseItem) -> dataBaseItem.equals(tableListItem))).collect(Collectors.toList()));
            Platform.runLater(() -> FXCollections.sort(tableList, (o1, o2) -> {
                try {
                    LocalDate date1 = o1.getDate();
                    LocalDate date2 = o2.getDate();
                    LocalTime time1 = o1.getTime();
                    LocalTime time2 = o2.getTime();
                    LocalDateTime localDateTime1 = LocalDateTime.of(date1, time1);
                    LocalDateTime localDateTime2 = LocalDateTime.of(date2, time2);
                    return localDateTime1.compareTo(localDateTime2);
                } catch (Exception var8) {
                    return 0;
                }
            }));
        }

    }

    @EventListener
    public void afterRefreshed(ApplicationContext.GraphicsInitializerEvent event) {
        this.allDevices = this.plcDataDefinitionFactory.getAllDevicesDataModel();
    }

    @EventListener
    public void afterStarted(ContextStartedEvent event) {
        this.updateHiPressureValue();
        this.updateLoPressureValue();
        ((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.HI_Air_Pressure_Limit)).addListener((observable, oldValue, newValue) -> this.updateHiPressureValue());
        ((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.LO_Air_Pressure_Limit)).addListener((observable, oldValue, newValue) -> this.updateLoPressureValue());
    }

    private void updateLoPressureValue() {
        try {
            Platform.runLater(() -> this.model.getAirPressureLoAlarm().setValue(String.valueOf(((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.LO_Air_Pressure_Limit)).getValue())));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateHiPressureValue() {
        try {
            Platform.runLater(() -> this.model.getAirPressureHiAlarm().setValue(String.valueOf(((RealDataType) this.allDevices.get("General").getAllValues().get(GeneralOutput.HI_Air_Pressure_Limit)).getValue())));
        } catch (Exception e) {
            e.printStackTrace();
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
}
