package com.batch.Database.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchControllerData {

    @Id
    private String unit  ;
    private long runningBatchID ;
    private int CurrentParallelStepsNo  ;
    private boolean controlBit;
    private boolean lockGeneralControl;
    
}
