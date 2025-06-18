package com.batch.Reporting.ReportsDetails;

import com.batch.Reporting.ReportsDTO.DTO;
import com.batch.Reporting.ReportsDataSetFactory.ReportDataSet;
import com.batch.Reporting.ReportsDataSetFactory.ReportsDataSetFactory;
import com.google.common.io.Resources;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
