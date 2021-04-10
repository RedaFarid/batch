
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int maxParallelSteps;
    private int acceptedErrorInDosePhases;
    private double maxBatchSize;
    
}
