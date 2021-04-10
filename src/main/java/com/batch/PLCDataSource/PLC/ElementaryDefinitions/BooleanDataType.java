
package com.batch.PLCDataSource.PLC.ElementaryDefinitions;

import javafx.beans.property.SimpleBooleanProperty;


public class BooleanDataType extends SimpleBooleanProperty implements ValueObject{
    public BooleanDataType(boolean initialValue) {
        super(initialValue);
    }
}
