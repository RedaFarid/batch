
package com.batch.GUI.RecipeEditor;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Material;
import com.batch.Database.Entities.Phase;
import com.batch.Database.Entities.Recipe;
import com.batch.Database.Entities.RecipeConf;
import com.batch.Database.Entities.TreeViewItemsData;
import com.batch.Database.Repositories.MaterialsRepository;
import com.batch.Database.Repositories.ParametersRepository;
import com.batch.Database.Repositories.PhaseRepository;
import com.batch.Database.Repositories.RecipeConfRepository;
import com.batch.Database.Repositories.TreeViewItemsDataRepository;
import com.batch.Database.Services.RecipeService;
import com.batch.GUI.InitialWindow.InitialWindow;
import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;

@Controller
public class RecipeEditorController {
    private static final Logger log = LogManager.getLogger(RecipeEditorController.class);
    private final ParametersRepository parametersRepository;
    private final PhaseRepository phaseRepository;
    private final RecipeService recipeService;
    private final TreeViewItemsDataRepository treeViewItemsDataRepository;
    private final RecipeConfRepository recipeConfRepository;
    private final MaterialsRepository materialsRepository;
    private final TaskExecutor executor;

    public List<Phase> getAllPhases() {
        return Lists.newArrayList(this.phaseRepository.findAll());
    }

    public List<Phase> getAllPhasesSortedForAUnit(String unit) {
        return Lists.newArrayList(this.phaseRepository.findAll()).stream().filter((item) -> item.getUnit().equals(unit)).sorted(Comparator.comparing(Phase::getPhaseType)).collect(Collectors.toList());
    }

    public List<Material> getAllMaterials() {
        return Lists.newArrayList(this.materialsRepository.findAll());
    }

    public Optional<Material> getMaterialById(Long id) {
        return this.materialsRepository.findById(id);
    }

    public RecipeConf getRecipeConfigurations() {
        AtomicReference<RecipeConf> recipeConf = new AtomicReference(new RecipeConf());
        Optional<RecipeConf> anyRecipeConf = Lists.newArrayList(this.recipeConfRepository.findAll()).stream().findAny();
        Objects.requireNonNull(recipeConf);
        anyRecipeConf.ifPresentOrElse(recipeConf::set, () -> recipeConf.set((RecipeConf)this.recipeConfRepository.save(new RecipeConf())));
        log.error(recipeConf);
        return (RecipeConf)recipeConf.get();
    }

    public List<TreeViewItemsData> getAllTreeItems() {
        return Lists.newArrayList(this.treeViewItemsDataRepository.findAll());
    }

    public TreeViewItemsData saveTreeItem(TreeViewItemsData treeViewItemsData) {
        return (TreeViewItemsData)this.treeViewItemsDataRepository.save(treeViewItemsData);
    }

    public Optional<TreeViewItemsData> getTreeItemById(Long id) {
        return this.treeViewItemsDataRepository.findById(id);
    }

    public void deleteTreeItemById(long itemID) {
        this.treeViewItemsDataRepository.deleteById(itemID);
    }

    public Optional<Recipe> createNewRecipe(Recipe recipe) {
        return this.recipeService.save(recipe);
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return this.recipeService.findById(id);
    }

    public void saveRecipe(Recipe selectedRecipe) {
        this.recipeService.save(selectedRecipe);
    }

    public ReadOnlyBooleanProperty getRecipesMapToFillTreeView(Consumer<LinkedHashMap<Long, List<TreeViewItemsData>>> consumer) {
        Task<LinkedHashMap<Long, List<TreeViewItemsData>>> longListLinkedHashMapTask = this.getRecipesMapTask();
        ReadOnlyBooleanProperty readOnlyBooleanProperty = longListLinkedHashMapTask.runningProperty();
        longListLinkedHashMapTask.setOnSucceeded((event) -> {
            try {
                consumer.accept((LinkedHashMap)longListLinkedHashMapTask.get());
            } catch (ExecutionException | InterruptedException e) {
                ApplicationContext.applicationContext.publishEvent(new InitialWindow.ExceptionWindowRequestEvent(new InitialWindow.ExceptionData(e, "Error getting recipes")));
            }

        });
        this.executor.execute(longListLinkedHashMapTask);
        return readOnlyBooleanProperty;
    }

    public ReturnData<LinkedHashMap<Long, List<TreeViewItemsData>>> getRecipesMap() {
        try {
            Task<LinkedHashMap<Long, List<TreeViewItemsData>>> longListLinkedHashMapTask = this.getRecipesMapTask();
            ReadOnlyBooleanProperty readOnlyBooleanProperty = longListLinkedHashMapTask.runningProperty();
            this.executor.execute(longListLinkedHashMapTask);
            return new ReturnData<LinkedHashMap<Long, List<TreeViewItemsData>>>(true, readOnlyBooleanProperty, (LinkedHashMap)longListLinkedHashMapTask.get(), (Exception)null);
        } catch (Exception e) {
            return new ReturnData<LinkedHashMap<Long, List<TreeViewItemsData>>>(false, (ReadOnlyBooleanProperty)null, null, e);
        }
    }

    private Task<LinkedHashMap<Long, List<TreeViewItemsData>>> getRecipesMapTask() {
        return new Task<LinkedHashMap<Long, List<TreeViewItemsData>>>() {
            protected LinkedHashMap<Long, List<TreeViewItemsData>> call() throws Exception {
                return (LinkedHashMap)RecipeEditorController.this.getAllTreeItems().stream().collect(Collectors.groupingBy(TreeViewItemsData::getParentID, LinkedHashMap::new, Collectors.toCollection(LinkedList::new)));
            }
        };
    }

    public void execute(Task<Boolean> task) {
        this.executor.execute(task);
    }

    public RecipeEditorController(final ParametersRepository parametersRepository, final PhaseRepository phaseRepository, final RecipeService recipeService, final TreeViewItemsDataRepository treeViewItemsDataRepository, final RecipeConfRepository recipeConfRepository, final MaterialsRepository materialsRepository, final TaskExecutor executor) {
        this.parametersRepository = parametersRepository;
        this.phaseRepository = phaseRepository;
        this.recipeService = recipeService;
        this.treeViewItemsDataRepository = treeViewItemsDataRepository;
        this.recipeConfRepository = recipeConfRepository;
        this.materialsRepository = materialsRepository;
        this.executor = executor;
    }

    public static record ReturnData<T>(boolean ok, ReadOnlyBooleanProperty readOnlyBooleanProperty, T object, Exception e) {
    }
}
