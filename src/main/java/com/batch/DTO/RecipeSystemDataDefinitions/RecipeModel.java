package com.batch.DTO.RecipeSystemDataDefinitions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Data
@ToString
@XmlRootElement
@AllArgsConstructor
@NoArgsConstructor
public class RecipeModel {
    private List<ParallelStepsModel> parallelSteps = new LinkedList<>();
    public RecipeModel(ParallelStepsModel... firstParallelStepsModel) {
        Collections.addAll(parallelSteps, firstParallelStepsModel);
    }
}
