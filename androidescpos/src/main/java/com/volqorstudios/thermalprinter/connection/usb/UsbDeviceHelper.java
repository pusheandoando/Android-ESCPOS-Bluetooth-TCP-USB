// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/usb/UsbDeviceHelper.java
package com.volqorstudios.thermalprinter.connection.usb;

import androidx.annotation.Nullable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbConstants;





public class UsbDeviceHelper {
    @Nullable
    public static UsbInterface findPrinterInterface(UsbDevice usbDevice) {
        if (usbDevice == null) {
            return null;
        }

        int interfacesCount = usbDevice.getInterfaceCount();
        for (int i = 0; i < interfacesCount; i++) {
            UsbInterface usbInterface = usbDevice.getInterface(i);
            if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
                return usbInterface;
            }
        }

        return null;
    }

    @Nullable
    public static UsbEndpoint findEndpointIn(UsbInterface usbInterface) {
        if (usbInterface == null) {
            return null;
        }

        int endpointsCount = usbInterface.getEndpointCount();
        for (int i = 0; i < endpointsCount; i++) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                return endpoint;
            }
        }

        return null;
    }
}