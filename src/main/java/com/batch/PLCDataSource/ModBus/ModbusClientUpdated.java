package com.batch.PLCDataSource.ModBus;


import com.batch.PLCDataSource.ModBus.Exceptions.PacketShiftException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class ModbusClientUpdated {
    private AtomicInteger transactionIdentifierCounter = new AtomicInteger(0);
    private Socket tcpClientSocket = new Socket();
    protected String ipAddress = "190.168.0.2";
    protected int port = 502;
    private byte[] transactionIdentifier = new byte[2];
    private byte[] protocolIdentifier = new byte[2];
    private byte[] length = new byte[2];
    private byte[] crc = new byte[2];
    private byte unitIdentifier = 1;
    private byte functionCode;
    private byte[] startingAddress = new byte[2];
    private byte[] quantity = new byte[2];
    private boolean udpFlag = false;
    private int connectTimeout = 500;
    private InputStream inStream;
    private DataOutputStream outStream;
    public byte[] receiveData;
    public byte[] sendData;

    public ModbusClientUpdated(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void Connect() throws Exception {
        this.tcpClientSocket = new Socket(this.ipAddress, this.port);
        this.tcpClientSocket.setSoTimeout(this.connectTimeout);
        this.tcpClientSocket.setKeepAlive(true);
        this.outStream = new DataOutputStream(this.tcpClientSocket.getOutputStream());
        this.inStream = this.tcpClientSocket.getInputStream();
    }

    public synchronized int[] ReadHoldingRegisters(int startingAddress, int quantity) throws Exception {
        if (transactionIdentifierCounter.get() > 120) {
            transactionIdentifierCounter.set(0);
        }
        if (this.tcpClientSocket == null) {
            throw new Exception("connection Error");
        } else if (startingAddress > 65535 | quantity > 120) {
            throw new IllegalArgumentException("Starting adress must be 0 - 65535; quantity must be 0 - 125");
        } else {
            int[] response = new int[quantity];
            this.transactionIdentifier = toByteArray(transactionIdentifierCounter.getAndIncrement());
            this.protocolIdentifier = toByteArray(0);
            this.length = toByteArray(6);
            this.functionCode = 3;
            this.startingAddress = toByteArray(startingAddress);
            this.quantity = toByteArray(quantity);
            byte[] data = new byte[]{
                    this.transactionIdentifier[1],
                    this.transactionIdentifier[0],
                    this.protocolIdentifier[1],
                    this.protocolIdentifier[0],
                    this.length[1],
                    this.length[0],
                    this.unitIdentifier,
                    this.functionCode,
                    this.startingAddress[1],
                    this.startingAddress[0],
                    this.quantity[1],
                    this.quantity[0],
                    this.crc[0],
                    this.crc[1]};
            int i = 0;
            if (this.tcpClientSocket.isConnected()) {
                this.outStream.write(data, 0, data.length - 2);
                data = new byte[2100];
                i = this.inStream.read(data, 0, data.length);
            }
            if ((data[0] != transactionIdentifier[1]) || (data[1] != transactionIdentifier[0])) {
                System.err.println("Recieve " + data[0] + " " + data[1] + " Send " + transactionIdentifier[1] + " " + transactionIdentifier[0] + "        " + i);
                throw new PacketShiftException();
            }

            if (data[7] == 131 & data[8] == 1) {
                throw new Exception("Function code not supported by master");
            } else if (data[7] == 131 & data[8] == 2) {
                throw new Exception("Starting adress invalid or starting adress + quantity invalid");
            } else if (data[7] == 131 & data[8] == 3) {
                throw new Exception("Quantity invalid");
            } else if (data[7] == 131 & data[8] == 4) {
                throw new Exception("Error reading");
            } else {
                for (i = 0; i < quantity; ++i) {
                    byte[] bytes = new byte[]{data[9 + i * 2], data[9 + i * 2 + 1]};
                    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                    response[i] = byteBuffer.getShort();
                }
                return response;
            }
        }
    }

    public synchronized void WriteSingleRegister(int startingAddress, int value) throws Exception {
        if (this.tcpClientSocket == null & !this.udpFlag) {
            throw new Exception("connection error");
        } else {
            byte[] registerValue = new byte[2];
            this.transactionIdentifier = toByteArray(transactionIdentifierCounter.getAndIncrement());
            this.protocolIdentifier = toByteArray(0);
            this.length = toByteArray(6);
            this.functionCode = 6;
            this.startingAddress = toByteArray(startingAddress);
            registerValue = toByteArray((short) value);
            byte[] data = new byte[]{this.transactionIdentifier[1], this.transactionIdentifier[0], this.protocolIdentifier[1], this.protocolIdentifier[0], this.length[1], this.length[0], this.unitIdentifier, this.functionCode, this.startingAddress[1], this.startingAddress[0], registerValue[1], registerValue[0], this.crc[0], this.crc[1]};

            if (this.tcpClientSocket.isConnected()) {
                this.outStream.write(data, 0, data.length - 2);
                data = new byte[2100];
                int numberOfBytes = this.inStream.read(data, 0, data.length);
            }
            if ((data[0] != transactionIdentifier[1]) || (data[1] != transactionIdentifier[0])) {
                System.err.println("Send " + data[0] + " " + data[1] + " Send " + transactionIdentifier[1] + " " + transactionIdentifier[0]);
                throw new PacketShiftException();
            }

            if ((data[7] & 255) == 134 & data[8] == 1) {
                throw new Exception("Function code not supported by master");
            } else if ((data[7] & 255) == 134 & data[8] == 2) {
                throw new Exception("Starting address invalid or starting address + quantity invalid");
            } else if ((data[7] & 255) == 134 & data[8] == 3) {
                throw new Exception("quantity invalid");
            } else if ((data[7] & 255) == 134 & data[8] == 4) {
                throw new Exception("error reading");
            }
        }
    }

    public synchronized void WriteMultipleRegisters(int startingAddress, int[] values) throws Exception {
        byte byteCount = (byte) (values.length * 2);
        byte[] quantityOfOutputs = toByteArray(values.length);
        if (this.tcpClientSocket == null & !this.udpFlag) {
            throw new Exception("connection error");
        } else {
            this.transactionIdentifier = toByteArray(transactionIdentifierCounter.getAndIncrement());
            this.protocolIdentifier = toByteArray(0);
            this.length = toByteArray(7 + values.length * 2);
            this.functionCode = 16;
            this.startingAddress = toByteArray(startingAddress);
            byte[] data = new byte[15 + values.length * 2];
            data[0] = this.transactionIdentifier[1];
            data[1] = this.transactionIdentifier[0];
            data[2] = this.protocolIdentifier[1];
            data[3] = this.protocolIdentifier[0];
            data[4] = this.length[1];
            data[5] = this.length[0];
            data[6] = this.unitIdentifier;
            data[7] = this.functionCode;
            data[8] = this.startingAddress[1];
            data[9] = this.startingAddress[0];
            data[10] = quantityOfOutputs[1];
            data[11] = quantityOfOutputs[0];
            data[12] = byteCount;

            for (int i = 0; i < values.length; ++i) {
                byte[] singleRegisterValue = toByteArray(values[i]);
                data[13 + i * 2] = singleRegisterValue[1];
                data[14 + i * 2] = singleRegisterValue[0];
            }

            if (this.tcpClientSocket.isConnected()) {
                this.outStream.write(data, 0, data.length - 2);
                data = new byte[2100];
                int numberOfBytes = this.inStream.read(data, 0, data.length);
            }
            if ((data[0] != transactionIdentifier[1]) || (data[1] != transactionIdentifier[0])) {
                System.err.println("Send " + data[0] + " " + data[1] + " Send " + transactionIdentifier[1] + " " + transactionIdentifier[0]);
                throw new PacketShiftException();
            }

            if ((data[7] & 255) == 144 & data[8] == 1) {
                throw new Exception("Function code not supported by master");
            } else if ((data[7] & 255) == 144 & data[8] == 2) {
                throw new Exception("Starting address invalid or starting address + quantity invalid");
            } else if ((data[7] & 255) == 144 & data[8] == 3) {
                throw new Exception("quantity invalid");
            } else if ((data[7] & 255) == 144 & data[8] == 4) {
                throw new Exception("error reading");
            }
        }
    }

    public void Disconnect() throws IOException {
        if (this.inStream != null) {
            this.inStream.close();
        }

        if (this.outStream != null) {
            this.outStream.close();
        }

        if (this.tcpClientSocket != null) {
            this.tcpClientSocket.close();
        }

        this.tcpClientSocket = null;
    }

    public static byte[] toByteArray(int value) {
        byte[] result = new byte[]{(byte) value, (byte) (value >> 8)};
        return result;
    }

    public boolean isConnected() {
        if ((tcpClientSocket != null)) {
            return (tcpClientSocket.isConnected() && (outStream != null) && (inStream != null));
        }
        return false;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getUDPFlag() {
        return this.udpFlag;
    }

    public void setUDPFlag(boolean udpFlag) {
        this.udpFlag = udpFlag;
    }

    public int getConnectionTimeout() {
        return this.connectTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectTimeout = connectionTimeout;
    }

    public void setUnitIdentifier(byte unitIdentifier) {
        this.unitIdentifier = unitIdentifier;
    }

    public byte getUnitIdentifier() {
        return this.unitIdentifier;
    }
}