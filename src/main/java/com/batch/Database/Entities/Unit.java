
package com.batch.Database.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Units")
public class Unit{

    @Id
    @Column(name = "Unit")
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
