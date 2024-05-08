package com.batch.GUI.FacePlates;

import com.batch.PLCDataSource.PLC.ComplexDataType.PumpInput;
import com.batch.PLCDataSource.PLC.ComplexDataType.PumpOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
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
import java.util.stream.Collectors;

public class PumpFacePlate extends ControlFacePlate {

    private Button Start, Stop;
    private Label modeLabel, sourceLabel;
    private ComboBox<String> monitoring_enable, mode, source;
    private CheckBox QControl, FB, Fault;
    private FaceplateTextField monitoring_time;

    private boolean faultCondition;

    public PumpFacePlate(Stage stage, RowDataDefinition dataModel) {
        super(stage, dataModel);
    }

    @Override
    protected void customizedGraphicsAndActions(RowDataDefinition dataModel, GridPane controlContainer, GridPane monitoringContainer, GridPane statusContainer) {
        //Icon
        Image image = new Image(Resources.getResource("FacePlatesIcons/Pump.png").toString());
        getImageView().setImage(image);

        //Buttons
        Start = new Button("Start Pump");
        Stop = new Button("Stop Pump");

        Start.setPrefWidth(200);
        Stop.setPrefWidth(200);

        //CmboBox
        monitoring_enable = new ComboBox<>();
        mode = new ComboBox<>();
        source = new ComboBox<>();
        monitoring_enable.getItems().addAll(FXCollections.observableArrayList(Arrays.stream(FB_Monitoring.values()).map(Enum::name).collect(Collectors.toList())));
        mode.getItems().addAll(FXCollections.observableArrayList(Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList())));
        source.getItems().addAll(FXCollections.observableArrayList(Arrays.stream(Source.values()).map(Enum::name).collect(Collectors.toList())));
        mode.setPrefWidth(125);
        source.setPrefWidth(125);
        monitoring_enable.setPrefWidth(200);

        //CheckBox
        QControl = new CheckBox("Q output");
        FB = new CheckBox("FB input");
        Fault = new CheckBox("Fault");

        QControl.setMouseTransparent(true);
        FB.setMouseTransparent(true);
        Fault.setMouseTransparent(true);

        //Fields
        monitoring_time = new FaceplateTextField();
        monitoring_time.setPromptText("0.0");
        monitoring_time.setRestrict("[0-9]");
        monitoring_time.setMaxLength(4);
        monitoring_time.setPrefWidth(200);

        //Labels
        modeLabel = new Label("Mode");
        sourceLabel = new Label("Source");
        
        modeLabel.setPrefWidth(70);
        sourceLabel.setPrefWidth(70);
        
        controlContainer.add(modeLabel, 1, 1);
        controlContainer.add(mode, 2, 1);
        controlContainer.add(sourceLabel, 1, 2);
        controlContainer.add(source, 2, 2);
        controlContainer.add(Start, 1, 3, 2, 1);
        controlContainer.add(Stop, 1, 4, 2, 1);

        monitoringContainer.add(monitoring_enable, 1, 1);
        monitoringContainer.add(monitoring_time, 1, 2);

        statusContainer.add(QControl, 1, 1);
        statusContainer.add(FB, 1, 2);
        statusContainer.add(Fault, 1, 3);

        checkDataForInitializingGraphics(dataModel);
        actionHandler(dataModel);
    }

    @Override
    protected void actionHandler(RowDataDefinition dataModel) {
        Start.setOnMousePressed(action -> onStartPressed(dataModel));
        Start.setOnMouseReleased(action -> onStartReleased(dataModel));
        Stop.setOnMousePressed(action -> onStopPressed(dataModel));
        Stop.setOnMouseReleased(action -> onStopReleased(dataModel));
        monitoring_time.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (monitoring_time.getText().length() > 0) {
                    ((IntegerDataType) dataModel.getAllValues().get(PumpOutput.Monitoring_Time)).setValue(Integer.parseInt(monitoring_time.getText()));
                }
                return null;
            }
        });
        
        monitoring_enable.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(FB_Monitoring.Enable_monitoring.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Enable_Monitoring)).setValue(true);
                }else if (newValue.equals(FB_Monitoring.Disable_monitoring.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Enable_Monitoring)).setValue(false);
                }
            }
        });
        mode.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Mode.Automatic.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Mode)).setValue(true);
                }else if (newValue.equals(Mode.Manual.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Mode)).setValue(false);
                }
            }
        });
        source.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Source.Remote.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Source)).setValue(true);
                }else if (newValue.equals(Source.Local.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Source)).setValue(false);
                }
            }
        });

        ((BooleanDataType) dataModel.getAllValues().get(PumpInput.Running)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> setOnRunningChange(dataModel));
        ((BooleanDataType) dataModel.getAllValues().get(PumpInput.Fault)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> setOnFaultChange(dataModel));

        ((BooleanDataType) dataModel.getAllValues().get(PumpInput.Feedback)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> FB.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(PumpInput.QControl)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> QControl.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(PumpInput.Fault)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> Fault.setSelected(newValue));
    }

    @Override
    protected void withFlasher(boolean flashTrigger) {
        if (faultCondition) {
            if (flashTrigger) {
                changeStatus("Fault", Color.YELLOW);
            } else {
                changeStatus("Fault", Color.RED);
            }
        }
    }

    @Override
    protected void checkDataForInitializingGraphics(RowDataDefinition dataModel) {
        boolean actualMode =  ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Mode)).getValue();
        boolean actualSource =  ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Source)).getValue();
        boolean actualonitoringEnable =  ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Enable_Monitoring)).getValue();
        int actualMonitoringTime =  ((IntegerDataType) dataModel.getAllValues().get(PumpOutput.Monitoring_Time)).getValue();
        
        if (actualMode) {
            mode.getSelectionModel().select(Mode.Automatic.name());
        }else{
            mode.getSelectionModel().select(Mode.Manual.name());
        }
        if (actualSource) {
            source.getSelectionModel().select(Source.Remote.name());
        }else{
            source.getSelectionModel().select(Source.Local.name());
        }
        if (actualonitoringEnable) {
            monitoring_enable.getSelectionModel().select(FB_Monitoring.Enable_monitoring.name());
        }else{
            monitoring_enable.getSelectionModel().select(FB_Monitoring.Disable_monitoring.name());
        }
        monitoring_time.setText(String.valueOf(actualMonitoringTime));
        
        FB.setSelected(((BooleanDataType) dataModel.getAllValues().get(PumpInput.Feedback)).getValue());
        QControl.setSelected(((BooleanDataType) dataModel.getAllValues().get(PumpInput.QControl)).getValue());
        Fault.setSelected(((BooleanDataType) dataModel.getAllValues().get(PumpInput.Fault)).getValue());
        
        setOnRunningChange(dataModel);
        setOnFaultChange(dataModel);
    }

    //User actions functions
    private void onStartPressed(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Start)).setValue(Boolean.TRUE);
    }
    private void onStartReleased(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Start)).setValue(Boolean.FALSE);
    }
    private void onStopPressed(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Stop)).setValue(Boolean.TRUE);
    }
    private void onStopReleased(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Stop)).setValue(Boolean.FALSE);
    }

    //System update functions
    private void setOnRunningChange(RowDataDefinition dataModel) {
        boolean newValue = ((BooleanDataType) dataModel.getAllValues().get(PumpInput.Running)).getValue();
        if (newValue){
            changeStatus("Running", Color.GREEN);
            changeColorOfImageView(Color.GREEN);
        } else {
            changeStatus("Stopped", Color.RED);
            changeColorOfImageView(Color.RED);
        }
    }
    private void setOnFaultChange(RowDataDefinition dataModel) {
        boolean x = ((BooleanDataType) dataModel.getAllValues().get(PumpInput.Fault)).getValue();
        faultCondition = x;
        setOnRunningChange(dataModel);
    }

    @Override
    protected void onResetPressed(MouseEvent action,RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Reset)).setValue(Boolean.TRUE);
    }

    @Override
    protected void onResetReleased(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(PumpOutput.Reset)).setValue(Boolean.FALSE);
    }
}
