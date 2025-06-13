package com.batch.Services.BatchController;

import com.batch.DTO.BatchSystemDataDefinitions.BatchOrders;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStates;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStepModel;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.BatchControllerData;
import com.batch.Database.Repositories.PhaseRepository;
import com.batch.Database.Services.BatchControllerDataService;
import com.batch.Database.Services.BatchesService;
import com.batch.Database.Services.RecipeConfigService;
import com.batch.PLCDataSource.ModBus.ModBusService;
import com.batch.PLCDataSource.PLC.ComplexDataType.Batches.BatchControl;
import com.batch.PLCDataSource.PLC.ComplexDataType.Batches.PhasesAttributes;
import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.batch.Services.LoggingService.MessageLoggingService;
import com.batch.Services.NotificationService.BackGroundServices;
import com.batch.Services.NotificationService.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchService {
    private final ModBusService modBusService;
    private final BatchesService batchesService;
    private final BatchControllerDataService batchControllerDataService;
    private final RecipeConfigService recipeConfigService;
    private final PhaseRepository phaseRepository;
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;
    private int counter;
    private int batchHeight = 0;
    private int currentParallelStep = 0;
    private long currentBatchId = 0L;
    private String unitName;
    private boolean controlBit = false;
    @Autowired
    @BackGroundServices
    private NotificationService notificationService;

    @Autowired
    private MessageLoggingService log;

    public BatchService(final ModBusService modBusService, final BatchesService batchesService, final BatchControllerDataService batchControllerDataService, final RecipeConfigService recipeConfigService, final PhaseRepository phaseRepository, final PLCDataDefinitionFactory plcDataDefinitionFactory) {
        this.modBusService = modBusService;
        this.batchesService = batchesService;
        this.batchControllerDataService = batchControllerDataService;
        this.recipeConfigService = recipeConfigService;
        this.phaseRepository = phaseRepository;
        this.plcDataDefinitionFactory = plcDataDefinitionFactory;
    }

    @Scheduled(
            initialDelay = 10000L,
            fixedDelay = 100L
    )
    public void run() {
        try {
            if (this.modBusService.getConnectionStatus().getValue()) {
                for (BatchControllerData data : this.batchControllerDataService.findAll()) {
                    try {
                        this.currentParallelStep = data.getCurrentParallelStepsNo();
                        this.currentBatchId = data.getRunningBatchID();
                        this.controlBit = data.isControlBit();
                        if (this.currentBatchId > 0L) {
                            this.batchesService.findById(this.currentBatchId).ifPresentOrElse((onLineBatch) -> {
                                this.batchHeight = onLineBatch.getModel().getParallelSteps().size();
                                this.unitName = onLineBatch.getUnitName();
                                if (this.batchHeight > this.currentParallelStep + 1) {
                                    if (this.currentParallelStep == 0) {
                                        this.updateCurrentBatchFromPLC(onLineBatch, 1, this.unitName);
                                        List<BatchStepModel> currentBatchSteps = onLineBatch.getModel().getParallelSteps().get(1).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).collect(Collectors.toList());
                                        this.closeSteps(currentBatchSteps);
                                        this.updatePLCFromCurrentBatch(onLineBatch, 1, this.unitName);
                                        boolean idle = onLineBatch.getModel().getParallelSteps().get(1).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Idle.name()));
                                        if (idle) {
                                            data.setCurrentParallelStepsNo(1);
                                            data.setRunningBatchID(this.currentBatchId);
                                            this.batchControllerDataService.save(data);
                                        }

                                        return;
                                    }

                                    this.updateCurrentBatchFromPLC(onLineBatch, this.currentParallelStep, this.unitName);
                                    if (!data.isLockGeneralControl()) {
                                        List<BatchStepModel> currentBatchSteps = onLineBatch.getModel().getParallelSteps().get(this.currentParallelStep).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).collect(Collectors.toList());
                                        boolean finished = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Finished.name()));
                                        boolean idle = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Idle.name()));
                                        boolean created = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Created.name()));
                                        if (finished & !this.controlBit) {
                                            ++this.currentParallelStep;
                                            this.closeStepsOfRegardingParallelStepNo(onLineBatch, this.currentParallelStep);
                                            this.controlBit = true;
                                        } else if (this.controlBit) {
                                            if (idle) {
                                                this.controlBit = false;
                                            }
                                        } else {
                                            switch (onLineBatch.getOrder()) {
                                                case "Start" -> this.startSteps(currentBatchSteps);
                                                case "Hold" -> this.holdSteps(currentBatchSteps);
                                                case "Abort" -> this.AbortSteps(currentBatchSteps);
                                                case "Create" -> this.createSteps(currentBatchSteps);
                                                case "Resume" -> this.resumeSteps(currentBatchSteps);
                                                case "Close" -> this.onCloseBatch(onLineBatch);
                                            }
                                        }
                                    } else {
                                        List<BatchStepModel> currentBatchSteps = onLineBatch.getModel().getParallelSteps().get(this.currentParallelStep).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).collect(Collectors.toList());
                                        boolean finished = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Finished.name()));
                                        boolean idle = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Idle.name()));
                                        if (finished) {
                                            this.batchControllerDataService.updateLockGeneralControl(false, this.unitName);
                                        }
                                    }

                                    this.adjustBatchState(onLineBatch, this.currentParallelStep, this.batchHeight);
                                    this.batchesService.save(onLineBatch);
                                    data.setCurrentParallelStepsNo(this.currentParallelStep);
                                    data.setControlBit(this.controlBit);
                                    this.batchControllerDataService.updateForBatchController(data);
                                    this.updatePLCFromCurrentBatch(onLineBatch, this.currentParallelStep, this.unitName);
                                } else {
                                    data.setCurrentParallelStepsNo(0);
                                    data.setRunningBatchID(0L);
                                    this.batchControllerDataService.save(data);
                                }

                            }, () -> {
                                data.setCurrentParallelStepsNo(0);
                                data.setRunningBatchID(0L);
                                this.batchControllerDataService.save(data);
                            });
                        } else {
                            this.clearRowDataDefinition(data.getUnit());
                        }
                    } catch (Exception e) {
                        log.logExcption("BatchService [Run]", e);
                        this.notificationService.newErrorMessage("Batch controller", "Main run inner", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.logExcption("BatchService [Run]", e);
            this.notificationService.newErrorMessage("Batch controller", "Main run", e.getMessage());
        }

    }

    private void startSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach((step) -> step.setOrder(BatchOrders.Start.name()));
    }

    private void holdSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach((step) -> step.setOrder(BatchOrders.Hold.name()));
    }

    private void AbortSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach((step) -> step.setOrder(BatchOrders.Abort.name()));
    }

    private void createSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach((step) -> step.setOrder(BatchOrders.Create.name()));
    }

    private void resumeSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach((step) -> step.setOrder(BatchOrders.Resume.name()));
    }

    private void closeSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach((step) -> step.setOrder(BatchOrders.Close.name()));
    }

    private void onCloseBatch(Batch onLineBatch) {
        onLineBatch.setState(BatchStates.Idle.name());
        this.currentBatchId = 0L;
        this.currentParallelStep = 0;
    }

    private void closeStepsOfRegardingParallelStepNo(Batch onLineBatch, int currentParallelStep) {
        onLineBatch.getModel().getParallelSteps().get(currentParallelStep).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).forEach((step) -> step.setOrder(BatchOrders.Close.name()));
    }

    private void adjustBatchState(Batch Batch, int parallelStepsNo, int batchHeight) {
        boolean aborted = false;
        boolean held = false;
        boolean idle = false;
        boolean finished = false;
        if (parallelStepsNo + 1 <= batchHeight) {
            aborted = Batch.getModel().getParallelSteps().get(parallelStepsNo).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Aborted.name()));
            held = Batch.getModel().getParallelSteps().get(parallelStepsNo).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Held.name()) || item.equals(BatchStates.Finished.name()));
            idle = Batch.getModel().getParallelSteps().get(parallelStepsNo).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Idle.name()));
            finished = Batch.getModel().getParallelSteps().get(parallelStepsNo).getSteps().stream().filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Finished.name()));
        }

        boolean allFinished = Batch.getModel().getParallelSteps().stream().flatMap((item) -> item.getSteps().stream()).filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).map(BatchStepModel::getState).allMatch((item) -> item.equals(BatchStates.Finished.name()));
        if (allFinished) {
            Batch.setState(BatchStates.Finished.name());
        } else if (finished) {
            Batch.setState(BatchStates.Created.name());
        } else if (held) {
            Batch.setState(BatchStates.Held.name());
        } else if (aborted) {
            Batch.setState(BatchStates.Aborted.name());
        } else if (idle) {
            Batch.setState(BatchStates.Idle.name());
        } else {
            Batch.setState(BatchStates.Running.name());
        }

    }

    private void updateCurrentBatchFromPLC(Batch batch, int parallelStepNo, String unitName) {
        this.recipeConfigService.findAll().stream().findAny().ifPresent((recipeConfig) -> {
            int maxNumberOfParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxNumberOfParallelSteps > 0) {
                this.counter = 1;
                batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().forEach((step) -> {
                    if (this.counter <= maxNumberOfParallelSteps) {
                        RowDataDefinition dataDefinition = this.plcDataDefinitionFactory.getAllDevicesDataModel().get(unitName + " [" + this.counter + "]");
                        String batchPhaseName = step.getPhaseName();
                        step.getParametersType().forEach((batchParameter) -> {
                            try {
                                String batchParameterName = batchParameter.getName();
                                RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unitName + " [" + this.counter + "]", batchPhaseName, batchParameterName + "IN");
                                if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                    boolean value = ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).getValue();
                                    step.getActualCheckParametersData().replace(batchParameterName, value);
                                } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                    double value = (double) ((RealDataType) dataDefinition.getAllValues().get(attribute)).getValue();
                                    step.getActualvalueParametersData().replace(batchParameterName, value);
                                } else {
                                    System.err.println("DataType error   ");
                                }

                                int phaseNumber = ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseIn)).getValue();
                                int status = ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Status)).getValue();
                                step.setState(this.getStatusToStep(status));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                        ++this.counter;
                    }

                });
            }

        });
    }

    private void updatePLCFromCurrentBatch(Batch batch, int parallelStepNo, String unitName) {
        this.clearRowDataDefinitionForOperation(batch, parallelStepNo, unitName);
        this.recipeConfigService.findAll().stream().findAny().ifPresent((recipeConfig) -> {
            int maxNumberOfParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxNumberOfParallelSteps > 0) {
                this.counter = 1;
                batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().forEach((step) -> {
                    if (this.counter <= maxNumberOfParallelSteps) {
                        RowDataDefinition dataDefinition = this.plcDataDefinitionFactory.getAllDevicesDataModel().get(unitName + " [" + this.counter + "]");
                        String batchPhaseName = step.getPhaseName();
                        step.getParametersType().forEach((batchParameter) -> {
                            String batchParameterName = batchParameter.getName();
                            RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unitName + " [" + this.counter + "]", batchPhaseName, batchParameterName + "OUT");
                            if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                boolean value = step.getCheckParametersData().get(batchParameterName);
                                ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).setValue(value);
                            } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                double value = step.getValueParametersData().get(batchParameterName);
                                ((RealDataType) dataDefinition.getAllValues().get(attribute)).setValue(value);
                            } else {
                                System.err.println("DataType error   ");
                            }

                            ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseOut)).setValue(step.getPhaseID());
                            ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Order)).setValue(this.getOrderFromStep(step.getOrder()));
                        });
                        ++this.counter;
                    }

                });
            }

        });
    }

    private void clearRowDataDefinition(String unit) {
        this.recipeConfigService.findAll().stream().findAny().ifPresent((recipeConfig) -> {
            int maxNumberOfParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxNumberOfParallelSteps > 0) {
                for (this.counter = 1; this.counter <= maxNumberOfParallelSteps; ++this.counter) {
                    RowDataDefinition dataDefinition = this.plcDataDefinitionFactory.getAllDevicesDataModel().get(unit + " [" + this.counter + "]");
                    if (dataDefinition != null) {
                        ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseOut)).setValue(0);
                        ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Order)).setValue(0);
                        Lists.newArrayList(this.phaseRepository.findAll()).stream().filter((Phase) -> Phase.getUnit().equals(unit)).forEachOrdered((phase) -> {
                            String batchPhaseName = phase.getName();
                            phase.getParameters().forEach((batchParameter) -> {
                                String batchParameterName = batchParameter.getName();
                                RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unit + " [" + this.counter + "]", batchPhaseName, batchParameterName + "OUT");
                                if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                    ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).setValue(false);
                                } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                    ((RealDataType) dataDefinition.getAllValues().get(attribute)).setValue(0.0F);
                                } else {
                                    System.err.println("DataType error   ");
                                }

                            });
                        });
                    }
                }
            }

        });
    }

    private void clearRowDataDefinitionForOperation(Batch batch, int parallelStepNo, String unit) {
        this.recipeConfigService.findAll().stream().findAny().ifPresent((recipeConfig) -> {
            int maxNumberOfParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxNumberOfParallelSteps > 0) {
                for (this.counter = 1; this.counter <= batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().size(); ++this.counter) {
                    RowDataDefinition dataDefinition = this.plcDataDefinitionFactory.getAllDevicesDataModel().get(unit + " [" + this.counter + "]");
                    Lists.newArrayList(this.phaseRepository.findAll()).stream().filter((Phase) -> Phase.getUnit().equals(unit)).filter((Phase) -> !(batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().stream().map(BatchStepModel::getPhaseName).collect(Collectors.toList())).contains(Phase.getName())).forEachOrdered((phase) -> {
                        String batchPhaseName = phase.getName();
                        phase.getParameters().forEach((batchParameter) -> {
                            String batchParameterName = batchParameter.getName();
                            RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unit + " [" + this.counter + "]", batchPhaseName, batchParameterName + "OUT");
                            if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).setValue(false);
                            } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                ((RealDataType) dataDefinition.getAllValues().get(attribute)).setValue(0.0F);
                            } else {
                                System.err.println("DataType error   ");
                            }

                        });
                    });
                }

                for (this.counter = batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().size() + 1; this.counter <= maxNumberOfParallelSteps; ++this.counter) {
                    RowDataDefinition dataDefinition = this.plcDataDefinitionFactory.getAllDevicesDataModel().get(unit + " [" + this.counter + "]");
                    ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseOut)).setValue(0);
                    ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Order)).setValue(0);
                    Lists.newArrayList(this.phaseRepository.findAll()).stream().filter((Phase) -> Phase.getUnit().equals(unit)).forEachOrdered((phase) -> {
                        String batchPhaseName = phase.getName();
                        phase.getParameters().forEach((batchParameter) -> {
                            String batchParameterName = batchParameter.getName();
                            RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unit + " [" + this.counter + "]", batchPhaseName, batchParameterName + "OUT");
                            if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).setValue(false);
                            } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                ((RealDataType) dataDefinition.getAllValues().get(attribute)).setValue(0.0F);
                            } else {
                                System.err.println("DataType error   ");
                            }

                        });
                    });
                }
            }

        });
    }

    private int getOrderFromStep(String StepOrder) {
        int ret = 0;
        if (StepOrder.equals(BatchOrders.Abort.name())) {
            ret = 5;
        } else if (StepOrder.equals(BatchOrders.Close.name())) {
            ret = 6;
        } else if (StepOrder.equals(BatchOrders.Create.name())) {
            ret = 1;
        } else if (StepOrder.equals(BatchOrders.Hold.name())) {
            ret = 3;
        } else if (StepOrder.equals(BatchOrders.Resume.name())) {
            ret = 4;
        } else if (StepOrder.equals(BatchOrders.Start.name())) {
            ret = 2;
        } else if (StepOrder.equals(BatchOrders.Finish.name())) {
            ret = 7;
        }

        return ret;
    }

    private String getStatusToStep(int status) {
        String var10000;
        switch (status) {
            case 1 -> var10000 = BatchStates.Idle.name();
            case 2 -> var10000 = BatchStates.Running.name();
            case 3 -> var10000 = BatchStates.Held.name();
            case 4 -> var10000 = BatchStates.Aborted.name();
            case 5 -> var10000 = BatchStates.Finished.name();
            default -> var10000 = BatchStates.Created.name();
        }

        return var10000;
    }
}
