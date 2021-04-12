package com.batch.Database.Entities;

import com.batch.DTO.RecipeSystemDataDefinitions.RecipeModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.xml.bind.*;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "RecipesDesign")
@EntityListeners(AuditingEntityListener.class)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Name")
    private String recipeName = "";
    @Column(name = "Unit")
    private String unitName = "";
    @CreatedDate
    private LocalDate creationDate;
    @LastModifiedDate
    private LocalDate lastUpdateDate;
    @CreatedDate
    private LocalTime creationTime;
    @LastModifiedDate
    private LocalTime lastUpdateTime;
    private String version = "1";
    private String state = "1";
    @Column(name = "Design", columnDefinition = "XML")
    private String rowModel;
    @Transient
    private RecipeModel model;

    public Recipe(Long id, String recipeName, String unitName, LocalDate creationDate, LocalDate lastUpdateDate, LocalTime creationTime, LocalTime lastUpdateTime, String version, String state, String rowModel, RecipeModel model) {
        this.id = id;
        this.recipeName = recipeName;
        this.unitName = unitName;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.version = version;
        this.state = state;
        this.rowModel = rowModel;
        this.model = model;
    }

    public Recipe(String recipeName, String version, String state, RecipeModel model) {
        this.recipeName = recipeName;
        this.version = version;
        this.state = state;
        this.model = model;
    }

    public Recipe(String name, String unitName, RecipeModel model) {
        this.model = model;
        this.recipeName = name;
        this.unitName = unitName;
    }

    @Override
    public String toString() {
        return String.format("Recipe{id=%-10d, recipeName='%-10s', unitName='%-10s', creationDate=%-10s, lastUpdateDate=%-10s, creationTime=%-10s, lastUpdateTime=%-10s, version='%-10s', state='%-10s', model=%s}", id, recipeName, unitName, creationDate, lastUpdateDate, creationTime, lastUpdateTime, version, state, model);
    }
}
