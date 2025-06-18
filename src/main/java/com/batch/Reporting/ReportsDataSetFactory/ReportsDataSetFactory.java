package com.batch.Reporting.ReportsDataSetFactory;


import com.batch.Reporting.ReportsDTO.DTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class ReportsDataSetFactory {


    public ReportDataSet createDataSetAndParametersFor(String report, List<DTO> dataList) {
        return switch (report) {
            //Adding all cases of reports
            case "Batch" ->  new BatchesDataSet(dataList);
            case "BatchConsumption" ->  new BatchConsumptionDataSet(dataList);

            default -> throw new IllegalStateException("Unexpected value: " + report);
        };
    }
}
