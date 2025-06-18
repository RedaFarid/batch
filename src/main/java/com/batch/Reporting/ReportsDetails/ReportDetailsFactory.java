package com.batch.Reporting.ReportsDetails;

import com.batch.Reporting.ReportsDTO.DTO;
import com.batch.Reporting.ReportsDataSetFactory.ReportDataSet;
import com.batch.Reporting.ReportsDataSetFactory.ReportsDataSetFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportDetailsFactory {

    private final Map<String, String> reportsFiles = Collections.synchronizedMap(new HashMap<>());
    private final ReportsDataSetFactory reportsDataSetFactory = new ReportsDataSetFactory();


    public ReportDetailsFactory() {
        //filling the locations regarding every report jrxml file

        reportsFiles.put("Batch", "/ReportsDesign/Batch.jrxml");
    }


    public Pane getReportDetailsPaneFor(String reportName, List<DTO> dataList) throws Exception {
        try (InputStream reportInputStream = getClass().getResourceAsStream((reportsFiles.get(reportName)))) {
            ReportDataSet dataset = reportsDataSetFactory.createDataSetAndParametersFor(reportName, dataList);
            JasperDesign jasperdesign = JRXmlLoader.load(reportInputStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperdesign);
            JasperPrint jasperprint = JasperFillManager.fillReport(jasperReport, dataset.getParameters(), new JRBeanCollectionDataSource(dataset.getDataSet()));
            return new JRPrintPreview(jasperprint);
        }
    }

}

