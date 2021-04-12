package com.batch.Database.Entities;

import com.batch.DTO.BatchSystemDataDefinitions.BatchModel;
import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Unit")
    private String unitName;
    @Column(name = "Name")
    private String batchName ;
    @CreatedDate
    private LocalDate creationDate;
    @CreatedDate
    private LocalTime creationTime;
    private String state;
    @Column(name = "[Order]")
    private String order;
    private String comment;
    @Column(name = "Content", columnDefinition = "XML")
    private String rowModel;
    @Transient
    private BatchModel model;
    
    public Batch(Long id, String batchName, String state, String order, String comment, BatchModel model) {
        this.id = id;
        this.batchName = batchName;
        this.state = state;
        this.order = order;
        this.comment = comment;
        this.model = model;
    }

    public Batch(String batchName, String state, BatchModel model) {
        this.batchName = batchName;
        this.state = state;
        this.model = model;
    }

    public Batch(String batchName, String unitName, String state, String order, String comment, BatchModel model) {
        this.batchName = batchName;
        this.state = state;
        this.order = order;
        this.comment = comment;
        this.model = model;
        this.unitName = unitName;
    }

    public Batch(Long id, String state, String order, BatchModel model) {
        this.id = id;
        this.state = state;
        this.order = order;
        this.model = model;
    }
    
    public Batch(Long id, String state, BatchModel model) {
        this.id = id;
        this.state = state;
        this.model = model;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return Objects.equal(id, batch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        getModel();
        return String.format("Batch{id=%-10d, unitName='%-10s', batchName='%-10s', creationDate=%-20s, creationTime=%-20s, state='%-10s', command='%-10s', comment='%-50s', model=%s}", id, unitName, batchName, creationDate, creationTime, state, order, comment, model);
    }
}
