package com.batch.Database.Entities;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("Materials")
public class Material {
    @Id
    private Long id;
    private String name;
    private String Comment;

    public Material(String name) {
        this.name = name;
    }

    public Material(String name, String comment) {
        this.name = name;
        this.Comment = comment;
    }

    public Material(final Long id, final String name, final String Comment) {
        this.id = id;
        this.name = name;
        this.Comment = Comment;
    }

    public Material() {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Material material = (Material) o;
            return Objects.equal(this.id, material.id);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getComment() {
        return this.Comment;
    }

    public void setComment(final String Comment) {
        this.Comment = Comment;
    }


    public String toString() {
        Long var10000 = this.getId();
        return "Material(id=" + var10000 + ", name=" + this.getName() + ", Comment=" + this.getComment() + ")";
    }
}
