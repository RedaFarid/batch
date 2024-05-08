package com.batch.Database.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
