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
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private final TreeView<String> treeView = new TreeView<>();
    private final RecipeTreeItem root = new RecipeTreeItem("System", TreeItemType.Folder);
    private final ToolBar toolBar = new ToolBar();
    private final ToolBar statusBar = new ToolBar();
    
    private final Button edit = new Button("Edit");
    private final Button discard = new Button("Discard changes");
    private final Button launch = new Button("Release for production");
    private final Button validate = new Button("Validate");
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel editing");
    private final Button maximize = new Button("maximize");

    
    private final FlowPane flowPane = new FlowPane();
    private final ScrollPane stepsScrollPane = new ScrollPane(flowPane);
    private final VBox pane = new VBox();
    private final ScrollPane scrollPane = new ScrollPane(pane);
    private final SplitPane splitPane = new SplitPane(treeView, scrollPane, stepsScrollPane);
    
    private final StringProperty editorMode = new SimpleStringProperty();
    
    private long SelectedItemID = -1;
    
    private RecipeModel recipeModel = new RecipeModel();
    private Recipe selectedRecipe;
    
    private String draggedStepPhaseName;
    
    private String returnData = "";
    private String unit;

    private final RecipeEditorController controller;

    private final ObjectProperty<Cursor> CURSOR_DEFAULT = new SimpleObjectProperty<>(Cursor.DEFAULT);
    private final ObjectProperty<Cursor> CURSOR_WAIT = new SimpleObjectProperty<>(Cursor.WAIT);

    private RecipeEditor(Stage ownerWindow) {
        this.mainWindow = this;
        this.ownerWindow = ownerWindow;
        this.controller = ApplicationContext.applicationContext.getBean(RecipeEditorController.class);
        graphicsBuilder();
        actionHandler();
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
        editorMode.setValue("Default");

        maximize.setPrefWidth(150);
        save.setPrefWidth(200);
        edit.setPrefWidth(200);
        discard.setPrefWidth(200);
        launch.setPrefWidth(200);
        validate.setPrefWidth(200);
        cancel.setPrefWidth(200);
        
        save.setDisable(true);
        edit.setDisable(true);
        discard.setDisable(true);
        launch.setDisable(true);
        validate.setDisable(true);
        cancel.setDisable(true);
        
        scrollPane.prefHeightProperty().bind(heightProperty());
        flowPane.prefHeightProperty().bind(heightProperty());
        flowPane.prefWidthProperty().bind(stepsScrollPane.widthProperty());
        
        stepsScrollPane.setMaxWidth(800);
        scrollPane.setPannable(true);
        
        flowPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        flowPane.setVgap(5);
        flowPane.setHgap(15);
        flowPane.setPadding(new Insets(5));
        flowPane.setAlignment(Pos.TOP_CENTER);

        pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(5);
        pane.setPadding(new Insets(20));
        pane.minHeightProperty().bind(scrollPane.heightProperty());
        pane.prefWidthProperty().bind(scrollPane.widthProperty());

        treeView.setPrefWidth(300);
        treeView.setMaxWidth(600);
        treeView.setRoot(root);
        treeView.setPadding(new Insets(0,0,20,0));

        toolBar.getItems().addAll(maximize, new Separator(), new Separator(), edit, discard, validate, save, /*launch, */cancel);

        statusBar.getItems().addAll(new Label("Batch interface status"));

        rootPane.setCenter(splitPane);
        rootPane.setTop(toolBar);
        rootPane.setBottom(statusBar);
        
        splitPane.setDividerPositions(0.1,0.6,0.3);
        
        setTitle("Recipe editor");
        setScene(new Scene(rootPane));
        initOwner(ownerWindow);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UTILITY);
        
    }
    private void actionHandler() {
        maximize.setOnMouseClicked(this::onMaximize);
        save.setOnMouseClicked(this::onSaveClicked);
        validate.setOnMouseClicked(this::onValidateClicked);
        edit.setOnMouseClicked(this::onEditClicked);
        discard.setOnMouseClicked(this::onDiscardClicked);
        launch.setOnMouseClicked(this::onLaunchClicked);
        cancel.setOnMouseClicked(this::onCancelClicked);
        treeView.setOnContextMenuRequested(action -> {
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
        treeView.setOnMouseClicked(this::onLoadRecipeAtClick);
        editorMode.addListener(this::onModeChange);

        pane.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setHvalue((Double)newValue ));
    }

    public void refreshAndUpdateAndShow(String unit) {
        try {
            final Task<Boolean> task = loadingTask(unit);
            final ReadOnlyBooleanProperty readOnlyBooleanProperty = task.runningProperty();
            ownerWindow.getScene().cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(CURSOR_WAIT).otherwise(CURSOR_DEFAULT));
            controller.execute(task);
        } catch (Exception e) {
            showErrorWindowForException("Error updating", e);
            e.printStackTrace();
        }
    }
    private Task<Boolean> loadingTask(String unit){
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                final List<Phase> allPhasesSortedForAUnit = controller.getAllPhasesSortedForAUnit(unit);
                setUnit(unit);
                Platform.runLater(() -> {
                    pane.getChildren().clear();
                    FillTreeFromDB();
                    flowPane.getChildren().clear();
                    allPhasesSortedForAUnit.forEach((Phase) -> {

                        Step step = new Step(Phase.getName(), false, mainWindow);
                        flowPane.getChildren().add(step);

                        step.setOnDragDetected(action -> {
                            Dragboard board = step.startDragAndDrop(TransferMode.ANY);
                            ClipboardContent content = new ClipboardContent();
                            content.putString(Phase.getName());
                            board.setContent(content);
                            draggedStepPhaseName = step.getModel().getPhaseName();
                        });

                    });
                    show();
                });
                return null;
            }
        };
    }

    private ParallelSteps adjustDragDropActionsForReceivingContainer(ParallelSteps parallelSteps) {
        ParallelSteps newParallelSteps = new ParallelSteps();

        parallelSteps.setOnDragOver(action -> {
            if (action.getGestureSource() != pane && action.getDragboard().hasString()) {
                action.acceptTransferModes(TransferMode.ANY);
            }
        });
        parallelSteps.setOnDragEntered(action -> {
            parallelSteps.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        });
        parallelSteps.setOnDragExited(action -> {
            parallelSteps.setBackground(pane.getBackground());
        });
        parallelSteps.setOnDragDropped(action -> {
            RecipeConf recipeConfigurations =controller.getRecipeConfigurations();
            if (parallelSteps.getChildren().size() < recipeConfigurations.getMaxParallelSteps()) {
                Dragboard db = action.getDragboard();

                Step newStep = new Step(draggedStepPhaseName, true, mainWindow);
                adjustDragDropActionsForStep(newStep, parallelSteps);

                parallelSteps.getChildren().add(newStep);
                parallelSteps.getModel().getSteps().add(newStep.getModel());

                pane.getChildren().add(newParallelSteps);
                recipeModel.getParallelSteps().add(newParallelSteps.getModel());
                adjustDragDropActionsForReceivingContainer(newParallelSteps);

                action.setDropCompleted(true);
            } else {
                showErrorWindow("Error adding new step", "You can't exceed the maximum number of steps \nAs defined in the recipe configurations.");
            }
        });
        return newParallelSteps;
    }
    private void adjustDragDropActionsForStep(Step newStep, ParallelSteps ownerParallelStep) {
        if (!newStep.getStepName().equals("Start")) {
            newStep.setOnDragDetected(action2 -> {
                Dragboard board = newStep.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(newStep.getStepName());
                board.setContent(content);
                draggedStepPhaseName = newStep.getModel().getPhaseName();
            });
            newStep.setOnDragDone(action2 -> {
                ownerParallelStep.getChildren().remove(newStep);
                ownerParallelStep.getModel().getSteps().remove(newStep.getModel());
                if (ownerParallelStep.getChildren().isEmpty()) {
                    recipeModel.getParallelSteps().remove(ownerParallelStep.getModel());
                    pane.getChildren().remove(ownerParallelStep);
                }
            });
            newStep.setOnContextMenuRequested(action3 -> {
                ContextMenu menu = new ContextMenu();
                MenuItem delete = new MenuItem("Delete            ");
                menu.getItems().addAll(delete);
                menu.show(mainWindow, action3.getScreenX(), action3.getScreenY());
                delete.setOnAction(action4 -> {
                    ownerParallelStep.getChildren().remove(newStep);
                    ownerParallelStep.getModel().getSteps().remove(newStep.getModel());
                    if (ownerParallelStep.getChildren().isEmpty()) {
                        recipeModel.getParallelSteps().remove(ownerParallelStep.getModel());
                        pane.getChildren().remove(ownerParallelStep);
                    }
                });
            });
        }
    }

    private void onMaximize(MouseEvent mouseEvent) {
        mainWindow.setMaximized(true);
    }
    private void onCreateFolder(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) treeView.getSelectionModel().getSelectedItem();
        if (parent != null){
            if (!parent.getItemType().equals(TreeItemType.Recipe)) {
                String ret = createNameWindow("Name of the Folder");
                if (!ret.equalsIgnoreCase("Cancel")) {
                    TreeViewItemsData treeItemDataModel = controller.saveTreeItem(new TreeViewItemsData(ret, parent.getItemID(), TreeItemType.Folder.name(), 0));
                    RecipeTreeItem treeItem = new RecipeTreeItem(treeItemDataModel.getName(), TreeItemType.Folder);
                    parent.getChildren().add(treeItem);
                    parent.setExpanded(true);

                    treeItem.setItemID(treeItemDataModel.getId());
                    treeItem.setItemParent(treeItemDataModel.getParentID());
                }
            }
        }
    }
    private void onCreateRecipe(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) treeView.getSelectionModel().getSelectedItem();
        if (parent != null && parent.getItemType().equals(TreeItemType.Folder)) {
            String ret = createNameWindow("Name of the Recipe");
            if (!ret.equalsIgnoreCase("Cancel")) {

                Recipe recipe = controller.createNewRecipe(new Recipe(ret, unit, new RecipeModel()));
                TreeViewItemsData treeItemDataModel = controller.saveTreeItem(new TreeViewItemsData(ret, parent.getItemID(), TreeItemType.Recipe.name(), recipe.getId()));

                RecipeTreeItem treeItem = new RecipeTreeItem(treeItemDataModel.getName(), TreeItemType.Recipe);
                parent.getChildren().add(treeItem);
                parent.setExpanded(true);

                treeItem.setItemID(treeItemDataModel.getId());
                treeItem.setItemParent(treeItemDataModel.getParentID());
                treeItem.setRecipe(recipe);

                recipeModel = recipe.getModel();
                selectedRecipe = recipe;
                clearPaneForNewRecipe();
                editorMode.setValue("edit");
            }
        }
        
    }
    private void onRefresh(ActionEvent action){
        FillTreeFromDB();
    }
    private void onLoadRecipe(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem) treeView.getSelectionModel().getSelectedItem();
        if (parent != null && parent.isLeaf() && parent.getItemType().equals(TreeItemType.Recipe)) {
            Recipe recipe = parent.getRecipe();
            if (recipe.getUnitName().equals(unit)) {
                recipeModel = recipe.getModel();
                selectedRecipe = recipe;
                LoadRecipeToGraphicsWithoutEdit();
                editorMode.setValue("Monitor");
            } else {
                showErrorWindow("Error you selected wrong recipe", "Please select recipe related to " + unit + " ,\nOr close recipe editor and start it again for " + recipe.getUnitName());
            }
        }
    }
    private void onDelete(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem)treeView.getSelectionModel().getSelectedItem();
        if (! parent.equals(root)) {
            DeleteTreeItemInDBRecursiveAction(parent);
            parent.getParent().getChildren().remove(parent);
        }
        LoadRecipeToGraphicsWithoutEdit();
    }
    private void onCopy(ActionEvent action) {
        
    }
    private void onCut(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem)treeView.getSelectionModel().getSelectedItem();
        if (parent != null /*&& parent.isLeaf()*/) {
            SelectedItemID = parent.getItemID();
        }
    }
    private void onPaste(ActionEvent action) {
        RecipeTreeItem parent = (RecipeTreeItem)treeView.getSelectionModel().getSelectedItem();
        if (parent != null && SelectedItemID > 0 && parent.getItemType().equals(TreeItemType.Folder) && notOneOfItsChild(SelectedItemID, parent.getItemID())) {
            controller.getTreeItemById(SelectedItemID).ifPresentOrElse(copied -> {
                copied.setParentID(parent.getItemID());
                controller.saveTreeItem(copied);
            }, () -> {});
            FillTreeFromDB();
        }
        
        SelectedItemID = -1;
    }
    private void onSaveClicked(MouseEvent action) {
        controller.getRecipeById(selectedRecipe.getId()).ifPresentOrElse(recipe -> {
            controller.saveRecipe(selectedRecipe);
            LoadRecipeToGraphicsWithoutEdit();
            FillTreeFromDB();
            editorMode.setValue("save");
        }, () -> {

        });
    }
    private void onValidateClicked(MouseEvent action) {
        //Checking if there are many end
        //checking if there are any empty parallel steps
        //checking if there are end at the end

        double total = recipeModel.getParallelSteps()
                .stream()
                .flatMap(item -> item.getSteps().stream())
                .filter(item -> !item.getPhaseName().equals("Start"))
                .filter(item -> !item.getPhaseName().equals("End"))
                .filter(item -> item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim()))
                .map(item -> item.getValueParametersData().get("Percentage %"))
                .reduce(0.0, Double::sum);
        
        if (total == 100 || total == 0.0) {
            List<String> phasesNames = controller.getAllPhases().stream().map(Phase::getName).collect(Collectors.toList());
            recipeModel.setParallelSteps(recipeModel.getParallelSteps()
                    .stream()
                    .map(psm -> {
                        ParallelStepsModel psmt = new ParallelStepsModel();
                        psm.getSteps().forEach(a -> {
                            if (!a.getPhaseName().equals("End") || !a.getPhaseName().equals("Start")) {
                                if (phasesNames.contains(a.getPhaseName())) {
                                    psmt.getSteps().add(a);
                                }
                            }
                        });
                        return psmt;
                    })
                    .filter(e -> e.getSteps().size() > 0)
                    .collect(LinkedList::new, LinkedList::add, LinkedList::addAll));

            ParallelSteps ps = new ParallelSteps();
            ParallelSteps startPs = new ParallelSteps();
            Step endStep = new Step("End", true, mainWindow);
            Step startStep = new Step("Start", true, mainWindow);
            recipeModel.getParallelSteps().add(ps.getModel());
            recipeModel.getParallelSteps().add(0, startPs.getModel());
            ps.getModel().getSteps().add(endStep.getModel());
            startPs.getModel().getSteps().add(startStep.getModel());

            LoadRecipeToGraphicsWithoutEdit();

            editorMode.setValue("validate");
        } else {
            showErrorWindow("Error adding new step", "You can't exceed the maximum number of steps \nAs defined in the recipe configurations.");
        }
    }
    private void onEditClicked(MouseEvent action){
        LoadRecipeToGraphicsWithEdit();
        editorMode.setValue("edit");
    }
    private void onDiscardClicked(MouseEvent action) {
        if (editorMode.getValue().equalsIgnoreCase("edit")) {
            controller.getRecipeById(selectedRecipe.getId()).ifPresentOrElse(recipe -> {
                recipeModel = recipe.getModel();
                selectedRecipe = recipe;
                LoadRecipeToGraphicsWithEdit();
            }, () -> {});
        } else {
            showErrorWindow("Error changing data", "Discarding changes happens only if in Edit mode ...");
        }
    }
    private void onLaunchClicked(MouseEvent action) {
        editorMode.setValue("launch");
    }
    private void onLoadRecipeAtClick(MouseEvent action) {
        try {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2) {
                RecipeTreeItem parent = (RecipeTreeItem) treeView.getSelectionModel().getSelectedItem();
                if (parent != null && parent.isLeaf() && parent.getItemType().equals(TreeItemType.Recipe)) {
                    Recipe recipe = parent.getRecipe();
                    if (recipe.getUnitName().equals(unit)) {
                        recipeModel = recipe.getModel();
                        selectedRecipe = recipe;
                        LoadRecipeToGraphicsWithoutEdit();
                        editorMode.setValue("Monitor");
                    } else {
                        showErrorWindow("Error you selected wrong recipe", "Please select recipe related to " + unit + " ,\nOr close recipe editor and start it again for " + recipe.getUnitName());
                    }
                }
            }
        }catch (Exception e){
            showErrorWindowForException("Error loading recipe", e);
            e.printStackTrace();
        }
    }
    private void onModeChange(ObservableValue<? extends String> observable, String oldValue, String newValue){
        switch (newValue.toLowerCase().trim()) {
            case "edit":
                pane.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, Insets.EMPTY)));
                save.setDisable(true);
                edit.setDisable(true);
                discard.setDisable(false);
                launch.setDisable(true);
                validate.setDisable(false);
                cancel.setDisable(false);
                break;
            case "monitor":
                pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                save.setDisable(true);
                edit.setDisable(false);
                discard.setDisable(true);
                launch.setDisable(false);
                validate.setDisable(true);
                cancel.setDisable(true);
                break;
            case "save":
                pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                save.setDisable(true);
                edit.setDisable(false);
                discard.setDisable(true);
                launch.setDisable(true);
                validate.setDisable(true);
                cancel.setDisable(true);
                break;
            case "validate":
                pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                save.setDisable(false);
                edit.setDisable(false);
                discard.setDisable(true);
                launch.setDisable(true);
                validate.setDisable(true);
                cancel.setDisable(false);
                break;
            case "launch":
                pane.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                save.setDisable(true);
                edit.setDisable(true);
                discard.setDisable(true);
                launch.setDisable(true);
                validate.setDisable(true);
                cancel.setDisable(true);
                break;
            case "discard":
                pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                save.setDisable(true);
                edit.setDisable(false);
                discard.setDisable(true);
                launch.setDisable(true);
                validate.setDisable(false);
                cancel.setDisable(false);
            case "default":
                save.setDisable(true);
                edit.setDisable(true);
                discard.setDisable(true);
                launch.setDisable(true);
                validate.setDisable(true);
                cancel.setDisable(true);
                break;
        }
    }
    private void onCancelClicked(MouseEvent action) {
        controller.getRecipeById(selectedRecipe.getId()).ifPresentOrElse(recipe -> {
            recipeModel = recipe.getModel();
            selectedRecipe = recipe;
            LoadRecipeToGraphicsWithoutEdit();
            editorMode.setValue("monitor");
        }, () -> {});
    }
    private void onRename(ActionEvent action){
        RecipeTreeItem parent = (RecipeTreeItem) treeView.getSelectionModel().getSelectedItem();
        if (parent != null && parent.getItemType().equals(TreeItemType.Recipe)) {
            String ret = createNameWindow("New Recipe name");
            if (!ret.equalsIgnoreCase("Cancel")) {
                Recipe recipe = parent.getRecipe();
                recipe.setRecipeName(ret);
                controller.saveRecipe(recipe);
                controller.getTreeItemById(parent.getItemID()).ifPresentOrElse(treeItemData -> {
                    treeItemData.setName(ret);
                    controller.saveTreeItem(treeItemData);
                }, () -> {});

            }
        } else if (parent != null && parent.getItemType().equals(TreeItemType.Folder)) {
            String ret = createNameWindow("New Folder name");
            if (!ret.equalsIgnoreCase("Cancel")) {
                controller.getTreeItemById(parent.getItemID()).ifPresentOrElse(treeItemData -> {
                    treeItemData.setName(ret);
                    controller.saveTreeItem(treeItemData);
                }, () -> {});
            }
        }
        FillTreeFromDB();
    }
    
    private void DeleteTreeItemInDBRecursiveAction(RecipeTreeItem item){
        item.getChildren().forEach(subItem -> {
            DeleteTreeItemInDBRecursiveAction(((RecipeTreeItem) subItem));
        });
        controller.deleteTreeItemById(item.getItemID());
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

    private void LoadRecipeToGraphicsWithoutEdit() {
        pane.getChildren().clear();
        for (ParallelStepsModel pSM : recipeModel.getParallelSteps()) {
                ParallelSteps parallStepTemp = new ParallelSteps();
                pane.getChildren().add(parallStepTemp);
                for (StepModel sm : pSM.getSteps()) {
                    Step step = new Step(sm.getPhaseName(), false, mainWindow);
                    step.setModel(sm);
                    parallStepTemp.getChildren().add(step);
                }
        }
    }
    private void LoadRecipeToGraphicsWithEdit() {
        pane.getChildren().clear();
        ParallelSteps parallelStepTemp = new ParallelSteps();
        for (ParallelStepsModel pSM : recipeModel.getParallelSteps()) {
            if (pSM.getSteps().stream().noneMatch(a -> a.getPhaseName().equals("End"))) {
                parallelStepTemp = adjustDragDropActionsForReceivingContainer(parallelStepTemp);
                parallelStepTemp.setModel(pSM);
                pane.getChildren().add(parallelStepTemp);
                for (StepModel sm : pSM.getSteps()) {
                    Step step = new Step(sm.getPhaseName(), true, mainWindow);
                    step.setModel(sm);
                    parallelStepTemp.getChildren().add(step);
                    adjustDragDropActionsForStep(step, parallelStepTemp);
                }
            }
        }
        ParallelSteps ps = new ParallelSteps();
        recipeModel.getParallelSteps().add(ps.getModel());
        adjustDragDropActionsForReceivingContainer(ps);
        pane.getChildren().add(ps);
    }
    
    private boolean notOneOfItsChild(long SelectedItem, long destination) {
        Map<Long, List<TreeViewItemsData>> groupedItemsByParentID =  controller.getAllTreeItems()
                .stream()
                .collect(Collectors.groupingBy(TreeViewItemsData::getParentID, LinkedHashMap::new , Collectors.toCollection(LinkedList::new )));
        return ! notOneOfItsChildRecursiveCheck(destination, SelectedItem, groupedItemsByParentID);
        
    }
    private boolean notOneOfItsChildRecursiveCheck(long destination, long SelectedItem, Map<Long, List<TreeViewItemsData>> groupedItemsByParentID) {
        if (groupedItemsByParentID.get(SelectedItem) != null) {
            for (TreeViewItemsData item : groupedItemsByParentID.get(SelectedItem)) {
                if (item.getId() == destination) {
                    return true;
                } else {
                    if (notOneOfItsChildRecursiveCheck(destination, item.getId(), groupedItemsByParentID)) {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }
    
    private void clearPaneForNewRecipe() {
        ParallelSteps startPane = new ParallelSteps();
        ParallelSteps ParallelStepsZero = new ParallelSteps();
        Step startStep = new Step("Start", false, mainWindow);

        startPane.getChildren().add(startStep);
        startPane.getModel().getSteps().add(startStep.getModel());
        
        pane.getChildren().clear();
        pane.getChildren().addAll(startPane, ParallelStepsZero);
        
        recipeModel.getParallelSteps().add(startPane.getModel());
        recipeModel.getParallelSteps().add(ParallelStepsZero.getModel());
        
        ParallelStepsZero.getChildren().clear();
        ParallelStepsZero.getModel().getSteps().clear();
        
        adjustDragDropActionsForReceivingContainer(ParallelStepsZero);
    }    
    
    private String createNameWindow(String labelString){
        
        Label label = new Label(labelString);
        TextField field = new TextField();
        field.setPromptText("Please enter the name ");
        field.setPrefWidth(350);
        
        
        Button Cancel = new Button("Cancel");
        Button Ok = new Button("Ok");
        
        Cancel.setPrefWidth(150);
        Ok.setPrefWidth(150);
        
        HBox buttonsContainer = new HBox();
        buttonsContainer.getChildren().addAll(Ok, Cancel);
        buttonsContainer.setSpacing(10);
        buttonsContainer.setPadding(new Insets(5));
        
        GridPane container = new GridPane();
        container.add(field, 0, 0);
        container.setPadding(new Insets(5));
        container.setVgap(5);
        container.setHgap(5);
        
        BorderPane root = new BorderPane();
        root.setBottom(buttonsContainer);
        root.setCenter(container);
        root.setTop(label);
        root.setPadding(new Insets(15));
        
        Scene scene = new Scene(root);
        
        Stage stage = new Stage();
        stage.setTitle("Please enter name ");
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(mainWindow);
        stage.initModality(Modality.NONE);
        stage.setScene(scene);

        Cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                returnData = "Cancel";
                stage.close();
            }
        });
        Ok.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!field.getText().isEmpty()) {
                    returnData = field.getText();
                } else {
                    returnData = "Cancel";
                }

                stage.close();
            }
        });
        field.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (!field.getText().isEmpty()) {
                        returnData = field.getText();
                    } else {
                        returnData = "Cancel";
                    }

                    stage.close();
                }
            }
        });
        stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    returnData = "Cancel";
                    stage.close();
                }
            }
        });
        stage.setOnCloseRequest(action -> returnData = "Cancel");
        
        field.requestFocus();
        stage.showAndWait();
        
        
        return returnData;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    private void showErrorWindow(String header, String content){
        Platform.runLater(() -> {
            Alert Error = new Alert(Alert.AlertType.ERROR);
            Error.setTitle("Error ");
            Error.setHeaderText(header);
            Error.setContentText(content);
            Error.initOwner(mainWindow);
            Error.show();
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

    @Override
    public void close() {
        hide();
    }
}