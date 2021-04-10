
package com.batch.Database.Entities;

import com.batch.DTO.RecipeSystemDataDefinitions.RecipeModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "RecipesDesign")
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
    private String state;
    @Column(name = "Design", columnDefinition = "varchar(max)")
    private String rowModel;
    @Transient
    private RecipeModel model;

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

    public RecipeModel getModel() {
        RecipeModel recipeModel = null;
        try {
            recipeModel = startUnMarshalling(rowModel);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        this.model = recipeModel;
        return recipeModel;
    }

    public void setModel(RecipeModel model) {
        String s = null;
        try {
            s = startMarshalling(model);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        this.model = model;
        this.rowModel = s;
    }

    private String startMarshalling(RecipeModel model) throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext jaxbcontext = JAXBContext.newInstance(RecipeModel.class);
        Marshaller marshaller = jaxbcontext.createMarshaller();
        marshaller.marshal(model, sw);
        return sw.toString();
    }
    private RecipeModel startUnMarshalling(String model) throws JAXBException {
        JAXBContext jaxbcontext = JAXBContext.newInstance(RecipeModel.class);
        Unmarshaller unMarshaller = jaxbcontext.createUnmarshaller();
        return ((RecipeModel) unMarshaller.unmarshal(new StringReader(model)));
    }

    @Override
    public String toString() {
        getModel();
        return String.format("Recipe{id=%-10d, recipeName='%-10s', unitName='%-10s', creationDate=%-10s, lastUpdateDate=%-10s, creationTime=%-10s, lastUpdateTime=%-10s, version='%-10s', state='%-10s', model=%s}", id, recipeName, unitName, creationDate, lastUpdateDate, creationTime, lastUpdateTime, version, state, model);
    }
}
