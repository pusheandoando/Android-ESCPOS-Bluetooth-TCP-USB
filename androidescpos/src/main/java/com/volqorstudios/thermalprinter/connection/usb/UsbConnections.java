// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/usb/UsbConnections.java
package com.volqorstudios.thermalprinter.connection.usb;

import android.content.Context;
import androidx.annotation.Nullable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.Collection;





public class UsbConnections {
    protected UsbManager usbManager;

    public UsbConnections(Context context) {
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    @Nullable
    public UsbConnection[] getList() {
        if (this.usbManager == null) {
            return null;
        }

        Collection<UsbDevice> devicesList = this.usbManager.getDeviceList().values();
        UsbConnection[] usbDevices = new UsbConnection[devicesList.size()];

        int i = 0;
        for (UsbDevice device : devicesList) {
            usbDevices[i++] = new UsbConnection(this.usbManager, device);
        }

        return usbDevices;
    }
}