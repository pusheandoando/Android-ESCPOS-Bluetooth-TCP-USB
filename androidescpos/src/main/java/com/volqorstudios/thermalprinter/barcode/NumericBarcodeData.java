// androidescpos/src/main/java/com/volqorstudios/thermalprinter/barcode/NumericBarcodeData.java
package com.volqorstudios.thermalprinter.barcode;

import com.volqorstudios.thermalprinter.PrinterSize;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;





public abstract class NumericBarcodeData extends BarcodeData {
    public NumericBarcodeData(PrinterSize printerSize, int barcodeType, String code, float widthMM, float heightMM, int textPosition) throws PrinterBarcodeException {
        super(printerSize, barcodeType, code, widthMM, heightMM, textPosition);
        this.validateCode();
    }

    @Override
    public int getColumnCount() {
        return this.getCodeLength() * 7 + 11;
    }

    private void validateCode() throws PrinterBarcodeException {
        int payloadLength = this.getCodeLength() - 1;

        if (this.code.length() < payloadLength) {
            throw new PrinterBarcodeException("Code is too short for the barcode type.");
        }

        try {
            String payload = this.code.substring(0, payloadLength);
            int checksum = 0;

            for (int i = 0; i < payloadLength; i++) {
                int position = payloadLength - 1 - i;
                int digit = Integer.parseInt(payload.substring(position, position + 1), 10);

                if (i % 2 == 0) {
                    digit = 3 * digit;
                }

                checksum += digit;
            }

            String checkDigit = String.valueOf(10 - (checksum % 10));

            if (checkDigit.length() == 2) {
                checkDigit = "0";
            }

            this.code = payload + checkDigit;

        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new PrinterBarcodeException("Invalid barcode number");
        }
    }
}