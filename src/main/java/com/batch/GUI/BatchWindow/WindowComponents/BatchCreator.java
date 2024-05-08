package com.batch.GUI.BatchWindow.WindowComponents;


import com.batch.ApplicationContext;
import com.batch.DTO.BatchSystemDataDefinitions.*;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Recipe;
import com.batch.Database.Entities.TreeViewItemsData;
import com.batch.GUI.BatchWindow.BatchesController;
import com.batch.GUI.BatchWindow.BatchesModel;
import com.batch.GUI.RecipeEditor.WindowComponents.RecipeTreeItem;
import com.batch.GUI.RecipeEditor.WindowComponents.TreeItemType;
import com.batch.Utilities.RestrictiveTextField;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;
import java.util.stream.Collectors;

public class BatchCreator extends Stage {

    private static volatile BatchCreator singleton = null;
    private BorderPane rootPane = new BorderPane();
    private GridPane parametersPane = new GridPane();

    private Scene scene = new Scene(rootPane);
    private Stage mainWindow;

    private TreeView<String> treeView = new TreeView<>();
    private RecipeTreeItem root = new RecipeTreeItem("System", TreeItemType.Folder);
    private ToolBar toolBar = new ToolBar();
    private ToolBar statusBar = new ToolBar();

    private Button CreateBatch = new Button("Create new batch ");

    private Recipe selectedRecipe;
    private Batch createdBatch;

    private final RestrictiveTextField selectedRecipeName = new RestrictiveTextField();
    private final RestrictiveTextField selectedRecipeID = new RestrictiveTextField();
    private final RestrictiveTextField batchID = new RestrictiveTextField();
    private final RestrictiveTextField batchQuantity = new RestrictiveTextField();
    private final RestrictiveTextField batchComment = new RestrictiveTextField();

    private final Label selectedRecipeNameLabel = new Label("Selected recipe name ");
    private final Label selectedRecipeIDLabel = new Label("Selected recipe ID ");
    private final Label batchIDLabel = new Label("Batch name");
    private final Label batchQuantityLabel = new Label("Batch Quantity (Kg)");
    private final Label batchCommentLabel = new Label("Batch comment");

    private final BatchesController controller;
    private final BatchesModel model;

    private double quantity = 0.0;

    public BatchCreator(Stage stage) {
        this.mainWindow = stage;
        this.controller = ApplicationContext.applicationContext.getBean(BatchesController.class);
        this.model = controller.getModel();
        graphicsBuilder();
        actionHandler();
        FillTreeFromDB();
    }
    public static BatchCreator getWindow(Stage stage) {
        synchronized (BatchCreator.class) {
            if (singleton == null) {
                singleton = new BatchCreator(stage);
            }
        }
        return singleton;
    }

    private void graphicsBuilder() {
        
        selectedRecipeID.setPrefWidth(150);
        selectedRecipeName.setPrefWidth(300);
        batchID.setPrefWidth(150);

        batchQuantity.setPrefWidth(300);
        batchQuantity.setPromptText("0.0");
        batchQuantity.setRestrict("[0-9].");
        batchQuantity.setMaxLength(10);

        batchComment.setPrefWidth(610);

        selectedRecipeIDLabel.setPrefWidth(150);
        selectedRecipeNameLabel.setPrefWidth(150);
        batchIDLabel.setPrefWidth(150);
        batchQuantityLabel.setPrefWidth(150);
        batchCommentLabel.setPrefWidth(150);

        selectedRecipeID.setEditable(false);
        selectedRecipeName.setEditable(false);

        CreateBatch.setPrefWidth(150);

        treeView.setMaxWidth(600);
        treeView.setRoot(root);
        treeView.setPadding(new Insets(0, 0, 20, 0));

        toolBar.getItems().addAll(CreateBatch);

        statusBar.getItems().addAll(new Label("Batch interfece status"));

        rootPane.setTop(toolBar);
        rootPane.setBottom(statusBar);
        rootPane.setCenter(parametersPane);
        rootPane.setLeft(treeView);

        parametersPane.setVgap(5);
        parametersPane.setHgap(5);
        parametersPane.setPadding(new Insets(30));
        parametersPane.add(selectedRecipeIDLabel, 1, 1);
        parametersPane.add(selectedRecipeID, 2, 1);
        parametersPane.add(selectedRecipeNameLabel, 3, 1);
        parametersPane.add(selectedRecipeName, 4, 1);
        parametersPane.add(batchIDLabel, 1, 3);
        parametersPane.add(batchID, 2, 3);
        parametersPane.add(batchQuantityLabel, 3, 3);
        parametersPane.add(batchQuantity, 4, 3);
        parametersPane.add(batchCommentLabel, 1, 4);
        parametersPane.add(batchComment, 2, 4, 3, 1);

        setTitle("Batch Creator");
        setScene(scene);
        initOwner(mainWindow);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UTILITY);
        
        
    }
    private void actionHandler() {
        CreateBatch.setOnMouseClicked(this::onCreateNewBatch);
        treeView.setOnMouseClicked(this::onLoadRecipeAtClick);
    }

    private synchronized void onCreateNewBatch(MouseEvent action) {
        try {
            if (!batchID.getText().isEmpty() && batchID.getText() != null) {
                controller.findBatchByName(batchID.getText()).ifPresentOrElse(batch -> {
                    errorWindow("Batch name already exist, try another name ");
                }, () -> {
                    if (!batchQuantity.getText().isEmpty() & !selectedRecipeID.getText().isEmpty() && batchQuantity.getText() != null && selectedRecipeID.getText() != null) {
                        quantity = Double.parseDouble(batchQuantity.getText());
                        controller.getRecipeConfig().ifPresentOrElse(recipeConfig -> {
                            if (quantity > recipeConfig.getMaxBatchSize() || quantity == 0.0) {
                                Alert Error = new Alert(Alert.AlertType.ERROR);
                                Error.setTitle("Error ");
                                Error.setHeaderText("Error creating batch");
                                Error.setContentText("Total batch quantity is greater than the maximum allowed value 15000 or equal to zero \nThe Entered quantity equals to " + quantity);
                                Error.initOwner(this);
                                Error.showAndWait();
                            } else {
                                BatchModel batchModel = new BatchModel();
                                Batch createdBatchNew = new Batch(batchID.getText(), selectedRecipe.getUnitName(), BatchStates.Idle.name(), BatchOrders.Create.name(), batchComment.getText(), batchModel);
                                List<BatchParallelStepsModel> listOfBatchParallelStepModel = new LinkedList<>();
                                batchModel.setParallelSteps(listOfBatchParallelStepModel);
                                selectedRecipe.getModel().getParallelSteps().forEach(recipeParallelStep -> {
                                    listOfBatchParallelStepModel.add(new BatchParallelStepsModel(recipeParallelStep.getSteps().stream().map(item -> {
                                        if (item.getPhaseType() != null) {
                                            if (item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())) {
                                                double percentage = item.getValueParametersData().get("Percentage %");
                                                double totalQty = quantity * percentage / 100.0;
                                                item.getValueParametersData().replace("Percentage %", totalQty);
                                            }
                                        }
                                        return new BatchStepModel(item);
                                    }).collect(Collectors.toList())));
                                });
                                createdBatchNew.setState(BatchStates.Created.name());
                                createdBatchNew = controller.createNewBatch(createdBatchNew);
                                this.createdBatch = createdBatchNew;
                                hide();
                            }
                        }, () -> {
                            errorWindow("Recipe configurations not found ");
                        });
                    } else {
                        errorWindow("Zero quantity entered or internal error\nCreate batch again ... ");
                    }
                });
            } else {
                errorWindow("Please add Batch name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLoadRecipeAtClick(MouseEvent action) {
        try {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2) {
                RecipeTreeItem parent = (RecipeTreeItem) treeView.getSelectionModel().getSelectedItem();
                if (parent != null && parent.isLeaf() && parent.getItemType().equals(TreeItemType.Recipe)) {
                    Recipe recipe = parent.getRecipe();
                    selectedRecipe = recipe;
                    selectedRecipeID.setText(String.valueOf(recipe.getId()));
                    selectedRecipeName.setText(recipe.getRecipeName());
                }
            }
        } catch (Exception e) {
            Alert Error = new Alert(Alert.AlertType.ERROR);
            Error.setTitle("Error ");
            Error.setHeaderText("Error loading recipe");
            Error.setContentText("Please loaded recipe again or error in database connection, so restart machine");
            Error.initOwner(this);
            Error.showAndWait();
        }
    }

    private void FillTreeFromDB(){
        Map<Long, List<TreeViewItemsData>> groupedItemsByParentID =  controller.getAllTreeItems().stream().collect(Collectors.groupingBy(TreeViewItemsData::getParentID, LinkedHashMap::new , Collectors.toCollection(LinkedList::new )));
        root.setExpanded(true);
        root.getChildren().clear();
        FillTreeItemRecursiveAction(0, root, groupedItemsByParentID);
    }
    private void FillTreeItemRecursiveAction(long parentID, RecipeTreeItem parent, Map<Long, List<TreeViewItemsData>> groupedItemsByParentID) {
        if (groupedItemsByParentID.get(parentID) != null) {
            groupedItemsByParentID.get(parentID).forEach(element -> {
                if (element.getItemType().equals(TreeItemType.Folder.name())) {
                    RecipeTreeItem item = new RecipeTreeItem(element.getName(), TreeItemType.Folder);
                    item.setExpanded(true);
                    item.setItemID(element.getId());

                    parent.getChildren().add(item);
                    //Recursive fill
                    FillTreeItemRecursiveAction(element.getId(), item, groupedItemsByParentID);
                } else if (element.getItemType().equals(TreeItemType.Recipe.name())) {
                    controller.getRecipeById(element.getRecipeID()).ifPresentOrElse(recipe -> {
                        RecipeTreeItem item = new RecipeTreeItem(element.getName(), TreeItemType.Recipe);
                        item.setExpanded(true);
                        item.setItemID(element.getId());
                        item.setRecipe(recipe);
                        parent.getChildren().add(item);
                        //Recursive fill
                        FillTreeItemRecursiveAction(element.getId(), item, groupedItemsByParentID);
                    }, () -> {
                    });
                }
            });
        }
    }

    private void errorWindow(String message) {
        Alert Error = new Alert(Alert.AlertType.ERROR);
        Error.setTitle("Error ");
        Error.setHeaderText("Error creating batch");
        Error.setContentText(message);
        Error.initOwner(this);
        Error.initStyle(StageStyle.UTILITY);
        Error.showAndWait();
    }

    public Optional<Batch> showAndReturnBatch() {
        showAndWait();
        return Optional.ofNullable(createdBatch);
    }

}
