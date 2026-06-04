// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/usb/UsbConnection.java
package com.volqorstudios.thermalprinter.connection.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.io.IOException;

import com.volqorstudios.thermalprinter.connection.DeviceConnection;
import com.volqorstudios.thermalprinter.exceptions.PrinterConnectionException;





public class UsbConnection extends DeviceConnection {
    private UsbManager usbManager;
    private UsbDevice usbDevice;

    public UsbConnection(UsbManager usbManager, UsbDevice usbDevice) {
        super();
        this.usbManager = usbManager;
        this.usbDevice = usbDevice;
    }

    public UsbDevice getDevice() {
        return this.usbDevice;
    }

    @Override
    public UsbConnection connect() throws PrinterConnectionException {
        if (this.isConnected()) {
            return this;
        }

        try {
            this.outputStream = new UsbOutputStream(this.usbManager, this.usbDevice);
            this.data = new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            this.outputStream = null;
            throw new PrinterConnectionException("Unable to connect to USB device.");
        }

        return this;
    }

    @Override
    public UsbConnection disconnect() {
        this.data = new byte[0];
        if (this.isConnected()) {
            try {
                this.outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.outputStream = null;
        }
        
        return this;
    }

    @Override
    public void send() throws PrinterConnectionException {
        this.send(0);
    }

    @Override
    public void send(int extraWaitMs) throws PrinterConnectionException {
        try {
            this.outputStream.write(this.data);
            this.data = new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            throw new PrinterConnectionException(e.getMessage());
        }
    }
}