package com.batch.Database.Entities;

import com.batch.DTO.BatchSystemDataDefinitions.BatchModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
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



    public BatchModel getModel() {
        BatchModel batchModel = null;
        try {
            batchModel = startUnMarshalling(rowModel);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        this.model = batchModel;
        return batchModel;
    }

    public void setModel(BatchModel model) {
        String s = null;
        try {
             s = startMarshalling(model);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        this.model = model;
        this.rowModel = s;
    }

    private String startMarshalling(BatchModel model) throws JAXBException {
        StringWriter sw = new StringWriter();
        JAXBContext jaxbcontext = JAXBContext.newInstance(BatchModel.class);
        Marshaller marshaller = jaxbcontext.createMarshaller();
        marshaller.marshal(model, sw);
        return sw.toString();
    }
    private BatchModel startUnMarshalling(String model) throws JAXBException {
        JAXBContext jaxbcontext = JAXBContext.newInstance(BatchModel.class);
        Unmarshaller unMarshaller = jaxbcontext.createUnmarshaller();
        return ((BatchModel) unMarshaller.unmarshal(new StringReader(model)));
    }

    @Override
    public String toString() {
        getModel();
        return String.format("Batch{id=%-10d, unitName='%-10s', batchName='%-10s', creationDate=%-20s, creationTime=%-20s, state='%-10s', command='%-10s', comment='%-50s', model=%s}", id, unitName, batchName, creationDate, creationTime, state, order, comment, model);
    }
}
