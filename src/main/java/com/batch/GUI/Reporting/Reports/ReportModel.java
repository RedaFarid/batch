
package com.batch.GUI.Reporting.Reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportModel {
    
    private long batchID;
    private String batchName;
    private LocalDate creationDate;
    private LocalTime creationTime;
    
    private List<ReportTableDataModel> data = new LinkedList<>();

}
