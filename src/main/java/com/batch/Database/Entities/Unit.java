
package com.batch.Database.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Units")
public class Unit{

    @Id
    @Column(name = "Unit")
    private String name;
    
}
