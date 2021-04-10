
package com.batch.PLCDataSource.PLC.ElementaryDefinitions;

public class Address {
    private int byteNumber;
    private int bitNumber;

    public Address(int byteNumber, int bitNumber) {
        this.byteNumber = byteNumber;
        this.bitNumber = bitNumber;
    }

    @Override
    public String toString() {
        return byteNumber + "." + bitNumber;
    }

    public int getByteNumber() {
        return byteNumber;
    }

    public void setByteNumber(int byteNumber) {
        this.byteNumber = byteNumber;
    }

    public int getBitNumber() {
        return bitNumber;
    }

    public void setBitNumber(int bitNumber) {
        this.bitNumber = bitNumber;
    } 
    
    
}
