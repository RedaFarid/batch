package com.batch.GUI.InitialWindow;

import com.batch.ApplicationContext;
import com.batch.PLCDataSource.ModBus.ModBusService;
import com.batch.PLCDataSource.PLC.ComplexDataType.*;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class InitialWindowController {

    private final InitialWindowModel model = new InitialWindowModel();
    private Map<String, RowDataDefinition> allDataDefinitions;

    private final ModBusService modBusService;



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
    }
    public synchronized void onSetAllInAutoReleased() {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Make_All_Devices_In_Automatic)).setValue(Boolean.FALSE);
    }

    public void onLogIn() {

    }
    public void onLogOut() {

    }



    @EventListener
    public void afterRefreshed(ApplicationContext.ApplicationListener event){
        allDataDefinitions = PLCDataDefinitionFactory.getSystem().getAllDevicesDataModel();
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
    }

    private void checkPLCConnection() {
        if (modBusService.getConnectionStatus().getValue()){
            Platform.runLater(() -> {
                model.getConnectionInfo().setValue("Connection to PLC is successfully established ");
                model.getConnectionStatus().setValue(true);
            });
        }else {
            Platform.runLater(() -> {
                model.getConnectionInfo().setValue("Connection to PLC is has been lost ");
                model.getConnectionStatus().setValue(false);
            });
        };

    }
    private void checkAirPressureAlarms() {
        boolean hi = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.HI_Air_Pressure_Alarm)).getValue();
        boolean lo = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Lo_Air_Pressure_Alarm)).getValue();
        Platform.runLater(() -> {
            if (hi) {
                model.getAirPressureStatus().setValue(true);
                model.getAirPressureInfo().setValue("Hi air pressure alarm");
            } else if (lo) {
                model.getAirPressureStatus().setValue(true);
                model.getAirPressureInfo().setValue("Lo air pressure alarm");
            } else {
                model.getAirPressureStatus().setValue(false);
                model.getAirPressureInfo().setValue("Normal air pressure");
            }
        });
    }
    private void checkOverUnderVoltageAlarms() {
        boolean alarm = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Over_Under_Voltage_Alarm)).getValue();
        Platform.runLater(() -> {
            if (alarm) {
                model.getOverUnderVoltageStatus().setValue(true);
                model.getOverUnderVoltageInfo().setValue("Over/Under voltage alarm");
            } else {
                model.getOverUnderVoltageStatus().setValue(false);
                model.getOverUnderVoltageInfo().setValue("Normal supply voltage");
            }
        });
    }
    private void checkESDAlarms() {
        boolean alarm = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.ESD_Alarm)).getValue();
        Platform.runLater(() -> {
            if (alarm) {
                model.getEsdStatus().setValue(true);
                model.getEsdInfo().setValue("ESD activated");
            } else {
                model.getEsdStatus().setValue(false);
                model.getEsdInfo().setValue("ESD not activated");
            }
        });
    }
}
