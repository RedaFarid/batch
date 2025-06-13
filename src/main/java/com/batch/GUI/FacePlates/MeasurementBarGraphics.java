package com.batch.GUI.FacePlates;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

public class MeasurementBarGraphics extends HBox {
    private final double Height;
    private final VBox paneContainer = new VBox();
    private final Cylinder pane = new Cylinder(25.0F, 150.0F);
    private final StackPane indecators = new StackPane();
    private final FloatProperty value;
    private final FloatProperty zero;
    private final FloatProperty span;
    private final FloatProperty lowWarning;
    private final FloatProperty lowAlarm;
    private final FloatProperty highWarning;
    private final FloatProperty highAlarm;
    private final HBox spanLimit = this.spanLimit();
    private final HBox szeroLimit = this.zeroLimit();
    private final HBox LA = this.lowAlarm();
    private final HBox LW = this.lowWarning();
    private final HBox HA = this.highAlarm();
    private final HBox HW = this.highWarning();
    private final PhongMaterial normal;
    private final PhongMaterial warning;
    private final PhongMaterial alarm;
    private final DoubleProperty angleX;
    private final DoubleProperty angleY;
    private double anchorX;
    private double anchorY;
    private double anchorAngleX;
    private double anchorAngleY;

    public MeasurementBarGraphics(FloatProperty value, FloatProperty zero, FloatProperty span, FloatProperty lowWarning, FloatProperty lowAlarm, FloatProperty highWarning, FloatProperty highAlarm) {
        this.normal = new PhongMaterial(Color.LIGHTGREEN);
        this.warning = new PhongMaterial(Color.YELLOW);
        this.alarm = new PhongMaterial(Color.RED);
        this.angleX = new SimpleDoubleProperty();
        this.angleY = new SimpleDoubleProperty();
        this.Height = 265.0F;
        this.value = value;
        this.zero = zero;
        this.span = span;
        this.lowWarning = lowWarning;
        this.lowAlarm = lowAlarm;
        this.highWarning = highWarning;
        this.highAlarm = highAlarm;
        this.initialization();
        this.animation();
    }

    private void initialization() {
        this.pane.setMaterial(this.normal);
        this.indecators.getChildren().addAll(this.spanLimit, this.LA, this.LW, this.HA, this.HW, this.szeroLimit);
        this.indecators.prefHeightProperty().bind(this.pane.heightProperty());
        this.LA.toFront();
        this.HA.toFront();
        this.paneContainer.toFront();
        this.paneContainer.setPrefHeight(265.0F);
        this.paneContainer.getChildren().add(this.pane);
        this.paneContainer.setAlignment(Pos.BOTTOM_CENTER);
        this.moveIndecators();
        this.getChildren().addAll(this.paneContainer, this.indecators);
        this.initMouseControl(this, this);
        this.setSpacing(1.0F);
        this.getTransforms().add(new Rotate(30.0F, Rotate.X_AXIS));
        this.getTransforms().add(new Rotate(-15.0F, Rotate.Y_AXIS));
    }

    private void animation() {
        this.value.addListener((observable, oldValue, newValue) -> this.moveIndecators());
        this.zero.addListener((observable, oldValue, newValue) -> this.moveIndecators());
        this.span.addListener((observable, oldValue, newValue) -> this.moveIndecators());
        this.lowWarning.addListener((observable, oldValue, newValue) -> this.moveIndecators());
        this.lowAlarm.addListener((observable, oldValue, newValue) -> this.moveIndecators());
        this.highWarning.addListener((observable, oldValue, newValue) -> this.moveIndecators());
        this.highAlarm.addListener((observable, oldValue, newValue) -> this.moveIndecators());
    }

    private HBox spanLimit() {
        HBox box = new HBox();
        box.setSpacing(2.0F);
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll((double) 0.0F, (double) 10.0F, (double) 10.0F, (double) 15.0F, (double) 10.0F, (double) 5.0F);
        triangle.setFill(Color.BLACK);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1.0F);
        Label label = new Label(" Span");
        label.setFont(Font.font(11.0F));
        label.setBackground(new Background(new BackgroundFill(Color.BLACK.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setStyle("-fx-text-fill :white;");
        label.setPrefWidth(50.0F);
        box.getChildren().addAll(triangle, label);
        return box;
    }

    private HBox zeroLimit() {
        HBox box = new HBox();
        box.setSpacing(2.0F);
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll((double) 0.0F, (double) 10.0F, (double) 10.0F, (double) 15.0F, (double) 10.0F, (double) 5.0F);
        triangle.setFill(Color.BLACK);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1.0F);
        Label label = new Label(" Zero");
        label.setFont(Font.font(11.0F));
        label.setBackground(new Background(new BackgroundFill(Color.BLACK.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setStyle("-fx-text-fill :white;");
        label.setPrefWidth(50.0F);
        box.getChildren().addAll(triangle, label);
        return box;
    }

    private HBox lowAlarm() {
        HBox box = new HBox();
        box.setSpacing(2.0F);
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll((double) 0.0F, (double) 10.0F, (double) 10.0F, (double) 15.0F, (double) 10.0F, (double) 5.0F);
        triangle.setFill(Color.RED);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1.0F);
        Label label = new Label(" L - A");
        label.setFont(Font.font(11.0F));
        label.setBackground(new Background(new BackgroundFill(Color.RED.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(50.0F);
        box.getChildren().addAll(triangle, label);
        return box;
    }

    private HBox lowWarning() {
        HBox box = new HBox();
        box.setSpacing(2.0F);
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll((double) 0.0F, (double) 10.0F, (double) 10.0F, (double) 15.0F, (double) 10.0F, (double) 5.0F);
        triangle.setFill(Color.YELLOW);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1.0F);
        Label label = new Label(" L - W");
        label.setFont(Font.font(11.0F));
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(50.0F);
        box.getChildren().addAll(triangle, label);
        return box;
    }

    private HBox highAlarm() {
        HBox box = new HBox();
        box.setSpacing(2.0F);
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll((double) 0.0F, (double) 10.0F, (double) 10.0F, (double) 15.0F, (double) 10.0F, (double) 5.0F);
        triangle.setFill(Color.RED);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1.0F);
        Label label = new Label(" H - A");
        label.setFont(Font.font(11.0F));
        label.setBackground(new Background(new BackgroundFill(Color.RED.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(50.0F);
        box.getChildren().addAll(triangle, label);
        return box;
    }

    private HBox highWarning() {
        HBox box = new HBox();
        box.setSpacing(2.0F);
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll((double) 0.0F, (double) 10.0F, (double) 10.0F, (double) 15.0F, (double) 10.0F, (double) 5.0F);
        triangle.setFill(Color.YELLOW);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeType(StrokeType.OUTSIDE);
        triangle.setStrokeWidth(1.0F);
        Label label = new Label(" H - W");
        label.setFont(Font.font(11.0F));
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(50.0F);
        box.getChildren().addAll(triangle, label);
        return box;
    }

    private void moveIndecators() {
        float qtyValue = this.value.getValue();
        float zeroValue = this.zero.getValue();
        float spanValue = this.span.getValue();
        float lowWarningValue = this.lowWarning.getValue();
        float lowAlarmValue = this.lowAlarm.getValue();
        float highWarningValue = this.highWarning.getValue();
        float highAlarmValue = this.highAlarm.getValue();
        float delta = spanValue - zeroValue;
        float lowWarningOffset = (lowWarningValue - zeroValue) / delta;
        float lowAlarmOffset = (lowAlarmValue - zeroValue) / delta;
        float highWarningOffset = (highWarningValue - zeroValue) / delta;
        float highAlarmOffset = (highAlarmValue - zeroValue) / delta;
        float moveLA = (float) ((double) (1.0F - lowAlarmOffset) * (double) 265.0F);
        float moveLW = (float) ((double) (1.0F - lowWarningOffset) * (double) 265.0F);
        float moveHA = (float) ((double) (1.0F - highAlarmOffset) * (double) 265.0F);
        float moveHW = (float) ((double) (1.0F - highWarningOffset) * (double) 265.0F);
        if (!((double) moveHA > (double) 265.0F) && !(moveHA < 0.0F) && !((double) moveLW > (double) 265.0F) && !(moveLW < 0.0F) && !((double) moveHA > (double) 265.0F) && !(moveHA < 0.0F) && !((double) moveHW > (double) 265.0F) && !(moveHW < 0.0F)) {
            this.LA.setTranslateY((double) moveLA - (double) 7.5F);
            this.LW.setTranslateY((double) moveLW - (double) 7.5F);
            this.HA.setTranslateY((double) moveHA - (double) 7.5F);
            this.HW.setTranslateY((double) moveHW - (double) 7.5F);
        }

        if ((double) ((qtyValue - zeroValue) / delta) * (double) 265.0F < (double) 265.0F) {
            this.pane.setHeight((double) ((qtyValue - zeroValue) / delta) * (double) 265.0F);
        } else {
            this.pane.setHeight(265.0F);
        }

        if (qtyValue < lowWarningValue && qtyValue > lowAlarmValue) {
            this.pane.setMaterial(this.warning);
        } else if (qtyValue < lowWarningValue && qtyValue < lowAlarmValue) {
            this.pane.setMaterial(this.alarm);
        } else if (qtyValue > highWarningValue && qtyValue < highAlarmValue) {
            this.pane.setMaterial(this.warning);
        } else if (qtyValue > highWarningValue && qtyValue > highAlarmValue) {
            this.pane.setMaterial(this.alarm);
        } else {
            this.pane.setMaterial(this.normal);
        }

        this.szeroLimit.setTranslateY(265.0F);
        this.spanLimit.setTranslateY(0.0F);
    }

    private void initMouseControl(HBox g, HBox scene) {
        Rotate rotateX;
        Rotate rotateY;
        g.getTransforms().addAll(rotateX = new Rotate(0.0F, Rotate.X_AXIS), rotateY = new Rotate(0.0F, Rotate.Y_AXIS));
        rotateX.angleProperty().bind(this.angleX);
        rotateY.angleProperty().bind(this.angleY);
        scene.setOnMousePressed((event) -> {
            this.anchorX = event.getSceneX();
            this.anchorY = event.getSceneY();
            this.anchorAngleX = this.angleX.get();
            this.anchorAngleY = this.angleY.get();
        });
        scene.setOnMouseDragged((event) -> {
            this.angleX.set(this.anchorAngleX - (this.anchorY - event.getSceneY()) * 0.1);
            this.angleY.set(this.anchorAngleY - (this.anchorX + event.getSceneX()) * 0.1);
        });
    }
}
