

package com.batch.GUI.FacePlates;

import com.batch.Utilities.RestrictiveTextField;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class FaceplateTextField extends RestrictiveTextField {
    private Background back;
    private Callback<String, Double> callback = (param) -> null;

    public FaceplateTextField() {
        this.back = new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)});
        this.initialize();
    }

    private void initialize() {
        this.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths((double)0.5F))}));
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Float.parseFloat(newValue);
            } catch (Exception var5) {
                this.setText("0.0");
            }

        });
        this.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (this.isEditable()) {
                switch (event.getCode()) {
                    case ENTER:
                        this.callback.call("");
                        this.setBackground(this.back);
                    case ESCAPE:
                    case TAB:
                        break;
                    default:
                        this.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)}));
                }
            }

        });
    }

    public void onEneterKeyPressed(Callback<String, Double> callback) {
        this.callback = callback;
    }
}
