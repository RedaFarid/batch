package com.batch.PLCDataSource.PLC.ComplexDataType;

import com.batch.ApplicationContext;
import com.batch.Database.Repositories.RecipeConfRepository;
import com.batch.Database.Repositories.UnitsRepository;
import com.batch.PLCDataSource.PLC.ComplexDataType.Batches.BatchPhasesDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PLCDataDefinitionFactory {
    private final PLCDataDefinition definition = new PLCDataDefinition();
    private final RecipeConfRepository recipeConfRepository;
    private final UnitsRepository unitsRepository;

    public PLCDataDefinitionFactory(final RecipeConfRepository recipeConfRepository, final UnitsRepository unitsRepository) {
        this.recipeConfRepository = recipeConfRepository;
        this.unitsRepository = unitsRepository;
    }

    @PostConstruct
    public void init() {
        try {
            this.addNewDeviceToDataDefinition(new LifeSignal("CommCheck"));
            this.addNewDeviceToDataDefinition(new Pump("P01"));
            this.addNewDeviceToDataDefinition(new Pump("P02"));
            this.addNewDeviceToDataDefinition(new Pump("P03"));
            this.addNewDeviceToDataDefinition(new Pump("P04"));
            this.addNewDeviceToDataDefinition(new Pump("P05"));
            this.addNewDeviceToDataDefinition(new Valve("V01"));
            this.addNewDeviceToDataDefinition(new Valve("V02"));
            this.addNewDeviceToDataDefinition(new Valve("V03"));
            this.addNewDeviceToDataDefinition(new Valve("V04"));
            this.addNewDeviceToDataDefinition(new Valve("V05"));
            this.addNewDeviceToDataDefinition(new Valve("V06"));
            this.addNewDeviceToDataDefinition(new Valve("V07"));
            this.addNewDeviceToDataDefinition(new Valve("V08"));
            this.addNewDeviceToDataDefinition(new Valve("V09"));
            this.addNewDeviceToDataDefinition(new Valve("V10"));
            this.addNewDeviceToDataDefinition(new Valve("V11"));
            this.addNewDeviceToDataDefinition(new Valve("V12"));
            this.addNewDeviceToDataDefinition(new Valve("V13"));
            this.addNewDeviceToDataDefinition(new Valve("V14"));
            this.addNewDeviceToDataDefinition(new Valve("V15"));
            this.addNewDeviceToDataDefinition(new Valve("V16"));
            this.addNewDeviceToDataDefinition(new Valve("V17"));
            this.addNewDeviceToDataDefinition(new Valve("V18"));
            this.addNewDeviceToDataDefinition(new Valve("V19"));
            this.addNewDeviceToDataDefinition(new Valve("V20"));
            this.addNewDeviceToDataDefinition(new Valve("V21"));
            this.addNewDeviceToDataDefinition(new Valve("V22"));
            this.addNewDeviceToDataDefinition(new Valve("V23"));
            this.addNewDeviceToDataDefinition(new Valve("V24"));
            this.addNewDeviceToDataDefinition(new Valve("V25"));
            this.addNewDeviceToDataDefinition(new Valve("V26"));
            this.addNewDeviceToDataDefinition(new Valve("V27"));
            this.addNewDeviceToDataDefinition(new Valve("V28"));
            this.addNewDeviceToDataDefinition(new Valve("V29"));
            this.addNewDeviceToDataDefinition(new Mixer("M01"));
            this.addNewDeviceToDataDefinition(new Mixer("M02"));
            this.addNewDeviceToDataDefinition(new Weight("W01"));
            this.addNewDeviceToDataDefinition(new Weight("W02"));
            this.addNewDeviceToDataDefinition(new Weight("W03"));
            this.addNewDeviceToDataDefinition(new Weight("W04"));
            this.addNewDeviceToDataDefinition(new Weight("W05"));
            this.addNewDeviceToDataDefinition(new Weight("L01"));
            this.addNewDeviceToDataDefinition(new General("General"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @EventListener
    public void afterAppStart(ApplicationContext.GraphicsInitializerEvent event) {
        this.AddBatchData();
    }

    public Map<String, RowDataDefinition> getAllDevicesDataModel() {
        return this.definition.getAllDevices();
    }

    public void AddBatchData() {
        Lists.newArrayList(this.recipeConfRepository.findAll()).stream().findAny().ifPresent(recipeConfig -> {
            int maxParallelSteps = recipeConfig.getMaxParallelSteps();
            if (maxParallelSteps > 0) {
                this.unitsRepository.findAll().forEach((unit) -> {
                    for (int i = 1; i <= maxParallelSteps; ++i) {
                        this.addBatchDataToDataDefinition(unit.getName() + " [" + i + "]", new BatchPhasesDataDefinition(unit.getName() + " [" + i + "]", unit.getName()));
                    }

                });
            }

        });
    }

    public void addListenerToAllInPropertiesOfAllDevices() {
        this.definition.getAllDevices().forEach((deviceNme, device) -> device.getAddresses().forEach((attributeName, attribute) -> {
            System.err.println(deviceNme + " " + attributeName + " " + attribute);
            switch (device.getTypes().get(attributeName)) {
                case Boolean ->
                        ((BooleanDataType) device.getAllValues().get(attributeName)).addListener((observable, oldValue, newValue) -> System.out.println(deviceNme + " " + attributeName + "Attribute object " + attributeName + " changed from " + oldValue + " to " + newValue));
                case Integer ->
                        ((IntegerDataType) device.getAllValues().get(attributeName)).addListener((observable, oldValue, newValue) -> System.out.println(deviceNme + " " + attributeName + "Attribute object " + attributeName + " changed from " + oldValue + " to " + newValue));
                case Real ->
                        ((RealDataType) device.getAllValues().get(attributeName)).addListener((observable, oldValue, newValue) -> System.out.println(deviceNme + " " + attributeName + "Attribute object " + attributeName + " changed from " + oldValue + " to " + newValue));
            }

        }));
    }

    public void addListenerToAllInPropertiesOfBatchData() {
        RowDataDefinition device = this.definition.getAllDevices().get("BatchSystem");
        String deviceNme = "BatchSystem";
        device.getAddresses().forEach((attributeName, attribute) -> {
            System.err.println(deviceNme + " " + attributeName + " " + attribute);
            if (device.getInOutIndecation().get(attributeName).equals(true)) {
                switch (device.getTypes().get(attributeName)) {
                    case Boolean ->
                            ((BooleanDataType) device.getAllValues().get(attributeName)).addListener((observable, oldValue, newValue) -> System.out.println(deviceNme + " " + attributeName + " changed from " + oldValue + " to " + newValue));
                    case Integer ->
                            ((IntegerDataType) device.getAllValues().get(attributeName)).addListener((observable, oldValue, newValue) -> System.out.println(deviceNme + " " + attributeName + " changed from " + oldValue + " to " + newValue));
                    case Real ->
                            ((RealDataType) device.getAllValues().get(attributeName)).addListener((observable, oldValue, newValue) -> System.out.println(deviceNme + " " + attributeName + " changed from " + oldValue + " to " + newValue));
                }
            }

        });
    }

    private void addNewDeviceToDataDefinition(RowDataDefinition device) throws Exception {
        int InLastAddress = this.definition.getInLastAddress();
        int OutLastAddress = this.definition.getOutLastAddress();
        device.createNewDeviceDataModel(InLastAddress, OutLastAddress);
        this.definition.setInLastAddress(InLastAddress + device.getInAddress());
        this.definition.setOutLastAddress(OutLastAddress + device.getOutAddress());
        this.definition.getAllDevices().put(device.getName(), device);
    }

    private void addBatchDataToDataDefinition(String step, BatchPhasesDataDefinition batch) {
        int InLastAddress = this.definition.getInLastAddress();
        int OutLastAddress = this.definition.getOutLastAddress();
        batch.setStepNo(step);
        batch.createNewDeviceDataModel(InLastAddress, OutLastAddress);
        this.definition.setInLastAddress(InLastAddress + batch.getInAddress());
        this.definition.setOutLastAddress(OutLastAddress + batch.getOutAddress());
        this.definition.getAllDevices().put(batch.getName(), batch);
    }
}
