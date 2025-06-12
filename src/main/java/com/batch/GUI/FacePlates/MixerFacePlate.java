package com.batch.GUI.FacePlates;

import com.batch.PLCDataSource.PLC.ComplexDataType.MixerInput;
import com.batch.PLCDataSource.PLC.ComplexDataType.MixerOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.google.common.io.Resources;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class MixerFacePlate extends ControlFacePlate {
    private Button Start;
    private Button Stop;
    private Label modeLbael;
    private Label sourceLabel;
    private Label SpeedLabel;
    private Label FinalSpeedLabel;
    private Label AmpereLabel;
    private ComboBox<String> mode;
    private ComboBox<String> source;
    private CheckBox QControl;
    private CheckBox Fault;
    private FaceplateTextField Speed;
    private FaceplateTextField FinalSpeed;
    private FaceplateTextField Ampere;
    private boolean faultCondition;

    public MixerFacePlate(Stage stage, RowDataDefinition dataModel) {
        super(stage, dataModel);
    }

    protected void customizedGraphicsAndActions(RowDataDefinition dataModel, GridPane controlContainer, GridPane monitoringContainer, GridPane statusContainer) {
        Image image = new Image(Resources.getResource("FacePlatesIcons/Mixer.png").toString());
        this.getImageView().setImage(image);
        this.Start = new Button("Start Mixer");
        this.Stop = new Button("Stop Mixer");
        this.Start.setPrefWidth(200.0F);
        this.Stop.setPrefWidth(200.0F);
        this.mode = new ComboBox();
        this.source = new ComboBox();
        this.mode.getItems().addAll(FXCollections.observableArrayList((Collection) Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList())));
        this.source.getItems().addAll(FXCollections.observableArrayList((Collection) Arrays.stream(Source.values()).map(Enum::name).collect(Collectors.toList())));
        this.mode.setPrefWidth(125.0F);
        this.source.setPrefWidth(125.0F);
        this.QControl = new CheckBox("Q output");
        this.Fault = new CheckBox("Fault ");
        this.QControl.setMouseTransparent(true);
        this.Fault.setMouseTransparent(true);
        this.Speed = new FaceplateTextField();
        this.Speed.setPromptText("0.0");
        this.Speed.setRestrict("[0-9].");
        this.Speed.setMaxLength(10);
        this.Speed.setPrefWidth(80.0F);
        this.FinalSpeed = new FaceplateTextField();
        this.FinalSpeed.setPromptText("0.0");
        this.FinalSpeed.setRestrict("[0-9].");
        this.FinalSpeed.setMaxLength(10);
        this.FinalSpeed.setPrefWidth(120.0F);
        this.FinalSpeed.setEditable(false);
        this.Ampere = new FaceplateTextField();
        this.Ampere.setPromptText("0.0");
        this.Ampere.setRestrict("[0-9].");
        this.Ampere.setMaxLength(10);
        this.Ampere.setPrefWidth(120.0F);
        this.Ampere.setEditable(false);
        this.modeLbael = new Label("Mode");
        this.sourceLabel = new Label("Source");
        this.SpeedLabel = new Label("Setpoint");
        this.FinalSpeedLabel = new Label("Output Speed");
        this.AmpereLabel = new Label("Motor Current");
        this.modeLbael.setPrefWidth(70.0F);
        this.sourceLabel.setPrefWidth(70.0F);
        controlContainer.add(this.modeLbael, 1, 1);
        controlContainer.add(this.mode, 2, 1);
        controlContainer.add(this.sourceLabel, 1, 2);
        controlContainer.add(this.source, 2, 2);
        controlContainer.add(this.Start, 1, 3, 2, 1);
        controlContainer.add(this.Stop, 1, 4, 2, 1);
        controlContainer.add(this.SpeedLabel, 1, 5);
        controlContainer.add(this.Speed, 2, 5);
        statusContainer.add(this.QControl, 1, 1);
        statusContainer.add(this.Fault, 1, 2);
        monitoringContainer.add(this.FinalSpeedLabel, 1, 1);
        monitoringContainer.add(this.FinalSpeed, 2, 1);
        monitoringContainer.add(this.AmpereLabel, 1, 2);
        monitoringContainer.add(this.Ampere, 2, 2);
        this.checkDataForInitializingGraphics(dataModel);
        this.actionHandler(dataModel);
    }

    protected void actionHandler(final RowDataDefinition dataModel) {
        this.Start.setOnMousePressed((action) -> this.onStartPressed(dataModel));
        this.Start.setOnMouseReleased((action) -> this.onStartReleased(dataModel));
        this.Stop.setOnMousePressed((action) -> this.onStopPressed(dataModel));
        this.Stop.setOnMouseReleased((action) -> this.onStopReleased(dataModel));
        this.mode.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Mode.Automatic.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Mode)).setValue(true);
                } else if (newValue.equals(Mode.Manual.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Mode)).setValue(false);
                }

            }
        });
        this.source.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Source.Remote.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Source)).setValue(true);
                } else if (newValue.equals(Source.Local.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Source)).setValue(false);
                }

            }
        });
        this.Speed.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (MixerFacePlate.this.Speed.getText().length() > 0) {
                    ((RealDataType) dataModel.getAllValues().get(MixerOutput.Speed_Setpoint)).setValue(Float.parseFloat(MixerFacePlate.this.Speed.getText()));
                }

                return null;
            }
        });
        ((RealDataType) dataModel.getAllValues().get(MixerInput.Output_Speed)).addListener((observable, oldValue, newValue) -> this.FinalSpeed.setText(String.valueOf(newValue)));
        ((RealDataType) dataModel.getAllValues().get(MixerInput.Ampere_Reading)).addListener((observable, oldValue, newValue) -> this.Ampere.setText(String.valueOf(newValue)));
        ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Running)).addListener((observable, oldValue, newValue) -> this.setOnRunningChange(dataModel));
        ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Fault)).addListener((observable, oldValue, newValue) -> this.setOnFaultChange(dataModel));
        ((BooleanDataType) dataModel.getAllValues().get(MixerInput.QControl)).addListener((observable, oldValue, newValue) -> this.QControl.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Fault)).addListener((observable, oldValue, newValue) -> this.Fault.setSelected(newValue));
    }

    protected void withFlasher(boolean flashTrigger) {
        if (this.faultCondition) {
            if (flashTrigger) {
                this.changeStatus("Fault", Color.YELLOW);
            } else {
                this.changeStatus("Fault", Color.RED);
            }
        }

    }

    protected void checkDataForInitializingGraphics(RowDataDefinition dataModel) {
        boolean actualMode = ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Mode)).getValue();
        boolean actualSource = ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Source)).getValue();
        double AmpereReading = (double) ((RealDataType) dataModel.getAllValues().get(MixerInput.Ampere_Reading)).getValue();
        double SpeedReading = (double) ((RealDataType) dataModel.getAllValues().get(MixerOutput.Speed_Setpoint)).getValue();
        double outputSpeed = (double) ((RealDataType) dataModel.getAllValues().get(MixerInput.Output_Speed)).getValue();
        if (actualMode) {
            this.mode.getSelectionModel().select(Mode.Automatic.name());
        } else {
            this.mode.getSelectionModel().select(Mode.Manual.name());
        }

        if (actualSource) {
            this.source.getSelectionModel().select(Source.Remote.name());
        } else {
            this.source.getSelectionModel().select(Source.Local.name());
        }

        this.QControl.setSelected(((BooleanDataType) dataModel.getAllValues().get(MixerInput.QControl)).getValue());
        this.Ampere.setText(String.valueOf(AmpereReading));
        this.Speed.setText(String.valueOf(SpeedReading));
        this.FinalSpeed.setText(String.valueOf(outputSpeed));
        this.Fault.setSelected(((BooleanDataType) dataModel.getAllValues().get(MixerInput.Fault)).getValue());
        this.QControl.setSelected(((BooleanDataType) dataModel.getAllValues().get(MixerInput.QControl)).getValue());
        this.setOnRunningChange(dataModel);
        this.setOnFaultChange(dataModel);
    }

    private void onStartPressed(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Start)).setValue(Boolean.TRUE);
    }

    private void onStartReleased(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Start)).setValue(Boolean.FALSE);
    }

    private void onStopPressed(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Stop)).setValue(Boolean.TRUE);
    }

    private void onStopReleased(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Stop)).setValue(Boolean.FALSE);
    }

    private void setOnRunningChange(RowDataDefinition dataModel) {
        boolean newValue = ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Running)).getValue();
        if (newValue) {
            this.changeStatus("Running", Color.GREEN);
            this.changeColorOfImageView(Color.GREEN);
        } else {
            this.changeStatus("Stopped", Color.RED);
            this.changeColorOfImageView(Color.RED);
        }

    }

    private void setOnFaultChange(RowDataDefinition dataModel) {
        boolean x = ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Fault)).getValue();
        this.faultCondition = x;
        this.setOnRunningChange(dataModel);
    }

    protected void onResetPressed(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Reset)).setValue(Boolean.TRUE);
    }

    protected void onResetReleased(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Reset)).setValue(Boolean.FALSE);
    }
}
