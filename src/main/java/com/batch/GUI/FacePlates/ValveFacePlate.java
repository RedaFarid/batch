package com.batch.GUI.FacePlates;


import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ComplexDataType.ValveInput;
import com.batch.PLCDataSource.PLC.ComplexDataType.ValveOutput;
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

public class ValveFacePlate extends ControlFacePlate {
    private Button Open, close;
    private Label modeLbael, sourceLabel;
    private ComboBox<String> monitoring_enable, mode, source;
    private CheckBox QOpen, QClose, OpenFB, CloseFB;
    private FaceplateTextField monitoring_time;
    
    private boolean faultCondition ;
            
    public ValveFacePlate(Stage stage, RowDataDefinition dataModel) {
        super(stage, dataModel);
    }

    @Override
    protected void customizedGraphicsAndActions(RowDataDefinition dataModel, GridPane controlContainer, GridPane monitoringContainer, GridPane statusContainer) {
        Image image = new Image(Resources.getResource("FacePlatesIcons/Valve.png").toString());
        getImageView().setImage(image);
        
        Open = new Button("Open valve");
        close = new Button("Close Valve");

        Open.setPrefWidth(200);
        close.setPrefWidth(200);
        
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
        QOpen = new CheckBox("Q Open");
        QClose = new CheckBox("Q Close");
        OpenFB = new CheckBox("FB Open");
        CloseFB = new CheckBox("FB Close");

        QOpen.setMouseTransparent(true);
        QClose.setMouseTransparent(true);
        OpenFB.setMouseTransparent(true);
        CloseFB.setMouseTransparent(true);

        //Fields
        monitoring_time = new FaceplateTextField();
        monitoring_time.setPromptText("0.0");
        monitoring_time.setRestrict("[0-9]");
        monitoring_time.setMaxLength(4);
        monitoring_time.setPrefWidth(200);

        //Labels
        modeLbael = new Label("Mode");
        sourceLabel = new Label("Source");
        
        modeLbael.setPrefWidth(70);
        sourceLabel.setPrefWidth(70);
        
        controlContainer.add(modeLbael, 1, 1);
        controlContainer.add(mode, 2, 1);
        controlContainer.add(sourceLabel, 1, 2);
        controlContainer.add(source, 2, 2);
        controlContainer.add(Open, 1, 3, 2, 1);
        controlContainer.add(close, 1, 4, 2, 1);

        monitoringContainer.add(monitoring_enable, 1, 1);
        monitoringContainer.add(monitoring_time, 1, 2);

        statusContainer.add(QOpen, 1, 1);
        statusContainer.add(QClose, 1, 2);
        statusContainer.add(OpenFB, 1, 3);
        statusContainer.add(CloseFB, 1, 4);
        

        checkDataForInitializingGraphics(dataModel);
        actionHandler(dataModel);
    }

    @Override
    protected void actionHandler(RowDataDefinition dataModel) {
        Open.setOnMousePressed(action -> onOpenPressed(dataModel));
        Open.setOnMouseReleased(action -> onOpenReleased(dataModel));
        close.setOnMousePressed(action -> onClosePressed(dataModel));
        close.setOnMouseReleased(action -> onCloseReleased(dataModel));
        monitoring_time.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (monitoring_time.getText().length() > 0) {
                    ((IntegerDataType) dataModel.getAllValues().get(ValveOutput.Monitoring_Time)).setValue(Integer.parseInt(monitoring_time.getText()));
                }
                return null;
            }
        });
        monitoring_enable.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(FB_Monitoring.Enable_monitoring.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Enable_Monitoring)).setValue(true);
                }else if (newValue.equals(FB_Monitoring.Disable_monitoring.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Enable_Monitoring)).setValue(false);
                }
            }
        });
        mode.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Mode.Automatic.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Mode)).setValue(true);
                }else if (newValue.equals(Mode.Manual.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Mode)).setValue(false);
                }
            }
        });
        source.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Source.Remote.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Source)).setValue(true);
                }else if (newValue.equals(Source.Local.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Source)).setValue(false);
                }
            }
        });

        ((BooleanDataType) dataModel.getAllValues().get(ValveInput.Opened_Closed)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> setOnOpenedClosedChange(dataModel));
        ((BooleanDataType) dataModel.getAllValues().get(ValveInput.Fault)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> setOnFaultChange(dataModel));
        
        ((IntegerDataType) dataModel.getAllValues().get(ValveOutput.Monitoring_Time)).addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> monitoring_time.setText(String.valueOf(newValue)));
        
        ((BooleanDataType) dataModel.getAllValues().get(ValveInput.QOpen)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> QOpen.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(ValveInput.QClose)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> QClose.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(ValveInput.FB_Open)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> OpenFB.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(ValveInput.FB_Close)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> CloseFB.setSelected(newValue));

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
        boolean actualMode =  ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Mode)).getValue();
        boolean actualSource =  ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Source)).getValue();
        boolean actualonitoringEnable =  ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Enable_Monitoring)).getValue();
        int actualMonitoringTime =  ((IntegerDataType) dataModel.getAllValues().get(ValveOutput.Monitoring_Time)).getValue();
        
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
        
        setOnOpenedClosedChange(dataModel);
        setOnFaultChange(dataModel);

        QOpen.setSelected(((BooleanDataType) dataModel.getAllValues().get(ValveInput.QOpen)).getValue());
        QClose.setSelected(((BooleanDataType) dataModel.getAllValues().get(ValveInput.QClose)).getValue());
        OpenFB.setSelected(((BooleanDataType) dataModel.getAllValues().get(ValveInput.FB_Open)).getValue());
        CloseFB.setSelected(((BooleanDataType) dataModel.getAllValues().get(ValveInput.FB_Close)).getValue());
    }
    
    private void onOpenPressed(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Open)).setValue(Boolean.TRUE);
    }
    private void onOpenReleased(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Open)).setValue(Boolean.FALSE);
    }
    private void onClosePressed(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Close)).setValue(Boolean.TRUE);
    }
    private void onCloseReleased(RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Close)).setValue(Boolean.FALSE);
    }
    private void setOnOpenedClosedChange(RowDataDefinition dataModel) {
        boolean opened_closed = ((BooleanDataType) dataModel.getAllValues().get(ValveInput.Opened_Closed)).getValue();
        
        if (opened_closed) {
            changeStatus("Opened", Color.GREEN);
            changeColorOfImageView(Color.GREEN);
        }else {
            changeStatus("Closed", Color.RED);
            changeColorOfImageView(Color.RED);
        }
    }
    private void setOnFaultChange(RowDataDefinition dataModel) {
        boolean newValue = ((BooleanDataType) dataModel.getAllValues().get(ValveInput.Fault)).getValue();
        faultCondition = newValue;
        setOnOpenedClosedChange(dataModel);
    }

    @Override
    protected void onResetPressed(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Reset)).setValue(Boolean.TRUE);
    }

    @Override
    protected void onResetReleased(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(ValveOutput.Reset)).setValue(Boolean.FALSE);
    }
}