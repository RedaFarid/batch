
package com.batch.GUI.FacePlates;

import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ComplexDataType.ValveInput;
import com.batch.PLCDataSource.PLC.ComplexDataType.ValveOutput;
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

public class ValveFacePlate extends ControlFacePlate {
    private Button Open;
    private Button close;
    private Label modeLbael;
    private Label sourceLabel;
    private ComboBox<String> monitoring_enable;
    private ComboBox<String> mode;
    private ComboBox<String> source;
    private CheckBox QOpen;
    private CheckBox QClose;
    private CheckBox OpenFB;
    private CheckBox CloseFB;
    private FaceplateTextField monitoring_time;
    private boolean faultCondition;

    public ValveFacePlate(Stage stage, RowDataDefinition dataModel) {
        super(stage, dataModel);
    }

    protected void customizedGraphicsAndActions(RowDataDefinition dataModel, GridPane controlContainer, GridPane monitoringContainer, GridPane statusContainer) {
        Image image = new Image(Resources.getResource("FacePlatesIcons/Valve.png").toString());
        this.getImageView().setImage(image);
        this.Open = new Button("Open valve");
        this.close = new Button("Close Valve");
        this.Open.setPrefWidth((double)200.0F);
        this.close.setPrefWidth((double)200.0F);
        this.monitoring_enable = new ComboBox();
        this.mode = new ComboBox();
        this.source = new ComboBox();
        this.monitoring_enable.getItems().addAll(FXCollections.observableArrayList((Collection)Arrays.stream(FB_Monitoring.values()).map(Enum::name).collect(Collectors.toList())));
        this.mode.getItems().addAll(FXCollections.observableArrayList((Collection)Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList())));
        this.source.getItems().addAll(FXCollections.observableArrayList((Collection)Arrays.stream(Source.values()).map(Enum::name).collect(Collectors.toList())));
        this.mode.setPrefWidth((double)125.0F);
        this.source.setPrefWidth((double)125.0F);
        this.monitoring_enable.setPrefWidth((double)200.0F);
        this.QOpen = new CheckBox("Q Open");
        this.QClose = new CheckBox("Q Close");
        this.OpenFB = new CheckBox("FB Open");
        this.CloseFB = new CheckBox("FB Close");
        this.QOpen.setMouseTransparent(true);
        this.QClose.setMouseTransparent(true);
        this.OpenFB.setMouseTransparent(true);
        this.CloseFB.setMouseTransparent(true);
        this.monitoring_time = new FaceplateTextField();
        this.monitoring_time.setPromptText("0.0");
        this.monitoring_time.setRestrict("[0-9]");
        this.monitoring_time.setMaxLength(4);
        this.monitoring_time.setPrefWidth((double)200.0F);
        this.modeLbael = new Label("Mode");
        this.sourceLabel = new Label("Source");
        this.modeLbael.setPrefWidth((double)70.0F);
        this.sourceLabel.setPrefWidth((double)70.0F);
        controlContainer.add(this.modeLbael, 1, 1);
        controlContainer.add(this.mode, 2, 1);
        controlContainer.add(this.sourceLabel, 1, 2);
        controlContainer.add(this.source, 2, 2);
        controlContainer.add(this.Open, 1, 3, 2, 1);
        controlContainer.add(this.close, 1, 4, 2, 1);
        monitoringContainer.add(this.monitoring_enable, 1, 1);
        monitoringContainer.add(this.monitoring_time, 1, 2);
        statusContainer.add(this.QOpen, 1, 1);
        statusContainer.add(this.QClose, 1, 2);
        statusContainer.add(this.OpenFB, 1, 3);
        statusContainer.add(this.CloseFB, 1, 4);
        this.checkDataForInitializingGraphics(dataModel);
        this.actionHandler(dataModel);
    }

    protected void actionHandler(final RowDataDefinition dataModel) {
        this.Open.setOnMousePressed((action) -> this.onOpenPressed(dataModel));
        this.Open.setOnMouseReleased((action) -> this.onOpenReleased(dataModel));
        this.close.setOnMousePressed((action) -> this.onClosePressed(dataModel));
        this.close.setOnMouseReleased((action) -> this.onCloseReleased(dataModel));
        this.monitoring_time.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (ValveFacePlate.this.monitoring_time.getText().length() > 0) {
                    ((IntegerDataType)dataModel.getAllValues().get(ValveOutput.Monitoring_Time)).setValue(Integer.parseInt(ValveFacePlate.this.monitoring_time.getText()));
                }

                return null;
            }
        });
        this.monitoring_enable.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(FB_Monitoring.Enable_monitoring.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Enable_Monitoring)).setValue(true);
                } else if (newValue.equals(FB_Monitoring.Disable_monitoring.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Enable_Monitoring)).setValue(false);
                }

            }
        });
        this.mode.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Mode.Automatic.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Mode)).setValue(true);
                } else if (newValue.equals(Mode.Manual.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Mode)).setValue(false);
                }

            }
        });
        this.source.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Source.Remote.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Source)).setValue(true);
                } else if (newValue.equals(Source.Local.name())) {
                    ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Source)).setValue(false);
                }

            }
        });
        ((BooleanDataType)dataModel.getAllValues().get(ValveInput.Opened_Closed)).addListener((observable, oldValue, newValue) -> this.setOnOpenedClosedChange(dataModel));
        ((BooleanDataType)dataModel.getAllValues().get(ValveInput.Fault)).addListener((observable, oldValue, newValue) -> this.setOnFaultChange(dataModel));
        ((IntegerDataType)dataModel.getAllValues().get(ValveOutput.Monitoring_Time)).addListener((observable, oldValue, newValue) -> this.monitoring_time.setText(String.valueOf(newValue)));
        ((BooleanDataType)dataModel.getAllValues().get(ValveInput.QOpen)).addListener((observable, oldValue, newValue) -> this.QOpen.setSelected(newValue));
        ((BooleanDataType)dataModel.getAllValues().get(ValveInput.QClose)).addListener((observable, oldValue, newValue) -> this.QClose.setSelected(newValue));
        ((BooleanDataType)dataModel.getAllValues().get(ValveInput.FB_Open)).addListener((observable, oldValue, newValue) -> this.OpenFB.setSelected(newValue));
        ((BooleanDataType)dataModel.getAllValues().get(ValveInput.FB_Close)).addListener((observable, oldValue, newValue) -> this.CloseFB.setSelected(newValue));
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
        boolean actualMode = ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Mode)).getValue();
        boolean actualSource = ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Source)).getValue();
        boolean actualonitoringEnable = ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Enable_Monitoring)).getValue();
        int actualMonitoringTime = ((IntegerDataType)dataModel.getAllValues().get(ValveOutput.Monitoring_Time)).getValue();
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
        this.setOnOpenedClosedChange(dataModel);
        this.setOnFaultChange(dataModel);
        this.QOpen.setSelected(((BooleanDataType)dataModel.getAllValues().get(ValveInput.QOpen)).getValue());
        this.QClose.setSelected(((BooleanDataType)dataModel.getAllValues().get(ValveInput.QClose)).getValue());
        this.OpenFB.setSelected(((BooleanDataType)dataModel.getAllValues().get(ValveInput.FB_Open)).getValue());
        this.CloseFB.setSelected(((BooleanDataType)dataModel.getAllValues().get(ValveInput.FB_Close)).getValue());
    }

    private void onOpenPressed(RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Open)).setValue(Boolean.TRUE);
    }

    private void onOpenReleased(RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Open)).setValue(Boolean.FALSE);
    }

    private void onClosePressed(RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Close)).setValue(Boolean.TRUE);
    }

    private void onCloseReleased(RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Close)).setValue(Boolean.FALSE);
    }

    private void setOnOpenedClosedChange(RowDataDefinition dataModel) {
        boolean opened_closed = ((BooleanDataType)dataModel.getAllValues().get(ValveInput.Opened_Closed)).getValue();
        if (opened_closed) {
            this.changeStatus("Opened", Color.GREEN);
            this.changeColorOfImageView(Color.GREEN);
        } else {
            this.changeStatus("Closed", Color.RED);
            this.changeColorOfImageView(Color.RED);
        }

    }

    private void setOnFaultChange(RowDataDefinition dataModel) {
        boolean newValue = ((BooleanDataType)dataModel.getAllValues().get(ValveInput.Fault)).getValue();
        this.faultCondition = newValue;
        this.setOnOpenedClosedChange(dataModel);
    }

    protected void onResetPressed(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Reset)).setValue(Boolean.TRUE);
    }

    protected void onResetReleased(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType)dataModel.getAllValues().get(ValveOutput.Reset)).setValue(Boolean.FALSE);
    }
}
