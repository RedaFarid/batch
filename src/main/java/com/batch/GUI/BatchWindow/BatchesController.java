package com.batch.GUI.BatchWindow;

import com.batch.Database.Entities.*;
import com.batch.Database.Repositories.MaterialsRepository;
import com.batch.Database.Repositories.PhaseRepository;
import com.batch.Database.Repositories.TreeViewItemsDataRepository;
import com.batch.Database.Services.BatchControllerDataService;
import com.batch.Database.Services.BatchesService;
import com.batch.Database.Services.RecipeConfigService;
import com.batch.Database.Services.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
public class BatchesController {

    private final BatchesModel model = new BatchesModel();

    private final TreeViewItemsDataRepository treeViewItemsDataRepository;
    private final RecipeService recipeService;
    private final BatchesService batchesService;
    private final RecipeConfigService recipeConfigService;
    private final MaterialsRepository materialsRepository;
    private final PhaseRepository phaseRepository;
    private final BatchControllerDataService batchControllerDataService;

    public BatchesModel getModel() {
        return model;
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

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeService.findById(id);
    }
    public Optional<Batch> findBatchByName(String batchName) {
        return batchesService.findByName(batchName);
    }
    public Optional<RecipeConf> getRecipeConfig() {
        return recipeConfigService.findAll().stream().findAny();
    }
    public Batch createNewBatch(Batch createdBatchNew) {
        return batchesService.save(createdBatchNew);
    }
    public Optional<Batch> getBatchByID(Long id) {
        return batchesService.findById(id);
    }
    public Optional<Material> getMaterialByName(long id) {
        return materialsRepository.findById(id);
    }
    public List<Phase> getAllPhases() {
        return phaseRepository.findAll();
    }
    public void controlWholeBatch(String unitName, Long batchId, String control) {
        batchesService.updateBatchControlOrder(batchId, control);
        batchControllerDataService.updateLockGeneralControl(false, unitName);
    }
    public Optional<BatchControllerData> getBatchControllerDataForUnit(String unitName) {
        return batchControllerDataService.findById(unitName);
    }

    @Transactional
    public void onControlBatchStep(long batchID, int parallelStepNumber, int stepNumber, String control) {
        batchesService.findById(batchID).ifPresent(batch -> {
            batchControllerDataService.updateLockGeneralControl(true, batch.getUnitName());
            batch.getModel().getParallelSteps().get(parallelStepNumber).getSteps().get(stepNumber).setOrder(control);
            batchesService.save(batch);
        });
    }

    //Batch observer functions
    public void updateLockGeneralControl(boolean b, String unitName) {
        batchControllerDataService.updateLockGeneralControl(b, unitName);
    }

    public void UpdateBatchControlOrder(Long id, String name) {
        batchesService.updateBatchControlOrder(id, name);
    }

    public void updateBatchControllerData(BatchControllerData batchControllerData) {
        batchControllerDataService.save(batchControllerData);
    }

    public void updateBatch(Batch batch) {
        batchesService.save(batch);
    }

    public void createBatchControllerData(BatchControllerData data) {
        batchControllerDataService.save(data);
    }
}
