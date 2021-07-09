package com.batch.GUI.BatchWindow.WindowComponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.batch.ApplicationContext;
import com.batch.DTO.BatchSystemDataDefinitions.BatchOrders;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStates;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStepModel;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Material;
import com.batch.Database.Entities.Phase;
import com.batch.GUI.BatchWindow.BatchesController;
import com.batch.GUI.InitialWindow.InitialWindow;
import com.batch.Utilities.Round;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.ExceptionDialog;

public class BatchStep extends VBox implements Runnable {

    private Label phaseTypeLabel;
    private Label Name;
    private Label materialName;

    private Stage mainWindow;
    private final Stage stepDetailsWindow = new Stage();

    private final BorderPane root = new BorderPane();
    private final VBox detailsContainer = new VBox();
    private final HBox bottomContainer = new HBox();
    private final Scene scene = new Scene(root);

    private final DropShadow shadow = new DropShadow(5, 5, 5, Color.GRAY);
    private final Effect defaultEffect = getEffect();

    private final Map<String, Color> colors = new HashMap<>();
    private Color selectedColor;
    private final BatchStepModel model;

    private long batchID;
    private int parallelStepNo;
    private int stepNo;

    private final Map<String, PhaseParameterType> paraType = new HashMap<>();
    private final Map<String, Node> paraReference = new HashMap<>();
    private final Map<String, ProgressBar> paraProgReferences = new HashMap<>();

    private final BatchesController controller;

    public BatchStep(long batchID, int parallelStepNo, int stepNo, BatchStepModel model, String phaseName, Stage window) {
        this.batchID = batchID;
        this.model = model;
        this.mainWindow = window;
        this.parallelStepNo = parallelStepNo;
        this.stepNo = stepNo;
        this.materialName = new Label();
        this.controller = ApplicationContext.applicationContext.getBean(BatchesController.class);
        List<Phase> list = controller.getAllPhases();
        list.add(new Phase(-1L, "Start", "", "Start", null));
        list.add(new Phase(-1L, "End", "", "End", null));
        list.stream()
                .filter(phase -> phase.getName().equals(phaseName))
                .findAny()
                .ifPresent(type -> {
                    colors.put(PhasesTypes.Dose_phase.name().replace("_", " ").trim(), Color.GREEN);
                    colors.put(PhasesTypes.Circulating_Phase.name().replace("_", " ").trim(), Color.OLIVE);
                    colors.put(PhasesTypes.Washing_phase.name().replace("_", " ").trim(), Color.ORANGERED);
                    colors.put(PhasesTypes.Transfere_phase.name().replace("_", " ").trim(), Color.DARKBLUE);
                    colors.put(PhasesTypes.Mixing_phase.name().replace("_", " ").trim(), Color.DARKSALMON);
                    colors.put("Start", Color.BLACK);
                    colors.put("End", Color.BLACK);
                    phaseTypeLabel = new Label(type.getPhaseType());
                    Name = (type.getName().equals("Start") || type.getName().equals("End")) ? new Label("") : new Label(type.getName());
                    initialization(type.getPhaseType());
                });

    }

    private void initialization(String type) {
        phaseTypeLabel.prefWidthProperty().bind(widthProperty());
        phaseTypeLabel.setAlignment(Pos.CENTER);

        phaseTypeLabel.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:White;-fx-font-size:16;");
        Name.setStyle("-fx-font-weight:normal;-fx-font-style:normal;-fx-text-fill:Black;-fx-font-size:16;");

        selectedColor = colors.get(type);
        phaseTypeLabel.setBackground(new Background(new BackgroundFill(selectedColor, CornerRadii.EMPTY, Insets.EMPTY)));
        setEffect(shadow);
        getChildren().addAll(phaseTypeLabel, Name, materialName);
        setPrefSize(400, 75);
        setSpacing(5);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        setPadding(new Insets(10));
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        createConfigurationWindow();
        setOnMouseClicked(action -> {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2) {
                if (!phaseTypeLabel.getText().equals("Start") && !phaseTypeLabel.getText().equals("End")) {
                    stepDetailsWindow.setX(action.getScreenX());
                    stepDetailsWindow.setY(action.getScreenY());
                    stepDetailsWindow.show();
                }
            }
        });
        setOnMouseEntered(event -> {
            setEffect(shadow);
            setCursor(Cursor.HAND);
        });
        setOnMouseExited(action -> {
            setEffect(defaultEffect);
            setCursor(Cursor.DEFAULT);
        });

        setOnContextMenuRequested(this::contextMenuManagement);
    }

    private void contextMenuManagement(ContextMenuEvent action) {
        try {
            MenuItem start = new MenuItem("Start                                  ");
            MenuItem hold = new MenuItem("Hold");
            MenuItem resume = new MenuItem("Resume");
            MenuItem abort = new MenuItem("Abort");
            MenuItem close = new MenuItem("Close");
            MenuItem cancel = new MenuItem("Cancel");

            start.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            hold.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            resume.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            abort.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            close.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");
            cancel.setStyle("-fx-font-size:13;-fx-font-family:tahoma;-fx-padding: 2;");

            start.setDisable(true);
            hold.setDisable(true);
            resume.setDisable(true);
            abort.setDisable(true);
            close.setDisable(true);

            if (model.getState().equals(BatchStates.Aborted.name())) {
                start.setDisable(true);
                hold.setDisable(true);
                resume.setDisable(true);
                abort.setDisable(true);
                close.setDisable(false);
            } else if (model.getState().equals(BatchStates.Held.name())) {
                start.setDisable(true);
                hold.setDisable(true);
                resume.setDisable(false);
                abort.setDisable(true);
                close.setDisable(true);
            } else if (model.getState().equals(BatchStates.Created.name())) {
            } else if (model.getState().equals(BatchStates.Idle.name())) {
            } else if (model.getState().equals(BatchStates.Finished.name())) {
            } else if (model.getState().equals(BatchStates.Running.name())) {
                start.setDisable(true);
                hold.setDisable(false);
                resume.setDisable(true);
                abort.setDisable(false);
                close.setDisable(true);
            }

            ContextMenu menu = new ContextMenu();
            menu.setStyle("-fx-background-color: WHITESMOKE;-fx-min-width:200;");
            menu.getItems().addAll(start, new SeparatorMenuItem(), hold, resume, new SeparatorMenuItem(), abort, close, new SeparatorMenuItem(), cancel);
            menu.show(getScene().getWindow(), action.getScreenX(), action.getScreenY());

            start.setOnAction(event -> controller.onControlBatchStep(batchID, parallelStepNo, stepNo, BatchOrders.Start.name()));
            hold.setOnAction(event -> controller.onControlBatchStep(batchID, parallelStepNo, stepNo, BatchOrders.Hold.name()));
            resume.setOnAction(event -> controller.onControlBatchStep(batchID, parallelStepNo, stepNo, BatchOrders.Resume.name()));
            abort.setOnAction(event -> controller.onControlBatchStep(batchID, parallelStepNo, stepNo, BatchOrders.Abort.name()));
            close.setOnAction(event -> controller.onControlBatchStep(batchID, parallelStepNo, stepNo, BatchOrders.Close.name()));
        }catch (Exception e){
            e.printStackTrace();
            showErrorWindowForException(e.getMessage(), e);
        }
    }

    private void createConfigurationWindow() {
        root.setCenter(detailsContainer);
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        root.setPadding(new Insets(5));
        root.setBottom(bottomContainer);
        root.setPrefWidth(500);

        detailsContainer.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        detailsContainer.setPadding(new Insets(10));

        fillDetailedContainerWithFields(detailsContainer, model);

        bottomContainer.setSpacing(10);
        bottomContainer.setPadding(new Insets(5));
        bottomContainer.setAlignment(Pos.CENTER);

        stepDetailsWindow.initModality(Modality.NONE);
        stepDetailsWindow.initOwner(mainWindow);
        stepDetailsWindow.setAlwaysOnTop(true);
        stepDetailsWindow.initStyle(StageStyle.UTILITY);
        stepDetailsWindow.setScene(scene);
        stepDetailsWindow.setTitle(Name.getText());
        stepDetailsWindow.setResizable(false);

    }

    private void fillDetailedContainerWithFields(VBox detailsContainer, BatchStepModel model) {
        model.getParametersType().forEach(parameter -> {
            String name = parameter.getName();
            String parameterType = parameter.getType();

            boolean ceckData = model.getCheckParametersData().get(name);
            boolean ceckActualData = model.getActualCheckParametersData().get(name);

            double valueData = model.getValueParametersData().get(name);
            double valueActualData = model.getActualvalueParametersData().get(name);

            HBox hbox = new HBox();
            hbox.setSpacing(5);
            hbox.setPadding(new Insets(1));
//            hbox.setAlignment(Pos.BASELINE_LEFT);

            if (parameterType.equals(PhaseParameterType.Check.name())) {
                CheckBox node = new CheckBox() {
                    @Override
                    public void arm() {
                    }
                };
                hbox.getChildren().add(node);

                paraType.put(name, PhaseParameterType.Check);
                paraReference.put(name, node);

            } else if (parameterType.equals(PhaseParameterType.Value.name())) {

                Label label = new Label(name);
                TextField nodeA = new TextField(String.valueOf(valueData));
                TextField node = new TextField(String.valueOf(valueActualData));

                label.setPrefSize(100, 25);

                ProgressBar progressBar = new ProgressBar(0.0);
                progressBar.setPrefSize(150, 25);

                ProgressIndicator ind = new ProgressIndicator();
                ind.setPrefWidth(35);
                ind.progressProperty().bind(progressBar.progressProperty());

                node.setPrefSize(80, 25);
                nodeA.setPrefSize(80, 25);

                node.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
                nodeA.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));

                node.setEditable(false);
                nodeA.setEditable(false);

                hbox.getChildren().addAll(label, node, nodeA, progressBar, ind);

                paraType.put(name, PhaseParameterType.Value);
                paraReference.put(name, node);
                paraProgReferences.put(name, progressBar);

            }
            detailsContainer.getChildren().add(hbox);
        });
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            try {
                if (model.getMaterialID() != 0) {
                    materialName.setText(controller.getMaterialByName(model.getMaterialID()).map(Material::getName).orElse("Unknown"));
                }
                if (!Name.getText().equals("")) {
                    if (model.getState().equals(BatchStates.Created.name())) {
                        setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (model.getState().equals(BatchStates.Idle.name())) {
                        setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (model.getState().equals(BatchStates.Running.name())) {
                        setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (model.getState().equals(BatchStates.Held.name())) {
                        setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (model.getState().equals(BatchStates.Aborted.name())) {
                        setBackground(new Background(new BackgroundFill(Color.VIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                    } else if (model.getState().equals(BatchStates.Finished.name())) {
                        setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                    paraType.forEach((name, type) -> {
                        Node node = paraReference.get(name);
                        ProgressBar bar = paraProgReferences.get(name);
                        switch (type) {
                            case Check:
                                CheckBox checkBox = ((CheckBox) node);

                                break;
                            case Value:
                                double valuableData = model.getValueParametersData().get(name);
                                double valueActualData = model.getActualvalueParametersData().get(name);
                                TextField textField = ((TextField) node);
                                textField.setText(String.valueOf(Round.RoundDouble(valueActualData, 4)));
                                double progress = valueActualData / valuableData;
                                bar.setProgress(progress);
                                break;
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
            exceptionDialog.getDialogPane().setMaxWidth(500);
            exceptionDialog.initOwner(mainWindow);
            exceptionDialog.initModality(Modality.WINDOW_MODAL);
            exceptionDialog.initStyle(StageStyle.UTILITY);
            exceptionDialog.show();
        });
    }
}
