
package com.batch.PLCDataSource.PLC.ComplexDataType.Batches;

import com.batch.ApplicationContext;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.Database.Repositories.PhaseRepository;
import com.batch.PLCDataSource.PLC.ComplexDataType.Alarming;
import com.batch.PLCDataSource.PLC.ComplexDataType.Logging;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.*;
import com.batch.Utilities.LogIdentefires;


public class BatchPhasesDataDefinition extends RowDataDefinition {

    private int InAddressTemp = 2;
    private int OutAddressTemp = 2;
    private int bitIncrement = 0;
    
    private String StepNo;
    private String unit = "";

    private final PhaseRepository phaseRepository;
        
    public BatchPhasesDataDefinition(String name, String unit) {
        super(name, 6, 6);
        this.unit = unit;
        this.phaseRepository = ApplicationContext.applicationContext.getBean(PhaseRepository.class);
    }

    @Override
    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        
        //Status
        addAttribute(BatchControl.PhaseIn, EDT.Integer, new Address(InAddress, 0), new IntegerDataType(0), In, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
        addAttribute(BatchControl.Status, EDT.Integer, new Address(InAddress + 2, 0), new IntegerDataType(0), In, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
        
        //Orders
        addAttribute(BatchControl.PhaseOut, EDT.Integer, new Address(OutAddress, 0), new IntegerDataType(0), Out, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
        addAttribute(BatchControl.Order, EDT.Integer, new Address(OutAddress + 2, 0), new IntegerDataType(0), Out, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
        
        InAddressTemp = 4;
        OutAddressTemp = 4;
        PhasesAttributes.getAttributes().addNewStep(StepNo);
        phaseRepository.findAll().stream().filter(Phase -> Phase.getUnit().equals(unit)).forEachOrdered(phase -> {
            PhasesAttributes.getAttributes().addNewPhase(StepNo, phase.getName());
            phase.getParameters().forEach(para -> {
                AttributeName inAttributeName = new AttributeName(phase.getName(), para.getName());
                AttributeName outAttributeName = new AttributeName(phase.getName(), para.getName());
                PhasesAttributes.getAttributes().addAttributeForPhaseAndParameter(StepNo, phase.getName(), para.getName() + "IN", inAttributeName);
                PhasesAttributes.getAttributes().addAttributeForPhaseAndParameter(StepNo, phase.getName(), para.getName() + "OUT", outAttributeName);
                if (para.getType().equals(PhaseParameterType.Check.name())) {
                    addAttribute(inAttributeName, EDT.Boolean, new Address(InAddress + InAddressTemp, bitIncrement), new BooleanDataType(Boolean.FALSE), In, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
                    addAttribute(outAttributeName, EDT.Boolean, new Address(OutAddress + OutAddressTemp, bitIncrement), new BooleanDataType(Boolean.FALSE), Out, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
                    bitIncrement++;
                    if (bitIncrement > 7) {
                        bitIncrement = 0;
                        InAddressTemp += 1;
                        OutAddressTemp += 1;
                    }
                } else if (para.getType().equals(PhaseParameterType.Value.name())){
                    addAttribute(inAttributeName, EDT.Real, new Address(InAddress + InAddressTemp, 0), new RealDataType(0.0f), In, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
                    addAttribute(outAttributeName, EDT.Real, new Address(OutAddress + OutAddressTemp, 0), new RealDataType(0.0f), Out, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
                    InAddressTemp += 4;
                    OutAddressTemp += 4;
                }
            });
        });
        if ((InAddressTemp % 2) == 0) {
            if (bitIncrement != 0) {
                InAddressTemp += 2;
                OutAddressTemp += 2;
            }
        } else {
            InAddressTemp += 1;
            OutAddressTemp += 1;
        }
        setInAddress(InAddressTemp);
        setOutAddress(OutAddressTemp);
    }
    
    public String getStepNo() {
        return StepNo;
    }

    public void setStepNo(String StepNo) {
        this.StepNo = StepNo;
    }

    private static class AttributeName implements RowAttripute {

        private String phase;
        private String Parameter;
        
        public AttributeName(String phase, String parameter) {
            this.Parameter = parameter;
            this.phase = phase;
        }

        public String getPhase() {
            return phase;
        }

        public void setPhase(String phase) {
            this.phase = phase;
        }

        public String getParameter() {
            return Parameter;
        }

        public void setParameter(String Parameter) {
            this.Parameter = Parameter;
        }
        
        @Override
        public String toString() {
            return phase + " " +  Parameter + hashCode();
        }
    }
}
