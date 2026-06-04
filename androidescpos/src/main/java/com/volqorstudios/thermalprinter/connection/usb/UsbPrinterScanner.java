// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/usb/UsbPrinterScanner.java
package com.volqorstudios.thermalprinter.connection.usb;

import android.content.Context;
import androidx.annotation.Nullable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbConstants;





public class UsbPrinterScanner extends UsbConnections {
    public UsbPrinterScanner(Context context) {
        super(context);
    }

    @Nullable
    public static UsbConnection selectFirstConnected(Context context) {
        UsbPrinterScanner scanner = new UsbPrinterScanner(context);
        UsbConnection[] printers = scanner.getList();

        if (printers == null || printers.length == 0) {
            return null;
        }

        return printers[0];
    }

    @Nullable
    public UsbConnection[] getList() {
        UsbConnection[] usbConnections = super.getList();

        if (usbConnections == null) {
            return null;
        }

        int count = 0;
        UsbConnection[] printersTmp = new UsbConnection[usbConnections.length];
        for (UsbConnection usbConnection : usbConnections) {
            UsbDevice device = usbConnection.getDevice();
            int usbClass = device.getDeviceClass();

            if ((usbClass == UsbConstants.USB_CLASS_PER_INTERFACE || usbClass == UsbConstants.USB_CLASS_MISC) && UsbDeviceHelper.findPrinterInterface(device) != null) {
                usbClass = UsbConstants.USB_CLASS_PRINTER;
            }

            if (usbClass == UsbConstants.USB_CLASS_PRINTER) {
                printersTmp[count++] = new UsbConnection(this.usbManager, device);
            }
        }

        UsbConnection[] usbPrinters = new UsbConnection[count];
        System.arraycopy(printersTmp, 0, usbPrinters, 0, count);
        return usbPrinters;
    }
}