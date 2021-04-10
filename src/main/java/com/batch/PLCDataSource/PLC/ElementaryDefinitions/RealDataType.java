
package com.batch.PLCDataSource.PLC.ElementaryDefinitions;

import javafx.beans.property.SimpleFloatProperty;


public class RealDataType extends SimpleFloatProperty implements ValueObject {

    public RealDataType(float initialValue) {
        super(initialValue);
    }
    
}
