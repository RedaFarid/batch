package com.batch.PLCDataSource.PLC.ComplexDataType;

import com.batch.ApplicationContext;
import com.batch.Database.Repositories.RecipeConfRepository;
import com.batch.Database.Repositories.UnitsRepository;
import com.batch.PLCDataSource.PLC.ComplexDataType.Batches.BatchPhasesDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.IntegerDataType;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.RealDataType;
import javafx.beans.value.ObservableValue;

import java.util.Map;

public class PLCDataDefinitionFactory {

    private static volatile PLCDataDefinitionFactory singelton = null;

    private PLCDataDefinition definition = new PLCDataDefinition();

    private final RecipeConfRepository recipesRepository;
    private final UnitsRepository unitsRepository;

    public PLCDataDefinitionFactory() {
        this.recipesRepository = ApplicationContext.applicationContext.getBean(RecipeConfRepository.class);
        this.unitsRepository = ApplicationContext.applicationContext.getBean(UnitsRepository.class);
        try {

            addNewDeviceToDataDefinition(new LifeSignal("CommCheck"));
            
            addNewDeviceToDataDefinition(new Pump("P01"));
            addNewDeviceToDataDefinition(new Pump("P02"));
            addNewDeviceToDataDefinition(new Pump("P03"));
            addNewDeviceToDataDefinition(new Pump("P04"));
            addNewDeviceToDataDefinition(new Pump("P05"));
            
            addNewDeviceToDataDefinition(new Valve("V01"));
            addNewDeviceToDataDefinition(new Valve("V02"));
            addNewDeviceToDataDefinition(new Valve("V03"));
            addNewDeviceToDataDefinition(new Valve("V04"));
            addNewDeviceToDataDefinition(new Valve("V05"));
            addNewDeviceToDataDefinition(new Valve("V06"));
            addNewDeviceToDataDefinition(new Valve("V07"));
            addNewDeviceToDataDefinition(new Valve("V08"));
            addNewDeviceToDataDefinition(new Valve("V09"));
            addNewDeviceToDataDefinition(new Valve("V10"));
            addNewDeviceToDataDefinition(new Valve("V11"));
            addNewDeviceToDataDefinition(new Valve("V12"));
            addNewDeviceToDataDefinition(new Valve("V13"));
            addNewDeviceToDataDefinition(new Valve("V14"));
            addNewDeviceToDataDefinition(new Valve("V15"));
            addNewDeviceToDataDefinition(new Valve("V16"));
            addNewDeviceToDataDefinition(new Valve("V17"));
            addNewDeviceToDataDefinition(new Valve("V18"));
            addNewDeviceToDataDefinition(new Valve("V19"));
            addNewDeviceToDataDefinition(new Valve("V20"));
            addNewDeviceToDataDefinition(new Valve("V21"));
            addNewDeviceToDataDefinition(new Valve("V22"));
            addNewDeviceToDataDefinition(new Valve("V23"));
            addNewDeviceToDataDefinition(new Valve("V24"));
            addNewDeviceToDataDefinition(new Valve("V25"));
            addNewDeviceToDataDefinition(new Valve("V26"));
            addNewDeviceToDataDefinition(new Valve("V27"));
            addNewDeviceToDataDefinition(new Valve("V28"));
            addNewDeviceToDataDefinition(new Valve("V29"));
            
            addNewDeviceToDataDefinition(new Mixer("M01"));
            addNewDeviceToDataDefinition(new Mixer("M02"));
            
            addNewDeviceToDataDefinition(new Weight("W01"));
            addNewDeviceToDataDefinition(new Weight("W02"));
            addNewDeviceToDataDefinition(new Weight("W03"));
            addNewDeviceToDataDefinition(new Weight("W04"));
            addNewDeviceToDataDefinition(new Weight("W05"));
            
            addNewDeviceToDataDefinition(new Weight("L01"));
            
            addNewDeviceToDataDefinition(new General("General"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PLCDataDefinitionFactory getSystem() {
        synchronized (PLCDataDefinitionFactory.class) {
            if (singelton == null) {
                singelton = new PLCDataDefinitionFactory();
            }
        }
        return singelton;
    }

    public Map<String, RowDataDefinition> getAllDevicesDataModel() {
        return definition.getAllDevices();
    }
    
    public void AddBatchData() {
        int maxParallelSteps = recipesRepository.findAll().stream().findAny().get().getMaxParallelSteps();
        if (maxParallelSteps > 0) {
            unitsRepository.findAll().forEach(unit -> {
                for (int i = 1; i <= maxParallelSteps; i++) {
                    addBatchDataToDataDefinition(String.valueOf(unit.getName() + " [" + i + "]"), new BatchPhasesDataDefinition(String.valueOf(unit.getName() + " [" + i + "]"), unit.getName()));
                }
            });
        }
    }

    public void addListenerToAllInPropertiesOfAllDevices() {
        definition.getAllDevices().forEach((deviceNme, device) -> {
            ((RowDataDefinition) device).getAddresses().forEach((attributeName, attribute) -> {
                System.err.println(deviceNme + " " + attributeName + " " + attribute);
//                if (((RowDataDefinition) device).getInOutIndecation().get(attributeName).equals(true)) {
                    switch (((RowDataDefinition) device).getTypes().get(attributeName)) {
                        case Boolean:
                            ((BooleanDataType) ((RowDataDefinition) device).getAllValues().get(attributeName)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                                System.out.println(deviceNme + " " + attributeName + "Attribute object " + attributeName + " changed from " + oldValue + " to " + newValue);
                            });
                            break;
                        case Integer:
                            ((IntegerDataType) ((RowDataDefinition) device).getAllValues().get(attributeName)).addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
                                System.out.println(deviceNme + " " + attributeName + "Attribute object " + attributeName + " changed from " + oldValue + " to " + newValue);
                            });
                            break;
                        case Real:
                            ((RealDataType) ((RowDataDefinition) device).getAllValues().get(attributeName)).addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
                                System.out.println(deviceNme + " " + attributeName + "Attribute object " + attributeName + " changed from " + oldValue + " to " + newValue);
                            });
                            break;
                        default:
                            break;
                    }
//                }
            });
        });
    }
    public void addListenerToAllInPropertiesOfBatchData() {
        RowDataDefinition device = definition.getAllDevices().get("BatchSystem");
                String deviceNme = "BatchSystem";
            
            device.getAddresses().forEach((attributeName, attribute) -> {
                System.err.println(deviceNme + " " + attributeName + " " + attribute);
                if (device.getInOutIndecation().get(attributeName).equals(true)) {
                    switch (device.getTypes().get(attributeName)) {
                        case Boolean:
                            ((BooleanDataType) ((RowDataDefinition) device).getAllValues().get(attributeName)).addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                                System.out.println(deviceNme + " " + attributeName + " changed from " + oldValue + " to " + newValue);
                            });
                            break;
                        case Integer:
                            ((IntegerDataType) ((RowDataDefinition) device).getAllValues().get(attributeName)).addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
                                System.out.println(deviceNme + " " + attributeName + " changed from " + oldValue + " to " + newValue);
                            });
                            break;
                        case Real:
                            ((RealDataType) ((RowDataDefinition) device).getAllValues().get(attributeName)).addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
                                System.out.println(deviceNme + " " + attributeName + " changed from " + oldValue + " to " + newValue);
                            });
                            break;
                        default:
                            break;
                    }
                }
            });
            
            
    }

    private void addNewDeviceToDataDefinition(RowDataDefinition device) throws Exception {
        int InLastAddress = definition.getInLastAddress();
        int OutLastAddress = definition.getOutLastAddress();

        
        device.createNewDeviceDataModel(InLastAddress, OutLastAddress);

        definition.setInLastAddress(InLastAddress + device.getInAddress());
        definition.setOutLastAddress(OutLastAddress + device.getOutAddress());

        definition.getAllDevices().put(device.getName(), device);
    }
    private void addBatchDataToDataDefinition( String step, BatchPhasesDataDefinition batch) {
        int InLastAddress = definition.getInLastAddress();
        int OutLastAddress = definition.getOutLastAddress();
        
        ((BatchPhasesDataDefinition) batch).setStepNo(step);
        batch.createNewDeviceDataModel(InLastAddress, OutLastAddress);
        
        definition.setInLastAddress(InLastAddress + batch.getInAddress());
        definition.setOutLastAddress(OutLastAddress + batch.getOutAddress());

        definition.getAllDevices().put(batch.getName(), batch);
    }
}
