package com.batch.Reporting.ReportsDataSetFactory;

import com.batch.Reporting.ReportsDTO.DTO;

import java.util.List;
import java.util.Map;


public class BatchesDataSet implements ReportDataSet {

    private final List<DTO> dataList;

    public BatchesDataSet(List<DTO> dataList) {
        this.dataList = dataList;
    }

    @Override
    public Map getParameters() {
        return parameters;
    }

    @Override
    public List<DTO> getDataSet() {
        DataSet.clear();
        DataSet.addAll(dataList);
        return DataSet;
    }
}
