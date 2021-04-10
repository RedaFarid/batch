
package com.batch.GUI.FacePlates;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;


public class MeasurementBarGraphics extends HBox {
    private VBox paneContainer = new VBox();
    private Cylinder pane = new Cylinder(25, 150);
    private StackPane indecators = new StackPane();

    private FloatProperty value, zero, span, lowWarning, lowAlarm, highWarning, highAlarm;

    private HBox spanLimit = spanLimit();
    private HBox szeroLimit = zeroLimit();
    
    private HBox LA = lowAlarm();
    private HBox LW = lowWarning();
    private HBox HA = highAlarm();
    private HBox HW = highWarning();
    
    private PhongMaterial normal = new PhongMaterial(Color.LIGHTGREEN);
    private PhongMaterial warning = new PhongMaterial(Color.YELLOW);
    private PhongMaterial alarm = new PhongMaterial(Color.RED);
    
    
    private double anchorX, anchorY;
    private double anchorAngleX;
    private double anchorAngleY;
    private DoubleProperty angleX = new SimpleDoubleProperty();
    private DoubleProperty angleY = new SimpleDoubleProperty();
    
    private final double Height = 265;

    public MeasurementBarGraphics(FloatProperty value, FloatProperty zero, FloatProperty span, FloatProperty lowWarning, FloatProperty lowAlarm, FloatProperty highWarning, FloatProperty highAlarm) {
        this.value = value;
        this.zero = zero;
        this.span = span;
        this.lowWarning = lowWarning;
        this.lowAlarm = lowAlarm;
        this.highWarning = highWarning;
        this.highAlarm = highAlarm;
        initialization();
        animation();
    }
    
    
    private void initialization(){
        
        
        pane.setMaterial(normal);
        
        indecators.getChildren().addAll(spanLimit, LA, LW, HA, HW, szeroLimit);
        indecators.prefHeightProperty().bind(pane.heightProperty());
        
        LA.toFront();
        HA.toFront();
        paneContainer.toFront();
        
        paneContainer.setPrefHeight(Height);
        paneContainer.getChildren().add(pane);
        paneContainer.setAlignment(Pos.BOTTOM_CENTER);
        
        moveIndecators();
        
        getChildren().addAll(paneContainer, indecators);
        initMouseControl(this, this);
        setSpacing(1);
        getTransforms().add(new Rotate(30, Rotate.X_AXIS));
        getTransforms().add(new Rotate(-15, Rotate.Y_AXIS));
        
    }
    private void animation() {
        value.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> moveIndecators());
                
        zero.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> moveIndecators());
        span.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> moveIndecators());
        lowWarning.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> moveIndecators());
        lowAlarm.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> moveIndecators());
        highWarning.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> moveIndecators());
        highAlarm.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> moveIndecators());
    }
    
    private HBox spanLimit(){
        HBox box = new HBox();
        box.setSpacing(2);
        
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double []{
            0.0, 10.0,
            10.0, 15.0,
            10.0, 5.0
        });
        triangle.setFill(Color.BLACK);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1);
        
        
        Label label = new Label(" Span");
        label.setFont(Font.font(11));
        label.setBackground(new Background(new BackgroundFill(Color.BLACK.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setStyle("-fx-text-fill :white;");
        label.setPrefWidth(50);
        
        box.getChildren().addAll(triangle, label);
        
        return box;
    }
    private HBox zeroLimit(){
        HBox box = new HBox();
        box.setSpacing(2);
        
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double []{
            0.0, 10.0,
            10.0, 15.0,
            10.0, 5.0
        });
        triangle.setFill(Color.BLACK);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1);
        
        
        Label label = new Label(" Zero");
        label.setFont(Font.font(11));
        label.setBackground(new Background(new BackgroundFill(Color.BLACK.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setStyle("-fx-text-fill :white;");
        label.setPrefWidth(50);
        
        box.getChildren().addAll(triangle, label);
        
        return box;
    }
    
    private HBox lowAlarm(){
        HBox box = new HBox();
        box.setSpacing(2);
        
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double []{
            0.0, 10.0,
            10.0, 15.0,
            10.0, 5.0
        });
        triangle.setFill(Color.RED);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1);
        
        
        Label label = new Label(" L - A");
        label.setFont(Font.font(11));
        label.setBackground(new Background(new BackgroundFill(Color.RED.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(50);
        
        box.getChildren().addAll(triangle, label);
        
        return box;
    }
    private HBox lowWarning(){
        HBox box = new HBox();
        box.setSpacing(2);
        
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double []{
            0.0, 10.0,
            10.0, 15.0,
            10.0, 5.0
        });
        triangle.setFill(Color.YELLOW);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1);
        
        Label label = new Label(" L - W");
        label.setFont(Font.font(11));
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(50);
        
        box.getChildren().addAll(triangle, label);
//        box.setAlignment(Pos.BASELINE_LEFT);
        
        return box;
    }
    private HBox highAlarm(){
        HBox box = new HBox();
        box.setSpacing(2);
        
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double []{
            0.0, 10.0,
            10.0, 15.0,
            10.0, 5.0
        });
        triangle.setFill(Color.RED);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1);
        
        Label label = new Label(" H - A");
        label.setFont(Font.font(11));
        label.setBackground(new Background(new BackgroundFill(Color.RED.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(50);
        
        box.getChildren().addAll(triangle, label);
//        box.setAlignment(Pos.BASELINE_LEFT);
        
        return box;
    }
    private HBox highWarning(){
        HBox box = new HBox();
        box.setSpacing(2);
        
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double []{
            0.0, 10.0,
            10.0, 15.0,
            10.0, 5.0
        });
        triangle.setFill(Color.YELLOW);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1);
        
        Label label = new Label(" H - W");
        label.setFont(Font.font(11));
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(50);
        
        box.getChildren().addAll(triangle, label);
//        box.setAlignment(Pos.BASELINE_LEFT);
        
        return box;
    }

    private void moveIndecators() {
        float qtyValue = value.getValue();
        
        float zeroValue = zero.getValue();
        float spanValue = span.getValue();
        
        float lowWarningValue = lowWarning.getValue();
        float lowAlarmValue = lowAlarm.getValue();
        float highWarningValue = highWarning.getValue();
        float highAlarmValue = highAlarm.getValue();
        
        float delta = spanValue - zeroValue;
        
        float lowWarningOffset = (lowWarningValue - zeroValue) / delta;
        float lowAlarmOffset = (lowAlarmValue - zeroValue) / delta;
        float highWarningOffset = (highWarningValue - zeroValue) / delta;
        float highAlarmOffset = (highAlarmValue - zeroValue) / delta;

        float moveLA = (float) ((1 - lowAlarmOffset) * Height);
        float moveLW = (float) ((1 - lowWarningOffset) * Height);
        float moveHA = (float) ((1 - highAlarmOffset) * Height);
        float moveHW = (float) ((1 - highWarningOffset) * Height);
        if (moveHA > Height || moveHA < 0
                || moveLW > Height || moveLW < 0
                || moveHA > Height || moveHA < 0
                || moveHW > Height || moveHW < 0) {
            
        }else{
            LA.setTranslateY(moveLA - 7.5);
            LW.setTranslateY(moveLW - 7.5);
            HA.setTranslateY(moveHA - 7.5);
            HW.setTranslateY(moveHW - 7.5);
        }
        if (((qtyValue - zeroValue) / delta * Height) < Height) {
            pane.setHeight((qtyValue - zeroValue) / delta * Height);
        }else{
            pane.setHeight(Height);
        }

        if (qtyValue < lowWarningValue && qtyValue > lowAlarmValue) {
            pane.setMaterial(warning);
        } else if (qtyValue < lowWarningValue && qtyValue < lowAlarmValue) {
            pane.setMaterial(alarm);
        } else if (qtyValue > highWarningValue && qtyValue < highAlarmValue) {
            pane.setMaterial(warning);
        } else if (qtyValue > highWarningValue && qtyValue > highAlarmValue) {
            pane.setMaterial(alarm);
        } else {
            pane.setMaterial(normal);
        }
        
        szeroLimit.setTranslateY(Height);
        spanLimit.setTranslateY(0.0);

    }
    
    private void initMouseControl(HBox g, HBox scene) {
        Rotate rotateX;
        Rotate rotateY;

        g.getTransforms().addAll(
                rotateX = new Rotate(0.0, Rotate.X_AXIS),
                rotateY = new Rotate(0.0, Rotate.Y_AXIS)
        );
        rotateX.angleProperty().bind(angleX);
        rotateY.angleProperty().bind(angleY);

        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });
        scene.setOnMouseDragged(event -> {
            angleX.set(anchorAngleX - (anchorY - event.getSceneY()) * 0.1);
            angleY.set(anchorAngleY - (anchorX + event.getSceneX()) * 0.1);
        });

    }

}
