// androidescpos/src/main/java/com/volqorstudios/thermalprinter/textparser/TextParserString.java
package com.volqorstudios.thermalprinter.textparser;

import java.util.Arrays;
import java.io.UnsupportedEncodingException;

import com.volqorstudios.thermalprinter.ThermalPrinter;
import com.volqorstudios.thermalprinter.PrinterCharset;
import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.exceptions.PrinterEncodingException;





public class TextParserString implements IPrinterTextElement {
    private ThermalPrinter printer;
    private String text;
    private byte[] textSize;
    private byte[] textColor;
    private byte[] textReverseColor;
    private byte[] textBold;
    private byte[] textUnderline;
    private byte[] textDoubleStrike;

    public TextParserString(TextParserColumn column, String text, byte[] textSize, byte[] textColor, byte[] textReverseColor, byte[] textBold, byte[] textUnderline, byte[] textDoubleStrike) {
        this.printer = column.getLine().getTextParser().getPrinter();
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
        this.textReverseColor = textReverseColor;
        this.textBold = textBold;
        this.textUnderline = textUnderline;
        this.textDoubleStrike = textDoubleStrike;
    }

    @Override
    public int length() throws PrinterEncodingException {
        PrinterCharset charset = this.printer.getCharset();

        int multiplier = 1;
        if (Arrays.equals(this.textSize, PrinterCommands.TEXT_SIZE_DOUBLE_WIDTH) || Arrays.equals(this.textSize, PrinterCommands.TEXT_SIZE_BIG)) {
            multiplier = 2;
        } else if (Arrays.equals(this.textSize, PrinterCommands.TEXT_SIZE_BIG_2)) {
            multiplier = 3;
        } else if (Arrays.equals(this.textSize, PrinterCommands.TEXT_SIZE_BIG_3)) {
            multiplier = 4;
        } else if (Arrays.equals(this.textSize, PrinterCommands.TEXT_SIZE_BIG_4)) {
            multiplier = 5;
        } else if (Arrays.equals(this.textSize, PrinterCommands.TEXT_SIZE_BIG_5)) {
            multiplier = 6;
        } else if (Arrays.equals(this.textSize, PrinterCommands.TEXT_SIZE_BIG_6)) {
            multiplier = 7;
        }

        if (charset != null) {
            try {
                return this.text.getBytes(charset.getName()).length * multiplier;
            } catch (UnsupportedEncodingException e) {
                throw new PrinterEncodingException(e.getMessage());
            }
        }

        return this.text.length() * multiplier;
    }

    @Override
    public TextParserString print(com.volqorstudios.thermalprinter.PrinterCommands printerCommands) throws PrinterEncodingException {
        printerCommands.printText(this.text, this.textSize, this.textColor, this.textReverseColor, this.textBold, this.textUnderline, this.textDoubleStrike);
        return this;
    }
}