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
import javafx.stage.StageStyle;
import javafx.util.Callback;

public abstract class MeasurementFacePlate extends Stage {
    private final BorderPane root = new BorderPane();
    private final Scene scene;
    private final Stage mainWindow;
    private final RowDataDefinition dataModel;
    protected HBox mainContainer;
    protected VBox controlContainer;
    protected VBox statusContainer;
    protected VBox statusVBox;
    protected GridPane signalsPane;
    protected GridPane controlPane;
    protected GridPane simulationPane;
    protected GridPane alarmsPane;
    protected Label mainLabel;
    protected Label Status;
    protected Label Control;
    protected Label Alarm;
    protected Label Signals;
    protected Label simulation;
    protected Label simulationValueLabel;
    protected Button reset;
    protected TextField reading;
    protected FaceplateTextField simulationValueField;
    protected CheckBox enableSimulation;
    private MeasurementBarGraphics barIndecator;
    private String unit;

    public MeasurementFacePlate(Stage stage, RowDataDefinition dataModel, String unit) {
        this.scene = new Scene(this.root);
        this.unit = "";
        this.mainWindow = stage;
        this.dataModel = dataModel;
        this.unit = unit;
        this.initalization();
        this.initialActions();
        this.customizedGraphicsAndActions(this.root, dataModel, this.controlPane, this.alarmsPane, this.signalsPane);
        this.flasherStartup();
    }

    private void initalization() {
        this.barIndecator = new MeasurementBarGraphics((RealDataType) this.dataModel.getAllValues().get(WeightInput.Weight), (RealDataType) this.dataModel.getAllValues().get(WeightOutput.Zero), (RealDataType) this.dataModel.getAllValues().get(WeightOutput.Span), (RealDataType) this.dataModel.getAllValues().get(WeightOutput.Low_Warning_SP), (RealDataType) this.dataModel.getAllValues().get(WeightOutput.Low_Alarm_Sp), (RealDataType) this.dataModel.getAllValues().get(WeightOutput.High_Warning_SP), (RealDataType) this.dataModel.getAllValues().get(WeightOutput.High_Alarm_SP));
        this.Control = new Label("Configurations ");
        this.Alarm = new Label("Alarms ");
        this.Signals = new Label("Signals ");
        this.Status = new Label();
        this.simulation = new Label("Simulation");
        this.simulationValueLabel = new Label("Value");
        this.simulationValueLabel.setPrefWidth(100.0F);
        this.reading = new TextField();
        this.reading.setEditable(false);
        this.reading.setFont(Font.font(17.0F));
        this.reading.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        this.reading.setText(String.format("Reading : %20.8f  " + this.unit, ((RealDataType) this.dataModel.getAllValues().get(WeightInput.Weight)).getValue()));
        this.simulationValueField = new FaceplateTextField();
        this.simulationValueField.setPromptText("0.0");
        this.simulationValueField.setRestrict("[0-9].");
        this.simulationValueField.setPrefWidth(130.0F);
        this.enableSimulation = new CheckBox("Enable simulation");
        this.controlContainer = new VBox();
        this.statusContainer = new VBox();
        this.mainContainer = new HBox();
        this.statusVBox = new VBox();
        this.controlContainer.setSpacing(5.0F);
        this.statusContainer.setSpacing(5.0F);
        this.statusVBox.setSpacing(10.0F);
        this.Status = new Label("Normal");
        this.Status.prefHeight(100.0F);
        this.Status.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        this.Status.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0F))));
        this.Status.setAlignment(Pos.CENTER);
        this.Status.setTextAlignment(TextAlignment.CENTER);
        this.Status.prefWidthProperty().bind(this.statusVBox.widthProperty());
        this.reset = new Button("Reset");
        this.reset.prefWidthProperty().bind(this.statusVBox.widthProperty());
        this.reset.setOnMousePressed((action) -> this.onResetPressed(action, this.dataModel));
        this.reset.setOnMouseReleased((action) -> this.onResetReleased(action, this.dataModel));
        this.signalsPane = new GridPane();
        this.controlPane = new GridPane();
        this.alarmsPane = new GridPane();
        this.simulationPane = new GridPane();
        this.signalsPane.setPadding(new Insets(5.0F));
        this.controlPane.setPadding(new Insets(5.0F));
        this.alarmsPane.setPadding(new Insets(5.0F));
        this.simulationPane.setPadding(new Insets(5.0F));
        this.signalsPane.setVgap(5.0F);
        this.controlPane.setVgap(5.0F);
        this.alarmsPane.setVgap(5.0F);
        this.simulationPane.setVgap(5.0F);
        this.signalsPane.setHgap(5.0F);
        this.controlPane.setHgap(5.0F);
        this.alarmsPane.setHgap(5.0F);
        this.simulationPane.setHgap(5.0F);
        this.signalsPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0F))));
        this.controlPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0F))));
        this.alarmsPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0F))));
        this.simulationPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0F))));
        this.simulationPane.add(this.simulationValueLabel, 0, 1);
        this.simulationPane.add(this.simulationValueField, 1, 1);
        this.simulationPane.add(this.enableSimulation, 0, 0, 2, 1);
        this.statusContainer.getChildren().addAll(this.Signals, this.signalsPane);
        this.controlContainer.getChildren().addAll(this.reading, this.Control, this.controlPane, this.Alarm, this.alarmsPane, this.simulation, this.simulationPane);
        this.statusVBox.getChildren().addAll(this.barIndecator, this.Status, this.reset, this.statusContainer);
        this.mainContainer.getChildren().addAll(this.statusVBox, this.controlContainer);
        this.mainContainer.setPadding(new Insets(10.0F));
        this.mainContainer.setSpacing(20.0F);
        this.mainLabel = new Label("Details Faceplate :: " + this.dataModel.getName());
        this.mainLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.mainLabel.prefWidthProperty().bind(this.root.widthProperty());
        this.mainLabel.setFont(Font.font(12.0F));
        this.mainLabel.setAlignment(Pos.CENTER);
        this.mainLabel.setPrefHeight(50.0F);
        this.root.setTop(this.mainLabel);
        this.root.setCenter(this.mainContainer);
        this.setResizable(false);
        this.setScene(this.scene);
        this.initOwner(this.mainWindow);
        this.initStyle(StageStyle.UTILITY);
        this.initModality(Modality.NONE);
        this.setTitle(this.dataModel.getName());
        this.scene.getStylesheets().add("Styles/Faceplate.css");
    }

    private void initialActions() {
        ((RealDataType) this.dataModel.getAllValues().get(WeightInput.Weight)).addListener((observable, oldValue, newValue) -> this.reading.setText(String.format("Reading : %20.8f  " + this.unit, newValue)));
        this.enableSimulation.selectedProperty().addListener((observable, oldValue, newValue) -> ((BooleanDataType) this.dataModel.getAllValues().get(WeightOutput.Enable_Simulation)).setValue(newValue));
        this.simulationValueField.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (MeasurementFacePlate.this.simulationValueField.getText().length() > 0) {
                    ((RealDataType) MeasurementFacePlate.this.dataModel.getAllValues().get(WeightOutput.Simulation_Value)).setValue(Float.parseFloat(MeasurementFacePlate.this.simulationValueField.getText()));
                }

                return null;
            }
        });
    }

    private void flasherStartup() {
        FlashingGenerator.getSystem().getFlasher().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        MeasurementFacePlate.this.withFlasher(newValue);
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
        return this.barIndecator;
    }

    protected void changeColorOfImageView(Color color) {
        Glow glow = new Glow(0.2);
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.GRAY, 1.0F, 1.0F, 1.0F, 1.0F);
        Light.Distant light = new Light.Distant(100.0F, 100.0F, color.brighter().brighter());
        Lighting lighting = new Lighting(light);
        Blend blend = new Blend(BlendMode.MULTIPLY, glow, shadow);
        Blend blend2 = new Blend(BlendMode.MULTIPLY, blend, lighting);
        this.barIndecator.setEffect(blend2);
    }

    protected void changeStatus(String statusString, Color color) {
        if (statusString != null) {
            this.Status.setText(statusString);
        }

        this.Status.setBackground(new Background(new BackgroundFill(color.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    protected void showFacePlate() {
        this.show();
    }
}
