// androidescpos/src/main/java/com/volqorstudios/thermalprinter/barcode/BarcodeUPCE.java
package com.volqorstudios.thermalprinter.barcode;

import com.volqorstudios.thermalprinter.PrinterSize;
import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;





public class BarcodeUPCE extends BarcodeData {
    public BarcodeUPCE(PrinterSize printerSize, String code, float widthMM, float heightMM, int textPosition) throws PrinterBarcodeException {
        super(printerSize, PrinterCommands.BARCODE_TYPE_UPCE, code, widthMM, heightMM, textPosition);
        this.validateCode();
    }

    @Override
    public int getCodeLength() {
        return 6;
    }

    @Override
    public int getColumnCount() {
        return this.getCodeLength() * 7 + 16;
    }

    private void validateCode() throws PrinterBarcodeException {
        int length = this.getCodeLength();

        if (this.code.length() < length) {
            throw new PrinterBarcodeException("Code is too short for the barcode type.");
        }

        try {
            this.code = this.code.substring(0, length);

            for (int i = 0; i < length; i++) {
                Integer.parseInt(this.code.substring(i, i + 1), 10);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new PrinterBarcodeException("Invalid barcode number");
        }
    }
}