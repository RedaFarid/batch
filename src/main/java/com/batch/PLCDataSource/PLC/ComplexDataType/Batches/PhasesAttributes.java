package com.batch.PLCDataSource.PLC.ComplexDataType.Batches;

import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PhasesAttributes {
    private static PhasesAttributes singleton = null;
    private final Map<String, Map<String, Map<String, RowAttripute>>> phasesAttributes = Collections.synchronizedMap(new LinkedHashMap());

    public static PhasesAttributes getAttributes() {
        if (singleton == null) {
            synchronized (PhasesAttributes.class) {
                singleton = new PhasesAttributes();
            }
        }

        return singleton;
    }

    public void addNewStep(String step) {
        Map<String, Map<String, RowAttripute>> newStepCollection = Collections.synchronizedMap(new LinkedHashMap());
        this.phasesAttributes.put(step, newStepCollection);
    }

    public void addNewPhase(String step, String phase) {
        Map<String, RowAttripute> newParametersCollection = Collections.synchronizedMap(new LinkedHashMap());
        ((Map) this.phasesAttributes.get(step)).put(phase, newParametersCollection);
    }

    public void addAttributeForPhaseAndParameter(String step, String phase, String parameter, RowAttripute attribute) {
        ((Map) ((Map) this.phasesAttributes.get(step)).get(phase)).put(parameter, attribute);
    }

    public RowAttripute getAttributeForPhaseAndParameter(String step, String phase, String parameter) {
        return (RowAttripute) ((Map) ((Map) this.phasesAttributes.get(step)).get(phase)).get(parameter);
    }
}
