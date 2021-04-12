
package com.batch.GUI.Reporting.Reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportTableDataModel {
    
    private int number;
    private String materialName;
    private double required;
    private double loaded;
    private double error;
    private double requiredPercent;
    private double actualPercent;

    
}
