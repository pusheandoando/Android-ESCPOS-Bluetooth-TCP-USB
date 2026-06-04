// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/bluetooth/BluetoothPrinterScanner.java
package com.volqorstudios.thermalprinter.connection.bluetooth;

import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

import com.volqorstudios.thermalprinter.exceptions.PrinterConnectionException;





public class BluetoothPrinterScanner extends BluetoothScanner {
    @Nullable
    public static BluetoothDeviceConnection selectFirstPaired() {
        BluetoothPrinterScanner scanner = new BluetoothPrinterScanner();
        BluetoothDeviceConnection[] printers = scanner.getList();
        
        if (printers != null && printers.length > 0) {
            for (BluetoothDeviceConnection printer : printers) {
                try {
                    return printer.connect();
                } catch (PrinterConnectionException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public BluetoothDeviceConnection[] getList() {
        BluetoothDeviceConnection[] allDevices = super.getList();
        
        if (allDevices == null) {
            return null;
        }

        int count = 0;
        BluetoothDeviceConnection[] printersTmp = new BluetoothDeviceConnection[allDevices.length];
        for (BluetoothDeviceConnection connection : allDevices) {
            BluetoothDevice device = connection.getDevice();
            int majorClass = device.getBluetoothClass().getMajorDeviceClass();
            int deviceClass = device.getBluetoothClass().getDeviceClass();
            
            if (majorClass == BluetoothClass.Device.Major.IMAGING && (deviceClass == 1664 || deviceClass == BluetoothClass.Device.Major.IMAGING)) {
                printersTmp[count++] = new BluetoothDeviceConnection(device);
            }
        }
        
        BluetoothDeviceConnection[] printers = new BluetoothDeviceConnection[count];
        System.arraycopy(printersTmp, 0, printers, 0, count);
        return printers;
    }
}