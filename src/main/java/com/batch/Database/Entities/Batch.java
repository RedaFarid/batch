package com.batch.Database.Entities;

import com.batch.DTO.BatchSystemDataDefinitions.BatchModel;
import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@Data
@Table("batches")
public class Batch {
    @Id
    private Long id;
    @Column("Unit")
    private String unitName;
    @Column("Name")
    private String batchName;
    @CreatedDate
    private LocalDate creationDate;
    @CreatedDate
    private LocalTime creationTime;
    private String state;
    @Column("Order")
    private String order;
    private String comment;
    @Column("Content")
    private String rowModel;
    private LocalDateTime endTime;
    private String client;
    private String product;
    @Transient
    private BatchModel model;
    @CreatedBy
    private String createdBy;

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

    public Batch(final Long id, final String unitName, final String batchName, final LocalDate creationDate, final LocalTime creationTime, final String state, final String order, final String comment, final String rowModel, final LocalDateTime endTime, final String client, final String product, final BatchModel model) {
        this.id = id;
        this.unitName = unitName;
        this.batchName = batchName;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.state = state;
        this.order = order;
        this.comment = comment;
        this.rowModel = rowModel;
        this.endTime = endTime;
        this.client = client;
        this.product = product;
        this.model = model;
    }

    public Batch() {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Batch batch = (Batch) o;
            return Objects.equal(this.id, batch.id);
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

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(final String unitName) {
        this.unitName = unitName;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public void setBatchName(final String batchName) {
        this.batchName = batchName;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(final LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalTime getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(final LocalTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getState() {
        return this.state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(final String order) {
        this.order = order;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getRowModel() {
        return this.rowModel;
    }

    public void setRowModel(final String rowModel) {
        this.rowModel = rowModel;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getClient() {
        return this.client;
    }

    public void setClient(final String client) {
        this.client = client;
    }

    public String getProduct() {
        return this.product;
    }

    public void setProduct(final String product) {
        this.product = product;
    }

    public BatchModel getModel() {
        return this.model;
    }

    public void setModel(final BatchModel model) {
        this.model = model;
    }


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String toString() {
        Long var10000 = this.getId();
        return "Batch(id=" + var10000 + ", unitName=" + this.getUnitName() + ", batchName=" + this.getBatchName() + ", creationDate=" + this.getCreationDate() + ", creationTime=" + this.getCreationTime() + ", state=" + this.getState() + ", order=" + this.getOrder() + ", comment=" + this.getComment() + ", rowModel=" + this.getRowModel() + ", endTime=" + this.getEndTime() + ", client=" + this.getClient() + ", product=" + this.getProduct() + ", model=" + this.getModel() + ")";
    }
}
