package com.batch.Services.LoggingService;


import com.batch.PLCDataSource.PLC.ComplexDataType.RowAttripute;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.EDT;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.ValueObject;
import com.batch.Utilities.LogIdentefires;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDataHolder {
    private String name;
    private RowAttripute attribute;
    private ValueObject value;
    private EDT type;
    private LogIdentefires identifier;
}
