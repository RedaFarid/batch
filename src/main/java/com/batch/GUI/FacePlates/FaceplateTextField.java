
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
    private Callback<String, Double> callback = (String param) -> null;
            
    public FaceplateTextField() {
        back = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
        intialize();
    }
    
    private void intialize(){
        setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.5))));
        addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (isEditable()) {
                switch (event.getCode()) {
                    case ENTER:
                        callback.call("");
                        setBackground(back);
                        break;
                    case ESCAPE:
                        break;
                    case TAB:
                        break;
                    default:
                        setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                        break;
                }
            }
        });
    }
    
    public void onEneterKeyPressed(Callback<String, Double> callback){
        this.callback = callback;
    }
}
