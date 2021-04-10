package com.batch.GUI.InitialWindow;

import com.batch.ApplicationContext;
import com.batch.PLCDataSource.PLC.ComplexDataType.*;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Services.LoggingService.LoggingService;
import com.batch.Utilities.Round;
import com.google.common.io.Resources;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Log4j2
public class FXApplication implements ApplicationListener<ApplicationContext.ApplicationListener> {

    //Scene
    private final BorderPane root = new BorderPane();
    private final Scene scene = new Scene(root);

    //Stages
    private Stage initialStage;


    private ExecutorService service;
    private final TabPane containerPane = new TabPane();

    private final Tab SCADATab = new Tab("    OverView    ");

    private final VBox batches = new VBox();
    private Parent scada = null;
    private final VBox topBars = new VBox();

    private final ToolBar toolBar = new ToolBar();
    private final MenuBar menuBar = new MenuBar();

    private final Menu view = new Menu("View");
    private final Menu Users = new Menu("Users");
    private final Menu RecipesSettings = new Menu("Recipes");
    private final Menu operations = new Menu("Operations");
    private final Menu alarms = new Menu("Alarms");
    private final Menu SystemMenu = new Menu("System");
    private final Menu Help = new Menu("Help");

    private final MenuItem enterFullScreenItem = new MenuItem("Enter full screen");
    private final MenuItem exitFullScreenItem = new MenuItem("Exit full screen");
    private final MenuItem closeAppItem = new MenuItem("Shutdown");
    private final MenuItem UserAdministrationMenuItem = new MenuItem("UserAdministration");
    private final MenuItem LoginItem = new MenuItem("Log in");
    private final MenuItem LogOutItem = new MenuItem("Log out");
    private final MenuItem Units = new MenuItem("Units");
    private final MenuItem Phases = new MenuItem("Phases");
    private final MenuItem materialItem = new MenuItem("Materials manager");
    private final MenuItem recipeEditorItem = new MenuItem("Recipe Editor");
    private final MenuItem batchCreatorItem = new MenuItem("Batch Creator");
    private final MenuItem reportingSystem = new MenuItem("Reporting System");
    private final MenuItem configurations = new MenuItem("Configurations");
    private final MenuItem autoUpdateAlarmsItem = new MenuItem("Auto update Alarms");
    private final MenuItem journalAlarms = new MenuItem("Journal Alarms");
    private final MenuItem airPressureSettings = new MenuItem("Air-Pressure Alarms");

    private final Button logIn = new Button("Log In");
    private final Button logOut = new Button("Log Out");
    private final Button shutDown = new Button("ShutDown");
    private final Button startWaterFill = new Button("Start water fill to HI_Alarm");
    private final Button changeAllDevicesToAutomatic = new Button("Auto all");

    private final Label connectionStatus = new Label("Starting connection with PLC system ...");
    private final Label airPressureStatus = new Label("Checking air pressure");
    private final Label overUnderVoltageStatus = new Label("Checking supply voltage");
    private final Label ESDStatus = new Label("Checking ESD status");

    private Map<String, ImageView> valves = new LinkedHashMap<>();
    private Map<String, ImageView> pumps = new LinkedHashMap<>();
    private Map<String, ImageView> mixers = new LinkedHashMap<>();
    private Map<String, Pane> levelBars = new LinkedHashMap<>();
    private Map<String, Label> levelLabels = new LinkedHashMap<>();
    private Map<String, Label> weightLabels = new LinkedHashMap<>();
    private AnchorPane SCADAPane;
    private Pane waterLevel;
    private Pane waterPress;
    private Pane airPress;

    private final Label lastAlarmField = new Label();

    //    private RecipeEditor recipeEditor;
//    private BatchCreator batchCreator;
//    private List<BatchObserver> batchObservers = Collections.synchronizedList(new LinkedList<>());
//    private AutoUpdateAlarmsWindow logWindow;
//    private AllAlarmsWindow allAlarmsWindow;

    private String returnData;


    private final Background onLineBackground = new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background offLineBackground = new Background(new BackgroundFill(Color.RED.brighter(), CornerRadii.EMPTY, Insets.EMPTY));


    @Autowired
    private LoggingService logger;

    @Autowired
    private InitialWindowController controller;

    private InitialWindowModel model;


    @Override
    public void onApplicationEvent(ApplicationContext.ApplicationListener listener) {
        initialStage = listener.getStage();
        model = controller.getModel();
        try {
            graphicsBuilder();
        } catch (Exception e) {
            log.fatal(e, e);
        }
    }


    private void graphicsBuilder() {
//        recipeEditor.initOwner(mainWindow);
//        recipeEditor.setHeight(900);
//        recipeEditor.setWidth(1500);
//        batchCreator.setHeight(600);
//        batchCreator.setWidth(800);


        lastAlarmField.prefWidthProperty().bind(topBars.widthProperty());
        lastAlarmField.setPadding(new Insets(3));
        lastAlarmField.setPrefHeight(30);
        lastAlarmField.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        connectionStatus.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        connectionStatus.setPadding(new Insets(5, 10, 5, 10));
        connectionStatus.setPrefSize(400, 30);
        connectionStatus.setAlignment(Pos.CENTER);

        airPressureStatus.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        airPressureStatus.setPadding(new Insets(5, 10, 5, 10));
        airPressureStatus.setPrefSize(180, 30);
        airPressureStatus.setAlignment(Pos.CENTER);

        overUnderVoltageStatus.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        overUnderVoltageStatus.setPadding(new Insets(5, 10, 5, 10));
        overUnderVoltageStatus.setPrefSize(180, 30);
        overUnderVoltageStatus.setAlignment(Pos.CENTER);

        ESDStatus.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        ESDStatus.setPadding(new Insets(5, 10, 5, 10));
        ESDStatus.setPrefSize(180, 30);
        ESDStatus.setAlignment(Pos.CENTER);

        batches.setPrefWidth(500);

        Pane spacePane = new Pane();
        spacePane.setPrefWidth(160);

        shutDown.setPrefWidth(100);
        startWaterFill.setPrefWidth(200);
        changeAllDevicesToAutomatic.setPrefWidth(200);

        toolBar.getItems().addAll(logIn, logOut, new Separator(), changeAllDevicesToAutomatic, startWaterFill, new Separator(), shutDown, spacePane, new Separator(), ESDStatus, overUnderVoltageStatus, airPressureStatus, new Separator(), connectionStatus, new Separator());

        menuBar.getMenus().addAll(view, Users, RecipesSettings, operations, alarms, SystemMenu, Help);
        view.getItems().addAll(enterFullScreenItem, exitFullScreenItem, new SeparatorMenuItem(), closeAppItem);
        Users.getItems().addAll(LoginItem, LogOutItem, new SeparatorMenuItem(), UserAdministrationMenuItem);
        RecipesSettings.getItems().addAll(Units, Phases);
        alarms.getItems().addAll(autoUpdateAlarmsItem, journalAlarms, airPressureSettings);
        SystemMenu.getItems().addAll(configurations, new SeparatorMenuItem());
        operations.getItems().addAll(materialItem, new SeparatorMenuItem(), recipeEditorItem, batchCreatorItem, new SeparatorMenuItem(), reportingSystem);

        try {
            scada = FXMLLoader.load(Resources.getResource("Views/SCADA.fxml"));
            SCADAPane = SCADAController.getParent();
            valves = SCADAController.getValves();
            pumps = SCADAController.getPumps();
            mixers = SCADAController.getMixers();
            levelBars = SCADAController.getLevelBars();
            levelLabels = SCADAController.getLevelLabels();
            weightLabels = SCADAController.getWeightLabels();
            waterLevel = SCADAController.getWaterLevel();
            waterPress = SCADAController.getWaterPress();
            airPress = SCADAController.getAirPress();

            scada.setScaleY(0.92);

        } catch (IOException ex) {
            Logger.getLogger(FXApplication.class.getName()).log(Level.SEVERE, null, ex);
            log.fatal(ex, ex);
        }

//        adjustGauges();
        topBars.getChildren().addAll(menuBar, toolBar, lastAlarmField);
        topBars.setAlignment(Pos.CENTER);



        containerPane.getTabs().addAll(SCADATab);
        containerPane.setStyle("-fx-open-tab-animation: NONE; -fx-close-tab-animation: NONE;");

        SCADATab.setContent(scada);
        SCADATab.setClosable(false);
        SCADATab.setStyle("-fx-border-color: darkblue; -fx-border-width:0.1;");


        //----------------------------------------------------------------------------------------------------------------------------------
        //Binding data
        connectionStatus.textProperty().bind(model.getConnectionInfo());
        connectionStatus.backgroundProperty().bind(Bindings.when(model.getConnectionStatus()).then(onLineBackground).otherwise(offLineBackground));



        //----------------------------------------------------------------------------------------------------------------------------------

        root.setCenter(containerPane);
        root.setTop(topBars);

        scene.getStylesheets().add(Resources.getResource("Styles/scada.css").toString());

        initialStage.setScene(scene);
        initialStage.setTitle("Mixing Platform");
        initialStage.setMaximized(true);
        initialStage.show();
    }

    @EventListener
    public void actionHandler(ContextStartedEvent event) {
        try {
//            materialItem.setOnAction(this::onMaterialManagerRequest);
//            batchCreatorItem.setOnAction(this::onBatchCreatorRequest);
//            recipeEditorItem.setOnAction(this::onRecipeEditorRequest);
//            reportaItem.setOnAction(this::onReportRequest);
            configurations.setOnAction(action -> {
//                ConfigurationsWindow.getConfigurationWindow(mainWindow, logger, service).show();
            });
//            autoUpdateAlarmsItem.setOnAction(this::onAutoUpdateAlarmsPressed);
//            journalAlarms.setOnAction(this::onJouralAlarmsPressed);
//            airPressureSettings.setOnAction(this::onAirPressureAlarmsSettings);

            enterFullScreenItem.setOnAction(action -> {
                initialStage.hide();
                initialStage.setFullScreen(true);
                initialStage.show();
            });
            exitFullScreenItem.setOnAction(action -> {
                initialStage.hide();
                initialStage.setFullScreen(false);
                initialStage.show();
            });

            closeAppItem.setOnAction(action -> {
                initialStage.close();
            });
            shutDown.setOnMouseClicked(action -> {
                initialStage.close();
            });

            startWaterFill.setOnMousePressed(action -> controller.atStartWaterFill(Boolean.TRUE));
            startWaterFill.setOnMouseReleased(action -> controller.atStartWaterFill(Boolean.FALSE));

            changeAllDevicesToAutomatic.setOnMousePressed(action -> controller.onSetAllInAutoPressed(mixers, pumps, valves));
            changeAllDevicesToAutomatic.setOnMouseReleased(action -> controller.onSetAllInAutoReleased());

            UserAdministrationMenuItem.setOnAction(action -> {});
            logIn.setOnMouseClicked(action -> controller.onLogIn());
            logOut.setOnMouseClicked(action -> controller.onLogOut());
            LoginItem.setOnAction(action -> controller.onLogIn());
            LogOutItem.setOnAction(action -> controller.onLogOut());

            valves.forEach(this::handleValveBlockIcon);
            pumps.forEach(this::handlePumpBlockIcon);
            mixers.forEach(this::handleMixerBlockIcon);
            levelBars.forEach(this::handleLevelBlockIcon);

//            Units.setOnAction(action -> {
//                UnitsWindow.getWindow(mainWindow).show();
//            });
//            Phases.setOnAction(action -> {
//                PhasesWindow.getWindow(mainWindow).showWindow();
//            });

//            setWaterTankLevel();
//            confirmationMessageControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLevelBlockIcon(String name, Pane bar) {
        try {
            Pane pane = new Pane();
            SCADAPane.getChildren().add(pane);
            pane.setLayoutX(bar.getLayoutX());
            pane.setLayoutY(bar.getLayoutY());
            pane.setPrefWidth(bar.getWidth());
            pane.setPrefHeight(bar.getHeight());
            bar.toFront();

            bar.setOpacity(0.6);

            Weight data = controller.getWeightByName(name);
            Label label = levelLabels.get(name);
            Label weightLabel = weightLabels.get(name);

            label.setText("0.0 %");

            double Height = bar.getHeight();

            bindStatusToMWeight(data, pane, bar, label, weightLabel, Height);
            ((FloatProperty) data.getAllValues().get(WeightInput.Weight)).addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Zero)).addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Span)).addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Low_Warning_SP)).addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.Low_Alarm_Sp)).addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.High_Warning_SP)).addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));
            ((FloatProperty) data.getAllValues().get(WeightOutput.High_Alarm_SP)).addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> bindStatusToMWeight(data, pane, bar, label, weightLabel, Height));

            Border back = pane.getBorder();
            bar.setOnMouseEntered(action -> {
                bar.setCursor(Cursor.HAND);
                bar.setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
            });
            bar.setOnMouseExited(action -> {
                bar.setCursor(Cursor.DEFAULT);
                bar.setBorder(back);
            });
//                    bar.setOnMouseClicked(action -> {
//                        if (action.getButton().equals(MouseButton.PRIMARY)) {
//                            try {
//                                WeightFacePlate facePlate = new WeightFacePlate(GUI.Windows.MainWindow.mainWindow, data, "Kg");
//                                facePlate.setX(action.getScreenX() > 1550 ? 1500 : action.getScreenX());
//                                facePlate.setY(action.getScreenY() > 600 ? 500 : action.getScreenY());
//                                facePlate.show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    bar.setOnContextMenuRequested(action -> {
//                        MenuItem trend = new MenuItem("Show realtime trend");
//                        MenuItem log = new MenuItem("Show Logged data");
//                        ContextMenu menu = new ContextMenu(trend, log);
//                        menu.show(initialStage, action.getScreenX(), action.getScreenY());
//                        trend.setOnAction(event -> {
//                            RealTimeTrend trenWindow = new RealTimeTrend(mainWindow, data.getAllValues().get(WeightInput.Weight), name);
//                            trenWindow.startTrending(1, 150);
//                            mainWindow.showingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                                if (!newValue) {
//                                    trenWindow.close();
//                                }
//                            });
//                        });
//                        log.setOnAction(event -> {
//                            try {
//                                LogTrend trenWindow = new LogTrend(mainWindow, data.getAllValues().get(WeightInput.Weight), name, WeightInput.Weight.toString());
//                                trenWindow.startTrending();
//                                mainWindow.showingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                                    if (!newValue) {
//                                        trenWindow.close();
//                                    }
//                                });
//                            } catch (Exception e) {
//
//                            }
//
//                        });
//                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleMixerBlockIcon(String name, ImageView mixer) {
        Pane pane = new Pane();
        SCADAPane.getChildren().add(pane);
        pane.setLayoutX(mixer.getLayoutX() + 5);
        pane.setLayoutY(mixer.getLayoutY() - 20);
        pane.setPrefWidth(mixer.getFitWidth() - 10);
        pane.setPrefHeight(mixer.getFitHeight() - 20);
        mixer.toFront();

        Background back = pane.getBackground();
        Mixer data = controller.getMixerByName(name);

        bindStatusToMixer(data, mixer);
        ((BooleanProperty) data.getAllValues().get(MixerInput.Running)).addListener((observable, oldValue, newValue) -> bindStatusToMixer(data, mixer));
        ((BooleanProperty) data.getAllValues().get(MixerInput.Fault)).addListener((observable, oldValue, newValue) -> bindStatusToMixer(data, mixer));

        mixer.setOnMouseEntered(action -> {
            mixer.setCursor(Cursor.HAND);
            pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setOpacity(0.6);
            pane.setEffect(new DropShadow(BlurType.ONE_PASS_BOX, Color.GRAY, 2, 2, 2, 2));
        });
        mixer.setOnMouseExited(action -> {
            mixer.setCursor(Cursor.DEFAULT);
            pane.setBackground(back);
        });
//                mixer.setOnMouseClicked(action -> {
//                    try {
//                        System.err.println("Weight " + name + " " + data);
//                        MixerFacePlate facePlate = new MixerFacePlate(GUI.Windows.MainWindow.mainWindow, data);
//                        facePlate.setX(action.getScreenX() > 1550 ? 1500 : action.getScreenX());
//                        facePlate.setY(action.getScreenY() > 600 ? 500 : action.getScreenY());
//                        facePlate.show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
    }
    private void handlePumpBlockIcon(String name, ImageView pump) {
        Pane pane = new Pane();
        SCADAPane.getChildren().add(pane);
        pane.setLayoutX(pump.getLayoutX() - 5);
        pane.setLayoutY(pump.getLayoutY() - 5);
        pane.setPrefWidth(pump.getFitWidth() + 10);
        pane.setPrefHeight(pump.getFitHeight() - 10);
        pump.toFront();

        Background back = pane.getBackground();
        Pump data = controller.getPumpByName(name);

        bindStatusToPump(data, pump);
        ((BooleanProperty) data.getAllValues().get(PumpInput.Running)).addListener((observable, oldValue, newValue) -> bindStatusToPump(data, pump));
        ((BooleanProperty) data.getAllValues().get(PumpInput.Fault)).addListener((observable, oldValue, newValue) -> bindStatusToPump(data, pump));

        pump.setOnMouseEntered(action -> {
            pump.setCursor(Cursor.HAND);
            pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setOpacity(0.6);
            pane.setEffect(new DropShadow(BlurType.ONE_PASS_BOX, Color.GRAY, 2, 2, 2, 2));
            Tooltip.install(pump, new Tooltip("Pump :\nName = " + name));
        });
        pump.setOnMouseExited(action -> {
            pump.setCursor(Cursor.DEFAULT);
            pane.setBackground(back);
        });

//                pump.setOnMouseClicked(action -> {
//                    PumpFacePlate facePlate = new PumpFacePlate(GUI.Windows.MainWindow.mainWindow, data);
//                    facePlate.setX(action.getScreenX() > 1550 ? 1500 : action.getScreenX());
//                    facePlate.setY(action.getScreenY() > 600 ? 500 : action.getScreenY());
//                    facePlate.show();
//                });
    }
    private void handleValveBlockIcon(String name, ImageView valve) {
        Pane pane = new Pane();
        SCADAPane.getChildren().add(pane);
        pane.setLayoutX(valve.getLayoutX() - 5);
        pane.setLayoutY(valve.getLayoutY() - 5);
        pane.setPrefWidth(valve.getFitWidth() - 10);
        pane.setPrefHeight(valve.getFitHeight() + 10);
        valve.toFront();

        Background back = pane.getBackground();
        Valve data = controller.getValveByName(name);

        Label label = new Label("Valve :\nName = " + name);
        label.setPadding(new Insets(10));
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));

        bindStatusToValve(data, valve);
        ((BooleanProperty) data.getAllValues().get(ValveInput.Opened_Closed)).addListener((observable, oldValue, newValue) -> bindStatusToValve(data, valve));
        ((BooleanProperty) data.getAllValues().get(ValveInput.Fault)).addListener((observable, oldValue, newValue) -> bindStatusToValve(data, valve));

        valve.setOnMouseEntered(action -> {
            valve.setCursor(Cursor.HAND);
            pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setOpacity(0.6);
            pane.setEffect(new DropShadow(BlurType.ONE_PASS_BOX, Color.GRAY, 2, 2, 2, 2));

            Tooltip.install(valve, new Tooltip("Valve :\nName = " + name));
        });
        valve.setOnMouseExited(action -> {
            valve.setCursor(Cursor.DEFAULT);
            pane.setBackground(back);
        });

//                valve.setOnMouseClicked(action -> {
//                    ValveFacePlate facePlate = new ValveFacePlate(GUI.Windows.MainWindow.mainWindow, data);
//                    facePlate.setX(action.getScreenX() > 1550 ? 1500 : action.getScreenX());
//                    facePlate.setY(action.getScreenY() > 600 ? 500 : action.getScreenY());
//                    facePlate.show();
//                });
    }


    private void bindStatusToValve(RowDataDefinition data, ImageView item) {
        Platform.runLater(() -> {
            try {
                boolean opened_closed = ((BooleanDataType) data.getAllValues().get(ValveInput.Opened_Closed)).getValue();
                boolean fault = ((BooleanDataType) data.getAllValues().get(ValveInput.Fault)).getValue();

                if (opened_closed) {
                    changeColorOfImageView(Color.GREEN, item);
                } else {
                    changeColorOfImageView(Color.RED, item);
                }
                if (fault) {
                    changeColorOfImageView(Color.YELLOW, item);
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
                    changeColorOfImageView(Color.GREEN, item);
                } else {
                    changeColorOfImageView(Color.RED, item);
                }
                if (fault) {
                    changeColorOfImageView(Color.YELLOW, item);
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
                    changeColorOfImageView(Color.GREEN, item);
                } else {
                    changeColorOfImageView(Color.RED, item);
                }
                if (fault) {
                    changeColorOfImageView(Color.YELLOW, item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private synchronized void bindStatusToMWeight(Weight data, Pane pane, Pane backGroundBar, Label label, Label weightLabel, double Height) {
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
                if (delta != 0) {
                    double percentage = ((qtyValue - zeroValue) / delta) * 100;
                    double calNewHeight = (qtyValue - zeroValue) / delta * Height;

                    if (Double.isNaN(percentage)) {
                        percentage = 0.0;
                    }
                    if (Double.isNaN(calNewHeight)) {
                        calNewHeight = 0.0;
                    }

                    backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

                    label.setText(Round.RoundDouble(percentage, 1) + " %");

                    if (((qtyValue - zeroValue) / delta * Height) < Height) {
                        pane.setPrefHeight(Height - calNewHeight);
                    } else {
                        pane.setPrefHeight(Height);
                    }

                    if (qtyValue == 0.0) {
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

//    private synchronized void bindStatusToLevel(Weight data, Pane pane, Pane backGroundBar, double Height) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    float qtyValue = ((RealDataType) data.getAllValues().get(WeightInput.Weight)).getValue();
//                    float zeroValue = ((RealDataType) data.getAllValues().get(WeightOutput.Zero)).getValue();
//                    float spanValue = ((RealDataType) data.getAllValues().get(WeightOutput.Span)).getValue();
//                    float lowWarningValue = ((RealDataType) data.getAllValues().get(WeightOutput.Low_Warning_SP)).getValue();
//                    float lowAlarmValue = ((RealDataType) data.getAllValues().get(WeightOutput.Low_Alarm_Sp)).getValue();
//                    float highWarningValue = ((RealDataType) data.getAllValues().get(WeightOutput.High_Warning_SP)).getValue();
//                    float highAlarmValue = ((RealDataType) data.getAllValues().get(WeightOutput.High_Alarm_SP)).getValue();
//
//                    float delta = spanValue - zeroValue;
//                    if (delta != 0) {
//                        double percentage = ((qtyValue - zeroValue) / delta) * 100;
//                        double calNewHeight = (qtyValue - zeroValue) / delta * Height;
//
//                        if (Double.isNaN(percentage)) {
//                            percentage = 0.0;
//                        }
//                        if (Double.isNaN(calNewHeight)) {
//                            calNewHeight = 0.0;
//                        }
//
//                        backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//                        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
//
//                        if (((qtyValue - zeroValue) / delta * Height) < Height) {
//                            pane.setPrefHeight(Height - calNewHeight);
//                        } else {
//                            pane.setPrefHeight(Height);
//                        }
//
//                        if (qtyValue == 0.0) {
//                            backGroundBar.setBackground(new Background(new BackgroundFill(Color.BLACK.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
//                        } else if (qtyValue < lowWarningValue && qtyValue > lowAlarmValue) {
//                            backGroundBar.setBackground(new Background(new BackgroundFill(Color.YELLOW.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
//                        } else if (qtyValue < lowWarningValue && qtyValue < lowAlarmValue) {
//                            backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
//                        } else if (qtyValue > highWarningValue && qtyValue < highAlarmValue) {
//                            backGroundBar.setBackground(new Background(new BackgroundFill(Color.YELLOW.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
//                        } else if (qtyValue > highWarningValue && qtyValue > highAlarmValue) {
//                            backGroundBar.setBackground(new Background(new BackgroundFill(Color.RED.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
//                        } else {
//                            backGroundBar.setBackground(new Background(new BackgroundFill(Color.GREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
//                        }
//                    } else {
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
    protected synchronized void changeColorOfImageView(Color color, ImageView imView) {
        try {
            Glow glow = new Glow(0.2);
            DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.GRAY, 1, 1, 1, 1);
            Light.Distant light = new Light.Distant(100, 100, color.brighter().brighter().brighter());
            Lighting lighting = new Lighting(light);
            Blend blend = new Blend(BlendMode.MULTIPLY, glow, shadow);
            Blend blend2 = new Blend(BlendMode.MULTIPLY, blend, lighting);

            imView.setEffect(blend2);
        } catch (Exception e) {
            e.printStackTrace();
            log.fatal(e,e);
        }
    }


//    private synchronized void onReportRequest(ActionEvent event) {
//        try {
//            scene.setCursor(Cursor.WAIT);
//            CompletableFuture.supplyAsync(() -> {
//                Platform.runLater(() -> {
//                    BatchArchieveWindow.getWindow(mainWindow).showAndUpdate();
//                    scene.setCursor(Cursor.DEFAULT);
//                });
//                return null;
//            }, service);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private synchronized void onMaterialManagerRequest(ActionEvent event) {
//        try {
//            scene.setCursor(Cursor.WAIT);
//            CompletableFuture.supplyAsync(() -> {
//                Platform.runLater(() -> {
//                    MaterialsWindow.getMaterialsWindow(mainWindow).showAndUpdate();
//                    scene.setCursor(Cursor.DEFAULT);
//                });
//                return null;
//            }, service);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private synchronized void onBatchCreatorRequest(ActionEvent event) {
//        try {
//            scene.setCursor(Cursor.WAIT);
//            CompletableFuture.supplyAsync(() -> {
//                Platform.runLater(() -> {
//                    handleEvents(mainWindow, batchCreator, batchObservers, containerPane);
//                    scene.setCursor(Cursor.DEFAULT);
//                });
//                return null;
//            }, service);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private synchronized void onRecipeEditorRequest(ActionEvent event) {
//        try {
//            CompletableFuture.supplyAsync(() -> {
//                Platform.runLater(() -> {
//                    String retVal = selectUnitWindow();
//                    if (!retVal.equals("Cancel")) {
//                        scene.setCursor(Cursor.WAIT);
//                        recipeEditor.hide();
//                        recipeEditor.refreshAndUpdateAndShow(retVal);
//                    }
//                    scene.setCursor(Cursor.DEFAULT);
//                });
//                return null;
//            }, service);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private synchronized void onAutoUpdateAlarmsPressed(ActionEvent event) {
//        Platform.runLater(() -> {
//            try {
//                if (!containerPane.getTabs().contains(logWindow)) {
//                    containerPane.getTabs().add(logWindow);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//    }
//    private synchronized void onJouralAlarmsPressed(ActionEvent event) {
//        Platform.runLater(() -> {
//            try {
//                if (!containerPane.getTabs().contains(allAlarmsWindow)) {
//                    containerPane.getTabs().add(allAlarmsWindow);
//                    allAlarmsWindow.update();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//    }
//    private synchronized void onAirPressureAlarmsSettings(ActionEvent event) {
//        Platform.runLater(() -> {
//            try {
//                Stage window = Utilities.getUtilitiesWindow(allDataDefinitions, mainWindow);
//                if (!window.isShowing()) {
//                    window.show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//    }

//
//    private void adjustGauges() {
//        Platform.runLater(() -> {
//            Gauge gauge1 = getGauge("Water level", "Meter");
//            Gauge gauge2 = getGauge("Air pressure", "Bar");
//
//            ((RealDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Water_Pressure)).addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            gauge1.valueProperty().setValue(newValue);
//                        }
//                    });
//                }
//            });
//            ((RealDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Air_Pressure)).addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            gauge2.valueProperty().setValue(newValue);
//                        }
//                    });
//                }
//            });
//
//            waterPress.getChildren().add(gauge1);
//            airPress.getChildren().add(gauge2);
//        });
//    }
//    private Gauge getGauge(String label, String unit) {
//        Gauge gauge
//                = GaugeBuilder.create()
//                .prefSize(160, 180) // Preferred size of control
//                .foregroundBaseColor(Color.BLACK) // Color for title, subtitle, unit, value, tick label, zeroColor, tick mark, major tick mark, medium tick mark and minor tick mark
//                .title(label) // Text for title
//                .titleColor(Color.BLACK) // Color for title text
//                .subTitle("") // Text for subtitle
//                .subTitleColor(Color.BLACK) // Color for subtitle text
//                .unit(unit) // Text for unit
//                .unitColor(Color.BLACK) // Color for unit text
//                .valueColor(Color.BLACK) // Color for value text
//                .decimals(5) // Number of decimals for the value/lcd text
//                .lcdVisible(true) // LCD instead of the plain value text
//                .lcdDesign(LcdDesign.STANDARD) // Design for LCD
//                .lcdFont(LcdFont.DIGITAL_BOLD) // Font for LCD (STANDARD, LCD, DIGITAL, DIGITAL_BOLD, ELEKTRA)
//                .scaleDirection(ScaleDirection.CLOCKWISE) // Direction of Scale (CLOCKWISE, COUNTER_CLOCKWISE)
//                .minValue(0) // Start value of Scale
//                .maxValue(8) // End value of Scale
//                .tickLabelDecimals(0) // Number of decimals for tick labels
//                .tickLabelLocation(TickLabelLocation.INSIDE) // Should tick labels be inside or outside Scale (INSIDE, OUTSIDE)
//                .tickLabelOrientation(TickLabelOrientation.HORIZONTAL) // Orientation of tick labels (ORTHOGONAL,  HORIZONTAL, TANGENT)
//                .onlyFirstAndLastTickLabelVisible(false) // Should only the first and last tick label be visible
//                .tickLabelSectionsVisible(false) // Should sections for tick labels be visible
//                .tickLabelColor(Color.BLACK) // Color for tick labels (overriden by tick label sections)
//                .tickMarkSectionsVisible(false) // Should sections for tick marks be visible
//                .majorTickMarksVisible(true) // Should major tick marks be visible
//                .majorTickMarkType(TickMarkType.LINE) // Tick mark type for major tick marks (LINE, DOT, TRIANGLE, TICK_LABEL)
//                .majorTickMarkColor(Color.BLACK) // Color for major tick marks (overriden by tick mark sections)
//                .mediumTickMarksVisible(true) // Should medium tick marks be visible
//                .mediumTickMarkType(TickMarkType.LINE) // Tick mark type for medium tick marks (LINE, DOT, TRIANGLE)
//                .mediumTickMarkColor(Color.BLACK) // Color for medium tick marks (overriden by tick mark sections)
//                .minorTickMarksVisible(true) // Should minor tick marks be visible
//                .minorTickMarkType(TickMarkType.LINE) // Tick mark type for minor tick marks (LINE, DOT, TRIANGLE)
//                .minorTickMarkColor(Color.BLACK) // Color for minor tick marks (override by tick mark sections)
//                .needleShape(NeedleShape.ANGLED) // Shape of needle (ANGLED, ROUND, FLAT)
//                .needleSize(NeedleSize.STANDARD) // Size of needle (THIN, STANDARD, THICK)
//                .needleColor(Color.CRIMSON) // Color of needle
//                .startFromZero(false) // Should needle start from the 0 value
//                .returnToZero(false) // Should needle return to the 0 value (only makes sense when animated==true)
//                .knobType(KnobType.STANDARD) // Type for center knob (STANDARD, PLAIN, METAL, FLAT)
//                .knobColor(Color.LIGHTGRAY) // Color of center knob
//                .interactive(false) // Should center knob be act as button
//                .checkThreshold(false) // Should each value be checked against threshold
//                .onThresholdExceeded(thresholdEvent -> System.out.println("Threshold exceeded")) // Handler (triggered if checkThreshold==true and the threshold is exceeded)
//                .onThresholdUnderrun(thresholdEvent -> System.out.println("Threshold underrun")) // Handler (triggered if checkThreshold==true and the threshold is underrun)
//                .gradientBarEnabled(true) // Should gradient filled bar be visible to visualize a range
//                .gradientBarStops(new Stop(0.0, Color.RED), // Color gradient that will be use to color fill bar
//                        new Stop(5, Color.YELLOW),
//                        new Stop(15.0, Color.LIGHTGREEN))
//                .sectionsVisible(true) // Should sections be visible
//                .checkSectionsForValue(true) // Should each section be checked against current value (if true section events will be fired)
//                .areasVisible(true) // Should areas be visible
//                .markersVisible(true) // Should markers be visible
//                .animated(true) // Should needle be animated
//                .animationDuration(500) // Speed of needle in milliseconds (10 - 10000 ms)
//                .build();
//
//        gauge.setSkin(new QuarterSkin(gauge));
//
//        return gauge;
//    }
//    private void setWaterTankLevel() {
//        Platform.runLater(() -> {
//            try {
//                Pane bar = waterLevel;
//
//                Pane pane = new Pane();
//                SCADAPane.getChildren().add(pane);
//                pane.setLayoutX(bar.getLayoutX());
//                pane.setLayoutY(bar.getLayoutY());
//                pane.setPrefWidth(bar.getWidth());
//                pane.setPrefHeight(bar.getHeight());
//                bar.toFront();
//
//                bar.setOpacity(0.6);
//
//                double Height = bar.getHeight();
//                Weight data = (Weight) allDataDefinitions.get("L01");
//
//                bindStatusToLevel(data, pane, bar, Height);
//                ((FloatProperty) data.getAllValues().get(WeightInput.Weight)).addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//                        bindStatusToLevel(data, pane, bar, Height);
//                    }
//                });
//                ((FloatProperty) data.getAllValues().get(WeightOutput.Zero)).addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//                        bindStatusToLevel(data, pane, bar, Height);
//                    }
//                });
//                ((FloatProperty) data.getAllValues().get(WeightOutput.Span)).addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//                        bindStatusToLevel(data, pane, bar, Height);
//                    }
//                });
//                ((FloatProperty) data.getAllValues().get(WeightOutput.Low_Warning_SP)).addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//                        bindStatusToLevel(data, pane, bar, Height);
//                    }
//                });
//                ((FloatProperty) data.getAllValues().get(WeightOutput.Low_Alarm_Sp)).addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//                        bindStatusToLevel(data, pane, bar, Height);
//                    }
//                });
//                ((FloatProperty) data.getAllValues().get(WeightOutput.High_Warning_SP)).addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//                        bindStatusToLevel(data, pane, bar, Height);
//                    }
//                });
//                ((FloatProperty) data.getAllValues().get(WeightOutput.High_Alarm_SP)).addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//                        bindStatusToLevel(data, pane, bar, Height);
//                    }
//                });
//
//                Border back = pane.getBorder();
//                bar.setOnMouseEntered(action -> {
//                    bar.setCursor(Cursor.HAND);
//                    bar.setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//                });
//                bar.setOnMouseExited(action -> {
//                    bar.setCursor(Cursor.DEFAULT);
//                    bar.setBorder(back);
//                });
//                bar.setOnMouseClicked(action -> {
//                    if (action.getButton().equals(MouseButton.PRIMARY)) {
//                        try {
//                            WeightFacePlate facePlate = new WeightFacePlate(GUI.Windows.MainWindow.mainWindow, data, "M");
//                            facePlate.setX(action.getScreenX() > 1550 ? 1500 : action.getScreenX());
//                            facePlate.setY(action.getScreenY() > 600 ? 500 : action.getScreenY());
//                            facePlate.show();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                bar.setOnContextMenuRequested(action -> {
//                    MenuItem trend = new MenuItem("Show realtime trend");
//                    MenuItem log = new MenuItem("Show Logged data");
//                    ContextMenu menu = new ContextMenu(trend);
//                    menu.show(mainWindow, action.getScreenX(), action.getScreenY());
//                    trend.setOnAction(event -> {
//                        RealTimeTrend trenWindow = new RealTimeTrend(mainWindow, data.getAllValues().get(WeightInput.Weight), "L01");
//                        trenWindow.startTrending(1, 150);
//                        mainWindow.showingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                            if (!newValue) {
//                                trenWindow.close();
//                            }
//                        });
//                    });
//                    log.setOnAction(event -> {
//                        LogTrend trenWindow = new LogTrend(mainWindow, data.getAllValues().get(WeightInput.Weight), "L01", WeightInput.Weight.toString());
//                        trenWindow.startTrending();
//                        mainWindow.showingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                            if (!newValue) {
//                                trenWindow.close();
//                            }
//                        });
//                    });
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//    }
//    private Clock getClock() {
//        Clock clock = ClockBuilder.create()
//                .skinType(ClockSkinType.TEXT)
//                .prefSize(100, 30)
//                .running(true)
//                .build();
//        return clock;
//    }
//    private String selectUnitWindow() {
//
//        Label label = new Label("Please select unit to create recipe");
//        ComboBox<String> field = new ComboBox();
//        field.setPromptText("Please enter the unit ");
//        field.setPrefWidth(350);
//        field.getItems().addAll(UnitDAOImpl.getInstance().GetAll().stream().map(unit -> unit.getName()).collect(Collectors.toList()));
//
//        Button Cancel = new Button("Cancel");
//        Button Ok = new Button("Ok");
//
//        Cancel.setPrefWidth(150);
//        Ok.setPrefWidth(150);
//
//        HBox buttonsContainer = new HBox();
//        buttonsContainer.getChildren().addAll(Ok, Cancel);
//        buttonsContainer.setSpacing(10);
//        buttonsContainer.setPadding(new Insets(5));
//
//        GridPane container = new GridPane();
//        container.add(field, 0, 0);
//        container.setPadding(new Insets(5));
//        container.setVgap(5);
//        container.setHgap(5);
//
//        BorderPane root = new BorderPane();
//        root.setBottom(buttonsContainer);
//        root.setCenter(container);
//        root.setTop(label);
//        root.setPadding(new Insets(15));
//
//        Scene scene = new Scene(root);
//
//        Stage stage = new Stage();
//        stage.setTitle("Please enter name ");
//        stage.initStyle(StageStyle.UTILITY);
//        stage.initOwner(mainWindow);
//        stage.initModality(Modality.NONE);
//        stage.setScene(scene);
//
//        Cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                returnData = "Cancel";
//                stage.close();
//            }
//        });
//        Ok.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (!field.getValue().isEmpty()) {
//                    returnData = field.getValue();
//                } else {
//                    returnData = "Cancel";
//                }
//
//                stage.close();
//            }
//        });
//        field.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                if (event.getCode().equals(KeyCode.ENTER)) {
//                    if (!field.getValue().isEmpty()) {
//                        returnData = field.getValue();
//                    } else {
//                        returnData = "Cancel";
//                    }
//
//                    stage.close();
//                }
//            }
//        });
//        stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                if (event.getCode().equals(KeyCode.ESCAPE)) {
//                    returnData = "Cancel";
//                    stage.close();
//                }
//            }
//        });
//        stage.setOnCloseRequest(action -> returnData = "Cancel");
//
//        field.requestFocus();
//        stage.showAndWait();
//
//        return returnData;
//    }
//
//    @Override
//    public void run() {
//        try {
//            batchObservers.forEach(batchObserver -> {
//                batchObserver.update();
//            });
//            updateLastAlarmField();
//            if (logWindow != null) {
//                logWindow.run();
//            }
//            confirmationMessageReset();
//            checkAirPressureAlarms();
//            checkOverUnderVoltageAlarms();
//            checkESDAlarms();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private void updateLastAlarmField() {
//        CompletableFuture.supplyAsync(() -> {
//            return LogsDAOImpl.getInstance().getLastEneteredLog();
//        }).thenAccept(log -> {
//            Platform.runLater(() -> {
//                if (!lastAlarmField.getText().equals(log.toString())) {
//                    lastAlarmField.setText(log.toString());
//                    if (log.getIdentifier().equals(LogIdentefires.Error.name())) {
//                        lastAlarmField.setStyle("-fx-background-color: red; -fx-dark-text-color: white;-fx-mid-text-color: white;"
//                                + "-fx-font-weight:bold;-fx-font-style:normal;-fx-font-size:16;-fx-font-family: monospace;");
//                    } else if (log.getIdentifier().equals(LogIdentefires.Warning.name())) {
//                        lastAlarmField.setStyle("-fx-background-color: yellow; -fx-dark-text-color: black;-fx-mid-text-color: black;"
//                                + "-fx-font-weight:bold;-fx-font-style:normal;-fx-font-size:16;-fx-font-family: monospace;");
//                    } else if (log.getIdentifier().equals(LogIdentefires.Info.name())) {
//                        lastAlarmField.setStyle("-fx-background-color: wheat; -fx-dark-text-color: black;-fx-mid-text-color: black;"
//                                + "-fx-font-weight:bold;-fx-font-style:normal;-fx-font-size:16;-fx-font-family: monospace;");
//                    } else if (log.getIdentifier().equals(LogIdentefires.System.name())) {
//                        lastAlarmField.setStyle("-fx-background-color: black; -fx-dark-text-color: white;-fx-mid-text-color: white;"
//                                + "-fx-font-weight:bold;-fx-font-style:normal;-fx-font-size:16;-fx-font-family: monospace;");
//                    }
//                }
//            });
//        });
//    }
//    private void initialization() {
//        BatchControllerDataDAOImpl.getInstance().GetAll().stream().filter(data -> data.getRunningBatchID() > 0).forEach(item -> {
//            Platform.runLater(() -> {
//                BatchDTO batch = BatchDAOImpl.getInstance().getByID(String.valueOf(item.getRunningBatchID()));
//                System.err.println("Batch " + batch.getState());
//                handleEventsAtInitialization(mainWindow, batchObservers, containerPane, batch);
//            });
//        });
//    }
//
//    private void confirmationMessageControl() {
//        //Check for start
//        Platform.runLater(() -> {
//            if (((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).getValue()) {
//                if (!Mixer_1_Manual_Add_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                    Mixer_1_Manual_Add_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                }
//            }
//        });
//        Platform.runLater(() -> {
//            if (((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).getValue()) {
//                if (!Mixer_2_Manual_Add_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                    Mixer_2_Manual_Add_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                }
//            }
//        });
//
//        Platform.runLater(() -> {
//            if (((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).getValue()) {
//                if (!IPC_Fill_From_Mixer_1_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                    IPC_Fill_From_Mixer_1_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                }
//            }
//        });
//        Platform.runLater(() -> {
//            if (((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).getValue()) {
//                if (!IPC_Fill_From_Mixer_2_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                    IPC_Fill_From_Mixer_2_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                }
//            }
//        });
//        Platform.runLater(() -> {
//            if (((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).getValue()) {
//                if (!IPC_Fill_From_Tank_1_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                    IPC_Fill_From_Tank_1_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                }
//            }
//        });
//        Platform.runLater(() -> {
//            if (((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).getValue()) {
//                if (!IPC_Fill_From_Tank_2_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                    IPC_Fill_From_Tank_2_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                }
//            }
//        });
//        Platform.runLater(() -> {
//            if (((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).getValue()) {
//                if (!IPC_Fill_From_Tank_3_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                    IPC_Fill_From_Tank_3_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                }
//            }
//        });
//
//        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) {
//                    Platform.runLater(() -> {
//                        if (!Mixer_1_Manual_Add_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                            Mixer_1_Manual_Add_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                        }
//                    });
//                }
//            }
//        });
//        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) {
//                    Platform.runLater(() -> {
//                        if (!Mixer_2_Manual_Add_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                            Mixer_2_Manual_Add_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                        }
//                    });
//                }
//            }
//        });
//
//        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) {
//                    Platform.runLater(() -> {
//                        if (!IPC_Fill_From_Mixer_1_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                            IPC_Fill_From_Mixer_1_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                        }
//                    });
//                }
//            }
//        });
//        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) {
//                    Platform.runLater(() -> {
//                        if (!IPC_Fill_From_Mixer_2_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                            IPC_Fill_From_Mixer_2_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                        }
//                    });
//                }
//            }
//        });
//        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) {
//                    Platform.runLater(() -> {
//                        if (!IPC_Fill_From_Tank_1_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                            IPC_Fill_From_Tank_1_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                        }
//                    });
//                }
//            }
//        });
//        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) {
//                    Platform.runLater(() -> {
//                        if (!IPC_Fill_From_Tank_2_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                            IPC_Fill_From_Tank_2_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                        }
//                    });
//                }
//            }
//        });
//        ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) {
//                    Platform.runLater(() -> {
//                        if (!IPC_Fill_From_Tank_3_Message.getWindow(mainWindow, allDataDefinitions).isShowing()) {
//                            IPC_Fill_From_Tank_3_Message.getWindow(mainWindow, allDataDefinitions).showAndWait();
//                        }
//                    });
//                }
//            }
//        });
//
//    }
//    private void confirmationMessageReset() {
//
//        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_1_Manual_Add_Message_Request)).getValue()) {
//            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_1_Manual_Add_Confirmation)).setValue(false);
//        }
//        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Mixer_2_Manual_Add_Message_Request)).getValue()) {
//            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_2_Manual_Add_Confirmation)).setValue(false);
//        }
//
//        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_1_Message_Request)).getValue()) {
//            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_1_Message_Confirmation)).setValue(false);
//        }
//        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Mixer_2_Message_Request)).getValue()) {
//            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Mixer_2_Message_Confirmation)).setValue(false);
//        }
//        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_1_Message_Request)).getValue()) {
//            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_1_Message_Confirmation)).setValue(false);
//        }
//        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_2_Message_Request)).getValue()) {
//            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_2_Message_Confirmation)).setValue(false);
//        }
//        if (!((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.IPC_Fill_From_Tank_3_Message_Request)).getValue()) {
//            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_3_Message_Confirmation)).setValue(false);
//        }
//
//    }
//
//    private void checkAirPressureAlarms() {
//        boolean hi = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.HI_Air_Pressure_Alarm)).getValue();
//        boolean lo = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Lo_Air_Pressure_Alarm)).getValue();
//        Platform.runLater(() -> {
//            if (hi) {
//                airPressureStatus.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//                airPressureStatus.setText("Hi air pressure alarm");
//            } else if (lo) {
//                airPressureStatus.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//                airPressureStatus.setText("Lo air pressure alarm");
//            } else {
//                airPressureStatus.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
//                airPressureStatus.setText("Normal air pressure");
//            }
//        });
//    }
//    private void checkOverUnderVoltageAlarms() {
//        boolean alarm = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.Over_Under_Voltage_Alarm)).getValue();
//        Platform.runLater(() -> {
//            if (alarm) {
//                overUnderVoltageStatus.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//                overUnderVoltageStatus.setText("Over/Under voltage alarm");
//            } else {
//                overUnderVoltageStatus.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
//                overUnderVoltageStatus.setText("Normal supply voltage");
//            }
//        });
//    }
//    private void checkESDAlarms() {
//        boolean alarm = ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralInput.ESD_Alarm)).getValue();
//        Platform.runLater(() -> {
//            if (alarm) {
//                ESDStatus.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//                ESDStatus.setText("ESD activated");
//            } else {
//                ESDStatus.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
//                ESDStatus.setText("ESD not activated");
//            }
//        });
//    }
//
//    private synchronized void handleEvents(Stage mainWindow, BatchCreator batchCreator, List<BatchObserver> batchObservers, TabPane containerPane) {
//        try {
//            batchCreator = new BatchCreator(mainWindow);
//            batchCreator.initOwner(mainWindow);
//            batchCreator.setHeight(600);
//            batchCreator.setWidth(1100);
//            batchCreator.setMinHeight(600);
//            batchCreator.setMinWidth(800);
//            batchCreator.setResizable(false);
//            BatchDTO batch = batchCreator.showAndReturnBatch();
//            if (batch != null) {
//                BatchObserver batchObserver = new BatchObserver(mainWindow, batch);
//                if (!containerPane.getTabs().contains(batchObserver)) {
//                    containerPane.getTabs().add(batchObserver);
//                    batchObservers.add(batchObserver);
//                    batchObserver.setOnBatchClose((BatchObserver batchObserver1) -> {
//                        containerPane.getTabs().remove(batchObserver1);
//                        batchObservers.remove(batchObserver1);
//                    });
//                } else {
//                }
//            } else {
//                logger.LogRecord(new LogDTO(LogIdentefires.Error.name(), "Returned batch is null"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            String message = null;
//            for (StackTraceElement object : e.getStackTrace()) {
//                message += "\n" + object.toString();
//            }
//            logger.LogRecord(new LogDTO(LogIdentefires.System.name(), "error creating batch  "));
//        }
//    }
//    private synchronized void handleEventsAtInitialization(Stage mainWindow, List<BatchObserver> batchObservers, TabPane containerPane, BatchDTO batch) {
//        try {
//            if (batch != null) {
//                BatchObserver batchObserver = new BatchObserver(mainWindow, batch);
//                if (!containerPane.getTabs().contains(batchObserver)) {
//                    containerPane.getTabs().add(batchObserver);
//                    batchObserver.update();
//                    batchObservers.add(batchObserver);
//                    batchObserver.setOnBatchClose((BatchObserver batchObserver1) -> {
//                        containerPane.getTabs().remove(batchObserver1);
//                        batchObservers.remove(batchObserver1);
//                    });
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            String message = null;
//            for (StackTraceElement object : e.getStackTrace()) {
//                message += "\n" + object.toString();
//            }
//            logger.LogRecord(new LogDTO(LogIdentefires.System.name(), "error creating batch  "));
//        }
//    }


    //Stages getter
    public Stage getInitialStage() {
        return initialStage;
    }

}
