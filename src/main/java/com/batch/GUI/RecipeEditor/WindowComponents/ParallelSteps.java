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

    private ParallelStepsModel model ;
    
    public ParallelSteps() {
        model = new ParallelStepsModel();
        initialization();
    }

    private void initialization() {
        setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 1, 0))));
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
        setMinHeight(50);
        getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
                setMaxWidth(getChildren().size() * 250.0 + 100.0);
                if (getChildren().size() == 1) {
                    setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 0))));
                } else {
                    setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 1, 0))));
                }
            }
        });
    }

    public ParallelStepsModel getModel() {
        return model;
    }

    public void setModel(ParallelStepsModel model) {
        this.model = model;
    }
    
    
}
