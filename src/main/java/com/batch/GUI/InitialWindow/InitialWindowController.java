package com.batch.GUI.InitialWindow;

import com.batch.ApplicationContext;
import com.batch.PLCDataSource.ModBus.ModBusService;
import com.batch.PLCDataSource.PLC.ComplexDataType.*;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
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


        Platform.runLater(() -> {
            model.getConnectionInfo().setValue("Connection to PLC is has been lost ");
            model.getConnectionStatus().setValue(false);
        });
        modBusService.getConnectionStatus().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                Platform.runLater(() -> {
                    model.getConnectionInfo().setValue("Connection to PLC is successfully established ");
                    model.getConnectionStatus().setValue(true);
                });
            }else {
                Platform.runLater(() -> {
                    model.getConnectionInfo().setValue("Connection to PLC is has been lost ");
                    model.getConnectionStatus().setValue(false);
                });
            }
        });
    }


}
