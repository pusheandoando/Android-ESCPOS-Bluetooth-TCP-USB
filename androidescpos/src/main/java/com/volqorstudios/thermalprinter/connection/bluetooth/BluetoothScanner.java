// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/bluetooth/BluetoothScanner.java
package com.volqorstudios.thermalprinter.connection.bluetooth;

import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;

import java.util.Set;





public class BluetoothScanner {
    protected BluetoothAdapter bluetoothAdapter;

    public BluetoothScanner() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @SuppressLint("MissingPermission")
    @Nullable
    public BluetoothDeviceConnection[] getList() {
        if (this.bluetoothAdapter == null) {
            return null;
        }

        if (!this.bluetoothAdapter.isEnabled()) {
            return null;
        }

        Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
        BluetoothDeviceConnection[] connections = new BluetoothDeviceConnection[bondedDevices.size()];
        
        int i = 0;
        for (BluetoothDevice device : bondedDevices) {
            connections[i++] = new BluetoothDeviceConnection(device);
        }
        
        return connections;
    }
}