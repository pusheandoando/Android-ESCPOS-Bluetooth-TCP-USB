// androidescpos/src/main/java/com/volqorstudios/thermalprinter/PrintImageHelper.java
package com.volqorstudios.thermalprinter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;





public class PrintImageHelper {
    public static String bitmapToHex(PrinterSize printerSize, Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return PrintImageHelper.bitmapToHex(printerSize, (BitmapDrawable) drawable);
        }

        return "";
    }

    public static String bitmapToHex(PrinterSize printerSize, Drawable drawable, boolean gradient) {
        if (drawable instanceof BitmapDrawable) {
            return PrintImageHelper.bitmapToHex(printerSize, (BitmapDrawable) drawable, gradient);
        }

        return "";
    }

    public static String bitmapToHex(PrinterSize printerSize, BitmapDrawable drawable) {
        return PrintImageHelper.bitmapToHex(printerSize, drawable.getBitmap());
    }

    public static String bitmapToHex(PrinterSize printerSize, BitmapDrawable drawable, boolean gradient) {
        return PrintImageHelper.bitmapToHex(printerSize, drawable.getBitmap(), gradient);
    }

    public static String bitmapToHex(PrinterSize printerSize, Bitmap bitmap) {
        return PrintImageHelper.bitmapToHex(printerSize, bitmap, true);
    }

    public static String bitmapToHex(PrinterSize printerSize, Bitmap bitmap, boolean gradient) {
        return PrintImageHelper.bytesToHex(printerSize.bitmapToBytes(bitmap, gradient));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            
            if (hex.length() == 1) {
                result.append("0");
            }
            result.append(hex);
        }
        
        return result.toString();
    }

    public static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        
        for (int i = 0; i < bytes.length; i++) {
            int pos = i * 2;
            bytes[i] = (byte) Integer.parseInt(hex.substring(pos, pos + 2), 16);
        }
        
        return bytes;
    }
}