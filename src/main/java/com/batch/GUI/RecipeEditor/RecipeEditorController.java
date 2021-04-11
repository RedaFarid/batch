package com.batch.GUI.RecipeEditor;

import com.batch.Database.Entities.*;
import com.batch.Database.Repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

//@Log4j2
//@Controller
@RequiredArgsConstructor
public class RecipeEditorController {

    private final ParametersRepository parametersRepository;
    private final PhaseRepository phaseRepository;
    private final RecipesRepository recipesRepository;
    private final TreeViewItemsDataRepository treeViewItemsDataRepository;
    private final RecipeConfRepository recipeConfRepository;
    private final MaterialsRepository materialsRepository;




    public List<Phase> getAllPhases() {
        return phaseRepository.findAll();
    }
    public List<Material> getAllMaterials() {
        return materialsRepository.findAll();
    }
    public Optional<Material> getMaterialById(Long id) {
        return materialsRepository.findById(id);
    }

    public RecipeConf getRecipeConfigurations() {
        AtomicReference<RecipeConf> recipeConf = new AtomicReference<>();
        recipeConfRepository.findAll().stream().findAny().ifPresentOrElse(recipeConf::set, () -> recipeConf.set(recipeConfRepository.save(new RecipeConf())));
        return recipeConf.get();
    }

    public List<TreeViewItemsData> getAllTreeItems() {
        return treeViewItemsDataRepository.findAll();
    }
    public TreeViewItemsData saveTreeItem(TreeViewItemsData treeViewItemsData) {
        return treeViewItemsDataRepository.save(treeViewItemsData);
    }
    public Recipe createNewRecipe(Recipe recipe) {
        return recipesRepository.save(recipe);
    }
    public Optional<TreeViewItemsData> getTreeItemById(Long id) {
        return treeViewItemsDataRepository.findById(id);
    }
    public void deleteTreeItemById(long itemID) {
        treeViewItemsDataRepository.deleteById(itemID);
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipesRepository.findById(id);
    }
    public void saveRecipe(Recipe selectedRecipe) {
        recipesRepository.save(selectedRecipe);
    }


}
