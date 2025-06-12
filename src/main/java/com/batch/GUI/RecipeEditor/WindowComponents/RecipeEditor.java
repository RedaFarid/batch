package com.batch.GUI.RecipeEditor.WindowComponents;

import com.batch.ApplicationContext;
import com.batch.DTO.RecipeSystemDataDefinitions.ParallelStepsModel;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.DTO.RecipeSystemDataDefinitions.RecipeModel;
import com.batch.DTO.RecipeSystemDataDefinitions.StepModel;
import com.batch.Database.Entities.Phase;
import com.batch.Database.Entities.Recipe;
import com.batch.Database.Entities.RecipeConf;
import com.batch.Database.Entities.TreeViewItemsData;
import com.batch.GUI.RecipeEditor.RecipeEditorController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeEditor extends Stage {
    private static volatile RecipeEditor singleton = null;
    private final Stage mainWindow;
    private final Stage ownerWindow;
    private final BorderPane rootPane = new BorderPane();
    private final TreeView<String> treeView = new TreeView();
    private final RecipeTreeItem root;
    private final ToolBar toolBar;
    private final ToolBar statusBar;
    private final Button edit;
    private final Button discard;
    private final Button launch;
    private final Button validate;
    private final Button save;
    private final Button cancel;
    private final Button maximize;
    private final FlowPane flowPane;
    private final ScrollPane stepsScrollPane;
    private final VBox pane;
    private final ScrollPane scrollPane;
    private final SplitPane splitPane;
    private final StringProperty editorMode;
    private final RecipeEditorController controller;
    private final ObjectProperty<Cursor> CURSOR_DEFAULT;
    private final ObjectProperty<Cursor> CURSOR_WAIT;
    private long SelectedItemID;
    private RecipeModel recipeModel;
    private Recipe selectedRecipe;
    private String draggedStepPhaseName;
    private String returnData;
    private String unit;

    private RecipeEditor(Stage ownerWindow) {
        this.root = new RecipeTreeItem("System", TreeItemType.Folder);
        this.toolBar = new ToolBar();
        this.statusBar = new ToolBar();
        this.edit = new Button("Edit");
        this.discard = new Button("Discard changes");
        this.launch = new Button("Release for production");
        this.validate = new Button("Validate");
        this.save = new Button("Save");
        this.cancel = new Button("Cancel editing");
        this.maximize = new Button("maximize");
        this.flowPane = new FlowPane();
        this.stepsScrollPane = new ScrollPane(this.flowPane);
        this.pane = new VBox();
        this.scrollPane = new ScrollPane(this.pane);
        this.splitPane = new SplitPane(this.treeView, this.scrollPane, this.stepsScrollPane);
        this.editorMode = new SimpleStringProperty();
        this.SelectedItemID = -1L;
        this.recipeModel = new RecipeModel();
        this.returnData = "";
        this.CURSOR_DEFAULT = new SimpleObjectProperty(Cursor.DEFAULT);
        this.CURSOR_WAIT = new SimpleObjectProperty(Cursor.WAIT);
        this.mainWindow = this;
        this.ownerWindow = ownerWindow;
        this.controller = ApplicationContext.applicationContext.getBean(RecipeEditorController.class);
        this.graphicsBuilder();
        this.actionHandler();
    }

    public static RecipeEditor getWindow(Stage ownerWindow) {
        synchronized (RecipeEditor.class) {
            if (singleton == null) {
                singleton = new RecipeEditor(ownerWindow);
            }
        }

        return singleton;
    }

    private void graphicsBuilder() {
        this.editorMode.setValue("Default");
        this.maximize.setPrefWidth(150.0F);
        this.save.setPrefWidth(200.0F);
        this.edit.setPrefWidth(200.0F);
        this.discard.setPrefWidth(200.0F);
        this.launch.setPrefWidth(200.0F);
        this.validate.setPrefWidth(200.0F);
        this.cancel.setPrefWidth(200.0F);
        this.save.setDisable(true);
        this.edit.setDisable(true);
        this.discard.setDisable(true);
        this.launch.setDisable(true);
        this.validate.setDisable(true);
        this.cancel.setDisable(true);
        this.scrollPane.prefHeightProperty().bind(this.heightProperty());
        this.flowPane.prefHeightProperty().bind(this.heightProperty());
        this.flowPane.prefWidthProperty().bind(this.stepsScrollPane.widthProperty());
        this.stepsScrollPane.setMaxWidth(800.0F);
        this.scrollPane.setPannable(true);
        this.flowPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.flowPane.setVgap(5.0F);
        this.flowPane.setHgap(15.0F);
        this.flowPane.setPadding(new Insets(5.0F));
        this.flowPane.setAlignment(Pos.TOP_CENTER);
        this.pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        this.pane.setAlignment(Pos.CENTER);
        this.pane.setSpacing(5.0F);
        this.pane.setPadding(new Insets(20.0F));
        this.pane.minHeightProperty().bind(this.scrollPane.heightProperty());
        this.pane.prefWidthProperty().bind(this.scrollPane.widthProperty());
        this.treeView.setPrefWidth(300.0F);
        this.treeView.setMaxWidth(600.0F);
        this.treeView.setRoot(this.root);
        this.treeView.setPadding(new Insets(0.0F, 0.0F, 20.0F, 0.0F));
        this.toolBar.getItems().addAll(this.maximize, new Separator(), new Separator(), this.edit, this.discard, this.validate, this.save, this.cancel);
        this.statusBar.getItems().addAll(new Label("Batch interface status"));
        this.rootPane.setCenter(this.splitPane);
        this.rootPane.setTop(this.toolBar);
        this.rootPane.setBottom(this.statusBar);
        this.splitPane.setDividerPositions(0.1, 0.6, 0.3);
        this.setTitle("Recipe editor");
        this.setScene(new Scene(this.rootPane));
        this.initOwner(this.ownerWindow);
        this.initModality(Modality.WINDOW_MODAL);
        this.initStyle(StageStyle.UTILITY);
    }

    private void actionHandler() {
        this.maximize.setOnMouseClicked(this::onMaximize);
        this.save.setOnMouseClicked(this::onSaveClicked);
        this.validate.setOnMouseClicked(this::onValidateClicked);
        this.edit.setOnMouseClicked(this::onEditClicked);
        this.discard.setOnMouseClicked(this::onDiscardClicked);
        this.launch.setOnMouseClicked(this::onLaunchClicked);
        this.cancel.setOnMouseClicked(this::onCancelClicked);
        this.treeView.setOnContextMenuRequested((action) -> {
            ContextMenu menu = new ContextMenu();
            MenuItem createFolder = new MenuItem("Create new Folder          ");
            MenuItem createRecipe = new MenuItem("Create new Recipe ");
            MenuItem refresh = new MenuItem("Refresh");
            MenuItem loadRecipe = new MenuItem("Load Recipe");
            MenuItem delete = new MenuItem("Delete ");
            MenuItem copy = new MenuItem("Copy");
            MenuItem cut = new MenuItem("Cut");
            MenuItem paste = new MenuItem("Paste");
            MenuItem rename = new MenuItem("Rename");
            menu.getItems().addAll(createFolder, createRecipe, new SeparatorMenuItem(), refresh, loadRecipe, new SeparatorMenuItem(), delete, copy, cut, paste, new SeparatorMenuItem(), rename);
            createFolder.setOnAction(this::onCreateFolder);
            createRecipe.setOnAction(this::onCreateRecipe);
            refresh.setOnAction(this::onRefresh);
            loadRecipe.setOnAction(this::onLoadRecipe);
            delete.setOnAction(this::onDelete);
            copy.setOnAction(this::onCopy);
            cut.setOnAction(this::onCut);
            paste.setOnAction(this::onPaste);
            rename.setOnAction(this::onRename);
            menu.show(this, action.getScreenX(), action.getScreenY());
        });
        this.treeView.setOnMouseClicked(this::onLoadRecipeAtClick);
        this.editorMode.addListener(this::onModeChange);
        this.pane.heightProperty().addListener((observable, oldValue, newValue) -> this.scrollPane.setHvalue((Double) newValue));
    }

    public void refreshAndUpdateAndShow(String unit) {
        try {
            Task<Boolean> task = this.loadingTask(unit);
            ReadOnlyBooleanProperty readOnlyBooleanProperty = task.runningProperty();
            this.ownerWindow.getScene().cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
            this.controller.execute(task);
        } catch (Exception e) {
            this.showErrorWindowForException("Error updating", e);
            e.printStackTrace();
        }

    }

    private Task<Boolean> loadingTask(final String unit) {
        return new Task<Boolean>() {
            protected Boolean call() throws Exception {
                List<Phase> allPhasesSortedForAUnit = RecipeEditor.this.controller.getAllPhasesSortedForAUnit(unit);
                RecipeEditor.this.setUnit(unit);
                Platform.runLater(() -> {
                    RecipeEditor.this.pane.getChildren().clear();
                    RecipeEditor.this.FillTreeFromDB();
                    RecipeEditor.this.flowPane.getChildren().clear();
                    allPhasesSortedForAUnit.forEach((Phase) -> {
                        Step step = new Step(Phase.getName(), false, RecipeEditor.this.mainWindow);
                        RecipeEditor.this.flowPane.getChildren().add(step);
                        step.setOnDragDetected((action) -> {
                            Dragboard board = step.startDragAndDrop(TransferMode.ANY);
                            ClipboardContent content = new ClipboardContent();
                            content.putString(Phase.getName());
                            board.setContent(content);
                            RecipeEditor.this.draggedStepPhaseName = step.getModel().getPhaseName();
                        });
                    });
                    RecipeEditor.this.show();
                });
                return null;
            }
        };
    }

    private ParallelSteps adjustDragDropActionsForReceivingContainer(ParallelSteps parallelSteps) {
        ParallelSteps newParallelSteps = new ParallelSteps();
        parallelSteps.setOnDragOver((action) -> {
            if (action.getGestureSource() != this.pane && action.getDragboard().hasString()) {
                action.acceptTransferModes(TransferMode.ANY);
            }

        });
        parallelSteps.setOnDragEntered((action) -> parallelSteps.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY))));
        parallelSteps.setOnDragExited((action) -> parallelSteps.setBackground(this.pane.getBackground()));
        parallelSteps.setOnDragDropped((action) -> {
            RecipeConf recipeConfigurations = this.controller.getRecipeConfigurations();
            if (parallelSteps.getChildren().size() < recipeConfigurations.getMaxParallelSteps()) {
                Dragboard db = action.getDragboard();
                Step newStep = new Step(this.draggedStepPhaseName, true, this.mainWindow);
                this.adjustDragDropActionsForStep(newStep, parallelSteps);
                parallelSteps.getChildren().add(newStep);
                parallelSteps.getModel().getSteps().add(newStep.getModel());
                this.pane.getChildren().add(newParallelSteps);
                this.recipeModel.getParallelSteps().add(newParallelSteps.getModel());
                this.adjustDragDropActionsForReceivingContainer(newParallelSteps);
                action.setDropCompleted(true);
            } else {
                this.showErrorWindow("Error adding new step", "You can't exceed the maximum number of steps \nAs defined in the recipe configurations.");
            }

        });
        return newParallelSteps;
    }

    private void adjustDragDropActionsForStep(Step newStep, ParallelSteps ownerParallelStep) {
        if (!newStep.getStepName().equals("Start")) {
            newStep.setOnDragDetected((action2) -> {
                Dragboard board = newStep.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(newStep.getStepName());
                board.setContent(content);
                this.draggedStepPhaseName = newStep.getModel().getPhaseName();
            });
            newStep.setOnDragDone((action2) -> {
                ownerParallelStep.getChildren().remove(newStep);
                ownerParallelStep.getModel().getSteps().remove(newStep.getModel());
                if (ownerParallelStep.getChildren().isEmpty()) {
                    this.recipeModel.getParallelSteps().remove(ownerParallelStep.getModel());
                    this.pane.getChildren().remove(ownerParallelStep);
                }

            });
            newStep.setOnContextMenuRequested((action3) -> {
                ContextMenu menu = new ContextMenu();
                MenuItem delete = new MenuItem("Delete            ");
                menu.getItems().addAll(delete);
                menu.show(this.mainWindow, action3.getScreenX(), action3.getScreenY());
                delete.setOnAction((action4) -> {
                    ownerParallelStep.getChildren().remove(newStep);
                    ownerParallelStep.getModel().getSteps().remove(newStep.getModel());
                    if (ownerParallelStep.getChildren().isEmpty()) {
                        this.recipeModel.getParallelSteps().remove(ownerParallelStep.getModel());
                        this.pane.getChildren().remove(ownerParallelStep);
                    }

                });
            });
        }

    }

    private void onMaximize(MouseEvent mouseEvent) {
        this.mainWindow.setMaximized(true);
    }

    private void onCreateFolder(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) this.treeView.getSelectionModel().getSelectedItem();
        if (parent != null && !parent.getItemType().equals(TreeItemType.Recipe)) {
            String ret = this.createNameWindow("Name of the Folder");
            if (!ret.equalsIgnoreCase("Cancel")) {
                TreeViewItemsData treeItemDataModel = this.controller.saveTreeItem(new TreeViewItemsData(ret, parent.getItemID(), TreeItemType.Folder.name(), 0L));
                RecipeTreeItem treeItem = new RecipeTreeItem(treeItemDataModel.getName(), TreeItemType.Folder);
                parent.getChildren().add(treeItem);
                parent.setExpanded(true);
                treeItem.setItemID(treeItemDataModel.getId());
                treeItem.setItemParent(treeItemDataModel.getParentID());
            }
        }

    }

    private void onCreateRecipe(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) this.treeView.getSelectionModel().getSelectedItem();
        if (parent != null && parent.getItemType().equals(TreeItemType.Folder)) {
            String ret = this.createNameWindow("Name of the Recipe");
            if (!ret.equalsIgnoreCase("Cancel")) {
                this.controller.createNewRecipe(new Recipe(ret, this.unit, new RecipeModel())).ifPresent((recipe) -> {
                    TreeViewItemsData treeItemDataModel = this.controller.saveTreeItem(new TreeViewItemsData(ret, parent.getItemID(), TreeItemType.Recipe.name(), recipe.getId()));
                    RecipeTreeItem treeItem = new RecipeTreeItem(treeItemDataModel.getName(), TreeItemType.Recipe);
                    parent.getChildren().add(treeItem);
                    parent.setExpanded(true);
                    treeItem.setItemID(treeItemDataModel.getId());
                    treeItem.setItemParent(treeItemDataModel.getParentID());
                    treeItem.setRecipe(recipe);
                    this.recipeModel = recipe.getModel();
                    this.selectedRecipe = recipe;
                    this.clearPaneForNewRecipe();
                    this.editorMode.setValue("edit");
                });
            }
        }

    }

    private void onRefresh(ActionEvent action) {
        this.FillTreeFromDB();
    }

    private void onLoadRecipe(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) this.treeView.getSelectionModel().getSelectedItem();
        if (parent != null && parent.isLeaf() && parent.getItemType().equals(TreeItemType.Recipe)) {
            Recipe recipe = parent.getRecipe();
            if (recipe.getUnitName().equals(this.unit)) {
                this.recipeModel = recipe.getModel();
                this.selectedRecipe = recipe;
                this.LoadRecipeToGraphicsWithoutEdit();
                this.editorMode.setValue("Monitor");
            } else {
                String var10002 = this.unit;
                this.showErrorWindow("Error you selected wrong recipe", "Please select recipe related to " + var10002 + " ,\nOr close recipe editor and start it again for " + recipe.getUnitName());
            }
        }

    }

    private void onDelete(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) this.treeView.getSelectionModel().getSelectedItem();
        if (!parent.equals(this.root)) {
            this.DeleteTreeItemInDBRecursiveAction(parent);
            parent.getParent().getChildren().remove(parent);
        }

        this.LoadRecipeToGraphicsWithoutEdit();
    }

    private void onCopy(ActionEvent action) {
    }

    private void onCut(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) this.treeView.getSelectionModel().getSelectedItem();
        if (parent != null) {
            this.SelectedItemID = parent.getItemID();
        }

    }

    private void onPaste(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) this.treeView.getSelectionModel().getSelectedItem();
        if (parent != null && this.SelectedItemID > 0L && parent.getItemType().equals(TreeItemType.Folder) && this.notOneOfItsChild(this.SelectedItemID, parent.getItemID())) {
            this.controller.getTreeItemById(this.SelectedItemID).ifPresentOrElse((copied) -> {
                copied.setParentID(parent.getItemID());
                this.controller.saveTreeItem(copied);
            }, () -> {
            });
            this.FillTreeFromDB();
        }

        this.SelectedItemID = -1L;
    }

    private void onSaveClicked(MouseEvent action) {
        this.controller.getRecipeById(this.selectedRecipe.getId()).ifPresentOrElse((recipe) -> {
            this.controller.saveRecipe(this.selectedRecipe);
            this.LoadRecipeToGraphicsWithoutEdit();
            this.FillTreeFromDB();
            this.editorMode.setValue("save");
        }, () -> {
        });
    }

    private void onValidateClicked(MouseEvent action) {
        double total = this.recipeModel.getParallelSteps().stream().flatMap((item) -> item.getSteps().stream()).filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).filter((item) -> item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())).map((item) -> item.getValueParametersData().get("Percentage %")).reduce((double) 0.0F, Double::sum);
        if ((!(total > 99.9) || !(total < 100.1)) && total != (double) 0.0F) {
            this.showErrorWindow("Error validating recipe", "Total percentages are not equal to 100% \nThe total equals to " + total);
        } else {
            List<String> phasesNames = this.controller.getAllPhases().stream().map(Phase::getName).collect(Collectors.toList());
            this.recipeModel.setParallelSteps(this.recipeModel.getParallelSteps().stream().map((psm) -> {
                ParallelStepsModel parallelStepsModel = new ParallelStepsModel();
                psm.getSteps().forEach((a) -> {
                    if ((!a.getPhaseName().equals("End") || !a.getPhaseName().equals("Start")) && phasesNames.contains(a.getPhaseName())) {
                        parallelStepsModel.getSteps().add(a);
                    }

                });
                return parallelStepsModel;
            }).filter((e) -> e.getSteps().size() > 0).collect(LinkedList::new, LinkedList::add, LinkedList::addAll));
            ParallelSteps ps = new ParallelSteps();
            ParallelSteps startPs = new ParallelSteps();
            Step endStep = new Step("End", true, this.mainWindow);
            Step startStep = new Step("Start", true, this.mainWindow);
            this.recipeModel.getParallelSteps().add(ps.getModel());
            this.recipeModel.getParallelSteps().add(0, startPs.getModel());
            ps.getModel().getSteps().add(endStep.getModel());
            startPs.getModel().getSteps().add(startStep.getModel());
            this.LoadRecipeToGraphicsWithoutEdit();
            this.editorMode.setValue("validate");
        }

    }

    private void onEditClicked(MouseEvent action) {
        this.LoadRecipeToGraphicsWithEdit();
        this.editorMode.setValue("edit");
    }

    private void onDiscardClicked(MouseEvent action) {
        if (this.editorMode.getValue().equalsIgnoreCase("edit")) {
            this.controller.getRecipeById(this.selectedRecipe.getId()).ifPresentOrElse((recipe) -> {
                this.recipeModel = recipe.getModel();
                this.selectedRecipe = recipe;
                this.LoadRecipeToGraphicsWithEdit();
            }, () -> {
            });
        } else {
            this.showErrorWindow("Error changing data", "Discarding changes happens only if in Edit mode ...");
        }

    }

    private void onLaunchClicked(MouseEvent action) {
        this.editorMode.setValue("launch");
    }

    private void onLoadRecipeAtClick(MouseEvent action) {
        try {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2) {
                RecipeTreeItem parent = (RecipeTreeItem) this.treeView.getSelectionModel().getSelectedItem();
                if (parent != null && parent.isLeaf() && parent.getItemType().equals(TreeItemType.Recipe)) {
                    Recipe recipe = parent.getRecipe();
                    if (recipe.getUnitName().equals(this.unit)) {
                        this.recipeModel = recipe.getModel();
                        this.selectedRecipe = recipe;
                        this.LoadRecipeToGraphicsWithoutEdit();
                        this.editorMode.setValue("Monitor");
                    } else {
                        String var10002 = this.unit;
                        this.showErrorWindow("Error you selected wrong recipe", "Please select recipe related to " + var10002 + " ,\nOr close recipe editor and start it again for " + recipe.getUnitName());
                    }
                }
            }
        } catch (Exception e) {
            this.showErrorWindowForException("Error loading recipe", e);
            e.printStackTrace();
        }

    }

    private void onModeChange(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        switch (newValue.toLowerCase().trim()) {
            case "edit":
                this.pane.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, Insets.EMPTY)));
                this.save.setDisable(true);
                this.edit.setDisable(true);
                this.discard.setDisable(false);
                this.launch.setDisable(true);
                this.validate.setDisable(false);
                this.cancel.setDisable(false);
                break;
            case "monitor":
                this.pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                this.save.setDisable(true);
                this.edit.setDisable(false);
                this.discard.setDisable(true);
                this.launch.setDisable(false);
                this.validate.setDisable(true);
                this.cancel.setDisable(true);
                break;
            case "save":
                this.pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                this.save.setDisable(true);
                this.edit.setDisable(false);
                this.discard.setDisable(true);
                this.launch.setDisable(true);
                this.validate.setDisable(true);
                this.cancel.setDisable(true);
                break;
            case "validate":
                this.pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                this.save.setDisable(false);
                this.edit.setDisable(false);
                this.discard.setDisable(true);
                this.launch.setDisable(true);
                this.validate.setDisable(true);
                this.cancel.setDisable(false);
                break;
            case "launch":
                this.pane.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                this.save.setDisable(true);
                this.edit.setDisable(true);
                this.discard.setDisable(true);
                this.launch.setDisable(true);
                this.validate.setDisable(true);
                this.cancel.setDisable(true);
                break;
            case "discard":
                this.pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                this.save.setDisable(true);
                this.edit.setDisable(false);
                this.discard.setDisable(true);
                this.launch.setDisable(true);
                this.validate.setDisable(false);
                this.cancel.setDisable(false);
            case "default":
                this.save.setDisable(true);
                this.edit.setDisable(true);
                this.discard.setDisable(true);
                this.launch.setDisable(true);
                this.validate.setDisable(true);
                this.cancel.setDisable(true);
        }

    }

    private void onCancelClicked(MouseEvent action) {
        this.controller.getRecipeById(this.selectedRecipe.getId()).ifPresentOrElse((recipe) -> {
            this.recipeModel = recipe.getModel();
            this.selectedRecipe = recipe;
            this.LoadRecipeToGraphicsWithoutEdit();
            this.editorMode.setValue("monitor");
        }, () -> this.showErrorWindow("Error to cancel", "Can not find the recipe in the database"));
    }

    private void onRename(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) this.treeView.getSelectionModel().getSelectedItem();
        if (parent != null && parent.getItemType().equals(TreeItemType.Recipe)) {
            String ret = this.createNameWindow("New Recipe name");
            if (!ret.equalsIgnoreCase("Cancel")) {
                Recipe recipe = parent.getRecipe();
                recipe.setRecipeName(ret);
                this.controller.saveRecipe(recipe);
                this.controller.getTreeItemById(parent.getItemID()).ifPresentOrElse((treeItemData) -> {
                    treeItemData.setName(ret);
                    this.controller.saveTreeItem(treeItemData);
                }, () -> {
                });
            }
        } else if (parent != null && parent.getItemType().equals(TreeItemType.Folder)) {
            String ret = this.createNameWindow("New Folder name");
            if (!ret.equalsIgnoreCase("Cancel")) {
                this.controller.getTreeItemById(parent.getItemID()).ifPresentOrElse((treeItemData) -> {
                    treeItemData.setName(ret);
                    this.controller.saveTreeItem(treeItemData);
                }, () -> {
                });
            }
        }

        this.FillTreeFromDB();
    }

    private void DeleteTreeItemInDBRecursiveAction(RecipeTreeItem item) {
        item.getChildren().forEach((subItem) -> this.DeleteTreeItemInDBRecursiveAction((RecipeTreeItem) subItem));
        this.controller.deleteTreeItemById(item.getItemID());
    }

    private void FillTreeFromDB() {
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

    private void LoadRecipeToGraphicsWithoutEdit() {
        this.pane.getChildren().clear();

        for (ParallelStepsModel pSM : this.recipeModel.getParallelSteps()) {
            ParallelSteps parallelStepTemp = new ParallelSteps();
            this.pane.getChildren().add(parallelStepTemp);

            for (StepModel sm : pSM.getSteps()) {
                Step step = new Step(sm.getPhaseName(), false, this.mainWindow);
                step.setModel(sm);
                parallelStepTemp.getChildren().add(step);
            }
        }

    }

    private void LoadRecipeToGraphicsWithEdit() {
        this.pane.getChildren().clear();
        ParallelSteps parallelStepTemp = new ParallelSteps();

        for (ParallelStepsModel pSM : this.recipeModel.getParallelSteps()) {
            if (pSM.getSteps().stream().noneMatch((a) -> a.getPhaseName().equals("End"))) {
                parallelStepTemp = this.adjustDragDropActionsForReceivingContainer(parallelStepTemp);
                parallelStepTemp.setModel(pSM);
                this.pane.getChildren().add(parallelStepTemp);

                for (StepModel sm : pSM.getSteps()) {
                    Step step = new Step(sm.getPhaseName(), true, this.mainWindow);
                    step.setModel(sm);
                    parallelStepTemp.getChildren().add(step);
                    this.adjustDragDropActionsForStep(step, parallelStepTemp);
                }
            }
        }

        ParallelSteps ps = new ParallelSteps();
        this.recipeModel.getParallelSteps().add(ps.getModel());
        this.adjustDragDropActionsForReceivingContainer(ps);
        this.pane.getChildren().add(ps);
    }

    private boolean notOneOfItsChild(long SelectedItem, long destination) {
        RecipeEditorController.ReturnData<LinkedHashMap<Long, List<TreeViewItemsData>>> recipesMap = this.controller.getRecipesMap();
        this.rootPane.cursorProperty().bind(Bindings.when(recipesMap.readOnlyBooleanProperty()).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
        Map<Long, List<TreeViewItemsData>> groupedItemsByParentID = recipesMap.object();
        return !this.notOneOfItsChildRecursiveCheck(destination, SelectedItem, groupedItemsByParentID);
    }

    private boolean notOneOfItsChildRecursiveCheck(long destination, long SelectedItem, Map<Long, List<TreeViewItemsData>> groupedItemsByParentID) {
        if (groupedItemsByParentID.get(SelectedItem) != null) {
            for (TreeViewItemsData item : groupedItemsByParentID.get(SelectedItem)) {
                if (item.getId() == destination) {
                    return true;
                }

                if (this.notOneOfItsChildRecursiveCheck(destination, item.getId(), groupedItemsByParentID)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private void clearPaneForNewRecipe() {
        ParallelSteps startPane = new ParallelSteps();
        ParallelSteps ParallelStepsZero = new ParallelSteps();
        Step startStep = new Step("Start", false, this.mainWindow);
        startPane.getChildren().add(startStep);
        startPane.getModel().getSteps().add(startStep.getModel());
        this.pane.getChildren().clear();
        this.pane.getChildren().addAll(startPane, ParallelStepsZero);
        this.recipeModel.getParallelSteps().add(startPane.getModel());
        this.recipeModel.getParallelSteps().add(ParallelStepsZero.getModel());
        ParallelStepsZero.getChildren().clear();
        ParallelStepsZero.getModel().getSteps().clear();
        this.adjustDragDropActionsForReceivingContainer(ParallelStepsZero);
    }

    private String createNameWindow(String labelString) {
        Label label = new Label(labelString);
        final TextField field = new TextField();
        field.setPromptText("Please enter the name ");
        field.setPrefWidth(350.0F);
        Button Cancel = new Button("Cancel");
        Button Ok = new Button("Ok");
        Cancel.setPrefWidth(150.0F);
        Ok.setPrefWidth(150.0F);
        HBox buttonsContainer = new HBox();
        buttonsContainer.getChildren().addAll(Ok, Cancel);
        buttonsContainer.setSpacing(10.0F);
        buttonsContainer.setPadding(new Insets(5.0F));
        GridPane container = new GridPane();
        container.add(field, 0, 0);
        container.setPadding(new Insets(5.0F));
        container.setVgap(5.0F);
        container.setHgap(5.0F);
        BorderPane root = new BorderPane();
        root.setBottom(buttonsContainer);
        root.setCenter(container);
        root.setTop(label);
        root.setPadding(new Insets(15.0F));
        Scene scene = new Scene(root);
        final Stage stage = new Stage();
        stage.setTitle("Please enter name ");
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(this.mainWindow);
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        Cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                RecipeEditor.this.returnData = "Cancel";
                stage.close();
            }
        });
        Ok.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (!field.getText().isEmpty()) {
                    RecipeEditor.this.returnData = field.getText();
                } else {
                    RecipeEditor.this.returnData = "Cancel";
                }

                stage.close();
            }
        });
        field.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (!field.getText().isEmpty()) {
                        RecipeEditor.this.returnData = field.getText();
                    } else {
                        RecipeEditor.this.returnData = "Cancel";
                    }

                    stage.close();
                }

            }
        });
        stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    RecipeEditor.this.returnData = "Cancel";
                    stage.close();
                }

            }
        });
        stage.setOnCloseRequest((action) -> this.returnData = "Cancel");
        field.requestFocus();
        stage.showAndWait();
        return this.returnData;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private void showErrorWindow(String header, String content) {
        Platform.runLater(() -> {
            Alert Error = new Alert(AlertType.ERROR);
            Error.setTitle("Error ");
            Error.setHeaderText(header);
            Error.setContentText(content);
            Error.initOwner(this.mainWindow);
            Error.initStyle(StageStyle.UTILITY);
            Error.show();
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

    public void close() {
        this.hide();
    }
}
