// androidescpos/src/main/java/com/volqorstudios/thermalprinter/barcode/BarcodeEAN13.java
package com.volqorstudios.thermalprinter.barcode;

import com.volqorstudios.thermalprinter.PrinterSize;
import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;





public class BarcodeEAN13 extends NumericBarcodeData {
    public BarcodeEAN13(PrinterSize printerSize, String code, float widthMM, float heightMM, int textPosition) throws PrinterBarcodeException {
        super(printerSize, PrinterCommands.BARCODE_TYPE_EAN13, code, widthMM, heightMM, textPosition);
    }

    @Override
    public int getCodeLength() {
        return 13;
    }
}