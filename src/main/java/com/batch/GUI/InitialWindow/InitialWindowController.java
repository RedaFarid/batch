package com.batch.GUI.InitialWindow;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.BatchControllerData;
import com.batch.Database.Entities.Log;
import com.batch.Database.Entities.Unit;
import com.batch.Database.Repositories.UnitsRepository;
import com.batch.Database.Services.BatchControllerDataService;
import com.batch.Database.Services.BatchesService;
import com.batch.PLCDataSource.ModBus.ModBusService;
import com.batch.PLCDataSource.PLC.ComplexDataType.*;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Services.LoggingService.LoggingService;
import com.batch.Services.UserAdministration.UserAuthorizationService;
import com.batch.Services.UserAdministration.WindowData;
import com.batch.Utilities.LogIdentefires;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class InitialWindowController {

    private final InitialWindowModel model = new InitialWindowModel();
    private Map<String, RowDataDefinition> allDataDefinitions;

    private final ModBusService modBusService;
    private final UnitsRepository unitsRepository;
    private final BatchesService batchesService;
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;
    private final BatchControllerDataService batchControllerDataService;
    private final UserAuthorizationService userAuthorizationService;
    private final LoggingService loggingService;

    public InitialWindowModel getModel() {
        return model;
    }

    //get Devices
    public Valve getValveByName(String name) {
        return (Valve) allDataDefinitions.get(name);
    }
    public Pump getPumpByName(String name) {
        return (Pump) allDataDefinitions.get(name);
    }
    public Mixer getMixerByName(String name) {
        return (Mixer) allDataDefinitions.get(name);
    }
    public Weight getWeightByName(String name) {
        return (Weight) allDataDefinitions.get(name);
    }

    //Some controls
    public void atStartWaterFill(boolean val) {
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Start_WaterTank_Fill_To_HiAlarm)).setValue(val);
        loggingService.LogRecord(new Log(LogIdentefires.Info.name(), "", "Start water fill"));
    }
    public synchronized void onSetAllInAutoPressed(Map<String, ImageView> mixers, Map<String, ImageView> pumps, Map<String, ImageView> valves) {
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Make_All_Devices_In_Automatic)).setValue(Boolean.TRUE);
        mixers.forEach((name, mixer) -> {
            ((BooleanDataType) allDataDefinitions.get(name).getAllValues().get(MixerOutput.Mode)).setValue(Boolean.TRUE);
        });
        pumps.forEach((name, mixer) -> {
            ((BooleanDataType) allDataDefinitions.get(name).getAllValues().get(PumpOutput.Mode)).setValue(Boolean.TRUE);
        });
        valves.forEach((name, mixer) -> {
            ((BooleanDataType) allDataDefinitions.get(name).getAllValues().get(ValveOutput.Mode)).setValue(Boolean.TRUE);
        });
        loggingService.LogRecord(new Log(LogIdentefires.Info.name(), "", "Set all devices to auto"));
    }
    public synchronized void onSetAllInAutoReleased() {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Make_All_Devices_In_Automatic)).setValue(Boolean.FALSE);
    }

    public void onLogIn() {
        userAuthorizationService.requestLogIn();
    }
    public void onLogOut() {
        userAuthorizationService.requestLogOff();
    }

    @EventListener
    public void afterRefreshed(ApplicationContext.GraphicsInitializerEvent event){
        allDataDefinitions = plcDataDefinitionFactory.getAllDevicesDataModel();
    }

    @EventListener
    private void init(ContextStartedEvent event){
        checkESDAlarms();
        checkPLCConnection();
        checkAirPressureAlarms();
        checkOverUnderVoltageAlarms();

        modBusService.getConnectionStatus().addListener((observable, oldValue, newValue) -> checkPLCConnection());
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.HI_Air_Pressure_Alarm)).addListener((observable, oldValue, newValue) -> checkAirPressureAlarms());
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.HI_Air_Pressure_Alarm)).addListener((observable, oldValue, newValue) -> checkAirPressureAlarms());
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Over_Under_Voltage_Alarm)).addListener((observable, oldValue, newValue) -> checkOverUnderVoltageAlarms());
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.ESD_Alarm)).addListener((observable, oldValue, newValue) -> checkESDAlarms());

        ((RealDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Water_Pressure)).addListener((observable, oldValue, newValue) -> Platform.runLater(() -> model.getGauge1().setValue(newValue)));
        ((RealDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Air_Pressure)).addListener((observable, oldValue, newValue) -> Platform.runLater(() -> model.getGauge2().setValue(newValue)));


        //Resetting window requests
        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).getValue()) {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_1_Manual_Add_Confirmation)).setValue(false);
        }
        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).getValue()) {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_2_Manual_Add_Confirmation)).setValue(false);
        }
        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).getValue()) {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_1_Message_Confirmation)).setValue(false);
        }
        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).getValue()) {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_2_Message_Confirmation)).setValue(false);
        }
        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).getValue()) {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_1_Message_Confirmation)).setValue(false);
        }
        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).getValue()) {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_2_Message_Confirmation)).setValue(false);
        }
        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).getValue()) {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_3_Message_Confirmation)).setValue(false);
        }
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_1_Manual_Add_Confirmation)).setValue(false);
            }
        });
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_2_Manual_Add_Confirmation)).setValue(false);
            }
        });
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_1_Message_Confirmation)).setValue(false);
            }
        });
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_2_Message_Confirmation)).setValue(false);
            }
        });
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_1_Message_Confirmation)).setValue(false);
            }
        });
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_2_Message_Confirmation)).setValue(false);
            }
        });
        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_3_Message_Confirmation)).setValue(false);
            }
        });
    }

    private void checkPLCConnection() {
        if (modBusService.getConnectionStatus().getValue()){
            Platform.runLater(() -> {
                model.getConnectionInfo().setValue("Connection to PLC is successfully established ");
                model.getConnectionStatus().setValue(true);
            });
        }else {
            Platform.runLater(() -> {
                model.getConnectionInfo().setValue("Connection to PLC has been lost ");
                model.getConnectionStatus().setValue(false);
            });
        };

    }
    private void checkAirPressureAlarms() {
        boolean hi = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.HI_Air_Pressure_Alarm)).getValue();
        boolean lo = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Lo_Air_Pressure_Alarm)).getValue();
        Platform.runLater(() -> {
            if (hi) {
                model.getAirPressureStatus().setValue(false);
                model.getAirPressureInfo().setValue("Hi air pressure alarm");
            } else if (lo) {
                model.getAirPressureStatus().setValue(false);
                model.getAirPressureInfo().setValue("Lo air pressure alarm");
            } else {
                model.getAirPressureStatus().setValue(true);
                model.getAirPressureInfo().setValue("Normal air pressure");
            }
        });
    }
    private void checkOverUnderVoltageAlarms() {
        boolean alarm = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Over_Under_Voltage_Alarm)).getValue();
        Platform.runLater(() -> {
            if (alarm) {
                model.getOverUnderVoltageStatus().setValue(false);
                model.getOverUnderVoltageInfo().setValue("Over/Under voltage alarm");
            } else {
                model.getOverUnderVoltageStatus().setValue(true);
                model.getOverUnderVoltageInfo().setValue("Normal supply voltage");
            }
        });
    }
    private void checkESDAlarms() {
        boolean alarm = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.ESD_Alarm)).getValue();
        Platform.runLater(() -> {
            if (alarm) {
                model.getEsdStatus().setValue(false);
                model.getEsdInfo().setValue("ESD activated");
            } else {
                model.getEsdStatus().setValue(true);
                model.getEsdInfo().setValue("ESD not activated");
            }
        });
    }

    public List<String> getAllUnitsNames() {
        return unitsRepository.findAll().stream().map(Unit::getName).collect(Collectors.toList());
    }

    public List<BatchControllerData> getAllBatchControllerData() {
        return batchControllerDataService.findAll();
    }

    public Optional<Batch> getBatchById(long runningBatchID) {
        return batchesService.findById(runningBatchID);
    }

    public void registerWindowToUserAuthorizationService(WindowData win) {
        userAuthorizationService.registerWindow(win);
    }

    @EventListener
    private void atAppStart(ContextStartedEvent event){
        loggingService.LogRecord(new Log(LogIdentefires.Info.name(), "", "System start"));
    }
    @EventListener
    private void atAppClose(ContextStartedEvent event){
        loggingService.LogRecord(new Log(LogIdentefires.Info.name(), "", "System stop"));
    }

}
