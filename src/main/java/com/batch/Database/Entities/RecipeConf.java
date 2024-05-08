
package com.batch.Database.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
