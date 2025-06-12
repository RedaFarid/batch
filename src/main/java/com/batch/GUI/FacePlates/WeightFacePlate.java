

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
    private CheckBox lowWarningSignal;
    private CheckBox lowAlarmSignal;
    private CheckBox HighWarningSignal;
    private CheckBox HighAlarmSignal;
    private CheckBox latchAlarmsTillReset;
    private CheckBox enableLowPassFilter;
    private FaceplateTextField Zero;
    private FaceplateTextField Span;
    private FaceplateTextField LowWarning;
    private FaceplateTextField HighWarning;
    private FaceplateTextField LowAlarm;
    private FaceplateTextField HighAlam;
    private FaceplateTextField coeffecient;
    private Label ZeroLabel;
    private Label SpanLabel;
    private Label LowWarningLabel;
    private Label HighWarningLabel;
    private Label LowAlarmLabel;
    private Label HighAlamLabel;
    private Label coeffecientLabel;
    private boolean faultCondition;

    public WeightFacePlate(Stage stage, RowDataDefinition dataModel, String unit) {
        super(stage, dataModel, unit);
    }

    protected void customizedGraphicsAndActions(BorderPane root, RowDataDefinition dataModel, GridPane configurationsContainer, GridPane alarmsSettingsContainer, GridPane statusContainer) {
        this.lowWarningSignal = new CheckBox("Low warning");
        this.lowAlarmSignal = new CheckBox("Low alarm");
        this.HighWarningSignal = new CheckBox("High warning");
        this.HighAlarmSignal = new CheckBox("High alarm");
        this.lowWarningSignal.setMouseTransparent(true);
        this.lowAlarmSignal.setMouseTransparent(true);
        this.HighWarningSignal.setMouseTransparent(true);
        this.HighAlarmSignal.setMouseTransparent(true);
        this.Zero = new FaceplateTextField();
        this.Zero.setPromptText("0.0");
        this.Zero.setRestrict("[0-9].");
        this.Zero.setMaxLength(10);
        this.Zero.setPrefWidth((double)200.0F);
        this.Span = new FaceplateTextField();
        this.Span.setPromptText("0.0");
        this.Span.setRestrict("[0-9].");
        this.Span.setMaxLength(10);
        this.Span.setPrefWidth((double)200.0F);
        this.LowWarning = new FaceplateTextField();
        this.LowWarning.setPromptText("0.0");
        this.LowWarning.setRestrict("[0-9].");
        this.LowWarning.setMaxLength(10);
        this.LowWarning.setPrefWidth((double)200.0F);
        this.LowAlarm = new FaceplateTextField();
        this.LowAlarm.setPromptText("0.0");
        this.LowAlarm.setRestrict("[0-9].");
        this.LowAlarm.setMaxLength(10);
        this.LowAlarm.setPrefWidth((double)200.0F);
        this.HighWarning = new FaceplateTextField();
        this.HighWarning.setPromptText("0.0");
        this.HighWarning.setRestrict("[0-9].");
        this.HighWarning.setMaxLength(10);
        this.HighWarning.setPrefWidth((double)200.0F);
        this.HighAlam = new FaceplateTextField();
        this.HighAlam.setPromptText("0.0");
        this.HighAlam.setRestrict("[0-9].");
        this.HighAlam.setMaxLength(10);
        this.HighAlam.setPrefWidth((double)200.0F);
        this.coeffecient = new FaceplateTextField();
        this.HighAlam.setPromptText("0.0");
        this.HighAlam.setRestrict("[0-9].");
        this.HighAlam.setMaxLength(10);
        this.HighAlam.setPrefWidth((double)200.0F);
        this.ZeroLabel = new Label("Zero ");
        this.SpanLabel = new Label("Span ");
        this.LowWarningLabel = new Label("Low warning ");
        this.HighWarningLabel = new Label("High warning ");
        this.LowAlarmLabel = new Label("Low alarm ");
        this.HighAlamLabel = new Label("High alarm ");
        this.coeffecientLabel = new Label("Coeffecient ");
        this.ZeroLabel.setPrefWidth((double)100.0F);
        this.SpanLabel.setPrefWidth((double)100.0F);
        this.LowWarningLabel.setPrefWidth((double)100.0F);
        this.HighWarningLabel.setPrefWidth((double)100.0F);
        this.LowAlarmLabel.setPrefWidth((double)100.0F);
        this.HighAlamLabel.setPrefWidth((double)100.0F);
        this.Zero.setPrefWidth((double)150.0F);
        this.Span.setPrefWidth((double)150.0F);
        this.LowWarning.setPrefWidth((double)150.0F);
        this.HighWarning.setPrefWidth((double)150.0F);
        this.LowAlarm.setPrefWidth((double)150.0F);
        this.HighAlam.setPrefWidth((double)150.0F);
        this.latchAlarmsTillReset = new CheckBox("Latch alarms till reset");
        this.enableLowPassFilter = new CheckBox("Enable low pass smoothing");
        configurationsContainer.add(this.latchAlarmsTillReset, 1, 0, 2, 1);
        configurationsContainer.add(this.enableLowPassFilter, 1, 1, 2, 1);
        configurationsContainer.add(this.coeffecient, 2, 2);
        configurationsContainer.add(this.coeffecientLabel, 1, 2);
        configurationsContainer.add(this.Zero, 2, 5);
        configurationsContainer.add(this.ZeroLabel, 1, 5);
        configurationsContainer.add(this.Span, 2, 6);
        configurationsContainer.add(this.SpanLabel, 1, 6);
        alarmsSettingsContainer.add(this.HighAlam, 2, 1);
        alarmsSettingsContainer.add(this.HighWarning, 2, 2);
        alarmsSettingsContainer.add(this.LowWarning, 2, 3);
        alarmsSettingsContainer.add(this.LowAlarm, 2, 4);
        alarmsSettingsContainer.add(this.HighAlamLabel, 1, 1);
        alarmsSettingsContainer.add(this.HighWarningLabel, 1, 2);
        alarmsSettingsContainer.add(this.LowWarningLabel, 1, 3);
        alarmsSettingsContainer.add(this.LowAlarmLabel, 1, 4);
        statusContainer.add(this.lowWarningSignal, 1, 1);
        statusContainer.add(this.lowAlarmSignal, 1, 2);
        statusContainer.add(this.HighWarningSignal, 1, 3);
        statusContainer.add(this.HighAlarmSignal, 1, 4);
        this.checkDataForInitializingGraphics(dataModel);
        this.actionHandler(dataModel);
    }

    protected void actionHandler(final RowDataDefinition dataModel) {
        this.latchAlarmsTillReset.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                ((BooleanDataType)dataModel.getAllValues().get(WeightOutput.Latch_Alarms_Till_Reset)).setValue(newValue);
            }
        });
        this.enableLowPassFilter.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                ((BooleanDataType)dataModel.getAllValues().get(WeightOutput.Enable_Low_Pass_Filter)).setValue(newValue);
            }
        });
        this.Zero.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (WeightFacePlate.this.Zero.getText().length() > 0) {
                    ((RealDataType)dataModel.getAllValues().get(WeightOutput.Zero)).setValue(Float.parseFloat(WeightFacePlate.this.Zero.getText()));
                }

                return null;
            }
        });
        this.Span.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (WeightFacePlate.this.Span.getText().length() > 0) {
                    ((RealDataType)dataModel.getAllValues().get(WeightOutput.Span)).setValue(Float.parseFloat(WeightFacePlate.this.Span.getText()));
                }

                return null;
            }
        });
        this.LowWarning.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (WeightFacePlate.this.LowWarning.getText().length() > 0) {
                    ((RealDataType)dataModel.getAllValues().get(WeightOutput.Low_Warning_SP)).setValue(Float.parseFloat(WeightFacePlate.this.LowWarning.getText()));
                }

                return null;
            }
        });
        this.LowAlarm.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (WeightFacePlate.this.LowAlarm.getText().length() > 0) {
                    ((RealDataType)dataModel.getAllValues().get(WeightOutput.Low_Alarm_Sp)).setValue(Float.parseFloat(WeightFacePlate.this.LowAlarm.getText()));
                }

                return null;
            }
        });
        this.HighWarning.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (WeightFacePlate.this.HighWarning.getText().length() > 0) {
                    ((RealDataType)dataModel.getAllValues().get(WeightOutput.High_Warning_SP)).setValue(Float.parseFloat(WeightFacePlate.this.HighWarning.getText()));
                }

                return null;
            }
        });
        this.HighAlam.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (WeightFacePlate.this.HighAlam.getText().length() > 0) {
                    ((RealDataType)dataModel.getAllValues().get(WeightOutput.High_Alarm_SP)).setValue(Float.parseFloat(WeightFacePlate.this.HighAlam.getText()));
                }

                return null;
            }
        });
        this.coeffecient.onEneterKeyPressed(new Callback<String, Double>() {
            public Double call(String param) {
                if (WeightFacePlate.this.coeffecient.getText().length() > 0) {
                    ((RealDataType)dataModel.getAllValues().get(WeightOutput.Low_Pass_Coeffecient)).setValue(Float.parseFloat(WeightFacePlate.this.coeffecient.getText()));
                }

                return null;
            }
        });
        ((BooleanDataType)dataModel.getAllValues().get(WeightInput.High_Alarm)).addListener((observable, oldValue, newValue) -> this.HighAlarmSignal.setSelected(newValue));
        ((BooleanDataType)dataModel.getAllValues().get(WeightInput.High_Warning)).addListener((observable, oldValue, newValue) -> this.HighWarningSignal.setSelected(newValue));
        ((BooleanDataType)dataModel.getAllValues().get(WeightInput.Low_Alarm)).addListener((observable, oldValue, newValue) -> this.lowAlarmSignal.setSelected(newValue));
        ((BooleanDataType)dataModel.getAllValues().get(WeightInput.Low_Warning)).addListener((observable, oldValue, newValue) -> this.lowWarningSignal.setSelected(newValue));
    }

    protected void withFlasher(boolean flashTrigger) {
    }

    protected void checkDataForInitializingGraphics(RowDataDefinition dataModel) {
        float zeroValue = ((RealDataType)dataModel.getAllValues().get(WeightOutput.Zero)).getValue();
        float spanValue = ((RealDataType)dataModel.getAllValues().get(WeightOutput.Span)).getValue();
        float lowWarnValue = ((RealDataType)dataModel.getAllValues().get(WeightOutput.Low_Warning_SP)).getValue();
        float lowAlaValue = ((RealDataType)dataModel.getAllValues().get(WeightOutput.Low_Alarm_Sp)).getValue();
        float highWarnValue = ((RealDataType)dataModel.getAllValues().get(WeightOutput.High_Warning_SP)).getValue();
        float highAlaValue = ((RealDataType)dataModel.getAllValues().get(WeightOutput.High_Alarm_SP)).getValue();
        boolean enableLowPass = ((BooleanDataType)dataModel.getAllValues().get(WeightOutput.Enable_Low_Pass_Filter)).getValue();
        boolean enableSimulationR = ((BooleanDataType)dataModel.getAllValues().get(WeightOutput.Enable_Simulation)).getValue();
        boolean enableLatchAlarms = ((BooleanDataType)dataModel.getAllValues().get(WeightOutput.Latch_Alarms_Till_Reset)).getValue();
        float lowPassCoeffecient = ((RealDataType)dataModel.getAllValues().get(WeightOutput.Low_Pass_Coeffecient)).getValue();
        float simulationValue = ((RealDataType)dataModel.getAllValues().get(WeightOutput.Simulation_Value)).getValue();
        boolean lowAlarmSig = ((BooleanDataType)dataModel.getAllValues().get(WeightInput.Low_Alarm)).getValue();
        boolean lowWarningSig = ((BooleanDataType)dataModel.getAllValues().get(WeightInput.Low_Warning)).getValue();
        boolean highAlarmSig = ((BooleanDataType)dataModel.getAllValues().get(WeightInput.High_Alarm)).getValue();
        boolean highWarningSig = ((BooleanDataType)dataModel.getAllValues().get(WeightInput.High_Warning)).getValue();
        this.Zero.setText(String.valueOf(zeroValue));
        this.Span.setText(String.valueOf(spanValue));
        this.LowWarning.setText(String.valueOf(lowWarnValue));
        this.LowAlarm.setText(String.valueOf(lowAlaValue));
        this.HighWarning.setText(String.valueOf(highWarnValue));
        this.HighAlam.setText(String.valueOf(highAlaValue));
        this.enableLowPassFilter.setSelected(enableLowPass);
        this.coeffecient.setText(String.valueOf(lowPassCoeffecient));
        super.simulationValueField.setText(String.valueOf(simulationValue));
        this.enableLowPassFilter.setSelected(enableLowPass);
        this.latchAlarmsTillReset.setSelected(enableLatchAlarms);
        this.enableSimulation.setSelected(enableSimulationR);
        this.HighAlarmSignal.setSelected(highAlarmSig);
        this.HighWarningSignal.setSelected(highWarningSig);
        this.lowAlarmSignal.setSelected(lowAlarmSig);
        this.lowWarningSignal.setSelected(lowWarningSig);
    }

    protected void onResetPressed(MouseEvent action, RowDataDefinition dataModel) {
    }

    protected void onResetReleased(MouseEvent action, RowDataDefinition dataModel) {
    }
}
