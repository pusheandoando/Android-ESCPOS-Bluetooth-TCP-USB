// androidescpos/src/main/java/com/volqorstudios/thermalprinter/textparser/TextParserImage.java
package com.volqorstudios.thermalprinter.textparser;

import com.volqorstudios.thermalprinter.ThermalPrinter;
import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.PrintImageHelper;
import com.volqorstudios.thermalprinter.exceptions.PrinterEncodingException;
import com.volqorstudios.thermalprinter.exceptions.PrinterConnectionException;





public class TextParserImage implements IPrinterTextElement {
    private int length;
    private byte[] image;

    public TextParserImage(TextParserColumn column, String textAlign, String hexString) {
        this(column, textAlign, PrintImageHelper.hexToBytes(hexString));
    }

    public TextParserImage(TextParserColumn column, String textAlign, byte[] image) {
        ThermalPrinter printer = column.getLine().getTextParser().getPrinter();

        int byteWidth = ((int) image[4] & 0xFF) + ((int) image[5] & 0xFF) * 256;
        int width = byteWidth * 8;
        int height = ((int) image[6] & 0xFF) + ((int) image[7] & 0xFF) * 256;
        int byteDiff = (int) Math.floor(((float) (printer.getPrinterWidthPx() - width)) / 8f);
        int whiteBytesToInsert = 0;

        if (textAlign.equals(TextParser.TAG_ALIGN_CENTER)) {
            whiteBytesToInsert = Math.round(((float) byteDiff) / 2f);
        } else if (textAlign.equals(TextParser.TAG_ALIGN_RIGHT)) {
            whiteBytesToInsert = byteDiff;
        }

        if (whiteBytesToInsert > 0) {
            int newByteWidth = byteWidth + whiteBytesToInsert;
            byte[] paddedImage = PrinterCommands.buildGSv0Header(newByteWidth, height);
            
            for (int i = 0; i < height; i++) {
                System.arraycopy(image, (byteWidth * i + 8), paddedImage, (newByteWidth * i + whiteBytesToInsert + 8), byteWidth);
            }
            
            image = paddedImage;
        }

        this.length = (int) Math.ceil(((float) byteWidth * 8) / ((float) printer.getPrinterCharSizeWidthPx()));
        this.image = image;
    }

    @Override
    public int length() throws PrinterEncodingException {
        return this.length;
    }

    @Override
    public TextParserImage print(PrinterCommands printerCommands) throws PrinterConnectionException {
        printerCommands.printImage(this.image);
        return this;
    }
}