package com.batch.GUI.FacePlates;


import java.util.Arrays;
import java.util.stream.Collectors;

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

public class MixerFacePlate extends ControlFacePlate {

    private Button Start, Stop;
    private Label modeLbael, sourceLabel, SpeedLabel, FinalSpeedLabel, AmpereLabel;
    private ComboBox<String>mode, source;
    private CheckBox QControl, Fault;
    private FaceplateTextField Speed, FinalSpeed, Ampere;

    private boolean faultCondition;

    public MixerFacePlate(Stage stage, RowDataDefinition dataModel) {
        super(stage, dataModel);
    }

    @Override
    protected void customizedGraphicsAndActions(RowDataDefinition dataModel, GridPane controlContainer, GridPane monitoringContainer, GridPane statusContainer) {
        //Icon
        Image image = new Image(Resources.getResource("FacePlatesIcons/Mixer.png").toString());
        getImageView().setImage(image);

        //Buttons
        Start = new Button("Start Mixer");
        Stop = new Button("Stop Mixer");

        Start.setPrefWidth(200);
        Stop.setPrefWidth(200);

        //ComboBox
        mode = new ComboBox<>();
        source = new ComboBox<>();
        mode.getItems().addAll(FXCollections.observableArrayList(Arrays.stream(Mode.values()).map(Enum::name).collect(Collectors.toList())));
        source.getItems().addAll(FXCollections.observableArrayList(Arrays.stream(Source.values()).map(Enum::name).collect(Collectors.toList())));
        mode.setPrefWidth(125);
        source.setPrefWidth(125);

        //CheckBox
        QControl = new CheckBox("Q output");
        Fault = new CheckBox("Fault ");

        QControl.setMouseTransparent(true);
        Fault.setMouseTransparent(true);

        //Fields
        Speed = new FaceplateTextField();
        Speed.setPromptText("0.0");
        Speed.setRestrict("[0-9].");
        Speed.setMaxLength(10);
        Speed.setPrefWidth(80);
        
        FinalSpeed = new FaceplateTextField();
        FinalSpeed.setPromptText("0.0");
        FinalSpeed.setRestrict("[0-9].");
        FinalSpeed.setMaxLength(10);
        FinalSpeed.setPrefWidth(120);
        FinalSpeed.setEditable(false);
        
        Ampere = new FaceplateTextField();
        Ampere.setPromptText("0.0");
        Ampere.setRestrict("[0-9].");
        Ampere.setMaxLength(10);
        Ampere.setPrefWidth(120);
        Ampere.setEditable(false);
        
        //Labels
        modeLbael = new Label("Mode");
        sourceLabel = new Label("Source");
        SpeedLabel = new Label("Setpoint");
        FinalSpeedLabel = new Label("Output Speed");
        AmpereLabel = new Label("Motor Current");
        
        modeLbael.setPrefWidth(70);
        sourceLabel.setPrefWidth(70);
        
        controlContainer.add(modeLbael, 1, 1);
        controlContainer.add(mode, 2, 1);
        controlContainer.add(sourceLabel, 1, 2);
        controlContainer.add(source, 2, 2);
        controlContainer.add(Start, 1, 3, 2, 1);
        controlContainer.add(Stop, 1, 4, 2, 1);
        controlContainer.add(SpeedLabel, 1, 5);
        controlContainer.add(Speed, 2, 5);

        statusContainer.add(QControl, 1, 1);
        statusContainer.add(Fault, 1, 2);
        
        monitoringContainer.add(FinalSpeedLabel, 1, 1);
        monitoringContainer.add(FinalSpeed, 2, 1);
        monitoringContainer.add(AmpereLabel, 1, 2);
        monitoringContainer.add(Ampere, 2, 2);

        checkDataForInitializingGraphics(dataModel);
        actionHandler(dataModel);
    }

    @Override
    protected void actionHandler(RowDataDefinition dataModel) {
        Start.setOnMousePressed(action -> onStartPressed(dataModel));
        Start.setOnMouseReleased(action -> onStartReleased(dataModel));
        Stop.setOnMousePressed(action -> onStopPressed(dataModel));
        Stop.setOnMouseReleased(action -> onStopReleased(dataModel));
        mode.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Mode.Automatic.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Mode)).setValue(true);
                }else if (newValue.equals(Mode.Manual.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Mode)).setValue(false);
                }
            }
        });
        source.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(Source.Remote.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Source)).setValue(true);
                }else if (newValue.equals(Source.Local.name())) {
                    ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Source)).setValue(false);
                }
            }
        });
        
        Speed.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (Speed.getText().length() > 0) {
                    ((RealDataType) dataModel.getAllValues().get(MixerOutput.Speed_Setpoint)).setValue(Float.parseFloat(Speed.getText()));
                }
                return null;
            }
        });
        
        ((RealDataType) dataModel.getAllValues().get(MixerInput.Output_Speed)).addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> FinalSpeed.setText(String.valueOf(newValue)));
        ((RealDataType) dataModel.getAllValues().get(MixerInput.Ampere_Reading)).addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> Ampere.setText(String.valueOf(newValue)));

        ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Running)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> setOnRunningChange(dataModel));
        ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Fault)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> setOnFaultChange(dataModel));

        ((BooleanDataType) dataModel.getAllValues().get(MixerInput.QControl)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> QControl.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Fault)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> Fault.setSelected(newValue));
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
        boolean actualMode =  ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Mode)).getValue();
        boolean actualSource =  ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Source)).getValue();
        
        double AmpereReading = ((RealDataType) dataModel.getAllValues().get(MixerInput.Ampere_Reading)).getValue();
        double SpeedReading = ((RealDataType) dataModel.getAllValues().get(MixerOutput.Speed_Setpoint)).getValue();
        double outputSpeed = ((RealDataType) dataModel.getAllValues().get(MixerInput.Output_Speed)).getValue();
        
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
        
        QControl.setSelected(((BooleanDataType) dataModel.getAllValues().get(MixerInput.QControl)).getValue());
        Ampere.setText(String.valueOf(AmpereReading));
        Speed.setText(String.valueOf(SpeedReading));
        FinalSpeed.setText(String.valueOf(outputSpeed));
        
        Fault.setSelected(((BooleanDataType) dataModel.getAllValues().get(MixerInput.Fault)).getValue());
        QControl.setSelected(((BooleanDataType) dataModel.getAllValues().get(MixerInput.QControl)).getValue());
        
        setOnRunningChange(dataModel);
        setOnFaultChange(dataModel);
    }

    //User actions functions
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

    //System update functions
    private void setOnRunningChange(RowDataDefinition dataModel) {
        boolean newValue = ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Running)).getValue();
        if (newValue){
            changeStatus("Running", Color.GREEN);
            changeColorOfImageView(Color.GREEN);
        } else {
            changeStatus("Stopped", Color.RED);
            changeColorOfImageView(Color.RED);
        }
    }
    private void setOnFaultChange(RowDataDefinition dataModel) {
        boolean x = ((BooleanDataType) dataModel.getAllValues().get(MixerInput.Fault)).getValue();
        faultCondition = x;
        setOnRunningChange(dataModel);
    }

    @Override
    protected void onResetPressed(MouseEvent action,RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Reset)).setValue(Boolean.TRUE);
    }

    @Override
    protected void onResetReleased(MouseEvent action, RowDataDefinition dataModel) {
        ((BooleanDataType) dataModel.getAllValues().get(MixerOutput.Reset)).setValue(Boolean.FALSE);
    }
}
