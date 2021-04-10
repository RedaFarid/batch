package com.batch.Database.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
