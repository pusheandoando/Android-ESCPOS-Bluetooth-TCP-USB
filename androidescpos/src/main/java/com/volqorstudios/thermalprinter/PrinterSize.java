// androidescpos/src/main/java/com/volqorstudios/thermalprinter/PrinterSize.java
package com.volqorstudios.thermalprinter;

import android.graphics.Bitmap;





public abstract class PrinterSize {
    public static final float INCH_TO_MM = 25.4f;

    protected int printerDpi;
    protected float printerWidthMM;
    protected int printerNbrCharactersPerLine;
    protected int printerWidthPx;
    protected int printerCharSizeWidthPx;

    protected PrinterSize(int printerDpi, float printerWidthMM, int printerNbrCharactersPerLine) {
        this.printerDpi = printerDpi;
        this.printerWidthMM = printerWidthMM;
        this.printerNbrCharactersPerLine = printerNbrCharactersPerLine;
        int printingWidthPx = this.mmToPx(this.printerWidthMM);
        this.printerWidthPx = printingWidthPx + (printingWidthPx % 8);
        this.printerCharSizeWidthPx = printingWidthPx / this.printerNbrCharactersPerLine;
    }

    public int getPrinterNbrCharactersPerLine() {
        return this.printerNbrCharactersPerLine;
    }

    public float getPrinterWidthMM() {
        return this.printerWidthMM;
    }

    public int getPrinterDpi() {
        return this.printerDpi;
    }

    public int getPrinterWidthPx() {
        return this.printerWidthPx;
    }

    public int getPrinterCharSizeWidthPx() {
        return this.printerCharSizeWidthPx;
    }

    public int mmToPx(float mmSize) {
        return Math.round(mmSize * ((float) this.printerDpi) / PrinterSize.INCH_TO_MM);
    }

    public byte[] bitmapToBytes(Bitmap bitmap, boolean gradient) {
        boolean isSizeEdit = false;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int maxWidth = this.printerWidthPx;
        int maxHeight = 256;

        if (bitmapWidth > maxWidth) {
            bitmapHeight = Math.round(((float) bitmapHeight) * ((float) maxWidth) / ((float) bitmapWidth));
            bitmapWidth = maxWidth;
            isSizeEdit = true;
        }

        if (bitmapHeight > maxHeight) {
            bitmapWidth = Math.round(((float) bitmapWidth) * ((float) maxHeight) / ((float) bitmapHeight));
            bitmapHeight = maxHeight;
            isSizeEdit = true;
        }

        if (isSizeEdit) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
        }

        return PrinterCommands.bitmapToBytes(bitmap, gradient);
    }
}