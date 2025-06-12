
package com.batch.PLCDataSource.PLC.ElementaryDefinitions;

public class Address {
    private int byteNumber;
    private int bitNumber;

    public Address(int byteNumber, int bitNumber) {
        this.byteNumber = byteNumber;
        this.bitNumber = bitNumber;
    }

    public String toString() {
        return this.byteNumber + "." + this.bitNumber;
    }

    public int getByteNumber() {
        return this.byteNumber;
    }

    public void setByteNumber(int byteNumber) {
        this.byteNumber = byteNumber;
    }

    public int getBitNumber() {
        return this.bitNumber;
    }

    public void setBitNumber(int bitNumber) {
        this.bitNumber = bitNumber;
    }
}
