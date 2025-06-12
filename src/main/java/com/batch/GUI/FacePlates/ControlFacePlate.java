
package com.batch.GUI.FacePlates;

import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.Utilities.FlashingGenerator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class ControlFacePlate extends Stage {
    private BorderPane root = new BorderPane();
    private Scene scene;
    private ImageView imView;
    private HBox mainContainer;
    private VBox controlContainer;
    private VBox statusContainer;
    private VBox statusVBox;
    private GridPane signalsPane;
    private GridPane controlPane;
    private GridPane monitoringPane;
    private Label mainLabel;
    private Label Status;
    private Label Control;
    private Label Monitoring;
    private Label Signals;
    private Button reset;
    private Stage mainWindow;
    private RowDataDefinition dataModel;

    public ControlFacePlate(Stage stage, RowDataDefinition dataModel) {
        this.scene = new Scene(this.root);
        this.imView = new ImageView();
        this.mainWindow = stage;
        this.dataModel = dataModel;
        this.initalization();
        this.customizedGraphicsAndActions(dataModel, this.controlPane, this.monitoringPane, this.signalsPane);
        this.flasherStartubg();
    }

    private void initalization() {
        DropShadow shadow = new DropShadow((double)0.5F, (double)0.5F, (double)0.5F, Color.CORAL);
        this.imView.setFitWidth((double)100.0F);
        this.imView.setFitHeight((double)100.0F);
        this.imView.setEffect(shadow);
        this.Control = new Label("Controls ");
        this.Monitoring = new Label("Monitoring ");
        this.Signals = new Label("Signals ");
        this.Status = new Label();
        this.controlContainer = new VBox();
        this.statusContainer = new VBox();
        this.mainContainer = new HBox();
        this.statusVBox = new VBox();
        this.controlContainer.setSpacing((double)5.0F);
        this.statusContainer.setSpacing((double)5.0F);
        this.statusVBox.setSpacing((double)10.0F);
        this.Status = new Label("Idle");
        this.Status.prefHeight((double)100.0F);
        this.Status.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)}));
        this.Status.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths((double)1.0F))}));
        this.Status.setAlignment(Pos.CENTER);
        this.Status.setTextAlignment(TextAlignment.CENTER);
        this.Status.prefWidthProperty().bind(this.statusVBox.widthProperty());
        this.reset = new Button("Reset");
        this.reset.prefWidthProperty().bind(this.statusVBox.widthProperty());
        this.reset.setOnMousePressed((action) -> this.onResetPressed(action, this.dataModel));
        this.reset.setOnMouseReleased((action) -> this.onResetReleased(action, this.dataModel));
        this.signalsPane = new GridPane();
        this.controlPane = new GridPane();
        this.monitoringPane = new GridPane();
        this.signalsPane.setPadding(new Insets((double)5.0F));
        this.controlPane.setPadding(new Insets((double)5.0F));
        this.monitoringPane.setPadding(new Insets((double)5.0F));
        this.signalsPane.setVgap((double)5.0F);
        this.controlPane.setVgap((double)5.0F);
        this.monitoringPane.setVgap((double)5.0F);
        this.signalsPane.setHgap((double)5.0F);
        this.controlPane.setHgap((double)5.0F);
        this.monitoringPane.setHgap((double)5.0F);
        this.signalsPane.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths((double)1.0F))}));
        this.controlPane.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths((double)1.0F))}));
        this.monitoringPane.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths((double)1.0F))}));
        this.statusContainer.getChildren().addAll(new Node[]{this.Signals, this.signalsPane});
        this.controlContainer.getChildren().addAll(new Node[]{this.Control, this.controlPane, this.Monitoring, this.monitoringPane});
        this.statusVBox.getChildren().addAll(new Node[]{this.imView, this.Status, this.reset, this.statusContainer});
        this.mainContainer.getChildren().addAll(new Node[]{this.statusVBox, this.controlContainer});
        this.mainContainer.setPadding(new Insets((double)10.0F));
        this.mainContainer.setSpacing((double)20.0F);
        this.mainLabel = new Label("Details Faceplate :: " + this.dataModel.getName());
        this.mainLabel.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)}));
        this.mainLabel.prefWidthProperty().bind(this.root.widthProperty());
        this.mainLabel.setFont(Font.font((double)12.0F));
        this.mainLabel.setAlignment(Pos.CENTER);
        this.mainLabel.setPrefHeight((double)50.0F);
        this.root.setTop(this.mainLabel);
        this.root.setCenter(this.mainContainer);
        this.setResizable(false);
        this.setScene(this.scene);
        this.initOwner(this.mainWindow);
        this.initStyle(StageStyle.UTILITY);
        this.initModality(Modality.NONE);
        this.setTitle(this.dataModel.getName());
        this.scene.getStylesheets().add("/GUI/Styles/Faceplate.css");
    }

    private void flasherStartubg() {
        FlashingGenerator.getSystem().getFlasher().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        ControlFacePlate.this.withFlasher(newValue);
                    }
                });
            }
        });
    }

    protected abstract void customizedGraphicsAndActions(RowDataDefinition dataModel, GridPane controlContainer, GridPane monitoringContainer, GridPane statusContainer);

    protected abstract void actionHandler(RowDataDefinition dataModel);

    protected abstract void checkDataForInitializingGraphics(RowDataDefinition dataModel);

    protected abstract void withFlasher(boolean flashTrigger);

    protected abstract void onResetPressed(MouseEvent action, RowDataDefinition dataModel);

    protected abstract void onResetReleased(MouseEvent action, RowDataDefinition dataModel);

    protected ImageView getImageView() {
        return this.imView;
    }

    protected void changeColorOfImageView(Color color) {
        Glow glow = new Glow(0.2);
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.GRAY, (double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F);
        Light.Distant light = new Light.Distant((double)100.0F, (double)100.0F, color.brighter().brighter());
        Lighting lighting = new Lighting(light);
        Blend blend = new Blend(BlendMode.MULTIPLY, glow, shadow);
        Blend blend2 = new Blend(BlendMode.MULTIPLY, blend, lighting);
        this.imView.setEffect(blend2);
    }

    protected void changeStatus(String statusString, Color color) {
        Platform.runLater(() -> {
            if (statusString != null) {
                this.Status.setText(statusString);
            }

            this.Status.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(color.brighter(), CornerRadii.EMPTY, Insets.EMPTY)}));
        });
    }

    protected void showFacePlate() {
        this.show();
    }
}
