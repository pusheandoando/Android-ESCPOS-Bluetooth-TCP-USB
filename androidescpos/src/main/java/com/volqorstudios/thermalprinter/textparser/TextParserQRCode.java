// androidescpos/src/main/java/com/volqorstudios/thermalprinter/textparser/TextParserQRCode.java
package com.volqorstudios.thermalprinter.textparser;

import java.util.Hashtable;

import com.volqorstudios.thermalprinter.ThermalPrinter;
import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.exceptions.PrinterParserException;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;





public class TextParserQRCode extends TextParserImage {
    private static byte[] buildQRImageBytes(TextParserColumn column, Hashtable<String, String> attributes, String data) throws PrinterParserException, PrinterBarcodeException {
        ThermalPrinter printer = column.getLine().getTextParser().getPrinter();
        data = data.trim();

        int size = printer.mmToPx(20f);

        if (attributes.containsKey(TextParser.ATTR_QRCODE_SIZE)) {
            String value = attributes.get(TextParser.ATTR_QRCODE_SIZE);
            
            if (value == null) {
                throw new PrinterParserException("Invalid QR code size attribute");
            }
            
            try {
                size = printer.mmToPx(Float.parseFloat(value));
            } catch (NumberFormatException e) {
                throw new PrinterParserException("Invalid QR code size value");
            }
        }

        return PrinterCommands.buildQRCodeBytes(data, size);
    }

    public TextParserQRCode(TextParserColumn column, String textAlign, Hashtable<String, String> attributes, String data) throws PrinterParserException, PrinterBarcodeException {
        super(column, textAlign, TextParserQRCode.buildQRImageBytes(column, attributes, data));
    }
}