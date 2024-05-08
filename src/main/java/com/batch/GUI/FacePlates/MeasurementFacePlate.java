package com.batch.GUI.FacePlates;


import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ComplexDataType.WeightInput;
import com.batch.PLCDataSource.PLC.ComplexDataType.WeightOutput;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Utilities.FlashingGenerator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public abstract class MeasurementFacePlate extends Stage {

    private BorderPane root = new BorderPane();
    private Scene scene = new Scene(root);
    private MeasurementBarGraphics barIndecator ;

    protected HBox mainContainer;
    protected VBox controlContainer, statusContainer, statusVBox;
    protected GridPane signalsPane, controlPane, simulationPane, alarmsPane;
    protected Label mainLabel, Status, Control, Alarm, Signals, simulation, simulationValueLabel;
    protected Button reset ;
    protected TextField reading;
    protected FaceplateTextField simulationValueField;
    protected CheckBox enableSimulation;

    private Stage mainWindow;
    private RowDataDefinition dataModel;
    private String unit = "";

    public MeasurementFacePlate(Stage stage, RowDataDefinition dataModel, String unit) {
        mainWindow = stage;
        this.dataModel = dataModel;
        this.unit = unit;
        initalization();
        initialActions();
        customizedGraphicsAndActions(root, dataModel, controlPane, alarmsPane, signalsPane);
        flasherStartubg();
    }

    private void initalization() {
        barIndecator = new MeasurementBarGraphics(
                ((RealDataType) dataModel.getAllValues().get(WeightInput.Weight)),
                ((RealDataType) dataModel.getAllValues().get(WeightOutput.Zero)),
                ((RealDataType) dataModel.getAllValues().get(WeightOutput.Span)),
                ((RealDataType) dataModel.getAllValues().get(WeightOutput.Low_Warning_SP)),
                ((RealDataType) dataModel.getAllValues().get(WeightOutput.Low_Alarm_Sp)),
                ((RealDataType) dataModel.getAllValues().get(WeightOutput.High_Warning_SP)),
                ((RealDataType) dataModel.getAllValues().get(WeightOutput.High_Alarm_SP))
        );
        
        Control = new Label("Configurations ");
        Alarm = new Label("Alarms ");
        Signals = new Label("Signals ");
        Status = new Label();
        simulation = new Label("Simulation");
        simulationValueLabel = new Label("Value");
        
        simulationValueLabel.setPrefWidth(100);
        
        reading = new TextField();
        reading.setEditable(false);
        reading.setFont(Font.font(17));
        reading.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        reading.setText(String.format("Reading : %20.8f  " + unit, ((RealDataType) dataModel.getAllValues().get(WeightInput.Weight)).getValue()));
        
        
        simulationValueField = new FaceplateTextField();
        simulationValueField.setPromptText("0.0");
        simulationValueField.setPrefWidth(130);
        
        enableSimulation = new CheckBox("Enable simulation");
        

        controlContainer = new VBox();
        statusContainer = new VBox();
        mainContainer = new HBox();
        statusVBox = new VBox();

        controlContainer.setSpacing(5);
        statusContainer.setSpacing(5);
        statusVBox.setSpacing(10);

        Status = new Label("Normal");
        Status.prefHeight(100);
        Status.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        Status.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        Status.setAlignment(Pos.CENTER);
        Status.setTextAlignment(TextAlignment.CENTER);
        Status.prefWidthProperty().bind(statusVBox.widthProperty());
        
        reset = new Button("Reset");
        reset.prefWidthProperty().bind(statusVBox.widthProperty());
        reset.setOnMousePressed(action -> onResetPressed(action, dataModel));
        reset.setOnMouseReleased(action -> onResetReleased(action, dataModel));

        signalsPane = new GridPane();
        controlPane = new GridPane();
        alarmsPane = new GridPane();
        simulationPane = new GridPane();

        signalsPane.setPadding(new Insets(5));
        controlPane.setPadding(new Insets(5));
        alarmsPane.setPadding(new Insets(5));
        simulationPane.setPadding(new Insets(5));
        

        signalsPane.setVgap(5);
        controlPane.setVgap(5);
        alarmsPane.setVgap(5);
        simulationPane.setVgap(5);

        signalsPane.setHgap(5);
        controlPane.setHgap(5);
        alarmsPane.setHgap(5);
        simulationPane.setHgap(5);

        signalsPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        controlPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        alarmsPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        simulationPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        
        simulationPane.add(simulationValueLabel, 0, 1);
        simulationPane.add(simulationValueField, 1, 1);
        simulationPane.add(enableSimulation, 0, 0, 2, 1);
        

        statusContainer.getChildren().addAll(Signals, signalsPane);
        controlContainer.getChildren().addAll(reading, Control, controlPane, Alarm, alarmsPane, simulation, simulationPane);
        statusVBox.getChildren().addAll(barIndecator, Status, reset, statusContainer);

        mainContainer.getChildren().addAll(statusVBox, controlContainer);
        mainContainer.setPadding(new Insets(10));
        mainContainer.setSpacing(20);

        mainLabel = new Label("Details Faceplate :: " + dataModel.getName());
        mainLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        mainLabel.prefWidthProperty().bind(root.widthProperty());
        mainLabel.setFont(Font.font(12));
        mainLabel.setAlignment(Pos.CENTER);
        mainLabel.setPrefHeight(50);

        root.setTop(mainLabel);
        root.setCenter(mainContainer);

        setResizable(false);
        setScene(scene);
        initOwner(mainWindow);
        initModality(Modality.NONE);
        setTitle(dataModel.getName());
        
        scene.getStylesheets().add("/GUI/Styles/Faceplate.css");

    }

    private void initialActions() {
        ((RealDataType) dataModel.getAllValues().get(WeightInput.Weight)).addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> reading.setText(String.format("Reading : %20.8f  " + unit, newValue)));

        enableSimulation.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> ((BooleanDataType) dataModel.getAllValues().get(WeightOutput.Enable_Simulation)).setValue(newValue));
        simulationValueField.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (simulationValueField.getText().length() > 0) {
                    ((RealDataType) dataModel.getAllValues().get(WeightOutput.Simulation_Value)).setValue(Float.parseFloat(simulationValueField.getText()));
                }
                return null;
            }
        });
    }
    private void flasherStartubg() {
        FlashingGenerator.getSystem().getFlasher().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        withFlasher(newValue);
                    }
                });

            }
        });
    }
    protected abstract void customizedGraphicsAndActions(BorderPane root, RowDataDefinition dataModel, GridPane configurationsContainer, GridPane alarmsSettingsContainer, GridPane statusContainer);
    protected abstract void actionHandler(RowDataDefinition dataModel);
    protected abstract void checkDataForInitializingGraphics(RowDataDefinition dataModel);
    protected abstract void withFlasher(boolean flashTrigger);
    protected abstract void onResetPressed(MouseEvent action, RowDataDefinition dataModel);
    protected abstract void onResetReleased(MouseEvent action, RowDataDefinition dataModel);
    protected Pane getImageView() {
        return barIndecator;
    }
    protected void changeColorOfImageView(Color color) {
        Glow glow = new Glow(0.2);
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.GRAY, 1, 1, 1, 1);
        Light.Distant light = new Light.Distant(100, 100, color.brighter().brighter());
        Lighting lighting = new Lighting(light);
        Blend blend = new Blend(BlendMode.MULTIPLY, glow, shadow);
        Blend blend2 = new Blend(BlendMode.MULTIPLY, blend, lighting);

        barIndecator.setEffect(blend2);

    }
    protected void changeStatus(String statusString, Color color) {
        if (statusString != null) {
            Status.setText(statusString);
        }
        Status.setBackground(new Background(new BackgroundFill(color.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
    }
    protected void showFacePlate() {
        show();
    }
    
}
