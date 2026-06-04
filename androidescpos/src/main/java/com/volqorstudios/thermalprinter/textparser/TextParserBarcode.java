// androidescpos/src/main/java/com/volqorstudios/thermalprinter/textparser/TextParserBarcode.java
package com.volqorstudios.thermalprinter.textparser;

import java.util.Hashtable;

import com.volqorstudios.thermalprinter.ThermalPrinter;
import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.barcode.Barcode39;
import com.volqorstudios.thermalprinter.barcode.Barcode128;
import com.volqorstudios.thermalprinter.barcode.BarcodeData;
import com.volqorstudios.thermalprinter.barcode.BarcodeEAN8;
import com.volqorstudios.thermalprinter.barcode.BarcodeUPCA;
import com.volqorstudios.thermalprinter.barcode.BarcodeUPCE;
import com.volqorstudios.thermalprinter.barcode.BarcodeEAN13;
import com.volqorstudios.thermalprinter.exceptions.PrinterParserException;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;
import com.volqorstudios.thermalprinter.exceptions.PrinterEncodingException;





public class TextParserBarcode implements IPrinterTextElement {
    private BarcodeData barcodeData;
    private int length;
    private byte[] align;

    public TextParserBarcode(TextParserColumn column, String textAlign, Hashtable<String, String> attributes, String code) throws PrinterParserException, PrinterBarcodeException {
        ThermalPrinter printer = column.getLine().getTextParser().getPrinter();
        code = code.trim();

        this.align = PrinterCommands.TEXT_ALIGN_LEFT;

        if (textAlign.equals(TextParser.TAG_ALIGN_CENTER)) {
            this.align = PrinterCommands.TEXT_ALIGN_CENTER;
        } else if (textAlign.equals(TextParser.TAG_ALIGN_RIGHT)) {
            this.align = PrinterCommands.TEXT_ALIGN_RIGHT;
        }

        this.length = printer.getPrinterNbrCharactersPerLine();

        float height = 10f;
        if (attributes.containsKey(TextParser.ATTR_BARCODE_HEIGHT)) {
            String value = attributes.get(TextParser.ATTR_BARCODE_HEIGHT);
            
            if (value == null) {
                throw new PrinterParserException("Invalid barcode attribute: " + TextParser.ATTR_BARCODE_HEIGHT);
            }
            
            try {
                height = Float.parseFloat(value);
            } catch (NumberFormatException e) {
                throw new PrinterParserException("Invalid barcode height value");
            }
        }

        float width = 0f;
        if (attributes.containsKey(TextParser.ATTR_BARCODE_WIDTH)) {
            String value = attributes.get(TextParser.ATTR_BARCODE_WIDTH);
            
            if (value == null) {
                throw new PrinterParserException("Invalid barcode attribute: " + TextParser.ATTR_BARCODE_WIDTH);
            }
            
            try {
                width = Float.parseFloat(value);
            } catch (NumberFormatException e) {
                throw new PrinterParserException("Invalid barcode width value");
            }
        }

        int textPosition = PrinterCommands.BARCODE_TEXT_POSITION_BELOW;
        if (attributes.containsKey(TextParser.ATTR_BARCODE_TEXT_POSITION)) {
            String value = attributes.get(TextParser.ATTR_BARCODE_TEXT_POSITION);
            
            if (value == null) {
                throw new PrinterParserException("Invalid barcode attribute: " + TextParser.ATTR_BARCODE_TEXT_POSITION);
            }
            
            if (value.equals(TextParser.ATTR_BARCODE_TEXT_NONE)) {
                textPosition = PrinterCommands.BARCODE_TEXT_POSITION_NONE;
            } else if (value.equals(TextParser.ATTR_BARCODE_TEXT_ABOVE)) {
                textPosition = PrinterCommands.BARCODE_TEXT_POSITION_ABOVE;
            }
        }

        String barcodeType = TextParser.ATTR_BARCODE_TYPE_EAN13;
        if (attributes.containsKey(TextParser.ATTR_BARCODE_TYPE)) {
            barcodeType = attributes.get(TextParser.ATTR_BARCODE_TYPE);
            
            if (barcodeType == null) {
                throw new PrinterParserException("Invalid barcode type attribute");
            }
        }

        switch (barcodeType) {
            case TextParser.ATTR_BARCODE_TYPE_EAN8:
                this.barcodeData = new BarcodeEAN8(printer, code, width, height, textPosition);
                break;
            case TextParser.ATTR_BARCODE_TYPE_EAN13:
                this.barcodeData = new BarcodeEAN13(printer, code, width, height, textPosition);
                break;
            case TextParser.ATTR_BARCODE_TYPE_UPCA:
                this.barcodeData = new BarcodeUPCA(printer, code, width, height, textPosition);
                break;
            case TextParser.ATTR_BARCODE_TYPE_UPCE:
                this.barcodeData = new BarcodeUPCE(printer, code, width, height, textPosition);
                break;
            case TextParser.ATTR_BARCODE_TYPE_128:
                this.barcodeData = new Barcode128(printer, code, width, height, textPosition);
                break;
            case TextParser.ATTR_BARCODE_TYPE_39:
                this.barcodeData = new Barcode39(printer, code, width, height, textPosition);
                break;
            default:
                throw new PrinterParserException("Unsupported barcode type: " + barcodeType);
        }
    }

    @Override
    public int length() throws PrinterEncodingException {
        return this.length;
    }

    @Override
    public TextParserBarcode print(PrinterCommands printerCommands) {
        printerCommands.setAlign(this.align).printBarcode(this.barcodeData);
        return this;
    }
}