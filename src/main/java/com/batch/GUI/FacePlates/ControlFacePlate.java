package com.batch.GUI.FacePlates;

import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.Utilities.FlashingGenerator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public abstract class ControlFacePlate extends Stage {

    private BorderPane root = new BorderPane();
    private Scene scene = new Scene(root);
    private ImageView imView = new ImageView();

    private HBox mainContainer;
    private VBox controlContainer, statusContainer, statusVBox;
    private GridPane signalsPane, controlPane, monitoringPane;
    private Label mainLabel, Status, Control, Monitoring, Signals;
    private Button reset ;

    private Stage mainWindow;
    private RowDataDefinition dataModel;

    public ControlFacePlate(Stage stage, RowDataDefinition dataModel) {
        mainWindow = stage;
        this.dataModel = dataModel;
        initalization();
        customizedGraphicsAndActions(dataModel, controlPane, monitoringPane, signalsPane);
        flasherStartubg();
    }

    private void initalization() {
        DropShadow shadow = new DropShadow(0.5, 0.5, 0.5, Color.CORAL);
        imView.setFitWidth(100);
        imView.setFitHeight(100);
        imView.setEffect(shadow);

        
        
        Control = new Label("Controls ");
        Monitoring = new Label("Monitoring ");
        Signals = new Label("Signals ");
        Status = new Label();

        controlContainer = new VBox();
        statusContainer = new VBox();
        mainContainer = new HBox();
        statusVBox = new VBox();

        controlContainer.setSpacing(5);
        statusContainer.setSpacing(5);
        statusVBox.setSpacing(10);

        Status = new Label("Idle");
        Status.prefHeight(100);
        Status.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        Status.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        Status.setAlignment(Pos.CENTER);
        Status.setTextAlignment(TextAlignment.CENTER);
        Status.prefWidthProperty().bind(statusVBox.widthProperty());
        
        reset = new Button("Reset");
        reset.prefWidthProperty().bind(statusVBox.widthProperty());
        reset.setOnMousePressed(action -> onResetPressed(action, dataModel));
        reset.setOnMouseReleased(action -> onResetReleased(action, dataModel));

        signalsPane = new GridPane();
        controlPane = new GridPane();
        monitoringPane = new GridPane();

        signalsPane.setPadding(new Insets(5));
        controlPane.setPadding(new Insets(5));
        monitoringPane.setPadding(new Insets(5));

        signalsPane.setVgap(5);
        controlPane.setVgap(5);
        monitoringPane.setVgap(5);

        signalsPane.setHgap(5);
        controlPane.setHgap(5);
        monitoringPane.setHgap(5);

        signalsPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        controlPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        monitoringPane.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        statusContainer.getChildren().addAll(Signals, signalsPane);
        controlContainer.getChildren().addAll(Control, controlPane, Monitoring, monitoringPane);
        statusVBox.getChildren().addAll(imView, Status, reset, statusContainer);

        mainContainer.getChildren().addAll(statusVBox, controlContainer);
        mainContainer.setPadding(new Insets(10));
        mainContainer.setSpacing(20);

        mainLabel = new Label("Details Faceplate :: " + dataModel.getName());
        mainLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        mainLabel.prefWidthProperty().bind(root.widthProperty());
        mainLabel.setFont(Font.font(12));
        mainLabel.setAlignment(Pos.CENTER);
        mainLabel.setPrefHeight(50);

        root.setTop(mainLabel);
        root.setCenter(mainContainer);

        setResizable(false);
//        setHeight(250);
//        setWidth(300);
        setScene(scene);
        initOwner(mainWindow);
        initModality(Modality.NONE);
        setTitle(dataModel.getName());
        
        scene.getStylesheets().add("/GUI/Styles/Faceplate.css");

    }
    private void flasherStartubg() {
        FlashingGenerator.getSystem().getFlasher().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        withFlasher(newValue);
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
        return imView;
    }
    protected void changeColorOfImageView(Color color) {
        Glow glow = new Glow(0.2);
        DropShadow shadow = new DropShadow(BlurType.GAUSSIAN, Color.GRAY, 1, 1, 1, 1);
        Light.Distant light = new Light.Distant(100, 100, color.brighter().brighter());
        Lighting lighting = new Lighting(light);
        Blend blend = new Blend(BlendMode.MULTIPLY, glow, shadow);
        Blend blend2 = new Blend(BlendMode.MULTIPLY, blend, lighting);

        imView.setEffect(blend2);

    }
    protected void changeStatus(String statusString, Color color) {
        Platform.runLater(() -> {
            if (statusString != null) {
                Status.setText(statusString);
            }
            Status.setBackground(new Background(new BackgroundFill(color.brighter(), CornerRadii.EMPTY, Insets.EMPTY)));
        });
    }
    protected void showFacePlate() {
        show();
    }
    
}
