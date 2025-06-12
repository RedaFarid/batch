package com.batch.Database.Entities;

import com.batch.DTO.RecipeSystemDataDefinitions.RecipeModel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("RecipesDesign")
public class Recipe {
    @Id
    private Long id;
    @Column("Name")
    private String recipeName = "";
    @Column("Unit")
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
    @Column("Design")
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

    public Recipe() {
    }

    public String toString() {
        return String.format("Recipe{id=%-10d, recipeName='%-10s', unitName='%-10s', creationDate=%-10s, lastUpdateDate=%-10s, creationTime=%-10s, lastUpdateTime=%-10s, version='%-10s', state='%-10s', model=%s}", this.id, this.recipeName, this.unitName, this.creationDate, this.lastUpdateDate, this.creationTime, this.lastUpdateTime, this.version, this.state, this.rowModel);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getRecipeName() {
        return this.recipeName;
    }

    public void setRecipeName(final String recipeName) {
        this.recipeName = recipeName;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(final String unitName) {
        this.unitName = unitName;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(final LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(final LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public LocalTime getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(final LocalTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalTime getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(final LocalTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getState() {
        return this.state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getRowModel() {
        return this.rowModel;
    }

    public void setRowModel(final String rowModel) {
        this.rowModel = rowModel;
    }

    public RecipeModel getModel() {
        return this.model;
    }

    public void setModel(final RecipeModel model) {
        this.model = model;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Recipe other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$recipeName = this.getRecipeName();
                Object other$recipeName = other.getRecipeName();
                if (this$recipeName == null) {
                    if (other$recipeName != null) {
                        return false;
                    }
                } else if (!this$recipeName.equals(other$recipeName)) {
                    return false;
                }

                Object this$unitName = this.getUnitName();
                Object other$unitName = other.getUnitName();
                if (this$unitName == null) {
                    if (other$unitName != null) {
                        return false;
                    }
                } else if (!this$unitName.equals(other$unitName)) {
                    return false;
                }

                Object this$creationDate = this.getCreationDate();
                Object other$creationDate = other.getCreationDate();
                if (this$creationDate == null) {
                    if (other$creationDate != null) {
                        return false;
                    }
                } else if (!this$creationDate.equals(other$creationDate)) {
                    return false;
                }

                Object this$lastUpdateDate = this.getLastUpdateDate();
                Object other$lastUpdateDate = other.getLastUpdateDate();
                if (this$lastUpdateDate == null) {
                    if (other$lastUpdateDate != null) {
                        return false;
                    }
                } else if (!this$lastUpdateDate.equals(other$lastUpdateDate)) {
                    return false;
                }

                Object this$creationTime = this.getCreationTime();
                Object other$creationTime = other.getCreationTime();
                if (this$creationTime == null) {
                    if (other$creationTime != null) {
                        return false;
                    }
                } else if (!this$creationTime.equals(other$creationTime)) {
                    return false;
                }

                Object this$lastUpdateTime = this.getLastUpdateTime();
                Object other$lastUpdateTime = other.getLastUpdateTime();
                if (this$lastUpdateTime == null) {
                    if (other$lastUpdateTime != null) {
                        return false;
                    }
                } else if (!this$lastUpdateTime.equals(other$lastUpdateTime)) {
                    return false;
                }

                Object this$version = this.getVersion();
                Object other$version = other.getVersion();
                if (this$version == null) {
                    if (other$version != null) {
                        return false;
                    }
                } else if (!this$version.equals(other$version)) {
                    return false;
                }

                Object this$state = this.getState();
                Object other$state = other.getState();
                if (this$state == null) {
                    if (other$state != null) {
                        return false;
                    }
                } else if (!this$state.equals(other$state)) {
                    return false;
                }

                Object this$rowModel = this.getRowModel();
                Object other$rowModel = other.getRowModel();
                if (this$rowModel == null) {
                    if (other$rowModel != null) {
                        return false;
                    }
                } else if (!this$rowModel.equals(other$rowModel)) {
                    return false;
                }

                Object this$model = this.getModel();
                Object other$model = other.getModel();
                if (this$model == null) {
                    return other$model == null;
                } else return this$model.equals(other$model);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Recipe;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $recipeName = this.getRecipeName();
        result = result * 59 + ($recipeName == null ? 43 : $recipeName.hashCode());
        Object $unitName = this.getUnitName();
        result = result * 59 + ($unitName == null ? 43 : $unitName.hashCode());
        Object $creationDate = this.getCreationDate();
        result = result * 59 + ($creationDate == null ? 43 : $creationDate.hashCode());
        Object $lastUpdateDate = this.getLastUpdateDate();
        result = result * 59 + ($lastUpdateDate == null ? 43 : $lastUpdateDate.hashCode());
        Object $creationTime = this.getCreationTime();
        result = result * 59 + ($creationTime == null ? 43 : $creationTime.hashCode());
        Object $lastUpdateTime = this.getLastUpdateTime();
        result = result * 59 + ($lastUpdateTime == null ? 43 : $lastUpdateTime.hashCode());
        Object $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : $version.hashCode());
        Object $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        Object $rowModel = this.getRowModel();
        result = result * 59 + ($rowModel == null ? 43 : $rowModel.hashCode());
        Object $model = this.getModel();
        result = result * 59 + ($model == null ? 43 : $model.hashCode());
        return result;
    }
}
