package com.batch.Utilities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class RestrictiveTextField extends TextField {

    private IntegerProperty maxLength = new SimpleIntegerProperty(this, "maxLength", -1);
    private StringProperty restrict = new SimpleStringProperty(this, "restrict");

    public RestrictiveTextField() {
        textProperty().addListener(new ChangeListener<String>() {
            private boolean ignore;
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s1) {
                setText(s1.toUpperCase());
                if (ignore || s1 == null) {
                    return;
                }
                if (maxLength.get() > -1 && s1.length() > maxLength.get()) {
                    ignore = true;
                    setText(s1.substring(0, maxLength.get()));
                    ignore = false;
                }
                if (restrict.get() != null && !restrict.get().equals("") && !s1.matches(restrict.get() + "*")) {
                    ignore = true;
                    setText(s);
                    ignore = false;
                }
            }
        });
    }

    public IntegerProperty maxLengthProperty() {
        return maxLength;
    }

    public int getMaxLength() {
        return maxLength.get();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength.set(maxLength);
    }

    public StringProperty restrictProperty() {
        return restrict;
    }

    public String getRestrict() {
        return restrict.get();
    }

    public void setRestrict(String restrict) {
        this.restrict.set(restrict);
    }
}
