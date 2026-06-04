// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/bluetooth/BluetoothDeviceConnection.java
package com.volqorstudios.thermalprinter.connection.bluetooth;

import java.util.UUID;
import java.util.Arrays;
import java.io.IOException;

import android.os.ParcelUuid;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter;

import com.volqorstudios.thermalprinter.connection.DeviceConnection;
import com.volqorstudios.thermalprinter.exceptions.PrinterConnectionException;





public class BluetoothDeviceConnection extends DeviceConnection {
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket = null;

    public BluetoothDeviceConnection(BluetoothDevice device) {
        super();
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return this.device;
    }

    @Override
    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && super.isConnected();
    }

    @SuppressLint("MissingPermission")
    @Override
    public BluetoothDeviceConnection connect() throws PrinterConnectionException {
        if (this.isConnected()) {
            return this;
        }

        if (this.device == null) {
            throw new PrinterConnectionException("Bluetooth device is not connected.");
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        UUID uuid = this.resolveDeviceUUID();
        
        try {
            this.socket = this.device.createRfcommSocketToServiceRecord(uuid);
            adapter.cancelDiscovery();
            this.socket.connect();
            this.outputStream = this.socket.getOutputStream();
            this.data = new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            this.disconnect();
            throw new PrinterConnectionException("Unable to connect to bluetooth device.");
        }

        return this;
    }

    protected UUID resolveDeviceUUID() {
        ParcelUuid[] uuids = this.device.getUuids();
        
        if (uuids != null && uuids.length > 0) {
            if (Arrays.asList(uuids).contains(new ParcelUuid(BluetoothDeviceConnection.SPP_UUID))) {
                return BluetoothDeviceConnection.SPP_UUID;
            }

            return uuids[0].getUuid();
        }

        return BluetoothDeviceConnection.SPP_UUID;
    }

    @Override
    public BluetoothDeviceConnection disconnect() {
        this.data = new byte[0];
        
        if (this.outputStream != null) {
            try {
                this.outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.outputStream = null;
        }
        
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.socket = null;
        }
        
        return this;
    }
}