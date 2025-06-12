
package com.batch.GUI.FacePlates;

import com.batch.PLCDataSource.PLC.ComplexDataType.PumpInput;
import com.batch.PLCDataSource.PLC.ComplexDataType.PumpOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.google.common.io.Resources;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
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

public class PumpFacePlate extends ControlFacePlate {
    private Button Start;
    private Button Stop;
    private Label modeLabel;
    private Label sourceLabel;
    private ComboBox<String> monitoring_enable;
    private ComboBox<String> mode;
    private ComboBox<String> source;
    private CheckBox QControl;
    private CheckBox FB;
    private CheckBox Fault;
    private FaceplateTextField monitoring_time;
    private boolean faultCondition;

    public PumpFacePlate(Stage stage, RowDataDefinition dataModel) {
        super(stage, dataModel);
    }

    protected void customizedGraphicsAndActions(RowDataDefinition dataModel, GridPane controlContainer, GridPane monitoringContainer, GridPane statusContainer) {
        Image image = new Image(Resources.getResource("FacePlatesIcons/Pump.png").toString());
        this.getImageView().setImage(image);
        this.Start = new Button("Start Pump");
        this.Stop = new Button("Stop Pump");
        this.Start.setPrefWidth((double)200.0F);
        this.Stop.setPrefWidth((double)200.0F);
        this.monitoring_enable = new ComboBox();
        this.mode = new ComboBox();
        this.source = new ComboBox();
        this.monitoring_enable.getItems().addAll(FXCollections.observableArrayList((Collection)Arrays.stream(FB_Monitoring.values()).map(Enum::name).collect(Collectors.toList())));
        this.mode.getItems().addAll(FXCollections.observableArrayList((Collection)Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList())));
        this.source.getItems().addAll(FXCollections.observableArrayList((Collection)Arrays.stream(Source.values()).map(Enum::name).collect(Collectors.toList())));
        this.mode.setPrefWidth((double)125.0F);
        this.source.setPrefWidth((double)125.0F);
        this.monitoring_enable.setPrefWidth((double)200.0F);
        this.QControl = new CheckBox("Q output");
        this.FB = new CheckBox("FB input");
        this.Fault = new CheckBox("Fault");
        this.QControl.setMouseTransparent(true);
        this.FB.setMouseTransparent(true);
        this.Fault.setMouseTransparent(true);
        this.monitoring_time = new FaceplateTextField();
        this.monitoring_time.setPromptText("0.0");
        this.monitoring_time.setRestrict("[0-9]");
        this.monitoring_time.setMaxLength(4);
        this.monitoring_time.setPrefWidth((double)200.0F);
        this.modeLabel = new Label("Mode");
        this.sourceLabel = new Label("Source");
        this.modeLabel.setPrefWidth((double)70.0F);
        this.sourceLabel.setPrefWidth((double)70.0F);
        controlContainer.add(this.modeLabel, 1, 1);
        controlContainer.add(this.mode, 2, 1);
        controlContainer.add(this.sourceLabel, 1, 2);
        controlContainer.add(this.source, 2, 2);
        controlContainer.add(this.Start, 1, 3, 2, 1);
        controlContainer.add(this.Stop, 1, 4, 2, 1);
        monitoringContainer.add(this.monitoring_enable, 1, 1);
        monitoringContainer.add(this.monitoring_time, 1, 2);
        statusContainer.add(this.QControl, 1, 1);
        statusContainer.add(this.FB, 1, 2);
        statusContainer.add(this.Fault, 1, 3);
        this.checkDataForInitializingGraphics(dataModel);
        this.actionHandler(dataModel);
    }

    protected void actionHandler(final RowDataDefinition dataModel) {
        this.Start.setOnMousePressed((action) -> this.onStartPressed(dataModel));
        this.Start.setOnMouseReleased((action) -> this.onStartReleased(dataModel));
        this.Stop.setOnMousePressed((action) -> this.onStopPressed(dataModel));
        this.Stop.setOnMouseReleased((action) -> this.onStopReleased(dataModel));
        this.monitoring_time.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (PumpFacePlate.this.monitoring_time.getText().length() > 0) {
                    ((IntegerDataType)dataModel.getAllValues().get(PumpOutput.Monitoring_Time)).setValue(Integer.parseInt(PumpFacePlate.this.monitoring_time.getText()));
                }

                return null;
            }
        });
        this.monitoring_enable.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(FB_Monitoring.Enable_monitoring.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Enable_Monitoring)).setValue(true);
                } else if (newValue.equals(FB_Monitoring.Disable_monitoring.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Enable_Monitoring)).setValue(false);
                }

            }
        });
        this.mode.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Mode.Automatic.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Mode)).setValue(true);
                } else if (newValue.equals(Mode.Manual.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Mode)).setValue(false);
                }

            }
        });
        this.source.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Source.Remote.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Source)).setValue(true);
                } else if (newValue.equals(Source.Local.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Source)).setValue(false);
                }

            }
        });
        ((BooleanDataType)dataModel.getAllValues().get(PumpInput.Running)).addListener((observable, oldValue, newValue) -> this.setOnRunningChange(dataModel));
        ((BooleanDataType)dataModel.getAllValues().get(PumpInput.Fault)).addListener((observable, oldValue, newValue) -> this.setOnFaultChange(dataModel));
        ((BooleanDataType)dataModel.getAllValues().get(PumpInput.Feedback)).addListener((observable, oldValue, newValue) -> this.FB.setSelected(newValue));
        ((BooleanDataType)dataModel.getAllValues().get(PumpInput.QControl)).addListener((observable, oldValue, newValue) -> this.QControl.setSelected(newValue));
        ((BooleanDataType)dataModel.getAllValues().get(PumpInput.Fault)).addListener((observable, oldValue, newValue) -> this.Fault.setSelected(newValue));
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
        boolean actualMode = ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Mode)).getValue();
        boolean actualSource = ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Source)).getValue();
        boolean actualonitoringEnable = ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Enable_Monitoring)).getValue();
        int actualMonitoringTime = ((IntegerDataType)dataModel.getAllValues().get(PumpOutput.Monitoring_Time)).getValue();
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

        if (actualonitoringEnable) {
            this.monitoring_enable.getSelectionModel().select(FB_Monitoring.Enable_monitoring.name());
        } else {
            this.monitoring_enable.getSelectionModel().select(FB_Monitoring.Disable_monitoring.name());
        }

        this.monitoring_time.setText(String.valueOf(actualMonitoringTime));
        this.FB.setSelected(((BooleanDataType)dataModel.getAllValues().get(PumpInput.Feedback)).getValue());
        this.QControl.setSelected(((BooleanDataType)dataModel.getAllValues().get(PumpInput.QControl)).getValue());
        this.Fault.setSelected(((BooleanDataType)dataModel.getAllValues().get(PumpInput.Fault)).getValue());
        this.setOnRunningChange(dataModel);
        this.setOnFaultChange(dataModel);
    }

    private void onStartPressed(RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Start)).setValue(Boolean.TRUE);
    }

    private void onStartReleased(RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Start)).setValue(Boolean.FALSE);
    }

    private void onStopPressed(RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Stop)).setValue(Boolean.TRUE);
    }

    private void onStopReleased(RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Stop)).setValue(Boolean.FALSE);
    }

    private void setOnRunningChange(RowDataDefinition dataModel) {
        boolean newValue = ((BooleanDataType)dataModel.getAllValues().get(PumpInput.Running)).getValue();
        if (newValue) {
            this.changeStatus("Running", Color.GREEN);
            this.changeColorOfImageView(Color.GREEN);
        } else {
            this.changeStatus("Stopped", Color.RED);
            this.changeColorOfImageView(Color.RED);
        }

    }

    private void setOnFaultChange(RowDataDefinition dataModel) {
        boolean x = ((BooleanDataType)dataModel.getAllValues().get(PumpInput.Fault)).getValue();
        this.faultCondition = x;
        this.setOnRunningChange(dataModel);
    }

    protected void onResetPressed(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Reset)).setValue(Boolean.TRUE);
    }

    protected void onResetReleased(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(PumpOutput.Reset)).setValue(Boolean.FALSE);
    }
}
