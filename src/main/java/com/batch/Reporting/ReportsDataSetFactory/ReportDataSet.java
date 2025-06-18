
package com.batch.Reporting.ReportsDataSetFactory;


import com.batch.Reporting.ReportsDTO.DTO;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


public interface ReportDataSet{
    
    Map<String, Object> parameters = new HashMap();
    List<DTO> DataSet = new ArrayList<>();
    Map getParameters();
    List<DTO> getDataSet() throws InterruptedException;
}
