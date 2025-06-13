package com.batch.GUI.BatchWindow.WindowComponents;

import com.batch.ApplicationContext;
import com.batch.DTO.BatchSystemDataDefinitions.BatchOrders;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStates;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStepModel;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Material;
import com.batch.Database.Entities.Phase;
import com.batch.GUI.BatchWindow.BatchesController;
import com.batch.Utilities.Round;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchStep extends VBox implements Runnable {
    private final Stage stepDetailsWindow = new Stage();
    private final BorderPane root = new BorderPane();
    private final VBox detailsContainer = new VBox();
    private final HBox bottomContainer = new HBox();
    private final Scene scene;
    private final DropShadow shadow;
    private final Effect defaultEffect;
    private final Map<String, Color> colors;
    private final BatchStepModel model;
    private final Map<String, PhaseParameterType> paraType;
    private final Map<String, Node> paraReference;
    private final Map<String, ProgressBar> paraProgReferences;
    private final BatchesController controller;
    private final Label materialName;
    private final Stage mainWindow;
    private final long batchID;
    private final int parallelStepNo;
    private final int stepNo;
    private Label phaseTypeLabel;
    private Label Name;
    private Color selectedColor;

    public BatchStep(long batchID, int parallelStepNo, int stepNo, BatchStepModel model, String phaseName, Stage window) {
        this.scene = new Scene(this.root);
        this.shadow = new DropShadow(5.0F, 5.0F, 5.0F, Color.GRAY);
        this.defaultEffect = this.getEffect();
        this.colors = new HashMap();
        this.paraType = new HashMap();
        this.paraReference = new HashMap();
        this.paraProgReferences = new HashMap();
        this.batchID = batchID;
        this.model = model;
        this.mainWindow = window;
        this.parallelStepNo = parallelStepNo;
        this.stepNo = stepNo;
        this.materialName = new Label();
        this.controller = ApplicationContext.applicationContext.getBean(BatchesController.class);
        List<Phase> list = this.controller.getAllPhases();
        list.add(new Phase(-1L, "Start", "", "Start", null));
        list.add(new Phase(-1L, "End", "", "End", null));
        list.stream().filter((phase) -> phase.getName().equals(phaseName)).findAny().ifPresent((type) -> {
            this.colors.put(PhasesTypes.Dose_phase.name().replace("_", " ").trim(), Color.GREEN);
            this.colors.put(PhasesTypes.Circulating_Phase.name().replace("_", " ").trim(), Color.OLIVE);
            this.colors.put(PhasesTypes.Washing_phase.name().replace("_", " ").trim(), Color.ORANGERED);
            this.colors.put(PhasesTypes.Transfere_phase.name().replace("_", " ").trim(), Color.DARKBLUE);
            this.colors.put(PhasesTypes.Mixing_phase.name().replace("_", " ").trim(), Color.DARKSALMON);
            this.colors.put("Start", Color.BLACK);
            this.colors.put("End", Color.BLACK);
            this.phaseTypeLabel = new Label(type.getPhaseType());
            this.Name = !type.getName().equals("Start") && !type.getName().equals("End") ? new Label(type.getName()) : new Label("");
            this.initialization(type.getPhaseType());
        });
    }

    private void initialization(String type) {
        this.phaseTypeLabel.prefWidthProperty().bind(this.widthProperty());
        this.phaseTypeLabel.setAlignment(Pos.CENTER);
        this.phaseTypeLabel.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:White;-fx-font-size:16;");
        this.Name.setStyle("-fx-font-weight:normal;-fx-font-style:normal;-fx-text-fill:Black;-fx-font-size:16;");
        this.selectedColor = this.colors.get(type);
        this.phaseTypeLabel.setBackground(new Background(new BackgroundFill(this.selectedColor, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setEffect(this.shadow);
        this.getChildren().addAll(this.phaseTypeLabel, this.Name, this.materialName);
        this.setPrefSize(400.0F, 75.0F);
        this.setSpacing(5.0F);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10.0F));
        this.setPadding(new Insets(10.0F));
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.0F))));
        this.createConfigurationWindow();
        this.setOnMouseClicked((action) -> {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2 && !this.phaseTypeLabel.getText().equals("Start") && !this.phaseTypeLabel.getText().equals("End")) {
                this.stepDetailsWindow.setX(action.getScreenX());
                this.stepDetailsWindow.setY(action.getScreenY());
                this.stepDetailsWindow.show();
            }

        });
        this.setOnMouseEntered((event) -> {
            this.setEffect(this.shadow);
            this.setCursor(Cursor.HAND);
        });
        this.setOnMouseExited((action) -> {
            this.setEffect(this.defaultEffect);
            this.setCursor(Cursor.DEFAULT);
        });
        this.setOnContextMenuRequested(this::contextMenuManagement);
    }

    private void contextMenuManagement(ContextMenuEvent action) {
        try {
            MenuItem start = new MenuItem("Start                                  ");
            MenuItem hold = new MenuItem("Hold");
            MenuItem resume = new MenuItem("Resume");
            MenuItem abort = new MenuItem("Abort");
            MenuItem close = new MenuItem("Close");
            MenuItem reset = new MenuItem("Forced reset [EMERGENCY ONLY]");
            MenuItem cancel = new MenuItem("Cancel");
            start.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            hold.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            resume.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            abort.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            close.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            reset.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            cancel.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            start.setDisable(true);
            hold.setDisable(true);
            resume.setDisable(true);
            abort.setDisable(true);
            close.setDisable(true);
            if (this.model.getState().equals(BatchStates.Aborted.name())) {
                start.setDisable(true);
                hold.setDisable(true);
                resume.setDisable(true);
                abort.setDisable(true);
                close.setDisable(false);
            } else if (this.model.getState().equals(BatchStates.Held.name())) {
                start.setDisable(true);
                hold.setDisable(true);
                resume.setDisable(false);
                abort.setDisable(true);
                close.setDisable(true);
            } else if (!this.model.getState().equals(BatchStates.Created.name()) && !this.model.getState().equals(BatchStates.Idle.name()) && !this.model.getState().equals(BatchStates.Finished.name()) && this.model.getState().equals(BatchStates.Running.name())) {
                start.setDisable(true);
                hold.setDisable(false);
                resume.setDisable(true);
                abort.setDisable(false);
                close.setDisable(true);
            }

            ContextMenu menu = new ContextMenu();
            menu.setStyle("-fx-background-color: WHITESMOKE;-fx-min-width:200;");
            menu.getItems().addAll(start, new SeparatorMenuItem(), hold, resume, new SeparatorMenuItem(), abort, close, new SeparatorMenuItem(), reset, new SeparatorMenuItem(), cancel);
            menu.show(this.getScene().getWindow(), action.getScreenX(), action.getScreenY());
            start.setOnAction((event) -> this.controller.onControlBatchStep(this.batchID, this.parallelStepNo, this.stepNo, BatchOrders.Start.name()));
            hold.setOnAction((event) -> this.controller.onControlBatchStep(this.batchID, this.parallelStepNo, this.stepNo, BatchOrders.Hold.name()));
            resume.setOnAction((event) -> this.controller.onControlBatchStep(this.batchID, this.parallelStepNo, this.stepNo, BatchOrders.Resume.name()));
            abort.setOnAction((event) -> this.controller.onControlBatchStep(this.batchID, this.parallelStepNo, this.stepNo, BatchOrders.Abort.name()));
            close.setOnAction((event) -> this.controller.onControlBatchStep(this.batchID, this.parallelStepNo, this.stepNo, BatchOrders.Finish.name()));
            reset.setOnAction((event) -> this.controller.onControlBatchStep(this.batchID, this.parallelStepNo, this.stepNo, BatchOrders.Create.name()));
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorWindowForException(e.getMessage(), e);
        }

    }

    private void createConfigurationWindow() {
        this.root.setCenter(this.detailsContainer);
        this.root.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        this.root.setPadding(new Insets(5.0F));
        this.root.setBottom(this.bottomContainer);
        this.root.setPrefWidth(500.0F);
        this.detailsContainer.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        this.detailsContainer.setPadding(new Insets(10.0F));
        this.fillDetailedContainerWithFields(this.detailsContainer, this.model);
        this.bottomContainer.setSpacing(10.0F);
        this.bottomContainer.setPadding(new Insets(5.0F));
        this.bottomContainer.setAlignment(Pos.CENTER);
        this.stepDetailsWindow.initModality(Modality.NONE);
        this.stepDetailsWindow.initOwner(this.mainWindow);
        this.stepDetailsWindow.setAlwaysOnTop(true);
        this.stepDetailsWindow.initStyle(StageStyle.UTILITY);
        this.stepDetailsWindow.setScene(this.scene);
        this.stepDetailsWindow.setTitle(this.Name.getText());
        this.stepDetailsWindow.setResizable(false);
    }

    private void fillDetailedContainerWithFields(VBox detailsContainer, BatchStepModel model) {
        model.getParametersType().forEach((parameter) -> {
            String name = parameter.getName();
            String parameterType = parameter.getType();
            boolean ceckData = model.getCheckParametersData().get(name);
            boolean ceckActualData = model.getActualCheckParametersData().get(name);
            double valueData = model.getValueParametersData().get(name);
            double valueActualData = model.getActualvalueParametersData().get(name);
            HBox hbox = new HBox();
            hbox.setSpacing(5.0F);
            hbox.setPadding(new Insets(1.0F));
            if (parameterType.equals(PhaseParameterType.Check.name())) {
                CheckBox node = new CheckBox() {
                    public void arm() {
                    }
                };
                hbox.getChildren().add(node);
                this.paraType.put(name, PhaseParameterType.Check);
                this.paraReference.put(name, node);
            } else if (parameterType.equals(PhaseParameterType.Value.name())) {
                Label label = new Label(name);
                TextField nodeA = new TextField(String.valueOf(valueData));
                TextField node = new TextField(String.valueOf(valueActualData));
                label.setPrefSize(100.0F, 25.0F);
                ProgressBar progressBar = new ProgressBar(0.0F);
                progressBar.setPrefSize(150.0F, 25.0F);
                ProgressIndicator ind = new ProgressIndicator();
                ind.setPrefWidth(35.0F);
                ind.progressProperty().bind(progressBar.progressProperty());
                node.setPrefSize(80.0F, 25.0F);
                nodeA.setPrefSize(80.0F, 25.0F);
                node.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
                nodeA.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
                node.setEditable(false);
                nodeA.setEditable(false);
                hbox.getChildren().addAll(label, node, nodeA, progressBar, ind);
                this.paraType.put(name, PhaseParameterType.Value);
                this.paraReference.put(name, node);
                this.paraProgReferences.put(name, progressBar);
            }

            detailsContainer.getChildren().add(hbox);
        });
    }

    public void run() {
        Platform.runLater(() -> {
            try {
                if (this.model.getMaterialID() != 0L) {
                    this.materialName.setText(this.controller.getMaterialByName(this.model.getMaterialID()).map(Material::getName).orElse("Unknown"));
                }

                if (!this.Name.getText().equals("")) {
                    if (this.model.getState().equals(BatchStates.Created.name())) {
                        this.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (this.model.getState().equals(BatchStates.Idle.name())) {
                        this.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (this.model.getState().equals(BatchStates.Running.name())) {
                        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (this.model.getState().equals(BatchStates.Held.name())) {
                        this.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (this.model.getState().equals(BatchStates.Aborted.name())) {
                        this.setBackground(new Background(new BackgroundFill(Color.VIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (this.model.getState().equals(BatchStates.Finished.name())) {
                        this.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    }

                    this.paraType.forEach((name, type) -> {
                        Node node = this.paraReference.get(name);
                        ProgressBar bar = this.paraProgReferences.get(name);
                        switch (type) {
                            case Check:
                                CheckBox checkBox = (CheckBox) node;
                                break;
                            case Value:
                                double valuableData = this.model.getValueParametersData().get(name);
                                double valueActualData = this.model.getActualvalueParametersData().get(name);
                                TextField textField = (TextField) node;
                                textField.setText(String.valueOf(Round.RoundDouble(valueActualData, 4)));
                                double progress = valueActualData / valuableData;
                                bar.setProgress(progress);
                        }

                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void showErrorWindowForException(String header, Throwable e) {
        Platform.runLater(() -> {
            ExceptionDialog exceptionDialog = new ExceptionDialog(e);
            exceptionDialog.setHeaderText(header);
            exceptionDialog.getDialogPane().setMaxWidth(500.0F);
            exceptionDialog.initOwner(this.mainWindow);
            exceptionDialog.initModality(Modality.WINDOW_MODAL);
            exceptionDialog.initStyle(StageStyle.UTILITY);
            exceptionDialog.show();
        });
    }
}
