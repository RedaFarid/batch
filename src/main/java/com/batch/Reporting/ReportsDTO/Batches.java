package com.batch.Reporting.ReportsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Batches extends DTO {
    private String batchId;
    private String batchName;
    private String productName;
    private String endTime;
    private String comment;
    private String createdBy;
    private String creationDate;
    private String creationTime;
    private String clientName;
    private String no;
    private String materialName;
    private String req;
    private String loaded;
    private String error;
    private String reqPer;
    private String actPer;
}