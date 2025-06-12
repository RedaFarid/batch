
package com.batch.GUI.BatchWindow;

import com.batch.DTO.BatchSystemDataDefinitions.BatchOrders;
import com.batch.DTO.BatchSystemDataDefinitions.BatchParallelStepsModel;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStepModel;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.BatchControllerData;
import com.batch.Database.Entities.Material;
import com.batch.Database.Entities.Phase;
import com.batch.Database.Entities.Recipe;
import com.batch.Database.Entities.RecipeConf;
import com.batch.Database.Entities.TreeViewItemsData;
import com.batch.Database.Repositories.MaterialsRepository;
import com.batch.Database.Repositories.PhaseRepository;
import com.batch.Database.Repositories.TreeViewItemsDataRepository;
import com.batch.Database.Services.BatchControllerDataService;
import com.batch.Database.Services.BatchesService;
import com.batch.Database.Services.RecipeConfigService;
import com.batch.Database.Services.RecipeService;
import com.batch.GUI.RecipeEditor.RecipeEditorController;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class BatchesController {
    private static final Logger log = LogManager.getLogger(BatchesController.class);
    private final BatchesModel model = new BatchesModel();
    private final TreeViewItemsDataRepository treeViewItemsDataRepository;
    private final RecipeService recipeService;
    private final BatchesService batchesService;
    private final RecipeConfigService recipeConfigService;
    private final MaterialsRepository materialsRepository;
    private final PhaseRepository phaseRepository;
    private final BatchControllerDataService batchControllerDataService;
    private final RecipeEditorController recipeEditorController;

    public BatchesModel getModel() {
        return this.model;
    }

    public ReadOnlyBooleanProperty getRecipesMapToFillTreeView(Consumer<LinkedHashMap<Long, List<TreeViewItemsData>>> consumer) {
        return this.recipeEditorController.getRecipesMapToFillTreeView(consumer);
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

    public Optional<Recipe> getRecipeById(Long id) {
        return this.recipeService.findById(id);
    }

    public Optional<Batch> findBatchByName(String batchName) {
        return this.batchesService.findByName(batchName);
    }

    public Optional<RecipeConf> getRecipeConfig() {
        return this.recipeConfigService.findAll().stream().findAny();
    }

    public Optional<Batch> createNewBatch(Batch createdBatchNew) {
        return this.batchesService.save(createdBatchNew);
    }

    public Optional<Batch> getBatchByID(Long id) {
        return this.batchesService.findById(id);
    }

    public Optional<Material> getMaterialByName(long id) {
        return this.materialsRepository.findById(id);
    }

    public List<Phase> getAllPhases() {
        return Lists.newArrayList(this.phaseRepository.findAll());
    }

    @Transactional
    public void controlWholeBatch(String unitName, Long batchId, String control) {
        this.batchesService.updateBatchControlOrder(batchId, control);
        this.batchControllerDataService.updateLockGeneralControl(false, unitName);
    }

    public Optional<BatchControllerData> getBatchControllerDataForUnit(String unitName) {
        return this.batchControllerDataService.findById(unitName);
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE
    )
    public void onControlBatchStep(long batchID, int parallelStepNumber, int stepNumber, String control) {
        this.batchesService.findById(batchID).ifPresent((batch) -> {
            this.batchControllerDataService.updateLockGeneralControl(true, batch.getUnitName());
            ((BatchStepModel)((BatchParallelStepsModel)batch.getModel().getParallelSteps().get(parallelStepNumber)).getSteps().get(stepNumber)).setOrder(control);
            this.batchesService.save(batch);
        });
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE
    )
    public void closeBatch(Batch batch) {
        this.UpdateBatchControlOrder(batch.getId(), BatchOrders.Close.name());
        this.updateLockGeneralControl(false, batch.getUnitName());
        this.batchesService.updateEndTime(batch.getId(), LocalDateTime.now());
    }

    public void updateLockGeneralControl(boolean b, String unitName) {
        this.batchControllerDataService.updateLockGeneralControl(b, unitName);
    }

    public void UpdateBatchControlOrder(Long id, String name) {
        this.batchesService.updateBatchControlOrder(id, name);
    }

    public void updateBatchControllerData(BatchControllerData batchControllerData) {
        this.batchControllerDataService.save(batchControllerData);
    }

    public void updateBatch(Batch batch) {
        this.batchesService.save(batch);
    }

    public void createBatchControllerData(BatchControllerData data) {
        this.batchControllerDataService.save(data);
    }

    public Optional<String> parseRecipeToDetailsString(Recipe selectedRecipe) {
        try {
            Optional<String> reduce = selectedRecipe.getModel().getParallelSteps().stream().flatMap((psm) -> psm.getSteps().stream()).filter((stepModel) -> stepModel.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " "))).map((sm) -> new RecipeDetails(sm.getPhaseID(), sm.getPhaseType(), sm.getPhaseName(), sm.getMaterialID(), (String)this.getMaterialByName(sm.getMaterialID()).map(Material::getName).orElse("not found material"), (Double)sm.getValueParametersData().get("Percentage %"))).map(RecipeDetails::getDetailsString).reduce((a, b) -> a + "\n" + b);
            if (reduce.isEmpty()) {
                reduce = Optional.of("No dose phases in this recipe");
            }

            return reduce;
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.of("Can not analyze recipe to extract details");
        }
    }

    public BatchesController(final TreeViewItemsDataRepository treeViewItemsDataRepository, final RecipeService recipeService, final BatchesService batchesService, final RecipeConfigService recipeConfigService, final MaterialsRepository materialsRepository, final PhaseRepository phaseRepository, final BatchControllerDataService batchControllerDataService, final RecipeEditorController recipeEditorController) {
        this.treeViewItemsDataRepository = treeViewItemsDataRepository;
        this.recipeService = recipeService;
        this.batchesService = batchesService;
        this.recipeConfigService = recipeConfigService;
        this.materialsRepository = materialsRepository;
        this.phaseRepository = phaseRepository;
        this.batchControllerDataService = batchControllerDataService;
        this.recipeEditorController = recipeEditorController;
    }

    private static class RecipeDetails {
        private long recipeId;
        private String phaseType;
        private String phaseName;
        private long matId;
        private String matName;
        private double qty;

        public String getDetailsString() {
            return "ID = %-5d  PhaseType = %-15S  PhaseName = %-35S  MaterialID = %-5d  MaterialName = %-25S  Quantity = %-4f ".formatted(this.recipeId, this.phaseType, this.phaseName, this.matId, this.matName, this.qty);
        }

        public long getRecipeId() {
            return this.recipeId;
        }

        public String getPhaseType() {
            return this.phaseType;
        }

        public String getPhaseName() {
            return this.phaseName;
        }

        public long getMatId() {
            return this.matId;
        }

        public String getMatName() {
            return this.matName;
        }

        public double getQty() {
            return this.qty;
        }

        public void setRecipeId(final long recipeId) {
            this.recipeId = recipeId;
        }

        public void setPhaseType(final String phaseType) {
            this.phaseType = phaseType;
        }

        public void setPhaseName(final String phaseName) {
            this.phaseName = phaseName;
        }

        public void setMatId(final long matId) {
            this.matId = matId;
        }

        public void setMatName(final String matName) {
            this.matName = matName;
        }

        public void setQty(final double qty) {
            this.qty = qty;
        }

        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof RecipeDetails)) {
                return false;
            } else {
                RecipeDetails other = (RecipeDetails)o;
                if (!other.canEqual(this)) {
                    return false;
                } else if (this.getRecipeId() != other.getRecipeId()) {
                    return false;
                } else if (this.getMatId() != other.getMatId()) {
                    return false;
                } else if (Double.compare(this.getQty(), other.getQty()) != 0) {
                    return false;
                } else {
                    Object this$phaseType = this.getPhaseType();
                    Object other$phaseType = other.getPhaseType();
                    if (this$phaseType == null) {
                        if (other$phaseType != null) {
                            return false;
                        }
                    } else if (!this$phaseType.equals(other$phaseType)) {
                        return false;
                    }

                    Object this$phaseName = this.getPhaseName();
                    Object other$phaseName = other.getPhaseName();
                    if (this$phaseName == null) {
                        if (other$phaseName != null) {
                            return false;
                        }
                    } else if (!this$phaseName.equals(other$phaseName)) {
                        return false;
                    }

                    Object this$matName = this.getMatName();
                    Object other$matName = other.getMatName();
                    if (this$matName == null) {
                        if (other$matName != null) {
                            return false;
                        }
                    } else if (!this$matName.equals(other$matName)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(final Object other) {
            return other instanceof RecipeDetails;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            long $recipeId = this.getRecipeId();
            result = result * 59 + (int)($recipeId >>> 32 ^ $recipeId);
            long $matId = this.getMatId();
            result = result * 59 + (int)($matId >>> 32 ^ $matId);
            long $qty = Double.doubleToLongBits(this.getQty());
            result = result * 59 + (int)($qty >>> 32 ^ $qty);
            Object $phaseType = this.getPhaseType();
            result = result * 59 + ($phaseType == null ? 43 : $phaseType.hashCode());
            Object $phaseName = this.getPhaseName();
            result = result * 59 + ($phaseName == null ? 43 : $phaseName.hashCode());
            Object $matName = this.getMatName();
            result = result * 59 + ($matName == null ? 43 : $matName.hashCode());
            return result;
        }

        public String toString() {
            long var10000 = this.getRecipeId();
            return "BatchesController.RecipeDetails(recipeId=" + var10000 + ", phaseType=" + this.getPhaseType() + ", phaseName=" + this.getPhaseName() + ", matId=" + this.getMatId() + ", matName=" + this.getMatName() + ", qty=" + this.getQty() + ")";
        }

        public RecipeDetails(final long recipeId, final String phaseType, final String phaseName, final long matId, final String matName, final double qty) {
            this.recipeId = recipeId;
            this.phaseType = phaseType;
            this.phaseName = phaseName;
            this.matId = matId;
            this.matName = matName;
            this.qty = qty;
        }

        public RecipeDetails() {
        }
    }
}
