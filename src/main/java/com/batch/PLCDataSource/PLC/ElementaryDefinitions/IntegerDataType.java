
package com.batch.PLCDataSource.PLC.ElementaryDefinitions;

import javafx.beans.property.SimpleIntegerProperty;


public class IntegerDataType extends SimpleIntegerProperty implements ValueObject{
    public IntegerDataType(int initialValue) {
        super(initialValue);
    }
}
