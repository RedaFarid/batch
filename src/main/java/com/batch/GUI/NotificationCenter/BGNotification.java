package com.batch.GUI.NotificationCenter;

import com.batch.Services.NotificationService.BackgroundServicesNotifier.BGAcknowledgementObject;
import com.batch.Services.NotificationService.ErrorObject;
import com.google.common.base.Objects;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;

public class BGNotification extends BorderPane {
    private static final Logger log = LogManager.getLogger(BGNotification.class);
    private final ErrorObject errorObject;
    private final Label header;
    private final ToolBar toolBar;
    private final VBox messageContent;
    private final Button reset;
    private final Button exportCSV;
    private final Button exportExcel;
    private final ScrollPane scrollPane;
    private final NCController controller;
    private final ObjectProperty<Cursor> CURSOR_DEFAULT;
    private final ObjectProperty<Cursor> CURSOR_WAIT;

    public BGNotification(ErrorObject errorObject) {
        this.CURSOR_DEFAULT = new SimpleObjectProperty(Cursor.DEFAULT);
        this.CURSOR_WAIT = new SimpleObjectProperty(Cursor.WAIT);
        this.errorObject = errorObject;
        String var10003 = StringUtils.capitalize(errorObject.getErrorFamily());
        this.header = new Label(var10003 + "\n" + LocalDateTime.now());
        this.messageContent = new VBox();
        this.reset = new Button("Reset all");
        this.toolBar = new ToolBar();
        this.exportCSV = new Button("Export CSV");
        this.exportExcel = new Button("Export excel");
        this.scrollPane = new ScrollPane();
        this.controller = new NCController();
        this.graphicsBuilder();
        this.errorHandler();
    }

    private void errorHandler() {
        this.errorObject.getErrorMessage().addListener((SetChangeListener<String>) change -> {
            try {
                String elementAdded = change.getElementAdded();
                if (elementAdded != null) {
                    Platform.runLater(() -> this.messageContent.getChildren().add(this.getMessage(elementAdded)));
                }

                String elementRemoved = change.getElementRemoved();
                if (elementRemoved != null) {
                    Platform.runLater(() -> this.messageContent.getChildren().remove(new BasicLabel(elementRemoved)));
                }
            } catch (Exception e) {
                log.fatal(e, e);
            }

        });
        this.reset.setOnMouseClicked((action) -> {
            ReadOnlyBooleanProperty readOnlyBooleanProperty = this.errorObject.resetError(new BGAcknowledgementObject(this.errorObject.getServiceName(), this.errorObject.getErrorFamily()));
            this.cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
        });
        this.exportCSV.setOnMouseClicked(this::exportCSV);
        this.exportExcel.setOnMouseClicked(this::exportExcel);
    }

    private void exportCSV(MouseEvent mouseEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export BackGround notifications to csv");
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("csv", "csv"));
        chooser.setInitialFileName("BackGround.csv");
        File file = chooser.showSaveDialog(this.getScene().getWindow());
        this.controller.onExportCSV(file, this.errorObject);
    }

    private void exportExcel(MouseEvent mouseEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Background notifications to excel");
        chooser.setInitialFileName("Background.xlsx");
        File file = chooser.showSaveDialog(this.getScene().getWindow());
        this.controller.onExportExcel(file, this.errorObject);
    }

    private BGNotification getThis() {
        return this;
    }

    private void graphicsBuilder() {
        this.header.setBackground(new Background(new BackgroundFill(Color.WHITE.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        this.header.setAlignment(Pos.CENTER_LEFT);
        this.header.prefWidthProperty().bind(this.widthProperty());
        this.header.setPadding(new Insets(0.0F, 0.0F, 10.0F, 5.0F));
        this.header.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:black;-fx-font-size:17;-fx-font-family: 'Times New Roman';");
        this.toolBar.setBackground(new Background(new BackgroundFill(Color.WHITE.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        this.toolBar.getItems().addAll(this.reset, this.exportCSV, this.exportExcel);
        this.scrollPane.setMaxHeight(250.0F);
        this.scrollPane.setContent(this.messageContent);
        this.reset.setPrefWidth(150.0F);
        this.exportCSV.setPrefWidth(150.0F);
        this.exportExcel.setPrefWidth(150.0F);
        this.errorObject.getErrorMessageList().forEach((message) -> this.messageContent.getChildren().add(this.getMessage(message)));
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5.0F), Insets.EMPTY)));
        this.setEffect(new DropShadow(5.0F, Color.GRAY));
        this.setPadding(new Insets(10.0F));
        this.setTop(new VBox(this.header, this.toolBar));
        this.setCenter(this.scrollPane);
    }

    private BasicLabel getMessage(String message) {
        BasicLabel label = new BasicLabel(message);
        label.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        label.prefWidthProperty().bind(this.widthProperty().subtract(40));
        label.setPadding(new Insets(0.0F, 0.0F, 0.0F, 5.0F));
        label.setOnMouseClicked((action) -> {
            if (action.getClickCount() == 2) {
                label.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
            }

        });
        return label;
    }

    private static class BasicLabel extends Label {
        private final String s;

        public BasicLabel(String s) {
            this.s = s;
            this.setText(s);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                BasicLabel hashLabel = (BasicLabel) o;
                return Objects.equal(this.s, hashLabel.s);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hashCode(this.s);
        }
    }
}
