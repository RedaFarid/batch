
package com.batch.Database.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TagLog")
public class TagLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String attribute;
    private double value;
    @CreatedDate
    private LocalTime time;
    @CreatedDate
    private LocalDate Date;

    public TagLog(String name, String attribute, double value) {
        this.name = name;
        this.attribute = attribute;
        this.value = value;
    }
}
