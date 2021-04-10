package com.batch.Services.BatchController;


import com.batch.DTO.BatchSystemDataDefinitions.BatchOrders;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStates;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStepModel;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Repositories.BatchesRepository;
import com.batch.Database.Repositories.PhaseRepository;
import com.batch.Database.Repositories.RecipeConfRepository;
import com.batch.Database.Services.BatchControllerDataService;
import com.batch.PLCDataSource.ModBus.ModBusService;
import com.batch.PLCDataSource.PLC.ComplexDataType.Batches.BatchControl;
import com.batch.PLCDataSource.PLC.ComplexDataType.Batches.PhasesAttriputes;
import com.batch.PLCDataSource.PLC.ComplexDataType.PLCDataDefinitionFactory;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BatchService {

    private int counter, i, j = 0;

    @Autowired
    private ModBusService modBusService;

    @Autowired
    private BatchesRepository batchesRepository;

    @Autowired
    private BatchControllerDataService batchControllerDataService;

    @Autowired
    private RecipeConfRepository recipeConfRepository;

    @Autowired
    private PhaseRepository phaseRepository;


    @Scheduled(fixedDelay = 100)
    public void run() {
        try {
            if (modBusService.getConnectionStatus().getValue()) {
                batchControllerDataService.findAll().forEach(data -> {
                    try {
                        Optional<Batch> onLineBatchOptional;
                        Batch onLineBatch;
                        int batchHeight = 0;
                        int currentParallelStep = 0;
                        long currentBatchId = 0;
                        String unitName;
                        boolean controlBit = false;

                        currentParallelStep = data.getCurrentParallelStepsNo();
                        currentBatchId = data.getRunningBatchID();
                        controlBit = data.isControlBit();
                        if (currentBatchId > 0) {
                            onLineBatchOptional = batchesRepository.findById(currentBatchId);
                            if (onLineBatchOptional.isPresent()) {
                                onLineBatch = onLineBatchOptional.get();
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
                                            batchControllerDataService.update(data);//TODO - check Update method instead of save
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
                                                case ("Start"):
                                                    startSteps(currentBatchSteps);
                                                    break;
                                                case ("Hold"):
                                                    holdSteps(currentBatchSteps);
                                                    break;
                                                case ("Abort"):
                                                    AbortSteps(currentBatchSteps);
                                                    break;
                                                case ("Create"):
                                                    createSteps(currentBatchSteps);
                                                    break;
                                                case ("Resume"):
                                                    resumeSteps(currentBatchSteps);
                                                    break;
                                                case ("Close"):
                                                    onCloseBatch(onLineBatch, currentBatchId, currentParallelStep);
                                                    break;
                                            }
                                        }
                                    } else {
                                        List<BatchStepModel> currentBatchSteps = onLineBatch.getModel().getParallelSteps().get(currentParallelStep).getSteps()
                                                .stream()
                                                .filter(item -> !item.getPhaseName().equals("Start"))
                                                .filter(item -> !item.getPhaseName().equals("End"))
                                                .collect(Collectors.toList());
                                        boolean finished = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch(item -> item.equals(BatchStates.Finished.name()));
                                        boolean idle = currentBatchSteps.stream().map(BatchStepModel::getState).allMatch(item -> item.equals(BatchStates.Idle.name()));
                                        if (finished) {
                                            batchControllerDataService.updateLockGeneralControl(false, unitName);
                                        }
                                    }

                                    //Adjusting data and sending to PLC and updating database
                                    adjustBatchState(onLineBatch, currentParallelStep, batchHeight);
                                    batchesRepository.save(onLineBatch);

                                    data.setCurrentParallelStepsNo(currentParallelStep);
                                    data.setControlBit(controlBit);
                                    batchControllerDataService.updateForBatchController(data);

                                    updatePLCFromCurrentBatch(onLineBatch, currentParallelStep, unitName);

                                } else {
                                    data.setCurrentParallelStepsNo(0);
                                    data.setRunningBatchID(0L);
                                    batchControllerDataService.update(data);
                                }
                            } else {
                                data.setCurrentParallelStepsNo(0);
                                data.setRunningBatchID(0L);
                                batchControllerDataService.update(data);
                            }
                        } else {
                            clearRowDataDefinition(data.getUnit());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void adjustBatchState(Batch Batch, int paralelStepsNo, int batchHeight) {
        boolean aborted = false;
        boolean held = false;
        boolean idle = false;
        boolean finished = false;
        if ((paralelStepsNo + 1) <= batchHeight) {
            aborted = Batch.getModel().getParallelSteps().get(paralelStepsNo).getSteps()
                    .stream()
                    .filter(item -> !item.getPhaseName().equals("Start"))
                    .filter(item -> !item.getPhaseName().equals("End"))
                    .map(BatchStepModel::getState)
                    .allMatch(item -> item.equals(BatchStates.Aborted.name()));
            held = Batch.getModel().getParallelSteps().get(paralelStepsNo).getSteps()
                    .stream()
                    .filter(item -> !item.getPhaseName().equals("Start"))
                    .filter(item -> !item.getPhaseName().equals("End"))
                    .map(BatchStepModel::getState)
                    .allMatch(item -> (item.equals(BatchStates.Held.name()) || (item.equals(BatchStates.Finished.name()))));
            idle = Batch.getModel().getParallelSteps().get(paralelStepsNo).getSteps()
                    .stream()
                    .filter(item -> !item.getPhaseName().equals("Start"))
                    .filter(item -> !item.getPhaseName().equals("End"))
                    .map(BatchStepModel::getState)
                    .allMatch(item -> item.equals(BatchStates.Idle.name()));
            finished = Batch.getModel().getParallelSteps().get(paralelStepsNo).getSteps()
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

//        Batch
//                .getModel()
//                .getParallelSteps()
//                .stream()
//                .flatMap(item -> item.getSteps().stream())
//                .filter(item -> !item.getPhaseName().equals("Start"))
//                .filter(item -> !item.getPhaseName().equals("End"))
//                .forEach(System.err::println);
//        System.err.println("");

        if (allFinished) {
            Batch.setState(BatchStates.Finished.name());
        } else if (!allFinished && finished) {
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
        int maxNumberOfParallelSteps = recipeConfRepository.findAll().stream().findAny().get().getMaxParallelSteps();
        if (maxNumberOfParallelSteps > 0) {
            counter = 1;
            batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().forEach(step -> {
                if (counter <= maxNumberOfParallelSteps) {
                    RowDataDefinition dataDefinition = PLCDataDefinitionFactory.getSystem().getAllDevicesDataModel().get(String.valueOf(unitName + " [" + counter + "]"));
                    String batchPhaseName = step.getPhaseName();
                    step.getParametersType().forEach(batchParameter -> {
                        try {
                            String batchParameterName = batchParameter.getName();
                            RowAttripute attripute = PhasesAttriputes.getAttributes().getAttriputeForPhaseAndParameter(String.valueOf(unitName + " [" + counter + "]"), batchPhaseName, batchParameterName + "IN");
                            if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                boolean value = ((BooleanDataType) dataDefinition.getAllValues().get(attripute)).getValue();
                                step.getActualCheckParametersData().replace(batchParameterName, value);
                            } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                double value = ((RealDataType) dataDefinition.getAllValues().get(attripute)).getValue();
                                step.getActualvalueParametersData().replace(batchParameterName, value);
                            } else {
                                System.err.println("DataType error   ");
                            }
                            int phaseNumber = ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseIn)).getValue();
                            int satus = ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Status)).getValue();
                            step.setState(getStatusToStep(satus));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    counter++;
                }
            });
        }
    }
    private void updatePLCFromCurrentBatch(Batch batch, int parallelStepNo, String unitName) {
        clearRowDataDefinitionForOperation(batch, parallelStepNo, unitName);
        int maxNumberOfParallelSteps = recipeConfRepository.findAll().stream().findAny().get().getMaxParallelSteps();
        if (maxNumberOfParallelSteps > 0) {
            counter = 1;
            batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().forEach(step -> {
                if (counter <= maxNumberOfParallelSteps) {
                    RowDataDefinition dataDefinition = PLCDataDefinitionFactory.getSystem().getAllDevicesDataModel().get(String.valueOf(unitName + " [" + counter + "]"));
                    String batchPhaseName = step.getPhaseName();
                    step.getParametersType().forEach(batchParameter -> {
                        String batchParameterName = batchParameter.getName();
                        RowAttripute attripute = PhasesAttriputes.getAttributes().getAttriputeForPhaseAndParameter(String.valueOf(unitName + " [" + counter + "]"), batchPhaseName, batchParameterName + "OUT");
                        if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                            boolean value = step.getCheckParametersData().get(batchParameterName);
                            ((BooleanDataType) dataDefinition.getAllValues().get(attripute)).setValue(value);
                        } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                            double value = step.getValueParametersData().get(batchParameterName);
                            ((RealDataType) dataDefinition.getAllValues().get(attripute)).setValue(value);
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
    }

    private void clearRowDataDefinition(String unit) {
        int maxNumberOfParallelSteps = recipeConfRepository.findAll().stream().findAny().get().getMaxParallelSteps();
        if (maxNumberOfParallelSteps > 0) {
            for (counter = 1; counter <= maxNumberOfParallelSteps; counter++) {
                RowDataDefinition dataDefinition = PLCDataDefinitionFactory.getSystem().getAllDevicesDataModel().get(String.valueOf(unit + " [" + counter + "]"));
                if (dataDefinition != null) {
                    ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseOut)).setValue(0);
                    ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Order)).setValue(0);
                    phaseRepository.findAll().stream().filter(Phase -> Phase.getUnit().equals(unit)).forEachOrdered(phase -> {
                        String batchPhaseName = phase.getName();
                        phase.getParameters().forEach(batchParameter -> {
                            String batchParameterName = batchParameter.getName();
                            RowAttripute attripute = PhasesAttriputes.getAttributes().getAttriputeForPhaseAndParameter(String.valueOf(unit + " [" + counter + "]"), batchPhaseName, batchParameterName + "OUT");
                            if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                                ((BooleanDataType) dataDefinition.getAllValues().get(attripute)).setValue(false);
                            } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                                ((RealDataType) dataDefinition.getAllValues().get(attripute)).setValue(0.0f);
                            } else {
                                System.err.println("DataType error   ");
                            }
                        });
                    });
                }
            }
        }
    }
    private void clearRowDataDefinitionForOperation(Batch batch, int parallelStepNo, String unit) {
        int maxNumberOfParallelSteps = recipeConfRepository.findAll().stream().findAny().get().getMaxParallelSteps();
        if (maxNumberOfParallelSteps > 0) {
            for (counter = 1; counter <= batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().size(); counter++) {
                RowDataDefinition dataDefinition = PLCDataDefinitionFactory.getSystem().getAllDevicesDataModel().get(String.valueOf(unit + " [" + counter + "]"));
                phaseRepository.findAll().stream().filter(Phase -> Phase.getUnit().equals(unit)).filter(Phase -> !batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().stream().map(step -> step.getPhaseName()).collect(Collectors.toList()).contains(Phase.getName())).forEachOrdered(phase -> {
                    String batchPhaseName = phase.getName();
                    phase.getParameters().forEach(batchParameter -> {
                        String batchParameterName = batchParameter.getName();
                        RowAttripute attripute = PhasesAttriputes.getAttributes().getAttriputeForPhaseAndParameter(String.valueOf(unit + " [" + counter + "]"), batchPhaseName, batchParameterName + "OUT");
                        if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                            ((BooleanDataType) dataDefinition.getAllValues().get(attripute)).setValue(false);
                        } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                            ((RealDataType) dataDefinition.getAllValues().get(attripute)).setValue(0.0f);
                        } else {
                            System.err.println("DataType error   ");
                        }
                    });
                });
            }
            for (counter = batch.getModel().getParallelSteps().get(parallelStepNo).getSteps().size() + 1; counter <= maxNumberOfParallelSteps; counter++) {
                RowDataDefinition dataDefinition = PLCDataDefinitionFactory.getSystem().getAllDevicesDataModel().get(String.valueOf(unit + " [" + counter + "]"));
                ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.PhaseOut)).setValue(0);
                ((IntegerDataType) dataDefinition.getAllValues().get(BatchControl.Order)).setValue(0);
                phaseRepository.findAll().stream().filter(Phase -> Phase.getUnit().equals(unit)).forEachOrdered(phase -> {
                    String batchPhaseName = phase.getName();
                    phase.getParameters().forEach(batchParameter -> {
                        String batchParameterName = batchParameter.getName();
                        RowAttripute attripute = PhasesAttriputes.getAttributes().getAttriputeForPhaseAndParameter(String.valueOf(unit + " [" + counter + "]"), batchPhaseName, batchParameterName + "OUT");
                        if (batchParameter.getType().equals(PhaseParameterType.Check.name())) {
                            ((BooleanDataType) dataDefinition.getAllValues().get(attripute)).setValue(false);
                        } else if (batchParameter.getType().equals(PhaseParameterType.Value.name())) {
                            ((RealDataType) dataDefinition.getAllValues().get(attripute)).setValue(0.0f);
                        } else {
                            System.err.println("DataType error   ");
                        }
                    });
                });
            }
        }
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
        String ret = "";
        switch (status) {
            case 1:
                ret = BatchStates.Idle.name();
                break;
            case 2:
                ret = BatchStates.Running.name();
                break;
            case 3:
                ret = BatchStates.Held.name();
                break;
            case 4:
                ret = BatchStates.Aborted.name();
                break;
            case 5:
                ret = BatchStates.Finished.name();
                break;
            default:
                ret = BatchStates.Created.name();
                break;
        }
        return ret;
    }

}
