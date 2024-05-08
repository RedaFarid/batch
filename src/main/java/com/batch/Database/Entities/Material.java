package com.batch.Database.Entities;

import com.google.common.base.Objects;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Materials")
@AllArgsConstructor
@NoArgsConstructor
public class Material {

    @Id
//    @SequenceGenerator(
//            name = "student_sequence",
//            sequenceName = "student_sequence",
//            allocationSize = 1
//    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
//            , generator = "student_sequence"
    )
    private Long id;
    @Column(updatable = false, nullable = false, columnDefinition = "varchar(250)", unique = true)
    private String name;

    private String Comment;

    public Material(String name) {
        this.name = name;
    }

    public Material(String name, String comment) {
        this.name = name;
        Comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Objects.equal(id, material.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
