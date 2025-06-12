

package com.batch.GUI.BatchWindow.WindowComponents;

import com.batch.ApplicationContext;
import com.batch.DTO.BatchSystemDataDefinitions.BatchModel;
import com.batch.DTO.BatchSystemDataDefinitions.BatchOrders;
import com.batch.DTO.BatchSystemDataDefinitions.BatchParallelStepsModel;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStates;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStepModel;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Recipe;
import com.batch.Database.Entities.TreeViewItemsData;
import com.batch.GUI.BatchWindow.BatchesController;
import com.batch.GUI.BatchWindow.BatchesModel;
import com.batch.GUI.RecipeEditor.WindowComponents.RecipeTreeItem;
import com.batch.GUI.RecipeEditor.WindowComponents.TreeItemType;
import com.batch.Utilities.RestrictiveTextField;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.dialog.ExceptionDialog;

public class BatchCreator extends Stage {
    private static volatile BatchCreator singleton = null;
    private BorderPane rootPane = new BorderPane();
    private GridPane parametersPane = new GridPane();
    private Scene scene;
    private Stage mainWindow;
    private TreeView<String> treeView;
    private RecipeTreeItem root;
    private ToolBar toolBar;
    private ToolBar statusBar;
    private Button CreateBatch;
    private Recipe selectedRecipe;
    private Batch createdBatch;
    private final RestrictiveTextField selectedRecipeName;
    private final RestrictiveTextField selectedRecipeID;
    private final RestrictiveTextField batchID;
    private final RestrictiveTextField batchQuantity;
    private final RestrictiveTextField batchComment;
    private final RestrictiveTextField clientName;
    private final RestrictiveTextField productName;
    private final Label selectedRecipeNameLabel;
    private final Label selectedRecipeIDLabel;
    private final Label batchIDLabel;
    private final Label batchQuantityLabel;
    private final Label batchCommentLabel;
    private final Label clientNameLabel;
    private final Label productNameLabel;
    private final BatchesController controller;
    private final BatchesModel model;
    private double quantity;
    private final ObjectProperty<Cursor> CURSOR_DEFAULT;
    private final ObjectProperty<Cursor> CURSOR_WAIT;

    public BatchCreator(Stage stage) {
        this.scene = new Scene(this.rootPane);
        this.treeView = new TreeView();
        this.root = new RecipeTreeItem("System", TreeItemType.Folder);
        this.toolBar = new ToolBar();
        this.statusBar = new ToolBar();
        this.CreateBatch = new Button("Create new batch ");
        this.selectedRecipeName = new RestrictiveTextField();
        this.selectedRecipeID = new RestrictiveTextField();
        this.batchID = new RestrictiveTextField();
        this.batchQuantity = new RestrictiveTextField();
        this.batchComment = new RestrictiveTextField();
        this.clientName = new RestrictiveTextField();
        this.productName = new RestrictiveTextField();
        this.selectedRecipeNameLabel = new Label("Selected recipe name ");
        this.selectedRecipeIDLabel = new Label("Selected recipe ID ");
        this.batchIDLabel = new Label("Batch name");
        this.batchQuantityLabel = new Label("Batch Quantity (Kg)");
        this.batchCommentLabel = new Label("Batch comment");
        this.clientNameLabel = new Label("Client name");
        this.productNameLabel = new Label("Product name");
        this.quantity = (double)0.0F;
        this.CURSOR_DEFAULT = new SimpleObjectProperty(Cursor.DEFAULT);
        this.CURSOR_WAIT = new SimpleObjectProperty(Cursor.WAIT);
        this.mainWindow = stage;
        this.controller = (BatchesController)ApplicationContext.applicationContext.getBean(BatchesController.class);
        this.model = this.controller.getModel();
        this.graphicsBuilder();
        this.actionHandler();
        this.fillTreeFromDB();
    }

    public static BatchCreator getWindow(Stage stage) {
        synchronized(BatchCreator.class) {
            if (singleton == null) {
                singleton = new BatchCreator(stage);
            }
        }

        return singleton;
    }

    private void graphicsBuilder() {
        this.selectedRecipeID.setPrefWidth((double)150.0F);
        this.selectedRecipeName.setPrefWidth((double)300.0F);
        this.batchID.setPrefWidth((double)150.0F);
        this.batchQuantity.setPrefWidth((double)300.0F);
        this.batchQuantity.setPromptText("0.0");
        this.batchQuantity.setRestrict("[0-9].");
        this.batchQuantity.setMaxLength(10);
        this.clientName.setPrefWidth((double)610.0F);
        this.batchComment.setPrefWidth((double)610.0F);
        this.productName.setPrefWidth((double)610.0F);
        this.selectedRecipeIDLabel.setPrefWidth((double)150.0F);
        this.selectedRecipeNameLabel.setPrefWidth((double)150.0F);
        this.batchIDLabel.setPrefWidth((double)150.0F);
        this.batchQuantityLabel.setPrefWidth((double)150.0F);
        this.batchCommentLabel.setPrefWidth((double)150.0F);
        this.clientNameLabel.setPrefWidth((double)150.0F);
        this.productNameLabel.setPrefWidth((double)150.0F);
        this.selectedRecipeID.setEditable(false);
        this.selectedRecipeName.setEditable(false);
        this.CreateBatch.setPrefWidth((double)150.0F);
        this.treeView.setMaxWidth((double)600.0F);
        this.treeView.setRoot(this.root);
        this.treeView.setPadding(new Insets((double)0.0F, (double)0.0F, (double)20.0F, (double)0.0F));
        this.toolBar.getItems().addAll(new Node[]{this.CreateBatch});
        this.statusBar.getItems().addAll(new Node[]{new Label("Batch interfece status")});
        this.rootPane.setTop(this.toolBar);
        this.rootPane.setBottom(this.statusBar);
        this.rootPane.setCenter(this.parametersPane);
        this.rootPane.setLeft(this.treeView);
        this.parametersPane.setVgap((double)5.0F);
        this.parametersPane.setHgap((double)5.0F);
        this.parametersPane.setPadding(new Insets((double)30.0F));
        this.parametersPane.add(this.selectedRecipeIDLabel, 1, 1);
        this.parametersPane.add(this.selectedRecipeID, 2, 1);
        this.parametersPane.add(this.selectedRecipeNameLabel, 3, 1);
        this.parametersPane.add(this.selectedRecipeName, 4, 1);
        this.parametersPane.add(this.batchIDLabel, 1, 3);
        this.parametersPane.add(this.batchID, 2, 3);
        this.parametersPane.add(this.batchQuantityLabel, 3, 3);
        this.parametersPane.add(this.batchQuantity, 4, 3);
        this.parametersPane.add(this.clientNameLabel, 1, 4);
        this.parametersPane.add(this.clientName, 2, 4, 3, 1);
        this.parametersPane.add(this.productNameLabel, 1, 5);
        this.parametersPane.add(this.productName, 2, 5, 3, 1);
        this.parametersPane.add(this.batchCommentLabel, 1, 6);
        this.parametersPane.add(this.batchComment, 2, 6, 3, 1);
        this.setTitle("Batch Creator");
        this.setScene(this.scene);
        this.initOwner(this.mainWindow);
        this.initModality(Modality.WINDOW_MODAL);
        this.initStyle(StageStyle.UTILITY);
    }

    private void actionHandler() {
        this.CreateBatch.setOnMouseClicked(this::onCreateNewBatch);
        this.treeView.setOnMouseClicked(this::onLoadRecipeAtClick);
    }

    private synchronized void onCreateNewBatch(MouseEvent action) {
        try {
            if (!StringUtils.isBlank(this.batchID.getText())) {
                if (!StringUtils.isBlank(this.productName.getText())) {
                    if (!StringUtils.isBlank(this.clientName.getText())) {
                        this.controller.findBatchByName(this.batchID.getText()).ifPresentOrElse((batch) -> this.showErrorWindow("Error", "Batch name already exist, try another name "), () -> {
                            if (!StringUtils.isBlank(this.batchQuantity.getText()) && !StringUtils.isBlank(this.selectedRecipeID.getText())) {
                                this.quantity = Double.parseDouble(this.batchQuantity.getText());
                                this.controller.getRecipeConfig().ifPresentOrElse((recipeConfig) -> {
                                    if (!(this.quantity > recipeConfig.getMaxBatchSize()) && this.quantity != (double)0.0F) {
                                        try {
                                            this.controller.parseRecipeToDetailsString(this.selectedRecipe).flatMap((details) -> this.showInfoWindow("Recipe details", details)).ifPresent((buttonType) -> {
                                                if (buttonType.equals(ButtonType.OK)) {
                                                    BatchModel batchModel = new BatchModel();
                                                    Batch createdBatchNew = new Batch(this.batchID.getText(), this.selectedRecipe.getUnitName(), BatchStates.Idle.name(), BatchOrders.Create.name(), this.batchComment.getText(), batchModel);
                                                    List<BatchParallelStepsModel> listOfBatchParallelStepModel = new LinkedList();
                                                    batchModel.setParallelSteps(listOfBatchParallelStepModel);
                                                    this.selectedRecipe.getModel().getParallelSteps().forEach((recipeParallelStep) -> listOfBatchParallelStepModel.add(new BatchParallelStepsModel(recipeParallelStep.getSteps().stream().map((item) -> {
                                                        if (item.getPhaseType() != null && item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())) {
                                                            double percentage = (Double)item.getValueParametersData().get("Percentage %");
                                                            double totalQty = this.quantity * percentage / (double)100.0F;
                                                            item.getValueParametersData().replace("Percentage %", totalQty);
                                                        }

                                                        return new BatchStepModel(item);
                                                    }).collect(Collectors.toList()))));
                                                    createdBatchNew.setState(BatchStates.Created.name());
                                                    createdBatchNew.setClient(this.clientName.getText());
                                                    this.controller.createNewBatch(createdBatchNew).ifPresentOrElse((savedBatch) -> {
                                                        this.createdBatch = savedBatch;
                                                        this.hide();
                                                    }, () -> this.showErrorWindow("Error", "Could not saved"));
                                                } else {
                                                    this.showErrorWindow("Warning", "Canceling creating batch");
                                                }

                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            this.showErrorWindowForException(e.getMessage(), e);
                                        }
                                    } else {
                                        this.showErrorWindow("Error creating batch", "Total batch quantity is greater than the maximum allowed value 15000 or equal to zero \nThe Entered quantity equals to " + this.quantity);
                                    }

                                }, () -> this.showErrorWindow("Error", "Recipe configurations not found "));
                            } else {
                                this.showErrorWindow("Error", "Zero quantity entered or internal error\nCreate batch again ... ");
                            }

                        });
                    } else {
                        this.showErrorWindow("Error", "Please enter client name");
                    }
                } else {
                    this.showErrorWindow("Error", "Please enter product name");
                }
            } else {
                this.showErrorWindow("Error", "Please add Batch name");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorWindowForException(e.getMessage(), e);
        }

    }

    private void onLoadRecipeAtClick(MouseEvent action) {
        try {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2) {
                RecipeTreeItem parent = (RecipeTreeItem)this.treeView.getSelectionModel().getSelectedItem();
                if (parent != null && parent.isLeaf() && parent.getItemType().equals(TreeItemType.Recipe)) {
                    Recipe recipe = parent.getRecipe();
                    this.selectedRecipe = recipe;
                    this.selectedRecipeID.setText(String.valueOf(recipe.getId()));
                    this.selectedRecipeName.setText(recipe.getRecipeName());
                }
            }
        } catch (Exception var4) {
            this.showErrorWindow("Error loading recipe", "Please loaded recipe again or error in database connection, so restart machine");
        }

    }

    private void fillTreeFromDB() {
        ReadOnlyBooleanProperty readOnlyBooleanProperty = this.controller.getRecipesMapToFillTreeView((a) -> {
            this.root.setExpanded(true);
            this.root.getChildren().clear();
            this.FillTreeItemRecursiveAction(0L, this.root, a);
        });
        this.rootPane.cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
    }

    private void FillTreeItemRecursiveAction(long parentID, RecipeTreeItem parent, Map<Long, List<TreeViewItemsData>> groupedItemsByParentID) {
        if (groupedItemsByParentID.get(parentID) != null) {
            (groupedItemsByParentID.get(parentID)).forEach((element) -> {
                if (element.getItemType().equals(TreeItemType.Folder.name())) {
                    RecipeTreeItem item = new RecipeTreeItem(element.getName(), TreeItemType.Folder);
                    item.setExpanded(true);
                    item.setItemID(element.getId());
                    parent.getChildren().add(item);
                    this.FillTreeItemRecursiveAction(element.getId(), item, groupedItemsByParentID);
                } else if (element.getItemType().equals(TreeItemType.Recipe.name())) {
                    this.controller.getRecipeById(element.getRecipeID()).ifPresentOrElse((recipe) -> {
                        RecipeTreeItem item = new RecipeTreeItem(element.getName(), TreeItemType.Recipe);
                        item.setExpanded(true);
                        item.setItemID(element.getId());
                        item.setRecipe(recipe);
                        parent.getChildren().add(item);
                        this.FillTreeItemRecursiveAction(element.getId(), item, groupedItemsByParentID);
                    }, () -> {
                    });
                }

            });
        }

    }

    public Optional<Batch> showAndReturnBatch() {
        this.initialize();
        this.showAndWait();
        return Optional.ofNullable(this.createdBatch);
    }

    private void initialize() {
        this.selectedRecipeName.setText("");
        this.selectedRecipeID.setText("");
        this.batchID.setText("");
        this.batchQuantity.setText("0.0");
        this.batchComment.setText("");
        this.clientName.setText("");
        this.productName.setText("");
        this.createdBatch = null;
        this.fillTreeFromDB();
    }

    private Optional<ButtonType> showInfoWindow(String header, String content) {
        Long var10000 = this.selectedRecipe.getId();
        String details = "Recipe id   = " + var10000 + "\nRecipe name   = " + this.selectedRecipe.getRecipeName() + "\n\n\n\n" + content;
        AtomicReference<Optional<ButtonType>> result = new AtomicReference(Optional.of(ButtonType.CANCEL));
        Stage stage = new Stage();
        TextArea textArea = new TextArea(details);
        textArea.setEditable(false);
        textArea.setStyle("-fx-font-family: monospace");
        Label label = new Label(header);
        Button ok = new Button("OK.");
        Button cancel = new Button("Cancel");
        ok.setOnMouseClicked((action) -> {
            result.set(Optional.of(ButtonType.OK));
            stage.hide();
        });
        cancel.setOnMouseClicked((action) -> {
            result.set(Optional.of(ButtonType.CANCEL));
            stage.hide();
        });
        ok.setPrefWidth((double)100.0F);
        cancel.setPrefWidth((double)100.0F);
        HBox hBox = new HBox(new Node[]{ok, cancel});
        hBox.setSpacing((double)5.0F);
        hBox.setPadding(new Insets((double)5.0F));
        VBox vBox = new VBox(new Node[]{label, textArea, hBox});
        vBox.setPadding(new Insets((double)5.0F));
        vBox.setMinWidth((double)1300.0F);
        vBox.setMinHeight((double)500.0F);
        textArea.prefHeightProperty().bind(vBox.heightProperty().subtract(80));
        stage.setScene(new Scene(vBox));
        stage.setTitle("Please confirm ");
        stage.initOwner(this);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
        return (Optional)result.get();
    }

    private void showErrorWindow(String header, String content) {
        Alert Error = new Alert(AlertType.ERROR);
        Error.setTitle("Error ");
        Error.setHeaderText(header);
        Error.setContentText(content);
        Error.initOwner(this);
        Error.initStyle(StageStyle.UTILITY);
        Error.showAndWait();
    }

    private void showErrorWindowForException(String header, Throwable e) {
        Platform.runLater(() -> {
            ExceptionDialog exceptionDialog = new ExceptionDialog(e);
            exceptionDialog.setHeaderText(header);
            exceptionDialog.getDialogPane().setMaxWidth((double)500.0F);
            exceptionDialog.initOwner(this.mainWindow);
            exceptionDialog.initModality(Modality.WINDOW_MODAL);
            exceptionDialog.initStyle(StageStyle.UTILITY);
            exceptionDialog.show();
        });
    }
}
