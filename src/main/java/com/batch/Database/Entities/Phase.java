
package com.batch.Database.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PhasesTypesName")
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String unit;
    @Column(name = "Type")
    private String phaseType;

    @OneToMany(mappedBy = "id", fetch=FetchType.EAGER)
    private List<Parameter> Parameters;


    @Override
    public String toString() {
        return String.format("Phase{id=%-5d, name='%-40s', unit='%-15s', phaseType='%-30s', Parameters=%-10s}", id, name, unit, phaseType, Parameters);
    }
}
