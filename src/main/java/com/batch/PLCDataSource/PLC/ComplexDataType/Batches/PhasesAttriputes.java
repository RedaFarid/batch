package com.batch.PLCDataSource.PLC.ComplexDataType.Batches;

import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PhasesAttriputes {

    private static PhasesAttriputes singelton = null;
    private Map<String, Map<String, Map<String, RowAttripute>>> phasesAttriputes;

    public PhasesAttriputes() {
        this.phasesAttriputes = Collections.synchronizedMap(new LinkedHashMap());
    }

    public static PhasesAttriputes getAttributes() {
        if (singelton == null) {
            synchronized (PhasesAttriputes.class) {
                singelton = new PhasesAttriputes();
            }
        }
        return singelton;
    }
    public void addNewStep(String step){
        Map<String, Map<String, RowAttripute>> newStepCollection = Collections.synchronizedMap(new LinkedHashMap());
        phasesAttriputes.put(step, newStepCollection);
    }

    public void addNewPhase(String step, String phase) {
        Map<String, RowAttripute> newParametersCollection = Collections.synchronizedMap(new LinkedHashMap());
        phasesAttriputes.get(step).put(phase, newParametersCollection);
    }

    public void addAttriputeForPhaseAndParameter(String step, String phase, String parameter, RowAttripute attripute) {
        phasesAttriputes.get(step).get(phase).put(parameter, attripute);
    }

    public RowAttripute getAttriputeForPhaseAndParameter(String step, String phase, String parameter) {
//        System.err.println("Step =  [" + step + "] Phase =  [" + phase + "] Parameter =  [" + parameter + "] attribute = [" + phasesAttriputes.get(step).get(phase).get(parameter) + "]");
        return phasesAttriputes.get(step).get(phase).get(parameter);
    }
}
