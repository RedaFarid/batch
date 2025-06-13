package com.batch.GUI.NotificationCenter;

import com.batch.Services.LoggingService.MessageLoggingService;
import com.batch.Services.NotificationService.BackGroundServices;
import com.batch.Services.NotificationService.ErrorObject;
import com.batch.Services.NotificationService.NotificationService;
import com.batch.Services.NotificationService.ServiceErrorsListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.List;
import java.util.Map;

@Controller
public class NCController {
    private final NCModel model = new NCModel();
    private final ObservableList<String> categories = FXCollections.observableArrayList();
    private final ObservableList<String> errors = FXCollections.observableArrayList();
    @Autowired
    @BackGroundServices
    private NotificationService bgNotificationService;

    @Autowired
    MessageLoggingService log;

    public NCModel getModel() {
        return this.model;
    }

    @Async
    @EventListener
    public void startListenersAtStartup(ContextStartedEvent contextStartedEvent) {
        this.bgNotificationService.getNotificationsDataStructure().addServicesListener(new ServiceErrorsListener() {
            public void newServiceAdded(String serviceAdded) {
                NCServicesView.addNewServicePart(serviceAdded);
            }

            public void serviceRemoved(String service) {
                NCServicesView.removeServicePart(service);
            }

            public void newFamilyAdded(String service, String family, ErrorObject errorObject) {
                NCServicesView.addNewBGErrorPartition(errorObject);
            }

            public void familyRemoved(String service, String family) {
                NCServicesView.removeFamilyPartition(service, family);
            }

            public void newErrorMessageAdded(String service, String family, String errorMessage, ErrorObject errorObject) {
            }

            public void atRegistering(List<String> services, Map<String, List<ErrorObject>> errors) {
                services.forEach(NCServicesView::addNewServicePart);
                errors.forEach((service, errorsList) -> errorsList.forEach(NCServicesView::addNewBGErrorPartition));
            }

            public void newErrorMessagePopUpOnly(String service, String family, String errorMessage) {
            }
        });
    }

    public ObservableList<String> getListOfCategories() {
        return this.categories;
    }

    public ObservableList<String> getListOfErrors() {
        return this.errors;
    }

    @Async
    public void onExportCSV(File file, ErrorObject errorObject) {
        String headerLine = "";

        try (
                FileWriter fW = new FileWriter(file);
                BufferedWriter bR = new BufferedWriter(fW)
        ) {
            headerLine = "service NameFamily NameerrorMessage,";
            headerLine = headerLine + "\n";

            try {
                bR.append(headerLine);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            List<String> dataBaseList = errorObject.getErrorMessageList();
            if (dataBaseList != null) {
                dataBaseList.forEach((item) -> {
                    String line = "";
                    line = line + errorObject.getServiceName() + ",";
                    line = line + errorObject.getErrorFamily() + ",";
                    line = line + item + ",";
                    line = line + item + "\n";

                    try {
                        bR.append(line);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            }
        } catch (Exception e) {
            log.logExcption("NCController [onExportCSV]", e);
        }

    }

    @Async
    public void onExportExcel(File file, ErrorObject errorObject) {
        String headerLine = "";

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            XSSFSheet firstSheet = xssfWorkbook.createSheet("FirstSheet");
            XSSFRow row = firstSheet.createRow(0);
            XSSFFont font = xssfWorkbook.createFont();
            font.setBold(true);
            firstSheet.setAutobreaks(true);
            row.setHeight((short) 400);
            XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
            cellStyle.setFont(font);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.MEDIUM);
            XSSFCell cell00 = row.createCell(0, CellType.STRING);
            XSSFCell cell10 = row.createCell(1, CellType.STRING);
            XSSFCell cell20 = row.createCell(2, CellType.STRING);
            XSSFCell cell30 = row.createCell(3, CellType.STRING);
            XSSFCell cell40 = row.createCell(4, CellType.STRING);
            cell00.setCellValue("Service Name");
            cell10.setCellValue("Family Name");
            cell20.setCellValue("Error Message");
            List<String> dataBaseList = errorObject.getErrorMessageList();
            if (dataBaseList != null) {
                dataBaseList.forEach((item) -> {
                    XSSFRow row1 = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell cell0 = row1.createCell(0, CellType.STRING);
                    cell0.setCellValue(String.valueOf(errorObject.getServiceName()));
                    XSSFCell cell1 = row1.createCell(1, CellType.STRING);
                    cell1.setCellValue(String.valueOf(errorObject.getErrorFamily()));
                    XSSFCell cell2 = row1.createCell(2, CellType.STRING);
                    cell2.setCellValue(String.valueOf(item));
                });
            }

            firstSheet.autoSizeColumn(0);
            firstSheet.autoSizeColumn(1);
            firstSheet.autoSizeColumn(2);
            firstSheet.autoSizeColumn(3);
            firstSheet.autoSizeColumn(4);
            xssfWorkbook.write(fileOutputStream);
            xssfWorkbook.close();
        } catch (Exception e) {
            log.logExcption("NCController [onExportExcel]", e);
        }

    }
}
