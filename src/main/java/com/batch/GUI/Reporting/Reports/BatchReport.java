//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.batch.GUI.Reporting.Reports;

import com.batch.Utilities.RestrictiveTextField;
import com.google.common.io.Resources;
import java.io.File;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.print.Printer.MarginType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.util.TriConsumer;

public class BatchReport extends Stage {
    private final TriConsumer<ReportModel, File, Runnable> consumer;
    private Printer printer = Printer.getDefaultPrinter();
    private PageLayout pageLayout;
    private double width;
    private double height;
    private Stage mainWindow;
    private VBox mainRoot;
    private BorderPane root;
    private VBox center;
    private Scene scene;
    private VBox table;
    private GridPane Header;
    private GridPane Footer;
    private GridPane mainData;
    private Button print;
    private Button export;
    private ReportModel dataModel;
    private Color TABLE_LABEL_COLOR;

    public BatchReport(ReportModel model, Stage window, TriConsumer<ReportModel, File, Runnable> consumer) {
        this.pageLayout = this.printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, MarginType.EQUAL);
        this.width = (double)0.0F;
        this.height = (double)0.0F;
        this.mainWindow = new Stage();
        this.mainRoot = new VBox();
        this.root = new BorderPane();
        this.center = new VBox();
        this.scene = new Scene(this.mainRoot);
        this.table = new VBox();
        this.Header = new GridPane();
        this.Footer = new GridPane();
        this.mainData = new GridPane();
        this.print = new Button("Print report ");
        this.export = new Button("export report ");
        this.TABLE_LABEL_COLOR = Color.LIGHTGRAY;
        this.dataModel = model;
        this.mainWindow = window;
        this.consumer = consumer;
        this.graphicsBuilder();
    }

    private void graphicsBuilder() {
        this.width = (this.pageLayout.getPrintableWidth() - this.pageLayout.getRightMargin() - this.pageLayout.getLeftMargin()) / (double)20.0F * (double)19.0F;
        this.height = (this.pageLayout.getPrintableHeight() - this.pageLayout.getTopMargin() - this.pageLayout.getBottomMargin()) / (double)20.0F * (double)19.0F;
        this.tableGraphics();
        this.HeaderGraphics();
        this.FooterGraphics();
        this.mainDataGraphics();
        this.root.setPrefWidth(this.width);
        this.root.setPrefHeight(this.height);
        this.root.setMaxWidth(this.width);
        this.root.setMaxHeight(this.height);
        this.print.setPrefWidth((double)150.0F);
        this.export.setPrefWidth((double)150.0F);
        this.root.setTop(this.Header);
        this.root.setBottom(this.Footer);
        this.root.setCenter(this.center);
        this.root.setPadding(Insets.EMPTY);
        this.root.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        this.root.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        this.center.getChildren().addAll(new Node[]{this.mainData, this.table});
        this.mainRoot.setPadding(new Insets((double)20.0F));
        this.mainRoot.setSpacing((double)5.0F);
        this.mainRoot.getChildren().addAll(new Node[]{this.root, this.print, this.export});
        this.print.setOnMouseClicked((action) -> {
            PrinterJob job = PrinterJob.createPrinterJob(this.printer);
            boolean success = job.printPage(this.pageLayout, this.root);
            if (success) {
                job.endJob();
                this.hide();
            } else {
                System.err.println("Error printing report");
            }

        });
        this.export.setOnMouseClicked((action) -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export report to xlsx");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel format", new String[]{"*.xlsx"}));
            chooser.setInitialFileName("Batch_" + this.dataModel.getBatchName() + "_.xlsx");
            File file = chooser.showSaveDialog(this.mainWindow);
            if (file != null) {
                this.consumer.accept(this.dataModel, file, (Runnable)() -> this.showErrorWindow("Report export", "Report exported successfully"));
            }

        });
        this.setScene(this.scene);
        this.setResizable(false);
        this.initOwner(this.mainWindow);
        this.initModality(Modality.NONE);
        this.setTitle("Batch details report");
        this.show();
    }

    private void tableGraphics() {
        Pane pane = new Pane();
        pane.setPrefSize((double)10.0F, (double)10.0F);
        this.table.setPadding(new Insets((double)10.0F));
        this.table.setSpacing((double)1.0F);
        this.table.getChildren().clear();
        this.table.getChildren().add(this.createTableHeader());
        this.dataModel.getData().stream().limit((long)(this.dataModel.getData().size() - 1)).forEach((item) -> this.table.getChildren().add(this.tableRow(item)));
        this.table.getChildren().addAll(new Node[]{pane});
        this.table.getChildren().addAll(new Node[]{this.totalsTableRow((ReportTableDataModel)this.dataModel.getData().get(this.dataModel.getData().size() - 1))});
    }

    private void HeaderGraphics() {
        Label label = new Label("Batch details report");
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:darkblue;-fx-font-size:22;-fx-font-family: 'Times New Roman';");
        label.setAlignment(Pos.BASELINE_LEFT);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPrefWidth(this.width * (double)2.0F / (double)3.0F);
        Label label2 = new Label("Circle for industrial software developments");
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:red;-fx-font-size:16;-fx-font-family: 'Arial';");
        label2.setAlignment(Pos.BASELINE_LEFT);
        label2.setTextAlignment(TextAlignment.CENTER);
        label2.setPrefWidth(this.width * (double)2.0F / (double)3.0F);
        ImageView imageView = new ImageView(new Image(Resources.getResource("Icons/stocks.png").toString()));
        imageView.setFitWidth((double)90.0F);
        imageView.setFitHeight((double)50.0F);
        this.Header.add(label, 1, 0);
        this.Header.add(label2, 1, 1);
        this.Header.add(imageView, 0, 0, 1, 2);
        this.Header.setHgap((double)10.0F);
        this.Header.setPadding(new Insets((double)0.0F));
        this.Header.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii((double)1.0F), new BorderWidths((double)1.0F))}));
        this.Header.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        this.Header.setMaxWidth(this.width);
    }

    private void FooterGraphics() {
        Label textArea = new Label();
        textArea.setText("");
        textArea.setStyle("-fx-font-weight:normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:7.5;-fx-font-family: 'Arial';");
        textArea.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F))}));
        textArea.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        textArea.setPrefWidth(this.width);
        textArea.setPrefHeight((double)80.0F);
        textArea.setAlignment(Pos.CENTER);
        textArea.setTextAlignment(TextAlignment.JUSTIFY);
        TextField Name = new TextField("Name");
        Name.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Name.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)1.0F))}));
        Name.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        Name.setAlignment(Pos.CENTER);
        Name.setPrefWidth(this.width / (double)10.0F * (double)3.0F);
        Name.setEditable(false);
        TextField Signature = new TextField("Signature");
        Signature.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Signature.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        Signature.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        Signature.setAlignment(Pos.CENTER);
        Signature.setPrefWidth(this.width / (double)10.0F * (double)2.0F);
        Signature.setEditable(false);
        TextField Name2 = new TextField("Name");
        Name2.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Name2.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        Name2.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        Name2.setAlignment(Pos.CENTER);
        Name2.setPrefWidth(this.width / (double)10.0F * (double)3.0F);
        Name2.setEditable(false);
        TextField Signature2 = new TextField("Signature");
        Signature2.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Signature2.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        Signature2.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        Signature2.setAlignment(Pos.CENTER);
        Signature2.setPrefWidth(this.width / (double)10.0F * (double)2.0F);
        Signature2.setEditable(false);
        TextField NameD = new TextField("");
        NameD.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        NameD.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)1.0F))}));
        NameD.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        NameD.setAlignment(Pos.CENTER);
        NameD.setPrefWidth(this.width / (double)10.0F * (double)3.0F);
        NameD.setEditable(false);
        TextField SignatureD = new TextField("");
        SignatureD.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        SignatureD.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        SignatureD.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        SignatureD.setAlignment(Pos.CENTER);
        SignatureD.setPrefWidth(this.width / (double)10.0F * (double)2.0F);
        SignatureD.setEditable(false);
        TextField Name2D = new TextField("");
        Name2D.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Name2D.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        Name2D.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        Name2D.setAlignment(Pos.CENTER);
        Name2D.setPrefWidth(this.width / (double)10.0F * (double)3.0F);
        Name2D.setEditable(false);
        TextField Signature2D = new TextField("");
        Signature2D.setStyle("-fx-font-weight:Normal;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        Signature2D.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        Signature2D.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        Signature2D.setAlignment(Pos.CENTER);
        Signature2D.setPrefWidth(this.width / (double)10.0F * (double)2.0F);
        Signature2D.setEditable(false);
        this.Footer.add(textArea, 0, 0, 4, 1);
        this.Footer.add(Name, 0, 1);
        this.Footer.add(Signature, 1, 1);
        this.Footer.add(Name2, 2, 1);
        this.Footer.add(Signature2, 3, 1);
        this.Footer.add(NameD, 0, 2);
        this.Footer.add(SignatureD, 1, 2);
        this.Footer.add(Name2D, 2, 2);
        this.Footer.add(Signature2D, 3, 2);
        this.Footer.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        this.Footer.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
    }

    private void mainDataGraphics() {
        HBox id = this.FieldTopRight("Batch ID", String.valueOf(this.dataModel.getBatchID()), this.width / (double)4.0F, this.width * (double)3.0F / (double)10.0F);
        HBox name = this.FieldCenterRight("Batch name ", this.dataModel.getBatchName(), this.width / (double)4.0F, this.width * (double)3.0F / (double)10.0F);
        HBox creationDate = this.FieldTopLeft("Creation Date", String.valueOf(this.dataModel.getCreationDate()), this.width / (double)4.0F, this.width * (double)3.0F / (double)10.0F);
        HBox creationTime = this.FieldCenterLeft("Creation time", String.valueOf(this.dataModel.getCreationTime()), this.width / (double)4.0F, this.width * (double)3.0F / (double)10.0F);
        HBox product = this.FieldBottomRight("Product name", String.valueOf(this.dataModel.getProduct()), this.width / (double)4.0F, this.width * (double)3.0F / (double)10.0F);
        HBox client = this.FieldBottomLeft("Client name", String.valueOf(this.dataModel.getClient()), this.width / (double)4.0F, this.width * (double)3.0F / (double)10.0F);
        HBox endDate = this.FieldCenterRight("End time", String.valueOf(this.dataModel.getEndTime()), this.width / (double)4.0F, this.width * (double)8.0F / (double)10.0F);
        HBox comment = this.FieldCenterRight("Comment", String.valueOf(this.dataModel.getComment()), this.width / (double)4.0F, this.width * (double)8.0F / (double)10.0F);
        this.mainData.add(id, 0, 0);
        this.mainData.add(creationDate, 1, 0);
        this.mainData.add(name, 0, 1);
        this.mainData.add(creationTime, 1, 1);
        this.mainData.add(product, 0, 2);
        this.mainData.add(client, 1, 2);
        this.mainData.add(endDate, 0, 3, 2, 1);
        this.mainData.add(comment, 0, 4, 2, 1);
        this.mainData.setPadding(new Insets((double)10.0F, (double)0.0F, (double)0.0F, (double)0.0F));
        this.mainData.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        this.mainData.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
    }

    private HBox FieldTopRight(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();
        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F))}));
        label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(this.TABLE_LABEL_COLOR, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label.setPrefWidth(LabelWidth - (double)1.0F);
        label.setEditable(false);
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)1.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label2.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);
        hbox.getChildren().addAll(new Node[]{label, label2});
        return hbox;
    }

    private HBox FieldTopLeft(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();
        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)1.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(this.TABLE_LABEL_COLOR, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label.setPrefWidth(LabelWidth - (double)1.0F);
        label.setEditable(false);
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)1.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label2.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);
        hbox.getChildren().addAll(new Node[]{label, label2});
        return hbox;
    }

    private HBox FieldBottomRight(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();
        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)1.0F))}));
        label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(this.TABLE_LABEL_COLOR, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label.setPrefWidth(LabelWidth - (double)1.0F);
        label.setEditable(false);
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label2.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);
        hbox.getChildren().addAll(new Node[]{label, label2});
        return hbox;
    }

    private HBox FieldBottomLeft(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();
        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(this.TABLE_LABEL_COLOR, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label.setPrefWidth(LabelWidth - (double)1.0F);
        label.setEditable(false);
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label2.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);
        hbox.getChildren().addAll(new Node[]{label, label2});
        return hbox;
    }

    private HBox FieldCenterRight(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();
        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)1.0F))}));
        label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(this.TABLE_LABEL_COLOR, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label.setPrefWidth(LabelWidth - (double)1.0F);
        label.setEditable(false);
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label2.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);
        hbox.getChildren().addAll(new Node[]{label, label2});
        return hbox;
    }

    private HBox FieldCenterLeft(String Label, String Data, double LabelWidth, double DatWidth) {
        HBox hbox = new HBox();
        TextField label = new TextField(Label);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(this.TABLE_LABEL_COLOR, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label.setPrefWidth(LabelWidth - (double)1.0F);
        label.setEditable(false);
        TextField label2 = new TextField(Data);
        label2.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:8;-fx-font-family: 'Times New Roman';");
        label2.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)1.0F, (double)0.0F))}));
        label2.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label2.setPrefWidth(DatWidth);
        label2.setEditable(false);
        hbox.getChildren().addAll(new Node[]{label, label2});
        return hbox;
    }

    private HBox FieldLabel(String text, double width) {
        Label label = new Label(" " + text);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:10;-fx-font-family: 'Times New Roman';");
        label.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)0.0F, (double)1.0F))}));
        label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(this.TABLE_LABEL_COLOR.saturate().darker(), new CornerRadii((double)0.0F), new Insets((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
        label.setPrefWidth(width);
        label.setPrefHeight((double)15.0F);
        HBox hbox = new HBox();
        hbox.getChildren().add(label);
        return hbox;
    }

    private HBox tableRow(ReportTableDataModel recordModel) {
        HBox hbox = new HBox();
        hbox.setSpacing((double)1.0F);
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
        No.setPrefWidth(this.width / (double)14.0F - (double)2.0F);
        material.setPrefWidth(this.width / (double)14.0F * (double)3.0F);
        required.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        loaded.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        error.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        requiredPercent.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        loadedPercent.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        No.setAlignment(Pos.BASELINE_LEFT);
        material.setAlignment(Pos.BASELINE_LEFT);
        required.setAlignment(Pos.BASELINE_LEFT);
        loaded.setAlignment(Pos.BASELINE_LEFT);
        error.setAlignment(Pos.BASELINE_LEFT);
        requiredPercent.setAlignment(Pos.BASELINE_LEFT);
        loadedPercent.setAlignment(Pos.BASELINE_LEFT);
        No.setPadding(new Insets((double)1.0F));
        material.setPadding(new Insets((double)1.0F));
        required.setPadding(new Insets((double)1.0F));
        loaded.setPadding(new Insets((double)1.0F));
        error.setPadding(new Insets((double)1.0F));
        requiredPercent.setPadding(new Insets((double)1.0F));
        loadedPercent.setPadding(new Insets((double)1.0F));
        No.setEditable(false);
        material.setEditable(true);
        required.setEditable(true);
        loaded.setEditable(true);
        error.setEditable(true);
        requiredPercent.setEditable(true);
        loadedPercent.setEditable(false);
        No.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        material.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        required.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        loaded.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        error.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        requiredPercent.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        loadedPercent.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        No.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        material.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        required.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        loaded.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        error.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        requiredPercent.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        loadedPercent.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        hbox.getChildren().addAll(new Node[]{No, material, required, loaded, error, requiredPercent, loadedPercent});
        return hbox;
    }

    private HBox totalsTableRow(ReportTableDataModel recordModel) {
        HBox hbox = new HBox();
        hbox.setSpacing((double)1.0F);
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
        Total.setPrefWidth(this.width / (double)14.0F * (double)4.0F - (double)2.0F);
        required.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        loaded.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        error.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        requiredPercent.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        loadedPercent.setPrefWidth(this.width / (double)14.0F * (double)2.0F);
        Total.setAlignment(Pos.BASELINE_LEFT);
        required.setAlignment(Pos.BASELINE_LEFT);
        loaded.setAlignment(Pos.BASELINE_LEFT);
        error.setAlignment(Pos.BASELINE_LEFT);
        requiredPercent.setAlignment(Pos.BASELINE_LEFT);
        loadedPercent.setAlignment(Pos.BASELINE_LEFT);
        Total.setPadding(new Insets((double)0.0F));
        required.setPadding(new Insets((double)0.0F));
        loaded.setPadding(new Insets((double)0.0F));
        error.setPadding(new Insets((double)0.0F));
        requiredPercent.setPadding(new Insets((double)0.0F));
        loadedPercent.setPadding(new Insets((double)0.0F));
        Total.setEditable(true);
        required.setEditable(true);
        loaded.setEditable(true);
        error.setEditable(true);
        requiredPercent.setEditable(true);
        loadedPercent.setEditable(false);
        Total.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        required.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        loaded.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        error.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        requiredPercent.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        loadedPercent.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F))}));
        Total.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        required.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        loaded.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        error.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        requiredPercent.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        loadedPercent.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        hbox.getChildren().addAll(new Node[]{Total, required, loaded, error, requiredPercent, loadedPercent});
        return hbox;
    }

    private HBox createTableHeader() {
        HBox hbox = new HBox();
        hbox.setSpacing((double)1.0F);
        hbox.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, new CornerRadii((double)0.0F), Insets.EMPTY)}));
        hbox.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F))}));
        Label number = new Label("No");
        number.setAlignment(Pos.CENTER);
        number.setTextAlignment(TextAlignment.CENTER);
        number.setPrefSize(this.width / (double)14.0F, (double)40.0F);
        number.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)0.0F, (double)0.0F))}));
        Label material = new Label("Material name");
        material.setAlignment(Pos.CENTER);
        material.setTextAlignment(TextAlignment.CENTER);
        material.setPrefSize(this.width / (double)14.0F * (double)3.0F, (double)40.0F);
        material.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)0.0F, (double)0.0F))}));
        Label required = new Label("Required");
        required.setAlignment(Pos.CENTER);
        required.setTextAlignment(TextAlignment.CENTER);
        required.setPrefSize(this.width / (double)7.0F, (double)40.0F);
        required.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)0.0F, (double)0.0F))}));
        Label loaded = new Label("Loaded");
        loaded.setAlignment(Pos.CENTER);
        loaded.setTextAlignment(TextAlignment.CENTER);
        loaded.setPrefSize(this.width / (double)7.0F - (double)1.0F, (double)40.0F);
        loaded.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)0.0F, (double)0.0F))}));
        Label error = new Label("Error");
        error.setAlignment(Pos.CENTER);
        error.setTextAlignment(TextAlignment.CENTER);
        error.setPrefSize(this.width / (double)7.0F, (double)40.0F);
        error.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)0.0F, (double)0.0F))}));
        Label rewuiredPercentage = new Label("Required %");
        rewuiredPercentage.setAlignment(Pos.CENTER);
        rewuiredPercentage.setTextAlignment(TextAlignment.CENTER);
        rewuiredPercentage.setPrefSize(this.width / (double)7.0F - (double)1.0F, (double)40.0F);
        rewuiredPercentage.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)0.0F, (double)0.0F))}));
        Label actualPercentage = new Label("Actual %");
        actualPercentage.setAlignment(Pos.CENTER);
        actualPercentage.setTextAlignment(TextAlignment.CENTER);
        actualPercentage.setPrefSize(this.width / (double)7.0F - (double)1.0F, (double)40.0F);
        actualPercentage.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii((double)0.0F), new BorderWidths((double)0.0F, (double)1.0F, (double)0.0F, (double)0.0F))}));
        hbox.getChildren().addAll(new Node[]{number, material, required, loaded, error, rewuiredPercentage, actualPercentage});
        return hbox;
    }

    private void showErrorWindow(String header, String content) {
        Platform.runLater(() -> {
            Alert Error = new Alert(AlertType.INFORMATION);
            Error.setTitle("Error ");
            Error.setHeaderText(header);
            Error.setContentText(content);
            Error.initOwner(this);
            Error.show();
        });
    }
}
