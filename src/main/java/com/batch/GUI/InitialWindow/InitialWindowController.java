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
import com.batch.Services.LoggingService.MessageLoggingService;
import com.batch.Services.NotificationService.ErrorObject;
import com.batch.Services.NotificationService.NotificationService;
import com.batch.Services.NotificationService.ServiceErrorsListener;
import com.batch.Services.UserAdministration.UserAuthorizationService;
import com.batch.Services.UserAdministration.WindowData;
import com.batch.Utilities.LogIdentefires;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Controller("InitialWindowController")
public class InitialWindowController {
    @Getter
    private final InitialWindowModel model = new InitialWindowModel();
    private final ModBusService modBusService;
    private final UnitsRepository unitsRepository;
    private final BatchesService batchesService;
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;
    private final BatchControllerDataService batchControllerDataService;
    private final UserAuthorizationService userAuthorizationService;

    @Getter
    private final MessageLoggingService messageLoggingService;

    @Autowired
    private final NotificationService notificationService;

    private Map<String, RowDataDefinition> allDataDefinitions;

    public InitialWindowController(final ModBusService modBusService, final UnitsRepository unitsRepository, final BatchesService batchesService, final PLCDataDefinitionFactory plcDataDefinitionFactory, final BatchControllerDataService batchControllerDataService, final UserAuthorizationService userAuthorizationService, final MessageLoggingService messageLoggingService, final InitialWindow initialWindow, final NotificationService notificationService) {
        this.modBusService = modBusService;
        this.unitsRepository = unitsRepository;
        this.batchesService = batchesService;
        this.plcDataDefinitionFactory = plcDataDefinitionFactory;
        this.batchControllerDataService = batchControllerDataService;
        this.userAuthorizationService = userAuthorizationService;
        this.messageLoggingService = messageLoggingService;
        this.notificationService = notificationService;
    }

    public Valve getValveByName(String name) {
        return (Valve) this.allDataDefinitions.get(name);
    }

    public Pump getPumpByName(String name) {
        return (Pump) this.allDataDefinitions.get(name);
    }

    public Mixer getMixerByName(String name) {
        return (Mixer) this.allDataDefinitions.get(name);
    }

    public Weight getWeightByName(String name) {
        return (Weight) this.allDataDefinitions.get(name);
    }

    public void atStartWaterFill(boolean val) {
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Start_WaterTank_Fill_To_HiAlarm)).setValue(val);
        this.messageLoggingService.logEvent(new Log(LogIdentefires.Info.name(), "", "Start water fill"));
    }

    public synchronized void onSetAllInAutoPressed(Map<String, ImageView> mixers, Map<String, ImageView> pumps, Map<String, ImageView> valves) {
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Make_All_Devices_In_Automatic)).setValue(Boolean.TRUE);
        mixers.forEach((name, mixer) -> ((BooleanDataType) this.allDataDefinitions.get(name).getAllValues().get(MixerOutput.Mode)).setValue(Boolean.TRUE));
        pumps.forEach((name, mixer) -> ((BooleanDataType) this.allDataDefinitions.get(name).getAllValues().get(PumpOutput.Mode)).setValue(Boolean.TRUE));
        valves.forEach((name, mixer) -> ((BooleanDataType) this.allDataDefinitions.get(name).getAllValues().get(ValveOutput.Mode)).setValue(Boolean.TRUE));
        this.messageLoggingService.logEvent(new Log(LogIdentefires.Info.name(), "", "Set all devices to auto"));
    }

    public synchronized void onSetAllInAutoReleased() {
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Make_All_Devices_In_Automatic)).setValue(Boolean.FALSE);
    }

    public void onLogIn() {
        this.userAuthorizationService.requestLogIn();
    }

    public void onLogOut() {
        this.userAuthorizationService.requestLogOff();
    }

    @EventListener
    public void withGraphicsEvent(ApplicationContext.GraphicsInitializerEvent event) {
        this.allDataDefinitions = this.plcDataDefinitionFactory.getAllDevicesDataModel();
        atStarted();
    }


    private void atStarted() {
        this.checkESDAlarms();
        this.checkPLCConnection();
        this.checkAirPressureAlarms();
        this.checkOverUnderVoltageAlarms();
        this.listenToNotifications();
        this.modBusService.getConnectionStatus().addListener((observable, oldValue, newValue) -> this.checkPLCConnection());
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.HI_Air_Pressure_Alarm)).addListener((observable, oldValue, newValue) -> this.checkAirPressureAlarms());
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Lo_Air_Pressure_Alarm)).addListener((observable, oldValue, newValue) -> this.checkAirPressureAlarms());
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Over_Under_Voltage_Alarm)).addListener((observable, oldValue, newValue) -> this.checkOverUnderVoltageAlarms());
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.ESD_Alarm)).addListener((observable, oldValue, newValue) -> this.checkESDAlarms());
        ((RealDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Water_Pressure)).addListener((observable, oldValue, newValue) -> Platform.runLater(() -> this.model.getGauge1().setValue(newValue)));
        ((RealDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Air_Pressure)).addListener((observable, oldValue, newValue) -> Platform.runLater(() -> this.model.getGauge2().setValue(newValue)));
        if (!((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).getValue()) {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_1_Manual_Add_Confirmation)).setValue(false);
        }

        if (!((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).getValue()) {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_2_Manual_Add_Confirmation)).setValue(false);
        }

        if (!((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).getValue()) {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_1_Message_Confirmation)).setValue(false);
        }

        if (!((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).getValue()) {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_2_Message_Confirmation)).setValue(false);
        }

        if (!((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).getValue()) {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_1_Message_Confirmation)).setValue(false);
        }

        if (!((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).getValue()) {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_2_Message_Confirmation)).setValue(false);
        }

        if (!((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).getValue()) {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_3_Message_Confirmation)).setValue(false);
        }

        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_1_Manual_Add_Confirmation)).setValue(false);
            }
        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_2_Manual_Add_Confirmation)).setValue(false);
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_1_Message_Confirmation)).setValue(false);
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_2_Message_Confirmation)).setValue(false);
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_1_Message_Confirmation)).setValue(false);
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_2_Message_Confirmation)).setValue(false);
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_3_Message_Confirmation)).setValue(false);
            }

        });
        messageLoggingService.system("Initial data model binded to PLC data");
    }

    private void listenToNotifications() {
        this.notificationService.getNotificationsDataStructure().addServicesListener(new ServiceErrorsListener() {
            public void newServiceAdded(String serviceAdded) {
            }

            public void serviceRemoved(String service) {
            }

            public void newFamilyAdded(String service, String family, ErrorObject errorObject) {
            }

            public void familyRemoved(String service, String family) {
            }

            public void newErrorMessageAdded(String service, String family, String errorMessage, ErrorObject errorObject) {
            }

            public void atRegistering(List<String> services, Map<String, List<ErrorObject>> errors) {
            }

            public void newErrorMessagePopUpOnly(String service, String family, String errorMessage) {
                // TODO: check here
            }
        });
    }

    private void checkPLCConnection() {
        if (this.modBusService.getConnectionStatus().getValue()) {
            Platform.runLater(() -> {
                this.model.getConnectionInfo().setValue("Connection to PLC is successfully established ");
                this.model.getConnectionStatus().setValue(true);
            });
        } else {
            Platform.runLater(() -> {
                this.model.getConnectionInfo().setValue("Connection to PLC has been lost ");
                this.model.getConnectionStatus().setValue(false);
            });
        }
    }

    private void checkAirPressureAlarms() {
        boolean hi = ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.HI_Air_Pressure_Alarm)).getValue();
        boolean lo = ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Lo_Air_Pressure_Alarm)).getValue();
        Platform.runLater(() -> {
            if (hi) {
                this.model.getAirPressureStatus().setValue(false);
                this.model.getAirPressureInfo().setValue("Hi air pressure alarm");
            } else if (lo) {
                this.model.getAirPressureStatus().setValue(false);
                this.model.getAirPressureInfo().setValue("Lo air pressure alarm");
            } else {
                this.model.getAirPressureStatus().setValue(true);
                this.model.getAirPressureInfo().setValue("Normal air pressure");
            }
        });
    }

    private void checkOverUnderVoltageAlarms() {
        boolean alarm = ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Over_Under_Voltage_Alarm)).getValue();
        Platform.runLater(() -> {
            if (alarm) {
                this.model.getOverUnderVoltageStatus().setValue(false);
                this.model.getOverUnderVoltageInfo().setValue("Over/Under voltage alarm");
            } else {
                this.model.getOverUnderVoltageStatus().setValue(true);
                this.model.getOverUnderVoltageInfo().setValue("Normal supply voltage");
            }
        });
    }

    private void checkESDAlarms() {
        boolean alarm = ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.ESD_Alarm)).getValue();
        Platform.runLater(() -> {
            if (alarm) {
                this.model.getEsdStatus().setValue(false);
                this.model.getEsdInfo().setValue("ESD activated");
            } else {
                this.model.getEsdStatus().setValue(true);
                this.model.getEsdInfo().setValue("ESD not activated");
            }
        });
    }

    public List<String> getAllUnitsNames() {
        return Lists.newArrayList(this.unitsRepository.findAll()).stream().map(Unit::getName).collect(Collectors.toList());
    }

    public List<BatchControllerData> getAllBatchControllerData() {
        return this.batchControllerDataService.findAll();
    }

    public Optional<Batch> getBatchById(long runningBatchID) {
        return this.batchesService.findById(runningBatchID);
    }

    public void registerWindowToUserAuthorizationService(WindowData win) {
        this.userAuthorizationService.registerWindow(win);
    }

    @EventListener
    private void atAppStart(ContextStartedEvent event) {
        this.messageLoggingService.logEvent(new Log(LogIdentefires.Info.name(), "", "System start"));
    }

    @EventListener
    private void atAppClose(ContextStoppedEvent event) {
        this.messageLoggingService.logEvent(new Log(LogIdentefires.Info.name(), "", "System stop"));
    }

    public PLCDataDefinitionFactory getPLCDataDefinitionFactory() {
        return this.plcDataDefinitionFactory;
    }

}
