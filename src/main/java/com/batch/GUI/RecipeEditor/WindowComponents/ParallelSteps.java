
package com.batch.GUI.RecipeEditor.WindowComponents;

import com.batch.DTO.RecipeSystemDataDefinitions.ParallelStepsModel;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ParallelSteps extends HBox {
    private ParallelStepsModel model = new ParallelStepsModel();

    public ParallelSteps() {
        this.initialization();
    }

    private void initialization() {
        this.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths((double)1.0F, (double)0.0F, (double)1.0F, (double)0.0F))}));
        this.setSpacing((double)10.0F);
        this.setPadding(new Insets((double)10.0F));
        this.setAlignment(Pos.CENTER);
        this.setMinHeight((double)50.0F);
        this.getChildren().addListener(new ListChangeListener<Node>() {
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
                ParallelSteps.this.setMaxWidth((double)ParallelSteps.this.getChildren().size() * (double)250.0F + (double)100.0F);
                if (ParallelSteps.this.getChildren().size() == 1) {
                    ParallelSteps.this.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths((double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F))}));
                } else {
                    ParallelSteps.this.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths((double)1.0F, (double)0.0F, (double)1.0F, (double)0.0F))}));
                }

            }
        });
    }

    public ParallelStepsModel getModel() {
        return this.model;
    }

    public void setModel(ParallelStepsModel model) {
        this.model = model;
    }
}
