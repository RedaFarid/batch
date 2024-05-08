package com.batch.Services.BatchController;


import com.batch.DTO.BatchSystemDataDefinitions.BatchOrders;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStates;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStepModel;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.Database.Entities.Batch;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@Service
@RequiredArgsConstructor
public class BatchService {

    private int counter;

    private int batchHeight = 0;
    private int currentParallelStep = 0;
    private long currentBatchId = 0;
    private String unitName;
    private boolean controlBit = false;

    
    private final ModBusService modBusService;
    private final BatchesService batchesService;
    private final BatchControllerDataService batchControllerDataService;
    private final RecipeConfigService recipeConfigService;
    private final PhaseRepository phaseRepository;
    private final PLCDataDefinitionFactory plcDataDefinitionFactory;


    @Scheduled(fixedDelay = 100)
    public void run() {
        try {
            if (modBusService.getConnectionStatus().getValue()) {
                batchControllerDataService.findAll().forEach(data -> {
                    try {
                        currentParallelStep = data.getCurrentParallelStepsNo();
                        currentBatchId = data.getRunningBatchID();
                        controlBit = data.isControlBit();
                        if (currentBatchId > 0) {
                            batchesService.findById(currentBatchId).ifPresentOrElse(onLineBatch -> {
                                batchHeight = onLineBatch.getModel().getParallelSteps().size();
                                unitName = onLineBatch.getUnitName();
                                if (batchHeight > (currentParallelStep + 1)) {
                                    //At starting new batch
                                    if (currentParallelStep == 0) {
                                        updateCurrentBatchFromPLC(onLineBatch, 1, unitName);

                                        List<BatchStepModel> currentBatchSteps = onLineBatch.getModel().getParallelSteps().get(1).getSteps()
                                                .stream()
                                                .filter(item -> !item.getPhaseName().equals("Start"))
                                                .filter(item -> !item.getPhaseName().equals("End"))
                                                .collect(Collectors.toList());
                                        closeSteps(currentBatchSteps);
                                        updatePLCFromCurrentBatch(onLineBatch, 1, unitName);

                                        boolean idle = onLineBatch.getModel().getParallelSteps().get(1).getSteps()
                                                .stream()
                                                .filter(item -> !item.getPhaseName().equals("Start"))
                                                .filter(item -> !item.getPhaseName().equals("End"))
                                                .map(BatchStepModel::getState)
                                                .allMatch(item -> item.equals(BatchStates.Idle.name()));
                                        if (idle) {
                                            data.setCurrentParallelStepsNo(1);
                                            data.setRunningBatchID(currentBatchId);
                                            batchControllerDataService.save(data);
                                        }
                                        return;
                                    }

                                    //-------------------Normal subroutine------------------------------
                                    //Getting data from PLC to update batch on database
                                    updateCurrentBatchFromPLC(onLineBatch, currentParallelStep, unitName);

                                    //Controlling the batch steps
                                    if (!data.isLockGeneralControl()) {
                                        List<BatchStepModel> currentBatchSteps = onLineBatch.getModel().getParallelSteps().get(currentParallelStep).getSteps()
                                                .stream()
                                                .filter(item -> !item.getPhaseName().equals("Start"))
                                                .filter(item -> !item.getPhaseName().equals("End"))
                                                .collect(Collectors.toList());
                                        boolean finished = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch(item -> item.equals(BatchStates.Finished.name()));
                                        boolean idle = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch(item -> item.equals(BatchStates.Idle.name()));
                                        boolean created = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch(item -> item.equals(BatchStates.Created.name()));
                                        if ((finished) & !controlBit) {
                                            currentParallelStep++;
                                            closeStepsOfRegardingParallelStepNo(onLineBatch, currentParallelStep);
                                            controlBit = true;
                                        } else if (controlBit) {
                                            if (idle) {
                                                controlBit = false;
                                            }
                                        } else {
                                            switch (onLineBatch.getOrder()) {
                                                case ("Start") -> startSteps(currentBatchSteps);
                                                case ("Hold") -> holdSteps(currentBatchSteps);
                                                case ("Abort") -> AbortSteps(currentBatchSteps);
                                                case ("Create") -> createSteps(currentBatchSteps);
                                                case ("Resume") -> resumeSteps(currentBatchSteps);
                                                case ("Close") -> onCloseBatch(onLineBatch, currentBatchId, currentParallelStep);
                                            }
                                        }
                                    } else {
                                        List<BatchStepModel> currentBatchSteps = onLineBatch.getModel().getParallelSteps().get(currentParallelStep).getSteps()
                                                .stream()
                                                .filter(item -> !item.getPhaseName().equals("Start"))
                                                .filter(item -> !item.getPhaseName().equals("End"))
                                                .toList();
                                        boolean finished = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch(item -> item.equals(BatchStates.Finished.name()));
                                        boolean idle = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch(item -> item.equals(BatchStates.Idle.name()));
                                        if (finished) {
                                            batchControllerDataService.updateLockGeneralControl(false, unitName);
                                        }
                                    }

                                    //Adjusting data and sending to PLC and updating database
                                    adjustBatchState(onLineBatch, currentParallelStep, batchHeight);
                                    batchesService.save(onLineBatch);

                                    data.setCurrentParallelStepsNo(currentParallelStep);
                                    data.setControlBit(controlBit);
                                    batchControllerDataService.updateForBatchController(data);

                                    updatePLCFromCurrentBatch(onLineBatch, currentParallelStep, unitName);

                                } else {
                                    data.setCurrentParallelStepsNo(0);
                                    data.setRunningBatchID(0L);
                                    batchControllerDataService.save(data);
                                }
                            }, () -> {
                                data.setCurrentParallelStepsNo(0);
                                data.setRunningBatchID(0L);
                                batchControllerDataService.save(data);
                            });
                        } else {
                            clearRowDataDefinition(data.getUnit());
                        }

                    } catch (Exception e) {
                        log.fatal(e, e);
                    }
                });
            }
        } catch (Exception e) {
            log.fatal(e, e);
        }
    }


    private void startSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach(step -> {
            step.setOrder(BatchOrders.Start.name());
        });
    }
    private void holdSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach(step -> {
            step.setOrder(BatchOrders.Hold.name());
        });
    }
    private void AbortSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach(step -> {
            step.setOrder(BatchOrders.Abort.name());
        });
    }
    private void createSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach(step -> {
            step.setOrder(BatchOrders.Create.name());
        });
    }
    private void resumeSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach(step -> {
            step.setOrder(BatchOrders.Resume.name());
        });
    }
    private void closeSteps(List<BatchStepModel> currentBatchSteps) {
        currentBatchSteps.forEach(step -> {
            step.setOrder(BatchOrders.Close.name());
        });
    }
    private void onCloseBatch(Batch onLineBatch, long currentBatchId, int currentParallelStep) {
        onLineBatch.setState(BatchStates.Idle.name());
        currentBatchId = 0;
        currentParallelStep = 0;
    }

    private void closeStepsOfRegardingParallelStepNo(Batch onLineBatch, int currentParallelStep) {
        onLineBatch.getModel().getParallelSteps().get(currentParallelStep).getSteps()
                .stream()
                .filter(item -> !item.getPhaseName().equals("Start"))
                .filter(item -> !item.getPhaseName().equals("End"))
                .forEach(step -> {
                    step.setOrder(BatchOrders.Close.name());
                });
    }

    private void adjustBatchState(Batch Batch, int parallelStepsNo, int batchHeight) {
        boolean aborted = false;
        boolean held = false;
        boolean idle = false;
        boolean finished = false;
        if ((parallelStepsNo + 1) <= batchHeight) {
            aborted = Batch.getModel().getParallelSteps().get(parallelStepsNo).getSteps()
                    .stream()
                    .filter(item -> !item.getPhaseName().equals("Start"))
                    .filter(item -> !item.getPhaseName().equals("End"))
                    .map(BatchStepModel::getState)
                    .allMatch(item -> item.equals(BatchStates.Aborted.name()));
            held = Batch.getModel().getParallelSteps().get(parallelStepsNo).getSteps()
                    .stream()
                    .filter(item -> !item.getPhaseName().equals("Start"))
                    .filter(item -> !item.getPhaseName().equals("End"))
                    .map(BatchStepModel::getState)
                    .allMatch(item -> (item.equals(BatchStates.Held.name()) || (item.equals(BatchStates.Finished.name()))));
            idle = Batch.getModel().getParallelSteps().get(parallelStepsNo).getSteps()
                    .stream()
                    .filter(item -> !item.getPhaseName().equals("Start"))
                    .filter(item -> !item.getPhaseName().equals("End"))
                    .map(BatchStepModel::getState)
                    .allMatch(item -> item.equals(BatchStates.Idle.name()));
            finished = Batch.getModel().getParallelSteps().get(parallelStepsNo).getSteps()
                    .stream()
                    .filter(item -> !item.getPhaseName().equals("Start"))
                    .filter(item -> !item.getPhaseName().equals("End"))
                    .map(BatchStepModel::getState)
                    .allMatch(item -> item.equals(BatchStates.Finished.name()));
        }

        boolean allFinished = Batch
                .getModel()
                .getParallelSteps()
                .stream()
                .flatMap(item -> item.getSteps().stream())
                .filter(item -> !item.getPhaseName().equals("Start"))
                .filter(item -> !item.getPhaseName().equals("End"))
                .map(BatchStepModel::getState)
                .allMatch(item -> item.equals(BatchStates.Finished.name()));

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
        recipeConfigService.findAll().stream().findAny().ifPresent(recipeConfig -> {
            final int maxNumberOfParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxNumberOfParallelSteps > 0) {
                counter = 1;
                batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().forEach(step -> {
                    if (counter <= maxNumberOfParallelSteps) {
                        RowDataDefinition dataDefinition = plcDataDefinitionFactory.getAllDevicesDataModel().get(unitName + " [" + counter + "]");
                        String batchPhaseName = step.getPhaseName();
                        step.getParametersType().forEach(batchParameter -> {
                            try {
                                String batchParameterName = batchParameter.getName();
                                RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unitName + " [" + counter + "]", batchPhaseName, batchParameterName + "IN");
                                if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                    boolean value = ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).getValue();
                                    step.getActualCheckParametersData().replace(batchParameterName, value);
                                } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                    double value = ((RealDataType) dataDefinition.getAllValues().get(attribute)).getValue();
                                    step.getActualvalueParametersData().replace(batchParameterName, value);
                                } else {
                                    System.err.println("DataType error   ");
                                }
                                int phaseNumber = ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseIn)).getValue();
                                int status = ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Status)).getValue();
                                step.setState(getStatusToStep(status));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        counter++;
                    }
                });
            }
        });
    }
    private void updatePLCFromCurrentBatch(Batch batch, int parallelStepNo, String unitName) {
        clearRowDataDefinitionForOperation(batch, parallelStepNo, unitName);
        recipeConfigService.findAll().stream().findAny().ifPresent(recipeConfig -> {
            final int maxNumberOfParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxNumberOfParallelSteps > 0) {
                counter = 1;
                batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().forEach(step -> {
                    if (counter <= maxNumberOfParallelSteps) {
                        RowDataDefinition dataDefinition = plcDataDefinitionFactory.getAllDevicesDataModel().get(unitName + " [" + counter + "]");
                        String batchPhaseName = step.getPhaseName();
                        step.getParametersType().forEach(batchParameter -> {
                            String batchParameterName = batchParameter.getName();
                            RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unitName + " [" + counter + "]", batchPhaseName, batchParameterName + "OUT");
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
                            ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Order)).setValue(getOrderFromStep(step.getOrder()));
                        });
                        counter++;
                    }
                });
            }
        });
    }

    private void clearRowDataDefinition(String unit) {
        recipeConfigService.findAll().stream().findAny().ifPresent(recipeConfig -> {
            final int maxNumberOfParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxNumberOfParallelSteps > 0) {
                for (counter = 1; counter <= maxNumberOfParallelSteps; counter++) {
                    RowDataDefinition dataDefinition = plcDataDefinitionFactory.getAllDevicesDataModel().get(unit + " [" + counter + "]");
                    if (dataDefinition != null) {
                        ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseOut)).setValue(0);
                        ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Order)).setValue(0);
                        phaseRepository.findAll().stream().filter(Phase -> Phase.getUnit().equals(unit)).forEachOrdered(phase -> {
                            String batchPhaseName = phase.getName();
                            phase.getParameters().forEach(batchParameter -> {
                                String batchParameterName = batchParameter.getName();
                                RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unit + " [" + counter + "]", batchPhaseName, batchParameterName + "OUT");
                                if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                    ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).setValue(false);
                                } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                    ((RealDataType) dataDefinition.getAllValues().get(attribute)).setValue(0.0f);
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
        recipeConfigService.findAll().stream().findAny().ifPresent(recipeConfig -> {
            final int maxNumberOfParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxNumberOfParallelSteps > 0) {
                for (counter = 1; counter <= batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().size(); counter++) {
                    RowDataDefinition dataDefinition = plcDataDefinitionFactory.getAllDevicesDataModel().get(unit + " [" + counter + "]");
                    phaseRepository.findAll().stream().filter(Phase -> Phase.getUnit().equals(unit)).filter(Phase -> !batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().stream().map(BatchStepModel::getPhaseName).collect(Collectors.toList()).contains(Phase.getName())).forEachOrdered(phase -> {
                        String batchPhaseName = phase.getName();
                        phase.getParameters().forEach(batchParameter -> {
                            String batchParameterName = batchParameter.getName();
                            RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unit + " [" + counter + "]", batchPhaseName, batchParameterName + "OUT");
                            if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).setValue(false);
                            } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                ((RealDataType) dataDefinition.getAllValues().get(attribute)).setValue(0.0f);
                            } else {
                                System.err.println("DataType error   ");
                            }
                        });
                    });
                }
                for (counter = batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().size() + 1; counter <= maxNumberOfParallelSteps; counter++) {
                    RowDataDefinition dataDefinition = plcDataDefinitionFactory.getAllDevicesDataModel().get(unit + " [" + counter + "]");
                    ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseOut)).setValue(0);
                    ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Order)).setValue(0);
                    phaseRepository.findAll().stream().filter(Phase -> Phase.getUnit().equals(unit)).forEachOrdered(phase -> {
                        String batchPhaseName = phase.getName();
                        phase.getParameters().forEach(batchParameter -> {
                            String batchParameterName = batchParameter.getName();
                            RowAttripute attribute = PhasesAttributes.getAttributes().getAttributeForPhaseAndParameter(unit + " [" + counter + "]", batchPhaseName, batchParameterName + "OUT");
                            if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                ((BooleanDataType) dataDefinition.getAllValues().get(attribute)).setValue(false);
                            } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                ((RealDataType) dataDefinition.getAllValues().get(attribute)).setValue(0.0f);
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
        return switch (status) {
            case 1 -> BatchStates.Idle.name();
            case 2 -> BatchStates.Running.name();
            case 3 -> BatchStates.Held.name();
            case 4 -> BatchStates.Aborted.name();
            case 5 -> BatchStates.Finished.name();
            default -> BatchStates.Created.name();
        };
    }

}
