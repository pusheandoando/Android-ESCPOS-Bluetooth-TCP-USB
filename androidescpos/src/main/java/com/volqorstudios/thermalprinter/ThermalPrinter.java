// androidescpos/src/main/java/com/volqorstudios/thermalprinter/ThermalPrinter.java
package com.volqorstudios.thermalprinter;

import android.graphics.Bitmap;

import com.volqorstudios.thermalprinter.textparser.TextParser;
import com.volqorstudios.thermalprinter.textparser.TextParserLine;
import com.volqorstudios.thermalprinter.textparser.TextParserColumn;
import com.volqorstudios.thermalprinter.textparser.TextParserString;
import com.volqorstudios.thermalprinter.connection.DeviceConnection;
import com.volqorstudios.thermalprinter.textparser.IPrinterTextElement;
import com.volqorstudios.thermalprinter.exceptions.PrinterParserException;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;
import com.volqorstudios.thermalprinter.exceptions.PrinterEncodingException;
import com.volqorstudios.thermalprinter.exceptions.PrinterConnectionException;





public class ThermalPrinter extends PrinterSize {
    private PrinterCommands printerCommands = null;

    public ThermalPrinter(DeviceConnection connection, int dpi, float widthMM, int charsPerLine) throws PrinterConnectionException {
        this(
            connection != null ? new PrinterCommands(connection) : null,
            dpi,
            widthMM,
            charsPerLine
        );
    }

    public ThermalPrinter(DeviceConnection connection, int dpi, float widthMM, int charsPerLine, PrinterCharset charset) throws PrinterConnectionException {
        this(
            connection != null ? new PrinterCommands(connection, charset) : null,
            dpi,
            widthMM,
            charsPerLine
        );
    }

    public ThermalPrinter(PrinterCommands printerCommands, int dpi, float widthMM, int charsPerLine) throws PrinterConnectionException {
        super(dpi, widthMM, charsPerLine);
        if (printerCommands != null) {
            this.printerCommands = printerCommands.connect();
        }
    }

    public ThermalPrinter disconnectPrinter() {
        if (this.printerCommands != null) {
            this.printerCommands.disconnect();
            this.printerCommands = null;
        }
        return this;
    }

    public ThermalPrinter useEscAsteriskCommand(boolean enable) {
        this.printerCommands.useEscAsteriskCommand(enable);
        return this;
    }

    public ThermalPrinter printFormattedText(String text) throws PrinterConnectionException, PrinterParserException, PrinterEncodingException, PrinterBarcodeException {
        return this.printFormattedText(text, 20f);
    }

    public ThermalPrinter printFormattedText(String text, float mmFeed) throws PrinterConnectionException, PrinterParserException, PrinterEncodingException, PrinterBarcodeException {
        return this.printFormattedText(text, this.mmToPx(mmFeed));
    }

    public ThermalPrinter printFormattedText(String text, int dotsFeed) throws PrinterConnectionException, PrinterParserException, PrinterEncodingException, PrinterBarcodeException {
        if (this.printerCommands == null || this.printerNbrCharactersPerLine == 0) {
            return this;
        }

        TextParser parser = new TextParser(this);
        TextParserLine[] lines = parser.setFormattedText(text).parse();

        this.printerCommands.reset();

        for (TextParserLine line : lines) {
            TextParserColumn[] columns = line.getColumns();
            IPrinterTextElement lastElement = null;

            for (TextParserColumn column : columns) {
                IPrinterTextElement[] elements = column.getElements();
                
                for (IPrinterTextElement element : elements) {
                    element.print(this.printerCommands);
                    lastElement = element;
                }
            }

            if (lastElement instanceof TextParserString) {
                this.printerCommands.newLine();
            }
        }

        this.printerCommands.feedPaper(dotsFeed);
        return this;
    }

    public ThermalPrinter printFormattedTextAndCut(String text) throws PrinterConnectionException, PrinterParserException, PrinterEncodingException, PrinterBarcodeException {
        return this.printFormattedTextAndCut(text, 20f);
    }

    public ThermalPrinter printFormattedTextAndCut(String text, float mmFeed) throws PrinterConnectionException, PrinterParserException, PrinterEncodingException, PrinterBarcodeException {
        return this.printFormattedTextAndCut(text, this.mmToPx(mmFeed));
    }

    public ThermalPrinter printFormattedTextAndCut(String text, int dotsFeed) throws PrinterConnectionException, PrinterParserException, PrinterEncodingException, PrinterBarcodeException {
        if (this.printerCommands == null || this.printerNbrCharactersPerLine == 0) {
            return this;
        }

        this.printFormattedText(text, dotsFeed);
        this.printerCommands.cutPaper();
        return this;
    }

    public ThermalPrinter printFormattedTextAndOpenCashBox(String text, float mmFeed) throws PrinterConnectionException, PrinterParserException, PrinterEncodingException, PrinterBarcodeException {
        return this.printFormattedTextAndOpenCashBox(text, this.mmToPx(mmFeed));
    }

    public ThermalPrinter printFormattedTextAndOpenCashBox(String text, int dotsFeed) throws PrinterConnectionException, PrinterParserException, PrinterEncodingException, PrinterBarcodeException {
        if (this.printerCommands == null || this.printerNbrCharactersPerLine == 0) {
            return this;
        }
        
        this.printFormattedTextAndCut(text, dotsFeed);
        this.printerCommands.openCashBox();
        return this;
    }

    public PrinterCharset getCharset() {
        return this.printerCommands.getCharset();
    }

    public byte[] bitmapToBytes(Bitmap bitmap, boolean gradient) {
        return super.bitmapToBytes(bitmap, gradient);
    }
}