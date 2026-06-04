// androidescpos/src/main/java/com/volqorstudios/thermalprinter/barcode/Barcode39.java
package com.volqorstudios.thermalprinter.barcode;

import com.volqorstudios.thermalprinter.PrinterSize;
import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;





public class Barcode39 extends BarcodeData {
    public Barcode39(PrinterSize printerSize, String code, float widthMM, float heightMM, int textPosition) throws PrinterBarcodeException {
        super(printerSize, PrinterCommands.BARCODE_TYPE_39, code, widthMM, heightMM, textPosition);
    }

    @Override
    public int getCodeLength() {
        return this.code.length();
    }

    @Override
    public int getColumnCount() {
        return (this.getCodeLength() + 4) * 16;
    }
}