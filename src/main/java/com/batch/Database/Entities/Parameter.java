
package com.batch.Database.Entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PhasesTypesParameters")
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;
    @Column(name = "[ParameterName]")
    private String name;
    @Column(name = "[ParameterType]")
    private String type;

    private Long id;

    @OneToOne()
    @JoinColumn(name = "ID")
    private Phase phase;

    public Parameter(String name, String type) {
        this.name = name;
        this.type = type;
    }


        @Override
    public String toString() {
        return String.format("Parameter{id=%-10d, name='%-10s', type='%-5s}", pid, name, type);
    }
}
