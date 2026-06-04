// androidescpos/src/main/java/com/volqorstudios/thermalprinter/barcode/BarcodeData.java
package com.volqorstudios.thermalprinter.barcode;

import com.volqorstudios.thermalprinter.PrinterSize;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;





public abstract class BarcodeData {
    protected int barcodeType;
    protected String code;
    protected int colWidth;
    protected int height;
    protected int textPosition;

    BarcodeData(PrinterSize printerSize, int barcodeType, String code, float widthMM, float heightMM, int textPosition) throws PrinterBarcodeException {
        this.barcodeType = barcodeType;
        this.code = code;
        this.height = printerSize.mmToPx(heightMM);
        this.textPosition = textPosition;

        if (widthMM == 0f) {
            widthMM = printerSize.getPrinterWidthMM() * 0.7f;
        }

        int targetWidthPx = widthMM > printerSize.getPrinterWidthMM() ? printerSize.getPrinterWidthPx() : printerSize.mmToPx(widthMM);
        int calculatedColWidth = (int) Math.round((double) targetWidthPx / (double) this.getColumnCount());

        if ((calculatedColWidth * this.getColumnCount()) > printerSize.getPrinterWidthPx()) {
            calculatedColWidth--;
        }

        if (calculatedColWidth == 0) {
            throw new PrinterBarcodeException("Barcode is too long for the paper size.");
        }

        this.colWidth = calculatedColWidth;
    }

    public abstract int getCodeLength();

    public abstract int getColumnCount();

    public int getBarcodeType() {
        return this.barcodeType;
    }

    public String getCode() {
        return this.code;
    }

    public int getHeight() {
        return this.height;
    }

    public int getTextPosition() {
        return this.textPosition;
    }

    public int getColWidth() {
        return this.colWidth;
    }
}