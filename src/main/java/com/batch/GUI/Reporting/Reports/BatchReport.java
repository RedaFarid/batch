package com.batch.GUI.Reporting.Reports;


import com.batch.Utilities.RestrictiveTextField;
import com.google.common.io.Resources;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.function.BiConsumer;

public class BatchReport extends Stage {

    private final BiConsumer<ReportModel, File> consumer;
    private Printer printer = Printer.getDefaultPrinter();
    private PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.EQUAL);
    
    
    private double width = 0.0;
    private double height = 0.0;

    private Stage mainWindow = new Stage();
    private VBox mainRoot = new VBox();
    
    private BorderPane root = new BorderPane();
    private VBox center = new VBox();
    private Scene scene = new Scene(mainRoot);
    private VBox table = new VBox();

    private GridPane Header = new GridPane();
    private GridPane Footer = new GridPane();
    private GridPane mainData = new GridPane();
    
    private Button print = new Button("Print report ");
    private Button export = new Button("export report ");

    private ReportModel DataModel;

    public BatchReport(ReportModel model, Stage window, BiConsumer<ReportModel, File> consumer) {
        DataModel = model;
        mainWindow = window;
        this.consumer = consumer;
        graphicsBuilder();
    }

    private void graphicsBuilder() {
        width = (pageLayout.getPrintableWidth() - pageLayout.getRightMargin() - pageLayout.getLeftMargin()) / 20 * 19;
        height = (pageLayout.getPrintableHeight() - pageLayout.getTopMargin() - pageLayout.getBottomMargin()) / 20 * 19;

        tableGraphics();
        HeaderGraphics();
        FooterGraphics();
        mainDataGraphics();

        root.setPrefWidth(width);
        root.setPrefHeight(height);
        root.setMaxWidth(width);
        root.setMaxHeight(height);

        print.setPrefWidth(150);
        export.setPrefWidth(150);

        root.setTop(Header);
        root.setBottom(Footer);
        root.setCenter(center);
        root.setPadding(Insets.EMPTY);
        root.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii(0), new BorderWidths(0))));
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));

        center.getChildren().addAll(mainData, table);

        mainRoot.setPadding(new Insets(20));
        mainRoot.setSpacing(5);
        mainRoot.getChildren().addAll(root, print, export);

        print.setOnMouseClicked(action -> {
            PrinterJob job = PrinterJob.createPrinterJob(printer);
            boolean success = job.printPage(pageLayout, root);
            if (success) {
                job.endJob();
                this.hide();
            }else{
                System.err.println("Error printing report");
            }
        });
        export.setOnMouseClicked(action -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export report to xlsx");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel format", "*.xlsx"));
            chooser.setInitialFileName("Batch_" + DataModel.getBatchName() + "_.xlsx");
            File file = chooser.showSaveDialog(mainWindow);
            consumer.accept(DataModel, file);
        });

        //window adjustments
        setScene(scene);
        setResizable(false);
        initOwner(mainWindow);
        initModality(Modality.NONE);
        setTitle("Batch details report");
        show();

    }

    private void tableGraphics() {
        Pane pane = new Pane();
        pane.setPrefSize(10, 10);
        
        table.setPadding(new Insets(10));
        table.setSpacing(1);
        table.getChildren().clear();

        table.getChildren().add(createTableHeader());
        DataModel.getData().stream().limit(DataModel.getData().size() - 1).forEach(item -> {
            table.getChildren().add(tableRow(item));
        });
        table.getChildren().addAll(pane);
        table.getChildren().addAll(totalsTableRow(DataModel.getData().get(DataModel.getData().size() - 1)));
    }
    private void HeaderGraphics() {
        Label label = new Label("Batch details report");
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:darkblue;-fx-font-size:22;-fx-font-family: 'Times New Roman';");
        label.setAlignment(Pos.BASELINE_LEFT);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPrefWidth(width * 2 / 3 );

        Label label2 = new Label("Circle for industrial software developments");
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:red;-fx-font-size:16;-fx-font-family: 'Arial';");
        label2.setAlignment(Pos.BASELINE_LEFT);
        label2.setTextAlignment(TextAlignment.CENTER);
        label2.setPrefWidth(width * 2 / 3 );
        
        ImageView imageView = new ImageView(new Image(Resources.getResource("Icons/stocks.png").toString()));
        imageView.setFitWidth(90);
        imageView.setFitHeight(50);

        Header.add(label, 1, 0);
        Header.add(label2, 1, 1);
        Header.add(imageView, 0, 0, 1, 2);

        Header.setHgap(10);
        Header.setPadding(new Insets(0));
        Header.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii(1), new BorderWidths(1))));
        Header.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        Header.setMaxWidth(width);
    }
    private void FooterGraphics() {
        Label textArea = new Label();
        textArea.setText("");
        textArea.setStyle("-fx-font-weight:normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:7.5;-fx-font-family: 'Arial';");
        textArea.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1, 1, 1, 1))));
        textArea.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        textArea.setPrefWidth(width);
        textArea.setPrefHeight(80);
        textArea.setAlignment(Pos.CENTER);
        textArea.setTextAlignment(TextAlignment.JUSTIFY);
        
        TextField Name = new TextField("Name");
        Name.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Name.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 1))));
        Name.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        Name.setAlignment(Pos.CENTER);
        Name.setPrefWidth(width / 10 * 3);
        Name.setEditable(false);
        
        TextField Signature = new TextField("Signature");
        Signature.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Signature.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        Signature.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        Signature.setAlignment(Pos.CENTER);
        Signature.setPrefWidth(width / 10 * 2);
        Signature.setEditable(false);
        
        TextField Name2 = new TextField("Name");
        Name2.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Name2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        Name2.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        Name2.setAlignment(Pos.CENTER);
        Name2.setPrefWidth(width / 10 * 3);
        Name2.setEditable(false);
        
        TextField Signature2 = new TextField("Signature");
        Signature2.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Signature2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        Signature2.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        Signature2.setAlignment(Pos.CENTER);
        Signature2.setPrefWidth(width / 10 * 2);
        Signature2.setEditable(false);
        
        TextField NameD = new TextField("");
        NameD.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        NameD.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 1))));
        NameD.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        NameD.setAlignment(Pos.CENTER);
        NameD.setPrefWidth(width / 10 * 3);
        NameD.setEditable(false);
        
        TextField SignatureD = new TextField("");
        SignatureD.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        SignatureD.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        SignatureD.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        SignatureD.setAlignment(Pos.CENTER);
        SignatureD.setPrefWidth(width / 10 * 2);
        SignatureD.setEditable(false);
        
        TextField Name2D = new TextField("");
        Name2D.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Name2D.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        Name2D.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        Name2D.setAlignment(Pos.CENTER);
        Name2D.setPrefWidth(width / 10 * 3);
        Name2D.setEditable(false);
        
        TextField Signature2D = new TextField("");
        Signature2D.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Signature2D.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        Signature2D.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        Signature2D.setAlignment(Pos.CENTER);
        Signature2D.setPrefWidth(width / 10 * 2);
        Signature2D.setEditable(false);

        Footer.add(textArea, 0, 0, 4,1);
        
        Footer.add(Name, 0, 1);
        Footer.add(Signature, 1, 1);
        
        Footer.add(Name2, 2, 1);
        Footer.add(Signature2, 3, 1);
        
        Footer.add(NameD, 0, 2);
        Footer.add(SignatureD, 1, 2);
        
        Footer.add(Name2D, 2, 2);
        Footer.add(Signature2D, 3, 2);
        

        Footer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii(0), new BorderWidths(0))));
        Footer.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
    }
    private void mainDataGraphics() {
        HBox id = FieldTopRight("Batch ID", String.valueOf(DataModel.getBatchID()), width / 4, width * 3 / 10);
        HBox name = FieldBottomRight("Batch name ", DataModel.getBatchName(), width / 4, width * 3 / 10);
        HBox creationDate = FieldTopLeft("Creation Date", String.valueOf(DataModel.getCreationDate()), width / 4, width * 3 / 10);
        HBox creationTime = FieldBottomLeft("Creation time", String.valueOf(DataModel.getCreationTime()), width / 4, width * 3 / 10);

        mainData.add(id, 0, 0);
        mainData.add(creationDate, 1, 0);

        mainData.add(name, 0, 1);
        mainData.add(creationTime, 1, 1);

//        mainData.setVgap(0);
//        mainData.setHgap(0);
        mainData.setPadding(new Insets(10, 0, 0, 0));
        mainData.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii(0), new BorderWidths(0))));
        mainData.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
    }

    private HBox FieldTopRight(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();

        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1, 1, 1, 1))));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label.setPrefWidth(LabelWidth - 1);
        label.setEditable(false);

        //data cert num
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1, 1, 1, 0))));
        label2.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);

        hbox.getChildren().addAll(label, label2);
        return hbox;
    }
    private HBox FieldTopLeft(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();

        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1, 1, 1, 0))));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label.setPrefWidth(LabelWidth - 1);
        label.setEditable(false);

        //data cert num
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1, 1, 1, 0))));
        label2.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);

        hbox.getChildren().addAll(label, label2);
        return hbox;
    }
    private HBox FieldBottomRight(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();

        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 1))));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label.setPrefWidth(LabelWidth - 1);
        label.setEditable(false);

        //data cert num
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        label2.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);

        hbox.getChildren().addAll(label, label2);
        return hbox;
    }
    private HBox FieldBottomLeft(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();

        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label.setPrefWidth(LabelWidth - 1);
        label.setEditable(false);

        //data cert num
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        label2.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);

        hbox.getChildren().addAll(label, label2);
        return hbox;
    }
    private HBox FieldCenterRight(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();

        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 1))));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label.setPrefWidth(LabelWidth - 1);
        label.setEditable(false);

        //data cert num
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        label2.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);

        hbox.getChildren().addAll(label, label2);
        return hbox;
    }
    private HBox FieldCenterLeft(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();

        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label.setPrefWidth(LabelWidth - 1);
        label.setEditable(false);

        //data cert num
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 1, 0))));
        label2.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);

        hbox.getChildren().addAll(label, label2);
        return hbox;
    }
    
    private HBox FieldLabel(String text, double width){
        
        Label label = new Label(" " + text);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 1))));
        label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.saturate().darker(), new CornerRadii(0), new Insets(0, 0, 0, 0))));
        label.setPrefWidth(width);
        label.setPrefHeight(15);
        
        HBox hbox = new HBox();
        hbox.getChildren().add(label);
        
        return hbox;
    }

    private HBox tableRow(ReportTableDataModel recordModel) {
        HBox hbox = new HBox();
        hbox.setSpacing(1);
        
        RestrictiveTextField No = new RestrictiveTextField();
        RestrictiveTextField material = new RestrictiveTextField();
        RestrictiveTextField required = new RestrictiveTextField();
        RestrictiveTextField loaded = new RestrictiveTextField();
        RestrictiveTextField error = new RestrictiveTextField();
        RestrictiveTextField requiredPercent = new RestrictiveTextField();
        RestrictiveTextField loadedPercent = new RestrictiveTextField();

        No.setText(String.valueOf(recordModel.getNumber()));
        material.setText(String.valueOf(recordModel.getMaterialName()));
        required.setText(String.valueOf(recordModel.getRequired()));
        loaded.setText(String.valueOf(recordModel.getLoaded()));
        error.setText(String.valueOf(recordModel.getError()));
        requiredPercent.setText(String.valueOf(recordModel.getRequiredPercent()));
        loadedPercent.setText(String.valueOf(recordModel.getActualPercent()));
        
        No.setAlignment(Pos.CENTER);
        material.setAlignment(Pos.CENTER);
        required.setAlignment(Pos.CENTER);
        loaded.setAlignment(Pos.CENTER);
        error.setAlignment(Pos.CENTER);
        requiredPercent.setAlignment(Pos.CENTER);
        loadedPercent.setAlignment(Pos.CENTER);
        
        
        No.setPrefWidth((width / 14) - 2);
        material.setPrefWidth(width / 14 * 3);
        required.setPrefWidth(width / 14 * 2);
        loaded.setPrefWidth(width / 14 * 2);
        error.setPrefWidth(width / 14 * 2);
        requiredPercent.setPrefWidth(width / 14 * 2);
        loadedPercent.setPrefWidth(width / 14 * 2);
        
        
        No.setAlignment(Pos.BASELINE_LEFT);
        material.setAlignment(Pos.BASELINE_LEFT);
        required.setAlignment(Pos.BASELINE_LEFT);
        loaded.setAlignment(Pos.BASELINE_LEFT);
        error.setAlignment(Pos.BASELINE_LEFT);
        requiredPercent.setAlignment(Pos.BASELINE_LEFT);
        loadedPercent.setAlignment(Pos.BASELINE_LEFT);
        
        No.setPadding(new Insets(1));
        material.setPadding(new Insets(1));
        required.setPadding(new Insets(1));
        loaded.setPadding(new Insets(1));
        error.setPadding(new Insets(1));
        requiredPercent.setPadding(new Insets(1));
        loadedPercent.setPadding(new Insets(1));
        
        No.setEditable(false);
        material.setEditable(true);
        required.setEditable(true);
        loaded.setEditable(true);
        error.setEditable(true);
        requiredPercent.setEditable(true);
        loadedPercent.setEditable(false);

        No.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        material.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        required.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        loaded.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        error.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        requiredPercent.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        loadedPercent.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));

        No.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        material.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        required.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        loaded.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        error.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        requiredPercent.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        loadedPercent.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));

        hbox.getChildren().addAll(No, material, required, loaded, error, requiredPercent, loadedPercent);

        return hbox;
    }
    private HBox totalsTableRow(ReportTableDataModel recordModel) {
        HBox hbox = new HBox();
        hbox.setSpacing(1);

        RestrictiveTextField Total = new RestrictiveTextField();
        RestrictiveTextField required = new RestrictiveTextField();
        RestrictiveTextField loaded = new RestrictiveTextField();
        RestrictiveTextField error = new RestrictiveTextField();
        RestrictiveTextField requiredPercent = new RestrictiveTextField();
        RestrictiveTextField loadedPercent = new RestrictiveTextField();

        Total.setText("Totals");
        required.setText(String.valueOf(recordModel.getRequired()));
        loaded.setText(String.valueOf(recordModel.getLoaded()));
        error.setText(String.valueOf(recordModel.getError()));
        requiredPercent.setText(String.valueOf(recordModel.getRequiredPercent()));
        loadedPercent.setText(String.valueOf(recordModel.getActualPercent()));
        
        Total.setAlignment(Pos.CENTER);
        required.setAlignment(Pos.CENTER);
        loaded.setAlignment(Pos.CENTER);
        error.setAlignment(Pos.CENTER);
        requiredPercent.setAlignment(Pos.CENTER);
        loadedPercent.setAlignment(Pos.CENTER);
        
        
        Total.setPrefWidth((width / 14 * 4) -2);
        required.setPrefWidth(width / 14 * 2);
        loaded.setPrefWidth(width / 14 * 2);
        error.setPrefWidth(width / 14 * 2);
        requiredPercent.setPrefWidth(width / 14 * 2);
        loadedPercent.setPrefWidth(width / 14 * 2);
        
        
        Total.setAlignment(Pos.BASELINE_LEFT);
        required.setAlignment(Pos.BASELINE_LEFT);
        loaded.setAlignment(Pos.BASELINE_LEFT);
        error.setAlignment(Pos.BASELINE_LEFT);
        requiredPercent.setAlignment(Pos.BASELINE_LEFT);
        loadedPercent.setAlignment(Pos.BASELINE_LEFT);
        
        Total.setPadding(new Insets(0));
        required.setPadding(new Insets(0));
        loaded.setPadding(new Insets(0));
        error.setPadding(new Insets(0));
        requiredPercent.setPadding(new Insets(0));
        loadedPercent.setPadding(new Insets(0));
        
        Total.setEditable(true);
        required.setEditable(true);
        loaded.setEditable(true);
        error.setEditable(true);
        requiredPercent.setEditable(true);
        loadedPercent.setEditable(false);

        Total.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        required.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        loaded.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        error.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        requiredPercent.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));
        loadedPercent.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0))));

        Total.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        required.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        loaded.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        error.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        requiredPercent.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        loadedPercent.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));

        hbox.getChildren().addAll(Total, required, loaded, error, requiredPercent, loadedPercent);

        return hbox;
    }
    private HBox createTableHeader() {
        HBox hbox = new HBox();
        hbox.setSpacing(1);
        hbox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
        hbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1, 1, 1, 1))));
        
        Label number = new Label("No");
        number.setAlignment(Pos.CENTER);
        number.setTextAlignment(TextAlignment.CENTER);
        number.setPrefSize(width / 14, 40);
        number.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 0))));
        
        Label material = new Label("Material name");
        material.setAlignment(Pos.CENTER);
        material.setTextAlignment(TextAlignment.CENTER);
        material.setPrefSize(width / 14 * 3, 40);
        material.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 0))));

        Label required = new Label("Required");
        required.setAlignment(Pos.CENTER);
        required.setTextAlignment(TextAlignment.CENTER);
        required.setPrefSize(width / 7, 40);
        required.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 0))));

        Label loaded = new Label("Loaded");
        loaded.setAlignment(Pos.CENTER);
        loaded.setTextAlignment(TextAlignment.CENTER);
        loaded.setPrefSize(width / 7 - 1, 40);
        loaded.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 0))));

        Label error = new Label("Error");
        error.setAlignment(Pos.CENTER);
        error.setTextAlignment(TextAlignment.CENTER);
        error.setPrefSize(width / 7, 40);
        error.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 0))));

        Label rewuiredPercentage = new Label("Required %");
        rewuiredPercentage.setAlignment(Pos.CENTER);
        rewuiredPercentage.setTextAlignment(TextAlignment.CENTER);
        rewuiredPercentage.setPrefSize(width / 7 - 1, 40);
        rewuiredPercentage.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 0))));
        
        Label actualPercentage = new Label("Actual %");
        actualPercentage.setAlignment(Pos.CENTER);
        actualPercentage.setTextAlignment(TextAlignment.CENTER);
        actualPercentage.setPrefSize(width / 7 - 1, 40);
        actualPercentage.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(0, 1, 0, 0))));
        

        hbox.getChildren().addAll(number, material, required, loaded, error, rewuiredPercentage, actualPercentage);

        return hbox;
    }
    
}