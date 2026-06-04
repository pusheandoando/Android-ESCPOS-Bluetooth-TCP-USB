// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/DeviceConnection.java
package com.volqorstudios.thermalprinter.connection;

import java.io.IOException;
import java.io.OutputStream;

import com.volqorstudios.thermalprinter.exceptions.PrinterConnectionException;





public abstract class DeviceConnection {
    protected OutputStream outputStream;
    protected byte[] data;

    public DeviceConnection() {
        this.outputStream = null;
        this.data = new byte[0];
    }

    public abstract DeviceConnection connect() throws PrinterConnectionException;
    public abstract DeviceConnection disconnect();

    public boolean isConnected() {
        return this.outputStream != null;
    }

    public void write(byte[] bytes) {
        byte[] merged = new byte[bytes.length + this.data.length];
        System.arraycopy(this.data, 0, merged, 0, this.data.length);
        System.arraycopy(bytes, 0, merged, this.data.length, bytes.length);
        this.data = merged;
    }

    public void send() throws PrinterConnectionException {
        this.send(0);
    }

    public void send(int extraWaitMs) throws PrinterConnectionException {
        if (!this.isConnected()) {
            throw new PrinterConnectionException("Unable to send data to device.");
        }

        try {
            this.outputStream.write(this.data);
            this.outputStream.flush();
            
            int waitMs = extraWaitMs + this.data.length / 16;
            
            this.data = new byte[0];
            
            if (waitMs > 0) {
                Thread.sleep(waitMs);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new PrinterConnectionException(e.getMessage());
        }
    }
}