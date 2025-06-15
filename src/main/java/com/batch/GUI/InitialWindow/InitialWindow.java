package com.batch.GUI.InitialWindow;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Group;
import com.batch.Database.Entities.Log;
import com.batch.GUI.Alarms.AllAlarmsWindow;
import com.batch.GUI.Alarms.Utilities;
import com.batch.GUI.BatchWindow.WindowComponents.BatchCreator;
import com.batch.GUI.BatchWindow.WindowComponents.BatchObserver;
import com.batch.GUI.FacePlates.MixerFacePlate;
import com.batch.GUI.FacePlates.PumpFacePlate;
import com.batch.GUI.FacePlates.ValveFacePlate;
import com.batch.GUI.FacePlates.WeightFacePlate;
import com.batch.GUI.InitialWindow.SubWindows.*;
import com.batch.GUI.MaterialsWindow.MaterialsWindow;
import com.batch.GUI.NotificationCenter.NCServicesView;
import com.batch.GUI.PhasesWindow.PhasesWindow;
import com.batch.GUI.RecipeEditor.WindowComponents.RecipeEditor;
import com.batch.GUI.Reporting.BatchArchiveWindow;
import com.batch.GUI.UnitsWindow.UnitsWindow;
import com.batch.GUI.UserAdministration.UserAdministrationWindow;
import com.batch.PLCDataSource.PLC.ComplexDataType.*;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Services.LoggingService.MessageLoggingService;
import com.batch.Services.UserAdministration.UserEvent;
import com.batch.Services.UserAdministration.UserEventMessage;
import com.batch.Services.UserAdministration.WindowData;
import com.batch.Utilities.LogIdentefires;
import com.batch.Utilities.Round;
import com.google.common.io.Resources;
import eu.hansolo.medusa.*;
import eu.hansolo.medusa.Clock.ClockSkinType;
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.Gauge.ScaleDirection;
import eu.hansolo.medusa.skins.QuarterSkin;
import io.github.palexdev.materialfx.controls.MFXNotification;
import io.github.palexdev.materialfx.controls.SimpleMFXNotificationPane;
import io.github.palexdev.materialfx.notifications.NotificationPos;
import io.github.palexdev.materialfx.notifications.NotificationsManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import org.controlsfx.dialog.ExceptionDialog;
import org.kordamp.ikonli.entypo.Entypo;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class InitialWindow implements ApplicationListener<ApplicationContext.GraphicsInitializerEvent> {

    @Getter
    private Stage initialStage;

    private final BorderPane root = new BorderPane();
    private final Scene scene;
    private final TabPane containerPane;
    private final Tab SCADATab;
    private final VBox batches;
    private final VBox topBars;
    private final ToolBar toolBar;
    private final MenuBar menuBar;
    private final Menu view;
    private final Menu Users;
    private final Menu RecipesSettings;
    private final Menu operations;
    private final Menu alarms;
    private final Menu SystemMenu;
    private final Menu Help;
    private final MenuItem enterFullScreenItem;
    private final MenuItem exitFullScreenItem;
    private final MenuItem closeAppItem;
    private final MenuItem UserAdministrationMenuItem;
    private final MenuItem LoginItem;
    private final MenuItem LogOutItem;
    private final MenuItem Units;
    private final MenuItem Phases;
    private final MenuItem materialItem;
    private final MenuItem recipeEditorItem;
    private final MenuItem batchCreatorItem;
    private final MenuItem reportingSystem;
    private final MenuItem configurations;
    private final MenuItem journalAlarms;
    private final MenuItem airPressureSettings;
    private final MenuItem notificationsCenter;
    private final MenuItem about;
    private final Button logIn;
    private final Button logOut;
    private final Button shutDown;
    private final Button startWaterFill;
    private final Button changeAllDevicesToAutomatic;
    private final Label connectionStatus;
    private final Label airPressureStatus;
    private final Label overUnderVoltageStatus;
    private final Label ESDStatus;
    private final Label lastAlarmField;
    private final Map<Long, BatchObserver> batchObservers;
    private final Background HEALTHY_BACKGROUND;
    private final Background FAULTY_BACKGROUND;
    private final Background CONNECTION_LOSS_BACKGROUND;
    private Parent scada;
    private Map<String, ImageView> valves;
    private Map<String, ImageView> pumps;
    private Map<String, ImageView> mixers;
    private Map<String, Pane> levelBars;
    private Map<String, Label> levelLabels;
    private Map<String, Label> weightLabels;
    private AnchorPane SCADAPane;
    private Pane waterLevel;
    private Pane waterPress;
    private Pane airPress;
    private RecipeEditor recipeEditor;
    private BatchCreator batchCreator;
    private AllAlarmsWindow allAlarmsWindow;
    private String returnData;
    private InitialWindowModel model;

    private InitialWindowController controller;
    private MessageLoggingService log;

    private UserAdministrationWindow userAdministrationWindow;
    private NCServicesView ncServicesView;

    private Map<String, RowDataDefinition> allDataDefinitions;

    public InitialWindow() {
        this.scene = new Scene(this.root);
        this.containerPane = new TabPane();
        this.SCADATab = new Tab("    OverView    ");
        this.batches = new VBox();
        this.scada = null;
        this.topBars = new VBox();
        this.toolBar = new ToolBar();
        this.menuBar = new MenuBar();
        this.view = new Menu("View");
        this.Users = new Menu("Users");
        this.RecipesSettings = new Menu("Recipes");
        this.operations = new Menu("Operations");
        this.alarms = new Menu("Alarms");
        this.SystemMenu = new Menu("System");
        this.Help = new Menu("Help");
        this.enterFullScreenItem = new MenuItem("Enter full screen");
        this.exitFullScreenItem = new MenuItem("Exit full screen");
        this.closeAppItem = new MenuItem("Shutdown");
        this.UserAdministrationMenuItem = new MenuItem("UserAdministration");
        this.LoginItem = new MenuItem("Log in");
        this.LogOutItem = new MenuItem("Log out");
        this.Units = new MenuItem("Units");
        this.Phases = new MenuItem("Phases");
        this.materialItem = new MenuItem("Materials manager");
        this.recipeEditorItem = new MenuItem("Recipe Editor");
        this.batchCreatorItem = new MenuItem("Batch Creator");
        this.reportingSystem = new MenuItem("Reporting System");
        this.configurations = new MenuItem("Configurations");
        this.journalAlarms = new MenuItem("Journal Alarms");
        this.airPressureSettings = new MenuItem("Air-Pressure Alarms");
        this.notificationsCenter = new MenuItem("Notification center");
        this.about = new MenuItem("About");
        this.logIn = new Button("Log In");
        this.logOut = new Button("Log Out");
        this.shutDown = new Button("ShutDown");
        this.startWaterFill = new Button("Start water fill to HI-Alarm");
        this.changeAllDevicesToAutomatic = new Button("Auto all");
        this.connectionStatus = new Label("Starting connection with PLC system ...");
        this.airPressureStatus = new Label("Checking air pressure");
        this.overUnderVoltageStatus = new Label("Checking supply voltage");
        this.ESDStatus = new Label("Checking ESD status");
        this.valves = new LinkedHashMap<>();
        this.pumps = new LinkedHashMap<>();
        this.mixers = new LinkedHashMap<>();
        this.levelBars = new LinkedHashMap<>();
        this.levelLabels = new LinkedHashMap<>();
        this.weightLabels = new LinkedHashMap<>();
        this.lastAlarmField = new Label();
        this.batchObservers = new ConcurrentHashMap<>();
        this.HEALTHY_BACKGROUND = new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(5.0F), Insets.EMPTY));
        this.FAULTY_BACKGROUND = new Background(new BackgroundFill(Color.RED.brighter(), new CornerRadii(5.0F), Insets.EMPTY));
        this.CONNECTION_LOSS_BACKGROUND = new Background(new BackgroundFill(Color.ORANGERED, new CornerRadii(5.0F), Insets.EMPTY));
    }

    @EventListener
    public void atRefreshed(ContextRefreshedEvent event) {
    }

    @EventListener
    public void atReady(ApplicationReadyEvent event) {
    }

    @EventListener
    public void onApplicationEvent(ApplicationContext.GraphicsInitializerEvent listener) {

        try {
            this.controller = (InitialWindowController) ApplicationContext.applicationContext.getBean("InitialWindowController");
            this.model = controller.getModel();
            this.allDataDefinitions = controller.getPLCDataDefinitionFactory().getAllDevicesDataModel();
            this.log = controller.getMessageLoggingService();

            this.controller.registerWindowToUserAuthorizationService(new WindowData("Recipe window"));
            this.controller.registerWindowToUserAuthorizationService(new WindowData("Batch window"));
            this.controller.registerWindowToUserAuthorizationService(new WindowData("Users window"));
            this.controller.registerWindowToUserAuthorizationService(new WindowData("Phases window"));
            this.controller.registerWindowToUserAuthorizationService(new WindowData("Units window"));
            this.controller.registerWindowToUserAuthorizationService(new WindowData("Reporting window"));
            this.controller.registerWindowToUserAuthorizationService(new WindowData("Material window"));

            this.initialStage = listener.getStage();
            this.allAlarmsWindow = AllAlarmsWindow.getWindow(this.initialStage);
            this.recipeEditor = RecipeEditor.getWindow(this.initialStage);
            this.batchCreator = BatchCreator.getWindow(this.initialStage);
            this.ncServicesView = new NCServicesView(this.initialStage);
            this.recipeEditor.setHeight(900.0F);
            this.recipeEditor.setWidth(1500.0F);
            this.recipeEditor.initOwner(this.initialStage);
            this.batchCreator.setHeight(600.0F);
            this.batchCreator.setWidth(1100.0F);
            this.batchCreator.setMinHeight(600.0F);
            this.batchCreator.setMinWidth(800.0F);
            this.batchCreator.setResizable(false);
            this.batchCreator.initOwner(this.initialStage);

            log.system("graphics initialized " + Thread.currentThread().getName());
        } catch (Exception e) {
            log.logExcption("Initial window [On Application Event]", e);
        }

    }

    @EventListener
    public void atStarted(ContextStartedEvent event) {
            graphicsBuilder();
            this.adjustGauges();
            this.connectionStatus.textProperty().bind(this.model.getConnectionInfo());
            this.connectionStatus.backgroundProperty().bind(Bindings.when(this.model.getConnectionStatus()).then(this.HEALTHY_BACKGROUND).otherwise(this.CONNECTION_LOSS_BACKGROUND));
            this.connectionStatus.textFillProperty().bind(Bindings.when(this.model.getConnectionStatus()).then(Color.BLACK).otherwise(Color.WHITE));
            this.airPressureStatus.textProperty().bind(this.model.getAirPressureInfo());
            this.airPressureStatus.backgroundProperty().bind(Bindings.when(this.model.getAirPressureStatus()).then(this.HEALTHY_BACKGROUND).otherwise(this.FAULTY_BACKGROUND));
            this.airPressureStatus.textFillProperty().bind(Bindings.when(this.model.getAirPressureStatus()).then(Color.BLACK).otherwise(Color.WHITE));
            this.overUnderVoltageStatus.textProperty().bind(this.model.getOverUnderVoltageInfo());
            this.overUnderVoltageStatus.backgroundProperty().bind(Bindings.when(this.model.getOverUnderVoltageStatus()).then(this.HEALTHY_BACKGROUND).otherwise(this.FAULTY_BACKGROUND));
            this.overUnderVoltageStatus.textFillProperty().bind(Bindings.when(this.model.getOverUnderVoltageStatus()).then(Color.BLACK).otherwise(Color.WHITE));
            this.ESDStatus.textProperty().bind(this.model.getEsdInfo());
            this.ESDStatus.backgroundProperty().bind(Bindings.when(this.model.getEsdStatus()).then(this.HEALTHY_BACKGROUND).otherwise(this.FAULTY_BACKGROUND));
            this.ESDStatus.textFillProperty().bind(Bindings.when(this.model.getEsdStatus()).then(Color.BLACK).otherwise(Color.WHITE));

            actionHandler();

        this.controller
                .getAllBatchControllerData()
                .stream()
                .filter((data) -> data.getRunningBatchID() > 0L)
                .forEach((item) ->
                        this.controller
                        .getBatchById(item.getRunningBatchID())
                        .ifPresentOrElse(this::createBatchObserver,
                                () -> this.log.logEvent(new Log(LogIdentefires.System.name(), "error creating batch view as batch not found in database ID= " + item.getRunningBatchID()))));


    }
    @EventListener
    public void atShowEvent(ApplicationContext.GraphicsShowEvent event){
        this.initialStage.show();
    }

    @EventListener
    public void atException(ExceptionWindowRequestEvent event) {
        ExceptionData exception = event.getException();
        Platform.runLater(() -> {
            ExceptionDialog exceptionDialog = new ExceptionDialog(exception.e);
            exceptionDialog.setHeaderText(exception.header);
            exceptionDialog.getDialogPane().setMaxWidth(500.0F);
            exceptionDialog.initOwner(this.initialStage);
            exceptionDialog.initModality(Modality.WINDOW_MODAL);
            exceptionDialog.initStyle(StageStyle.UTILITY);
            exceptionDialog.show();
        });
    }

    @Scheduled(fixedDelay = 500L, initialDelay = 2000L)
    public void run() {
        try {
            this.batchObservers.forEach((id, batchObserver) -> batchObserver.update());
        } catch (Exception e) {
            log.logExcption("InitialWindow [run]", e);
        }

    }

    @Scheduled(fixedDelay = 1000L, initialDelay = 20000L)
    public void updateAlarms() {
        try {
            Log lastEnteredLog = this.log.getLastEnteredLog();
            Platform.runLater(() -> {
                if (!this.lastAlarmField.getText().equals(lastEnteredLog.toString())) {
                    this.lastAlarmField.setText(lastEnteredLog.toString());
                    if (lastEnteredLog.getIdentifier().equals(LogIdentefires.Error.name())) {
                        this.lastAlarmField.setStyle("-fx-background-color: red; -fx-dark-text-color: white;-fx-mid-text-color: white;-fx-font-weight:bold;-fx-font-style:normal;-fx-font-size:16;-fx-font-family: monospace;");
                    } else if (lastEnteredLog.getIdentifier().equals(LogIdentefires.Warning.name())) {
                        this.lastAlarmField.setStyle("-fx-background-color: yellow; -fx-dark-text-color: black;-fx-mid-text-color: black;-fx-font-weight:bold;-fx-font-style:normal;-fx-font-size:16;-fx-font-family: monospace;");
                    } else if (lastEnteredLog.getIdentifier().equals(LogIdentefires.Info.name())) {
                        this.lastAlarmField.setStyle("-fx-background-color: wheat; -fx-dark-text-color: black;-fx-mid-text-color: black;-fx-font-weight:bold;-fx-font-style:normal;-fx-font-size:16;-fx-font-family: monospace;");
                    } else if (lastEnteredLog.getIdentifier().equals(LogIdentefires.System.name())) {
                        this.lastAlarmField.setStyle("-fx-background-color: black; -fx-dark-text-color: white;-fx-mid-text-color: white;-fx-font-weight:bold;-fx-font-style:normal;-fx-font-size:16;-fx-font-family: monospace;");
                    }
                }

            });
        } catch (Exception e) {
            log.logExcption("InitialWindow [updateAlarms]", e);
        }

    }

    @EventListener
    public void newUserLogIn(UserEvent event) {
        UserEventMessage message = event.getMessage();
        if (message.isLoggedOn()) {
            if (message.getUser().getUserName().equals("Administrator")) {
                this.recipeEditorItem.setDisable(false);
                this.batchCreatorItem.setDisable(false);
                this.Phases.setDisable(false);
                this.UserAdministrationMenuItem.setDisable(false);
                this.Units.setDisable(false);
                this.reportingSystem.setDisable(false);
                this.materialItem.setDisable(false);
            }

            message.getAllGroupsDTO().getList().forEach((windowGroupsDTO) -> {
                LinkedHashMap<String, List<Group>> rowGroup = windowGroupsDTO.getRowGroup();
            });
        } else {
            Platform.runLater(() -> {
                this.recipeEditorItem.setDisable(true);
                this.batchCreatorItem.setDisable(true);
                this.Phases.setDisable(true);
                this.UserAdministrationMenuItem.setDisable(true);
                this.Units.setDisable(true);
                this.reportingSystem.setDisable(true);
                this.materialItem.setDisable(true);
            });
        }

    }

    private void graphicsBuilder() {
        this.lastAlarmField.prefWidthProperty().bind(this.topBars.widthProperty());
        this.lastAlarmField.setPadding(new Insets(3.0F));
        this.lastAlarmField.setPrefHeight(30.0F);
        this.lastAlarmField.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        this.connectionStatus.setPadding(new Insets(5.0F, 10.0F, 5.0F, 10.0F));
        this.connectionStatus.setPrefSize(400.0F, 30.0F);
        this.connectionStatus.setAlignment(Pos.CENTER);
        this.airPressureStatus.setPadding(new Insets(5.0F, 10.0F, 5.0F, 10.0F));
        this.airPressureStatus.setPrefSize(180.0F, 30.0F);
        this.airPressureStatus.setAlignment(Pos.CENTER);
        this.overUnderVoltageStatus.setPadding(new Insets(5.0F, 10.0F, 5.0F, 10.0F));
        this.overUnderVoltageStatus.setPrefSize(180.0F, 30.0F);
        this.overUnderVoltageStatus.setAlignment(Pos.CENTER);
        this.ESDStatus.setPadding(new Insets(5.0F, 10.0F, 5.0F, 10.0F));
        this.ESDStatus.setPrefSize(180.0F, 30.0F);
        this.ESDStatus.setAlignment(Pos.CENTER);
        this.batches.setPrefWidth(500.0F);
        Pane spacePane = new Pane();
        spacePane.setPrefWidth(110.0F);
        this.shutDown.setPrefWidth(100.0F);
        this.startWaterFill.setPrefWidth(200.0F);
        this.changeAllDevicesToAutomatic.setPrefWidth(200.0F);
        this.toolBar.getItems().addAll(this.logIn, this.logOut, new Separator(), this.shutDown, new Separator(), this.changeAllDevicesToAutomatic, this.startWaterFill, new Separator(), this.ESDStatus, this.overUnderVoltageStatus, this.airPressureStatus, new Separator(), this.connectionStatus, new Separator(), this.getClock());
        this.menuBar.getMenus().addAll(this.view, this.Users, this.RecipesSettings, this.operations, this.alarms, this.Help);
        this.view.getItems().addAll(this.enterFullScreenItem, this.exitFullScreenItem, new SeparatorMenuItem(), this.closeAppItem);
        this.Users.getItems().addAll(this.LoginItem, this.LogOutItem, new SeparatorMenuItem(), this.UserAdministrationMenuItem);
        this.RecipesSettings.getItems().addAll(this.Units, this.Phases);
        this.alarms.getItems().addAll(this.journalAlarms, new SeparatorMenuItem(), this.notificationsCenter, new SeparatorMenuItem(), this.airPressureSettings);
        this.SystemMenu.getItems().addAll(this.configurations, new SeparatorMenuItem());
        this.operations.getItems().addAll(this.materialItem, new SeparatorMenuItem(), this.recipeEditorItem, this.batchCreatorItem, new SeparatorMenuItem(), this.reportingSystem);
        this.Help.getItems().add(this.about);

        try {
            this.scada = FXMLLoader.load(Resources.getResource("Views/SCADA.fxml"));
            this.SCADAPane = SCADAController.getParent();
            this.valves = SCADAController.getValves();
            this.pumps = SCADAController.getPumps();
            this.mixers = SCADAController.getMixers();
            this.levelBars = SCADAController.getLevelBars();
            this.levelLabels = SCADAController.getLevelLabels();
            this.weightLabels = SCADAController.getWeightLabels();
            this.waterLevel = SCADAController.getWaterLevel();
            this.waterPress = SCADAController.getWaterPress();
            this.airPress = SCADAController.getAirPress();
            this.scada.setScaleY(0.92);
        } catch (IOException ex) {
            log.logExcption("InitialWindow [GraphicsBuilder]", ex);
        }


        this.topBars.getChildren().addAll(this.menuBar, this.toolBar, this.lastAlarmField);
        this.topBars.setAlignment(Pos.CENTER);
        this.containerPane.getTabs().addAll(this.SCADATab);
        this.containerPane.setStyle("-fx-open-tab-animation: NONE; -fx-close-tab-animation: NONE;");
        this.SCADATab.setContent(this.scada);
        this.SCADATab.setClosable(false);
        this.SCADATab.setStyle("-fx-border-color: darkblue; -fx-border-width:0.1;");

        FontIcon loginFontIcon = new FontIcon(Entypo.LOGIN);
        FontIcon logoutFontIcon = new FontIcon(Entypo.LOG_OUT);
        FontIcon loginFontIconItem = new FontIcon(Entypo.LOGIN);
        FontIcon logoutFontIconItem = new FontIcon(Entypo.LOG_OUT);
        this.logIn.setGraphic(loginFontIcon);
        this.logOut.setGraphic(logoutFontIcon);
        this.LoginItem.setGraphic(loginFontIconItem);
        this.LogOutItem.setGraphic(logoutFontIconItem);
        this.root.setCenter(this.containerPane);
        this.root.setTop(this.topBars);
        this.scene.getStylesheets().add(Resources.getResource("Views/scada.css").toString());
        this.initialStage.setScene(this.scene);
        this.initialStage.setTitle("Mixing Platform");
        this.initialStage.setMaximized(true);

        try {
            this.initialStage.getIcons().add(new Image(ResourceUtils.getURL("Icons/splash.png").toString()));
        } catch (FileNotFoundException e) {
            log.logExcption("InitialWindow [GraphicsBuilder]", e);
        }


    }
    public void actionHandler() {
        try {
            this.Units.setOnAction((action) -> UnitsWindow.getWindow(this.initialStage).show());
            this.Phases.setOnAction((action) -> PhasesWindow.getWindow(this.initialStage).show());
            this.materialItem.setOnAction((action) -> MaterialsWindow.getMaterialsWindow(this.initialStage).show());
            this.batchCreatorItem.setOnAction(this::onBatchCreatorRequest);
            this.recipeEditorItem.setOnAction(this::onRecipeEditorRequest);
            this.reportingSystem.setOnAction((action) -> BatchArchiveWindow.getWindow(this.initialStage).show());
            this.configurations.setOnAction((action) -> {
            });
            this.journalAlarms.setOnAction(this::onJournalAlarmsPressed);
            this.airPressureSettings.setOnAction((action) -> Utilities.getUtilitiesWindow(this.initialStage).show());
            this.notificationsCenter.setOnAction((action) -> this.showNotificationCenter());
            this.enterFullScreenItem.setOnAction((action) -> {
                this.initialStage.hide();
                this.initialStage.setFullScreen(true);
                this.initialStage.show();
            });
            this.exitFullScreenItem.setOnAction((action) -> {
                this.initialStage.hide();
                this.initialStage.setFullScreen(false);
                this.initialStage.show();
            });
            this.closeAppItem.setOnAction((action) -> this.initialStage.close());
            this.shutDown.setOnMouseClicked((action) -> this.initialStage.close());
            this.startWaterFill.setOnMousePressed((action) -> this.controller.atStartWaterFill(Boolean.TRUE));
            this.startWaterFill.setOnMouseReleased((action) -> this.controller.atStartWaterFill(Boolean.FALSE));
            this.changeAllDevicesToAutomatic.setOnMousePressed((action) -> this.controller.onSetAllInAutoPressed(this.mixers, this.pumps, this.valves));
            this.changeAllDevicesToAutomatic.setOnMouseReleased((action) -> this.controller.onSetAllInAutoReleased());
            this.UserAdministrationMenuItem.setOnAction((action) -> {
                Stage stage = new Stage();
                Scene scene = new Scene(new BorderPane(this.userAdministrationWindow));
                stage.setScene(scene);
                stage.initOwner(this.initialStage);
                stage.initStyle(StageStyle.UTILITY);
                stage.setMinWidth(1200.0F);
                stage.setMinHeight(900.0F);
                stage.show();
            });
            this.logIn.setOnMouseClicked((action) -> this.controller.onLogIn());
            this.logOut.setOnMouseClicked((action) -> this.controller.onLogOut());
            this.LoginItem.setOnAction((action) -> this.controller.onLogIn());
            this.LogOutItem.setOnAction((action) -> this.controller.onLogOut());
            this.about.setOnAction((actionEvent) -> (new HelpWindow()).show());
            this.valves.forEach(this::handleValveBlockIcon);
            this.pumps.forEach(this::handlePumpBlockIcon);
            this.mixers.forEach(this::handleMixerBlockIcon);
            this.levelBars.forEach(this::handleLevelBlockIcon);
            this.setWaterTankLevel();
            this.confirmationMessageControl();
        } catch (Exception e) {
            log.logExcption("InitialWindow [actionHandler]", e);
        }
    }

    private void handleLevelBlockIcon(String name, Pane bar) {
        try {
            Pane pane = new Pane();
            this.SCADAPane.getChildren().add(pane);
            pane.setLayoutX(bar.getLayoutX());
            pane.setLayoutY(bar.getLayoutY());
            pane.setPrefWidth(bar.getWidth());
            pane.setPrefHeight(bar.getHeight());
            bar.toFront();
            bar.setOpacity(0.6);
            Weight data = this.controller.getWeightByName(name);
            Label label = this.levelLabels.get(name);
            Label weightLabel = this.weightLabels.get(name);
            label.setText("0.0 %");
            double Height = bar.getHeight();
            this.bindStatusToMWeight(data, pane, bar, label, weightLabel, Height);
            ((FloatProperty) data.getAllValues().get(WeightInput.Weight)).addListener((observable, oldValue, newValue) -> this.bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Zero)).addListener((observable, oldValue, newValue) -> this.bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Span)).addListener((observable, oldValue, newValue) -> this.bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Low_Warning_SP)).addListener((observable, oldValue, newValue) -> this.bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Low_Alarm_Sp)).addListener((observable, oldValue, newValue) -> this.bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.High_Warning_SP)).addListener((observable, oldValue, newValue) -> this.bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.High_Alarm_SP)).addListener((observable, oldValue, newValue) -> this.bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            Border back = pane.getBorder();
            bar.setOnMouseEntered((action) -> {
                bar.setCursor(Cursor.HAND);
                bar.setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2.0F))));
            });
            bar.setOnMouseExited((action) -> {
                bar.setCursor(Cursor.DEFAULT);
                bar.setBorder(back);
            });
            bar.setOnMouseClicked((action) -> {
                if (action.getButton().equals(MouseButton.PRIMARY)) {
                    try {
                        WeightFacePlate facePlate = new WeightFacePlate(this.initialStage, data, "Kg");
                        facePlate.setX(action.getScreenX() > (double) 1550.0F ? (double) 1500.0F : action.getScreenX());
                        facePlate.setY(action.getScreenY() > (double) 600.0F ? (double) 500.0F : action.getScreenY());
                        facePlate.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void handleMixerBlockIcon(String name, ImageView mixer) {
        Pane pane = new Pane();
        this.SCADAPane.getChildren().add(pane);
        pane.setLayoutX(mixer.getLayoutX() + (double) 5.0F);
        pane.setLayoutY(mixer.getLayoutY() - (double) 20.0F);
        pane.setPrefWidth(mixer.getFitWidth() - (double) 10.0F);
        pane.setPrefHeight(mixer.getFitHeight() - (double) 20.0F);
        mixer.toFront();
        Background back = pane.getBackground();
        Mixer data = this.controller.getMixerByName(name);
        this.bindStatusToMixer(data, mixer);
        ((BooleanProperty) data.getAllValues().get(MixerInput.Running)).addListener((observable, oldValue, newValue) -> this.bindStatusToMixer(data, mixer));
        ((BooleanProperty) data.getAllValues().get(MixerInput.Fault)).addListener((observable, oldValue, newValue) -> this.bindStatusToMixer(data, mixer));
        mixer.setOnMouseEntered((action) -> {
            mixer.setCursor(Cursor.HAND);
            pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setOpacity(0.6);
            pane.setEffect(new DropShadow(BlurType.ONE_PASS_BOX, Color.GRAY, 2.0F, 2.0F, 2.0F, 2.0F));
        });
        mixer.setOnMouseExited((action) -> {
            mixer.setCursor(Cursor.DEFAULT);
            pane.setBackground(back);
        });
        mixer.setOnMouseClicked((action) -> {
            try {
                MixerFacePlate facePlate = new MixerFacePlate(this.initialStage, data);
                facePlate.setX(action.getScreenX() > (double) 1550.0F ? (double) 1500.0F : action.getScreenX());
                facePlate.setY(action.getScreenY() > (double) 600.0F ? (double) 500.0F : action.getScreenY());
                facePlate.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
    private void handlePumpBlockIcon(String name, ImageView pump) {
        Pane pane = new Pane();
        this.SCADAPane.getChildren().add(pane);
        pane.setLayoutX(pump.getLayoutX() - (double) 5.0F);
        pane.setLayoutY(pump.getLayoutY() - (double) 5.0F);
        pane.setPrefWidth(pump.getFitWidth() + (double) 10.0F);
        pane.setPrefHeight(pump.getFitHeight() - (double) 10.0F);
        pump.toFront();
        Background back = pane.getBackground();
        Pump data = this.controller.getPumpByName(name);
        this.bindStatusToPump(data, pump);
        ((BooleanProperty) data.getAllValues().get(PumpInput.Running)).addListener((observable, oldValue, newValue) -> this.bindStatusToPump(data, pump));
        ((BooleanProperty) data.getAllValues().get(PumpInput.Fault)).addListener((observable, oldValue, newValue) -> this.bindStatusToPump(data, pump));
        pump.setOnMouseEntered((action) -> {
            pump.setCursor(Cursor.HAND);
            pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setOpacity(0.6);
            pane.setEffect(new DropShadow(BlurType.ONE_PASS_BOX, Color.GRAY, 2.0F, 2.0F, 2.0F, 2.0F));
            Tooltip.install(pump, new Tooltip("Pump :\nName = " + name));
        });
        pump.setOnMouseExited((action) -> {
            pump.setCursor(Cursor.DEFAULT);
            pane.setBackground(back);
        });
        pump.setOnMouseClicked((action) -> {
            try {
                PumpFacePlate facePlate = new PumpFacePlate(this.initialStage, data);
                facePlate.setX(action.getScreenX() > (double) 1550.0F ? (double) 1500.0F : action.getScreenX());
                facePlate.setY(action.getScreenY() > (double) 600.0F ? (double) 500.0F : action.getScreenY());
                facePlate.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
    private void handleValveBlockIcon(String name, ImageView valve) {
        Pane pane = new Pane();
        this.SCADAPane.getChildren().add(pane);
        pane.setLayoutX(valve.getLayoutX() - (double) 5.0F);
        pane.setLayoutY(valve.getLayoutY() - (double) 5.0F);
        pane.setPrefWidth(valve.getFitWidth() - (double) 10.0F);
        pane.setPrefHeight(valve.getFitHeight() + (double) 10.0F);
        valve.toFront();
        Background back = pane.getBackground();
        Valve data = this.controller.getValveByName(name);
        Label label = new Label("Valve :\nName = " + name);
        label.setPadding(new Insets(10.0F));
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        this.bindStatusToValve(data, valve);
        ((BooleanProperty) data.getAllValues().get(ValveInput.Opened_Closed)).addListener((observable, oldValue, newValue) -> this.bindStatusToValve(data, valve));
        ((BooleanProperty) data.getAllValues().get(ValveInput.Fault)).addListener((observable, oldValue, newValue) -> this.bindStatusToValve(data, valve));
        valve.setOnMouseEntered((action) -> {
            valve.setCursor(Cursor.HAND);
            pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setOpacity(0.6);
            pane.setEffect(new DropShadow(BlurType.ONE_PASS_BOX, Color.GRAY, 2.0F, 2.0F, 2.0F, 2.0F));
            Tooltip.install(valve, new Tooltip("Valve :\nName = " + name));
        });
        valve.setOnMouseExited((action) -> {
            valve.setCursor(Cursor.DEFAULT);
            pane.setBackground(back);
        });
        valve.setOnMouseClicked((action) -> {
            try {
                ValveFacePlate facePlate = new ValveFacePlate(this.initialStage, data);
                facePlate.setX(action.getScreenX() > (double) 1550.0F ? (double) 1500.0F : action.getScreenX());
                facePlate.setY(action.getScreenY() > (double) 600.0F ? (double) 500.0F : action.getScreenY());
                facePlate.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void setWaterTankLevel() {
        try {
            Pane bar = this.waterLevel;
            Pane pane = new Pane();
            this.SCADAPane.getChildren().add(pane);
            pane.setLayoutX(bar.getLayoutX());
            pane.setLayoutY(bar.getLayoutY());
            pane.setPrefWidth(bar.getWidth());
            pane.setPrefHeight(bar.getHeight());
            bar.toFront();
            bar.setOpacity(0.6);
            double Height = bar.getHeight();
            Weight data = this.controller.getWeightByName("L01");
            this.bindStatusToLevel(data, pane, bar, Height);
            ((FloatProperty) data.getAllValues().get(WeightInput.Weight)).addListener((observable, oldValue, newValue) -> this.bindStatusToLevel(data, pane, bar, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Zero)).addListener((observable, oldValue, newValue) -> this.bindStatusToLevel(data, pane, bar, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Span)).addListener((observable, oldValue, newValue) -> this.bindStatusToLevel(data, pane, bar, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Low_Warning_SP)).addListener((observable, oldValue, newValue) -> this.bindStatusToLevel(data, pane, bar, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Low_Alarm_Sp)).addListener((observable, oldValue, newValue) -> this.bindStatusToLevel(data, pane, bar, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.High_Warning_SP)).addListener((observable, oldValue, newValue) -> this.bindStatusToLevel(data, pane, bar, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.High_Alarm_SP)).addListener((observable, oldValue, newValue) -> this.bindStatusToLevel(data, pane, bar, Height));
            Border back = pane.getBorder();
            bar.setOnMouseEntered((action) -> {
                bar.setCursor(Cursor.HAND);
                bar.setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2.0F))));
            });
            bar.setOnMouseExited((action) -> {
                bar.setCursor(Cursor.DEFAULT);
                bar.setBorder(back);
            });
            bar.setOnMouseClicked((action) -> {
                if (action.getButton().equals(MouseButton.PRIMARY)) {
                    try {
                        WeightFacePlate facePlate = new WeightFacePlate(this.initialStage, data, "M");
                        facePlate.setX(action.getScreenX() > (double) 1550.0F ? (double) 1500.0F : action.getScreenX());
                        facePlate.setY(action.getScreenY() > (double) 600.0F ? (double) 500.0F : action.getScreenY());
                        facePlate.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showNotificationCenter() {
        Stage stage = new Stage();
        stage.setScene(new Scene(this.ncServicesView));
        stage.initOwner(this.initialStage);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Notification center");
        stage.show();
    }

    private void bindStatusToValve(RowDataDefinition data, ImageView item) {
        Platform.runLater(() -> {
            try {
                boolean opened_closed = ((BooleanDataType) data.getAllValues().get(ValveInput.Opened_Closed)).getValue();
                boolean fault = ((BooleanDataType) data.getAllValues().get(ValveInput.Fault)).getValue();
                if (opened_closed) {
                    this.changeColorOfImageView(Color.GREEN, item);
                } else {
                    this.changeColorOfImageView(Color.RED, item);
                }

                if (fault) {
                    this.changeColorOfImageView(Color.YELLOW, item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
    private void bindStatusToPump(RowDataDefinition data, ImageView item) {
        Platform.runLater(() -> {
            try {
                boolean running = ((BooleanDataType) data.getAllValues().get(PumpInput.Running)).getValue();
                boolean fault = ((BooleanDataType) data.getAllValues().get(PumpInput.Fault)).getValue();
                if (running) {
                    this.changeColorOfImageView(Color.GREEN, item);
                } else {
                    this.changeColorOfImageView(Color.RED, item);
                }

                if (fault) {
                    this.changeColorOfImageView(Color.YELLOW, item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
    private void bindStatusToMixer(Mixer data, ImageView item) {
        Platform.runLater(() -> {
            try {
                boolean running = ((BooleanDataType) data.getAllValues().get(MixerInput.Running)).getValue();
                boolean fault = ((BooleanDataType) data.getAllValues().get(MixerInput.Fault)).getValue();
                if (running) {
                    this.changeColorOfImageView(Color.GREEN, item);
                } else {
                    this.changeColorOfImageView(Color.RED, item);
                }

                if (fault) {
                    this.changeColorOfImageView(Color.YELLOW, item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
    private void bindStatusToMWeight(Weight data, Pane pane, Pane backGroundBar, Label label, Label weightLabel, double Height) {
        Platform.runLater(() -> {
            try {
                float qtyValue = ((RealDataType) data.getAllValues().get(WeightInput.Weight)).getValue();
                float zeroValue = ((RealDataType) data.getAllValues().get(WeightOutput.Zero)).getValue();
                float spanValue = ((RealDataType) data.getAllValues().get(WeightOutput.Span)).getValue();
                float lowWarningValue = ((RealDataType) data.getAllValues().get(WeightOutput.Low_Warning_SP)).getValue();
                float lowAlarmValue = ((RealDataType) data.getAllValues().get(WeightOutput.Low_Alarm_Sp)).getValue();
                float highWarningValue = ((RealDataType) data.getAllValues().get(WeightOutput.High_Warning_SP)).getValue();
                float highAlarmValue = ((RealDataType) data.getAllValues().get(WeightOutput.High_Alarm_SP)).getValue();
                float delta = spanValue - zeroValue;
                if (delta != 0.0F) {
                    double percentage = (qtyValue - zeroValue) / delta * 100.0F;
                    double calNewHeight = (double) ((qtyValue - zeroValue) / delta) * Height;
                    if (Double.isNaN(percentage)) {
                        percentage = 0.0F;
                    }

                    if (Double.isNaN(calNewHeight)) {
                        calNewHeight = 0.0F;
                    }

                    backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    label.setText(Round.RoundDouble(percentage, 1) + " %");
                    if ((double) ((qtyValue - zeroValue) / delta) * Height < Height) {
                        pane.setPrefHeight(Height - calNewHeight);
                    } else {
                        pane.setPrefHeight(Height);
                    }

                    if ((double) qtyValue == (double) 0.0F) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.BLACK.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (qtyValue < lowWarningValue && qtyValue > lowAlarmValue) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.YELLOW.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (qtyValue < lowWarningValue && qtyValue < lowAlarmValue) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (qtyValue > highWarningValue && qtyValue < highAlarmValue) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.YELLOW.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (qtyValue > highWarningValue && qtyValue > highAlarmValue) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.GREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                }

                weightLabel.setText(BigDecimal.valueOf(qtyValue).longValue() + " Kg");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
    private void bindStatusToLevel(Weight data, Pane pane, Pane backGroundBar, double Height) {
        Platform.runLater(() -> {
            try {
                float qtyValue = ((RealDataType) data.getAllValues().get(WeightInput.Weight)).getValue();
                float zeroValue = ((RealDataType) data.getAllValues().get(WeightOutput.Zero)).getValue();
                float spanValue = ((RealDataType) data.getAllValues().get(WeightOutput.Span)).getValue();
                float lowWarningValue = ((RealDataType) data.getAllValues().get(WeightOutput.Low_Warning_SP)).getValue();
                float lowAlarmValue = ((RealDataType) data.getAllValues().get(WeightOutput.Low_Alarm_Sp)).getValue();
                float highWarningValue = ((RealDataType) data.getAllValues().get(WeightOutput.High_Warning_SP)).getValue();
                float highAlarmValue = ((RealDataType) data.getAllValues().get(WeightOutput.High_Alarm_SP)).getValue();
                float delta = spanValue - zeroValue;
                if (delta != 0.0F) {
                    double percentage = (qtyValue - zeroValue) / delta * 100.0F;
                    double calNewHeight = (double) ((qtyValue - zeroValue) / delta) * Height;
                    if (Double.isNaN(percentage)) {
                        percentage = 0.0F;
                    }

                    if (Double.isNaN(calNewHeight)) {
                        calNewHeight = 0.0F;
                    }

                    backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    if ((double) ((qtyValue - zeroValue) / delta) * Height < Height) {
                        pane.setPrefHeight(Height - calNewHeight);
                    } else {
                        pane.setPrefHeight(Height);
                    }

                    if ((double) qtyValue == (double) 0.0F) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.BLACK.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (qtyValue < lowWarningValue && qtyValue > lowAlarmValue) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.YELLOW.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (qtyValue < lowWarningValue && qtyValue < lowAlarmValue) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (qtyValue > highWarningValue && qtyValue < highAlarmValue) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.YELLOW.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (qtyValue > highWarningValue && qtyValue > highAlarmValue) {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    } else {
                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.GREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    protected void changeColorOfImageView(Color color, ImageView imView) {
        Glow glow = new Glow(0.2);
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.GRAY, 1.0F, 1.0F, 1.0F, 1.0F);
        Light.Distant light = new Light.Distant(100.0F, 100.0F, color.brighter().brighter().brighter());
        Lighting lighting = new Lighting(light);
        Blend blend = new Blend(BlendMode.MULTIPLY, glow, shadow);
        Blend blend2 = new Blend(BlendMode.MULTIPLY, blend, lighting);
        imView.setEffect(blend2);
    }

    private void createBatchObserver(Batch batch) {
        Platform.runLater(() -> {
            BatchObserver batchObserver = new BatchObserver(this.initialStage, batch);
            if (!this.containerPane.getTabs().contains(batchObserver)) {
                this.containerPane.getTabs().add(batchObserver);
                this.batchObservers.put(batch.getId(), batchObserver);
                batchObserver.setOnBatchClose((batchObserver1) -> {
                    this.containerPane.getTabs().remove(batchObserver1);
                    this.batchObservers.remove(batchObserver1.getBatchID());
                });
                batchObserver.update();
                this.containerPane.getSelectionModel().select(batchObserver);
            }
        });
    }

    private void adjustGauges() {
        Gauge gauge1 = this.getGauge("Water level", "Meter");
        Gauge gauge2 = this.getGauge("Air pressure", "Bar");
        gauge1.valueProperty().bind(this.model.getGauge1());
        gauge2.valueProperty().bind(this.model.getGauge2());
        this.waterPress.getChildren().add(gauge1);
        this.airPress.getChildren().add(gauge2);
    }

    private Gauge getGauge(String label, String unit) {
        Gauge gauge = GaugeBuilder.create().prefSize(160.0F, 180.0F).foregroundBaseColor(Color.BLACK).title(label).titleColor(Color.BLACK).subTitle("").subTitleColor(Color.BLACK).unit(unit).unitColor(Color.BLACK).valueColor(Color.BLACK).decimals(5).lcdVisible(true).lcdDesign(LcdDesign.STANDARD).lcdFont(LcdFont.DIGITAL_BOLD).scaleDirection(ScaleDirection.CLOCKWISE).minValue(0.0F).maxValue(8.0F).tickLabelDecimals(0).tickLabelLocation(TickLabelLocation.INSIDE).tickLabelOrientation(TickLabelOrientation.HORIZONTAL).onlyFirstAndLastTickLabelVisible(false).tickLabelSectionsVisible(false).tickLabelColor(Color.BLACK).tickMarkSectionsVisible(false).majorTickMarksVisible(true).majorTickMarkType(TickMarkType.LINE).majorTickMarkColor(Color.BLACK).mediumTickMarksVisible(true).mediumTickMarkType(TickMarkType.LINE).mediumTickMarkColor(Color.BLACK).minorTickMarksVisible(true).minorTickMarkType(TickMarkType.LINE).minorTickMarkColor(Color.BLACK).needleShape(NeedleShape.ANGLED).needleSize(NeedleSize.STANDARD).needleColor(Color.CRIMSON).startFromZero(false).returnToZero(false).knobType(KnobType.STANDARD).knobColor(Color.LIGHTGRAY).interactive(false).checkThreshold(false).onThresholdExceeded((thresholdEvent) -> System.out.println("Threshold exceeded")).onThresholdUnderrun((thresholdEvent) -> System.out.println("Threshold underrun")).gradientBarEnabled(true).gradientBarStops(new Stop(0.0F, Color.RED), new Stop(5.0F, Color.YELLOW), new Stop(15.0F, Color.LIGHTGREEN)).sectionsVisible(true).checkSectionsForValue(true).areasVisible(true).markersVisible(true).animated(true).animationDuration(500L).build();
        gauge.setSkin(new QuarterSkin(gauge));
        return gauge;
    }

    private Clock getClock() {
        return ClockBuilder.create().skinType(ClockSkinType.TEXT).prefSize(100.0F, 30.0F).running(true).build();
    }

    private synchronized void onBatchCreatorRequest(ActionEvent event) {
        try {
            this.batchCreator.showAndReturnBatch().ifPresent(this::createBatchObserver);
        } catch (Exception e) {
            log.logExcption("InitilWindow [onBatchCreatorRequest]", e);
        }

    }

    private void onRecipeEditorRequest(ActionEvent event) {
        try {
            String retVal = this.selectUnitWindow();
            if (!retVal.equals("Cancel")) {
                this.recipeEditor.hide();
                this.recipeEditor.refreshAndUpdateAndShow(retVal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onJournalAlarmsPressed(ActionEvent event) {
        try {
            if (!this.containerPane.getTabs().contains(this.allAlarmsWindow)) {
                this.containerPane.getTabs().add(this.allAlarmsWindow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String selectUnitWindow() {
        Label label = new Label("Please select unit to create recipe");
        ComboBox<String> field = new ComboBox();
        field.setPromptText("Please enter the unit ");
        field.setPrefWidth(350.0F);
        field.getItems().addAll(this.controller.getAllUnitsNames());
        Button Cancel = new Button("Cancel");
        Button Ok = new Button("Ok");
        Cancel.setPrefWidth(150.0F);
        Ok.setPrefWidth(150.0F);
        HBox buttonsContainer = new HBox();
        buttonsContainer.getChildren().addAll(Ok, Cancel);
        buttonsContainer.setSpacing(10.0F);
        buttonsContainer.setPadding(new Insets(5.0F));
        GridPane container = new GridPane();
        container.add(field, 0, 0);
        container.setPadding(new Insets(5.0F));
        container.setVgap(5.0F);
        container.setHgap(5.0F);
        BorderPane root = new BorderPane();
        root.setBottom(buttonsContainer);
        root.setCenter(container);
        root.setTop(label);
        root.setPadding(new Insets(15.0F));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Please enter name ");
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(this.initialStage);
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        Cancel.setOnMouseClicked((event) -> {
            this.returnData = "Cancel";
            stage.close();
        });
        Ok.setOnMouseClicked((event) -> {
            if (!field.getValue().isEmpty()) {
                this.returnData = field.getValue();
            } else {
                this.returnData = "Cancel";
            }

            stage.close();
        });
        field.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (!field.getValue().isEmpty()) {
                    this.returnData = field.getValue();
                } else {
                    this.returnData = "Cancel";
                }

                stage.close();
            }

        });
        stage.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                this.returnData = "Cancel";
                stage.close();
            }

        });
        stage.setOnCloseRequest((action) -> this.returnData = "Cancel");
        field.requestFocus();
        stage.showAndWait();
        return this.returnData;
    }

    private void confirmationMessageControl() {
        if (((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).getValue() && !Mixer_1_Manual_Add_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
            Mixer_1_Manual_Add_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
        }

        if (((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).getValue() && !Mixer_2_Manual_Add_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
            Mixer_2_Manual_Add_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
        }

        if (((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).getValue() && !IPC_Fill_From_Mixer_1_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
            IPC_Fill_From_Mixer_1_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
        }

        if (((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).getValue() && !IPC_Fill_From_Mixer_2_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
            IPC_Fill_From_Mixer_2_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
        }

        if (((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).getValue() && !IPC_Fill_From_Tank_1_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
            IPC_Fill_From_Tank_1_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
        }

        if (((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).getValue() && !IPC_Fill_From_Tank_2_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
            IPC_Fill_From_Tank_2_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
        }

        if (((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).getValue() && !IPC_Fill_From_Tank_3_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
            IPC_Fill_From_Tank_3_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
        }

        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (!Mixer_1_Manual_Add_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
                        Mixer_1_Manual_Add_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
                    }

                });
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (!Mixer_2_Manual_Add_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
                        Mixer_2_Manual_Add_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
                    }

                });
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (!IPC_Fill_From_Mixer_1_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
                        IPC_Fill_From_Mixer_1_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
                    }

                });
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (!IPC_Fill_From_Mixer_2_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
                        IPC_Fill_From_Mixer_2_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
                    }

                });
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (!IPC_Fill_From_Tank_1_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
                        IPC_Fill_From_Tank_1_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
                    }

                });
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (!IPC_Fill_From_Tank_2_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
                        IPC_Fill_From_Tank_2_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
                    }

                });
            }

        });
        ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    if (!IPC_Fill_From_Tank_3_Message.getWindow(this.initialStage, this.allDataDefinitions).isShowing()) {
                        IPC_Fill_From_Tank_3_Message.getWindow(this.initialStage, this.allDataDefinitions).showAndWait();
                    }

                });
            }

        });
    }

    public void showNotificationDownButton(String title, String content, int duration) {
        FontIcon icon1 = new FontIcon("fas-info-circle");
        icon1.setIconColor(Color.BLUE);
        icon1.setIconSize(15);
        SimpleMFXNotificationPane notificationPane = new SimpleMFXNotificationPane(icon1, "System notification", title, content);
        notificationPane.setPrefWidth(500.0F);
        MFXNotification notification = new MFXNotification(notificationPane, false, true);
        notification.setHideAfterDuration(Duration.seconds(duration));
        notificationPane.setCloseHandler((closeEvent) -> notification.hideNotification());
        notificationPane.getOkButton().setOnMouseClicked((action) -> notification.hideNotification());
        Platform.runLater(() -> {
            NotificationsManager.send(NotificationPos.BOTTOM_RIGHT, notification, 5.0F, 5);
            notification.setAutoFix(true);
            notification.show(this.initialStage);
        });
    }

    public record ExceptionData(Exception e, String header) {
    }

    public static class ExceptionWindowRequestEvent extends ApplicationEvent {
        public ExceptionWindowRequestEvent(ExceptionData exceptionData) {
            super(exceptionData);
        }

        public ExceptionData getException() {
            return (ExceptionData) this.getSource();
        }
    }
}
