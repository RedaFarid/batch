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
import com.google.common.collect.Lists;

public class BatchPhasesDataDefinition extends RowDataDefinition {
    private final PhaseRepository phaseRepository;
    private int InAddressTemp = 2;
    private int OutAddressTemp = 2;
    private int bitIncrement = 0;
    private String StepNo;
    private String unit = "";

    public BatchPhasesDataDefinition(String name, String unit) {
        super(name, 6, 6);
        this.unit = unit;
        this.phaseRepository = ApplicationContext.applicationContext.getBean(PhaseRepository.class);
    }

    public void createNewDeviceDataModel(int InAddress, int OutAddress) {
        this.addAttribute(BatchControl.PhaseIn, EDT.Integer, new Address(InAddress, 0), new IntegerDataType(0), true, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(BatchControl.Status, EDT.Integer, new Address(InAddress + 2, 0), new IntegerDataType(0), true, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(BatchControl.PhaseOut, EDT.Integer, new Address(OutAddress, 0), new IntegerDataType(0), false, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
        this.addAttribute(BatchControl.Order, EDT.Integer, new Address(OutAddress + 2, 0), new IntegerDataType(0), false, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
        this.InAddressTemp = 4;
        this.OutAddressTemp = 4;
        PhasesAttributes.getAttributes().addNewStep(this.StepNo);
        Lists.newArrayList(this.phaseRepository.findAll()).stream().filter((Phase) -> Phase.getUnit().equals(this.unit)).forEachOrdered((phase) -> {
            PhasesAttributes.getAttributes().addNewPhase(this.StepNo, phase.getName());
            phase.getParameters().forEach((para) -> {
                AttributeName inAttributeName = new AttributeName(phase.getName(), para.getName());
                AttributeName outAttributeName = new AttributeName(phase.getName(), para.getName());
                PhasesAttributes.getAttributes().addAttributeForPhaseAndParameter(this.StepNo, phase.getName(), para.getName() + "IN", inAttributeName);
                PhasesAttributes.getAttributes().addAttributeForPhaseAndParameter(this.StepNo, phase.getName(), para.getName() + "OUT", outAttributeName);
                if (para.getType().equals(PhaseParameterType.Check.name())) {
                    this.addAttribute(inAttributeName, EDT.Boolean, new Address(InAddress + this.InAddressTemp, this.bitIncrement), new BooleanDataType(Boolean.FALSE), true, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
                    this.addAttribute(outAttributeName, EDT.Boolean, new Address(OutAddress + this.OutAddressTemp, this.bitIncrement), new BooleanDataType(Boolean.FALSE), false, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
                    ++this.bitIncrement;
                    if (this.bitIncrement > 7) {
                        this.bitIncrement = 0;
                        ++this.InAddressTemp;
                        ++this.OutAddressTemp;
                    }
                } else if (para.getType().equals(PhaseParameterType.Value.name())) {
                    this.addAttribute(inAttributeName, EDT.Real, new Address(InAddress + this.InAddressTemp, 0), new RealDataType(0.0F), true, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
                    this.addAttribute(outAttributeName, EDT.Real, new Address(OutAddress + this.OutAddressTemp, 0), new RealDataType(0.0F), false, Alarming.Disable, LogIdentefires.Info, Logging.Disable);
                    this.InAddressTemp += 4;
                    this.OutAddressTemp += 4;
                }

            });
        });
        if (this.InAddressTemp % 2 == 0) {
            if (this.bitIncrement != 0) {
                this.InAddressTemp += 2;
                this.OutAddressTemp += 2;
            }
        } else {
            ++this.InAddressTemp;
            ++this.OutAddressTemp;
        }

        this.setInAddress(this.InAddressTemp);
        this.setOutAddress(this.OutAddressTemp);
    }

    public String getStepNo() {
        return this.StepNo;
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
            return this.phase;
        }

        public void setPhase(String phase) {
            this.phase = phase;
        }

        public String getParameter() {
            return this.Parameter;
        }

        public void setParameter(String Parameter) {
            this.Parameter = Parameter;
        }

        public String toString() {
            String var10000 = this.phase;
            return var10000 + " " + this.Parameter + this.hashCode();
        }
    }
}
