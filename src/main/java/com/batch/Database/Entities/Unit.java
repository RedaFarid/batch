
package com.batch.Database.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
