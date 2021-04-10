package com.batch.GUI.FacePlates;


import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ComplexDataType.WeightInput;
import com.batch.PLCDataSource.PLC.ComplexDataType.WeightOutput;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class WeightFacePlate extends MeasurementFacePlate {

    private CheckBox lowWarningSignal, lowAlarmSignal, HighWarningSignal, HighAlarmSignal, latchAlarmsTillReset, enableLowPassFilter;
    private FaceplateTextField Zero, Span, LowWarning, HighWarning, LowAlarm, HighAlam, coeffecient;
    
    private Label ZeroLabel, SpanLabel, LowWarningLabel, HighWarningLabel, LowAlarmLabel, HighAlamLabel, coeffecientLabel;

    private boolean faultCondition;

    public WeightFacePlate(Stage stage, RowDataDefinition dataModel, String unit) {
        super(stage, dataModel, unit);
    }

    @Override
    protected void customizedGraphicsAndActions(BorderPane root, RowDataDefinition dataModel, GridPane configurationsContainer, GridPane alarmsSettingsContainer, GridPane statusContainer) {

        //CheckBox
        lowWarningSignal = new CheckBox("Low warning");
        lowAlarmSignal = new CheckBox("Low alarm");
        HighWarningSignal = new CheckBox("High warning");
        HighAlarmSignal = new CheckBox("High alarm");

        lowWarningSignal.setMouseTransparent(true);
        lowAlarmSignal.setMouseTransparent(true);
        HighWarningSignal.setMouseTransparent(true);
        HighAlarmSignal.setMouseTransparent(true);

        //Fields
        Zero = new FaceplateTextField();
        Zero.setPromptText("0.0");
        Zero.setRestrict("[0-9].");
        Zero.setMaxLength(10);
        Zero.setPrefWidth(200);

        Span = new FaceplateTextField();
        Span.setPromptText("0.0");
        Span.setRestrict("[0-9].");
        Span.setMaxLength(10);
        Span.setPrefWidth(200);

        LowWarning = new FaceplateTextField();
        LowWarning.setPromptText("0.0");
        LowWarning.setRestrict("[0-9].");
        LowWarning.setMaxLength(10);
        LowWarning.setPrefWidth(200);

        LowAlarm = new FaceplateTextField();
        LowAlarm.setPromptText("0.0");
        LowAlarm.setRestrict("[0-9].");
        LowAlarm.setMaxLength(10);
        LowAlarm.setPrefWidth(200);

        HighWarning = new FaceplateTextField();
        HighWarning.setPromptText("0.0");
        HighWarning.setRestrict("[0-9].");
        HighWarning.setMaxLength(10);
        HighWarning.setPrefWidth(200);

        HighAlam = new FaceplateTextField();
        HighAlam.setPromptText("0.0");
        HighAlam.setRestrict("[0-9].");
        HighAlam.setMaxLength(10);
        HighAlam.setPrefWidth(200);
        
        coeffecient = new FaceplateTextField();
        HighAlam.setPromptText("0.0");
        HighAlam.setRestrict("[0-9].");
        HighAlam.setMaxLength(10);
        HighAlam.setPrefWidth(200);

        //Labels
        
        ZeroLabel = new Label("Zero ");
        SpanLabel = new Label("Span ");
        LowWarningLabel = new Label("Low warning ");
        HighWarningLabel = new Label("High warning ");
        LowAlarmLabel = new Label("Low alarm ");
        HighAlamLabel = new Label("High alarm ");
        coeffecientLabel = new Label("Coeffecient ");
        
        
        ZeroLabel.setPrefWidth(100);
        SpanLabel.setPrefWidth(100);
        LowWarningLabel.setPrefWidth(100);
        HighWarningLabel.setPrefWidth(100);
        LowAlarmLabel.setPrefWidth(100);
        HighAlamLabel.setPrefWidth(100);
        
        Zero.setPrefWidth(150);
        Span.setPrefWidth(150);
        LowWarning.setPrefWidth(150);
        HighWarning.setPrefWidth(150);
        LowAlarm.setPrefWidth(150);
        HighAlam.setPrefWidth(150);
        
        latchAlarmsTillReset = new CheckBox("Latch alarms till reset");
        enableLowPassFilter = new CheckBox("Enable low pass smoothing");
        
        configurationsContainer.add(latchAlarmsTillReset, 1, 0, 2, 1);
        configurationsContainer.add(enableLowPassFilter, 1, 1, 2, 1);
        configurationsContainer.add(coeffecient, 2, 2);
        configurationsContainer.add(coeffecientLabel, 1, 2);
        configurationsContainer.add(Zero, 2, 5);
        configurationsContainer.add(ZeroLabel, 1, 5);
        configurationsContainer.add(Span, 2, 6);
        configurationsContainer.add(SpanLabel, 1, 6);
        
        alarmsSettingsContainer.add(HighAlam, 2, 1);
        alarmsSettingsContainer.add(HighWarning, 2, 2);
        alarmsSettingsContainer.add(LowWarning, 2, 3);
        alarmsSettingsContainer.add(LowAlarm, 2, 4);
        alarmsSettingsContainer.add(HighAlamLabel, 1, 1);
        alarmsSettingsContainer.add(HighWarningLabel, 1, 2);
        alarmsSettingsContainer.add(LowWarningLabel, 1, 3);
        alarmsSettingsContainer.add(LowAlarmLabel, 1, 4);
        
        statusContainer.add(lowWarningSignal, 1, 1);
        statusContainer.add(lowAlarmSignal, 1, 2);
        statusContainer.add(HighWarningSignal, 1, 3);
        statusContainer.add(HighAlarmSignal, 1, 4);

        checkDataForInitializingGraphics(dataModel);
        actionHandler(dataModel);
    }
    
    @Override
    protected void actionHandler(RowDataDefinition dataModel) {

        latchAlarmsTillReset.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                ((BooleanDataType) dataModel.getAllValues().get(WeightOutput.Latch_Alarms_Till_Reset)).setValue(newValue);
            }
        });
        enableLowPassFilter.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                ((BooleanDataType) dataModel.getAllValues().get(WeightOutput.Enable_Low_Pass_Filter)).setValue(newValue);
            }
        });
        
        Zero.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (Zero.getText().length() > 0) {
                        ((RealDataType) dataModel.getAllValues().get(WeightOutput.Zero)).setValue(Float.parseFloat(Zero.getText()));
                    }
                return null;
            }
        });
        Span.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (Span.getText().length() > 0) {
                    ((RealDataType) dataModel.getAllValues().get(WeightOutput.Span)).setValue(Float.parseFloat(Span.getText()));
                }
                return null;
            }
        });
        LowWarning.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (LowWarning.getText().length() > 0) {
                    ((RealDataType) dataModel.getAllValues().get(WeightOutput.Low_Warning_SP)).setValue(Float.parseFloat(LowWarning.getText()));
                }
                return null;
            }
        });
        LowAlarm.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (LowAlarm.getText().length() > 0) {
                    ((RealDataType) dataModel.getAllValues().get(WeightOutput.Low_Alarm_Sp)).setValue(Float.parseFloat(LowAlarm.getText()));
                }
                return null;
            }
        });
        HighWarning.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (HighWarning.getText().length() > 0) {
                    ((RealDataType) dataModel.getAllValues().get(WeightOutput.High_Warning_SP)).setValue(Float.parseFloat(HighWarning.getText()));
                }
                return null;
            }
        });
        HighAlam.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (HighAlam.getText().length() > 0) {
                        ((RealDataType) dataModel.getAllValues().get(WeightOutput.High_Alarm_SP)).setValue(Float.parseFloat(HighAlam.getText()));
                    }
                return null;
            }
        });
        coeffecient.onEneterKeyPressed(new Callback<String, Double>() {
            @Override
            public Double call(String param) {
                if (coeffecient.getText().length() > 0) {
                    ((RealDataType) dataModel.getAllValues().get(WeightOutput.Low_Pass_Coeffecient)).setValue(Float.parseFloat(coeffecient.getText()));
                }
                return null;
            }
        });
        
        ((BooleanDataType) dataModel.getAllValues().get(WeightInput.High_Alarm)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> HighAlarmSignal.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(WeightInput.High_Warning)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> HighWarningSignal.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(WeightInput.Low_Alarm)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> lowAlarmSignal.setSelected(newValue));
        ((BooleanDataType) dataModel.getAllValues().get(WeightInput.Low_Warning)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> lowWarningSignal.setSelected(newValue));
    }

    @Override
    protected void withFlasher(boolean flashTrigger) {
    }

    @Override
    protected void checkDataForInitializingGraphics(RowDataDefinition dataModel) {
        float zeroValue = ((RealDataType) dataModel.getAllValues().get(WeightOutput.Zero)).getValue();
        float spanValue = ((RealDataType) dataModel.getAllValues().get(WeightOutput.Span)).getValue();
        
        float lowWarnValue = ((RealDataType) dataModel.getAllValues().get(WeightOutput.Low_Warning_SP)).getValue();
        float lowAlaValue = ((RealDataType) dataModel.getAllValues().get(WeightOutput.Low_Alarm_Sp)).getValue();
        float highWarnValue = ((RealDataType) dataModel.getAllValues().get(WeightOutput.High_Warning_SP)).getValue();
        float highAlaValue = ((RealDataType) dataModel.getAllValues().get(WeightOutput.High_Alarm_SP)).getValue();
        
        boolean enableLowPass = ((BooleanDataType) dataModel.getAllValues().get(WeightOutput.Enable_Low_Pass_Filter)).getValue();
        boolean enableSimulationR = ((BooleanDataType) dataModel.getAllValues().get(WeightOutput.Enable_Simulation)).getValue();
        boolean enableLatchAlarms = ((BooleanDataType) dataModel.getAllValues().get(WeightOutput.Latch_Alarms_Till_Reset)).getValue();
        float lowPassCoeffecient = ((RealDataType) dataModel.getAllValues().get(WeightOutput.Low_Pass_Coeffecient)).getValue();
        float simulationValue = ((RealDataType) dataModel.getAllValues().get(WeightOutput.Simulation_Value)).getValue();
        
        boolean lowAlarmSig = ((BooleanDataType) dataModel.getAllValues().get(WeightInput.Low_Alarm)).getValue();
        boolean lowWarningSig = ((BooleanDataType) dataModel.getAllValues().get(WeightInput.Low_Warning)).getValue();
        boolean highAlarmSig = ((BooleanDataType) dataModel.getAllValues().get(WeightInput.High_Alarm)).getValue();
        boolean highWarningSig = ((BooleanDataType) dataModel.getAllValues().get(WeightInput.High_Warning)).getValue();
        
        
        Zero.setText(String.valueOf(zeroValue));
        Span.setText(String.valueOf(spanValue));

        LowWarning.setText(String.valueOf(lowWarnValue));
        LowAlarm.setText(String.valueOf(lowAlaValue));
        HighWarning.setText(String.valueOf(highWarnValue));
        HighAlam.setText(String.valueOf(highAlaValue));

        enableLowPassFilter.setSelected(enableLowPass);
        coeffecient.setText(String.valueOf(lowPassCoeffecient));
        super.simulationValueField.setText(String.valueOf(simulationValue));
        
        enableLowPassFilter.setSelected(enableLowPass);
        latchAlarmsTillReset.setSelected(enableLatchAlarms);
        enableSimulation.setSelected(enableSimulationR);
        
        HighAlarmSignal.setSelected(highAlarmSig);
        HighWarningSignal.setSelected(highWarningSig);
        lowAlarmSignal.setSelected(lowAlarmSig);
        lowWarningSignal.setSelected(lowWarningSig);
    }



    @Override
    protected void onResetPressed(MouseEvent action, RowDataDefinition dataModel) {
        
    }

    @Override
    protected void onResetReleased(MouseEvent action, RowDataDefinition dataModel) {
        
    }
}
