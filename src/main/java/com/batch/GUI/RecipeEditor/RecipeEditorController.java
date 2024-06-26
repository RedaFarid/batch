package com.batch.GUI.RecipeEditor;

import com.batch.Database.Entities.*;
import com.batch.Database.Repositories.*;
import com.batch.Database.Services.RecipeService;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class RecipeEditorController {

    private final ParametersRepository parametersRepository;
    private final PhaseRepository phaseRepository;
    private final RecipeService recipeService;
    private final TreeViewItemsDataRepository treeViewItemsDataRepository;
    private final RecipeConfRepository recipeConfRepository;
    private final MaterialsRepository materialsRepository;
    private final TaskExecutor executor;




    public List<Phase> getAllPhases() {
        return phaseRepository.findAll();
    }
    public List<Phase> getAllPhasesSortedForAUnit(String unit) {
        return phaseRepository.findAll().stream().filter(item -> item.getUnit().equals(unit)).sorted(Comparator.comparing(Phase::getPhaseType)).collect(Collectors.toList());
    }

    public List<Material> getAllMaterials() {
        return materialsRepository.findAll();
    }
    public Optional<Material> getMaterialById(Long id) {
        return materialsRepository.findById(id);
    }

    public RecipeConf getRecipeConfigurations() {
        AtomicReference<RecipeConf> recipeConf = new AtomicReference<>(new RecipeConf());
        recipeConfRepository.findAll().stream().findAny().ifPresentOrElse(recipeConf::set, () -> recipeConf.set(recipeConfRepository.save(new RecipeConf())));
        return recipeConf.get();
    }

    public List<TreeViewItemsData> getAllTreeItems() {
        return treeViewItemsDataRepository.findAll();
    }
    public TreeViewItemsData saveTreeItem(TreeViewItemsData treeViewItemsData) {
        return treeViewItemsDataRepository.save(treeViewItemsData);
    }
    public Optional<TreeViewItemsData> getTreeItemById(Long id) {
        return treeViewItemsDataRepository.findById(id);
    }
    public void deleteTreeItemById(long itemID) {
        treeViewItemsDataRepository.deleteById(itemID);
    }

    public Recipe createNewRecipe(Recipe recipe) {
        return recipeService.save(recipe);
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeService.findById(id);
    }
    public void saveRecipe(Recipe selectedRecipe) {
        recipeService.save(selectedRecipe);
    }

    public void execute(Task<Boolean> task) {
        executor.execute(task);
    }
}
