
package com.batch.Database.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "RecipeConfiguration")
public class RecipeConf {

    @Id
    @Column(name = "MaxNumberOfParallelSteps")
    private int maxParallelSteps;
    @Column(name = "AcceptedError")
    private int acceptedErrorInDosePhases;
    @Column(name = "MaxBatchSize")
    private double maxBatchSize;
    
}
