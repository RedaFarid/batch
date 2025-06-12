package com.batch.GUI.Controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.controlsfx.tools.Borders;

public class DataEntryPartition extends VBox {
    private final GridPane pane = new GridPane();
    private final Label partitionLabel = new Label();

    public DataEntryPartition(String label) {
        this.partitionLabel.setText(label);
        this.partitionLabel.setAlignment(Pos.BASELINE_LEFT);
        this.partitionLabel.setTextAlignment(TextAlignment.LEFT);
        this.partitionLabel.prefWidthProperty().bind(this.widthProperty());
        this.partitionLabel.setBackground(new Background(new BackgroundFill(Color.valueOf("61a2b1"), new CornerRadii(0.0F), new Insets(0.0F))));
        this.partitionLabel.setPadding(new Insets(3.0F, 0.0F, 0.0F, 10.0F));
        this.partitionLabel.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.GRAY, 0.1, 0.1, 0.1, 0.1));
        this.partitionLabel.setStyle("-fx-text-fill:white;");
        this.pane.setVgap(2.0F);
        this.pane.setHgap(5.0F);
        this.pane.prefHeightProperty().bindBidirectional(this.prefHeightProperty());
        Node calculateBorder2 = Borders.wrap(this.pane).lineBorder().radius(0.0F).thickness(1.5F).color(Color.valueOf("61a2b1")).title(label).innerPadding(5.0F).buildAll();
        this.getChildren().addAll(calculateBorder2);
    }

    public final void add(Node node, int i, int i1) {
        this.pane.add(node, i, i1);
    }

    public final void add(Node node, int i, int i1, int i2, int i3) {
        this.pane.add(node, i, i1, i2, i3);
    }

    public final void addRow(int i, Node... nodes) {
        this.pane.addRow(i, nodes);
    }

    public final void addColumn(int i, Node... nodes) {
        this.pane.addColumn(i, nodes);
    }

    public final void setVgap(double var1) {
        this.pane.vgapProperty().set(var1);
    }

    public final void setHgap(double var1) {
        this.pane.hgapProperty().set(var1);
    }

    public final void setPanePadding(Insets var1) {
        this.pane.paddingProperty().set(var1);
    }
}
