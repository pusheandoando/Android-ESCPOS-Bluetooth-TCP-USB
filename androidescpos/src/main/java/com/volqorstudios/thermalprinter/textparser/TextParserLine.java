// androidescpos/src/main/java/com/volqorstudios/thermalprinter/textparser/TextParserLine.java
package com.volqorstudios.thermalprinter.textparser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.volqorstudios.thermalprinter.exceptions.PrinterParserException;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;
import com.volqorstudios.thermalprinter.exceptions.PrinterEncodingException;





public class TextParserLine {
    private TextParser textParser;
    private int columnCount;
    private int charsPerColumn;
    private int forgottenChars;
    private int exceededChars;
    private TextParserColumn[] columns;

    public TextParserLine(TextParser textParser, String rawLine) throws PrinterParserException, PrinterBarcodeException, PrinterEncodingException {
        this.textParser = textParser;
        int totalChars = this.textParser.getPrinter().getPrinterNbrCharactersPerLine();

        Pattern pattern = Pattern.compile(TextParser.getAlignTagRegex());
        Matcher matcher = pattern.matcher(rawLine);

        ArrayList<String> columnParts = new ArrayList<String>();
        int lastPos = 0;

        while (matcher.find()) {
            int start = matcher.start();
            
            if (start > 0) {
                columnParts.add(rawLine.substring(lastPos, start));
            }
            
            lastPos = start;
        }
        columnParts.add(rawLine.substring(lastPos));

        this.columnCount = columnParts.size();
        this.charsPerColumn = (int) Math.floor(((float) totalChars) / ((float) this.columnCount));
        this.forgottenChars = totalChars - (this.charsPerColumn * this.columnCount);
        this.exceededChars = 0;
        this.columns = new TextParserColumn[this.columnCount];

        int i = 0;
        for (String part : columnParts) {
            this.columns[i++] = new TextParserColumn(this, part);
        }
    }

    public TextParser getTextParser() {
        return this.textParser;
    }

    public TextParserColumn[] getColumns() {
        return this.columns;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public int getCharsPerColumn() {
        return this.charsPerColumn;
    }

    public TextParserLine setCharsPerColumn(int value) {
        this.charsPerColumn = value;
        return this;
    }

    public int getForgottenChars() {
        return this.forgottenChars;
    }

    public TextParserLine setForgottenChars(int value) {
        this.forgottenChars = value;
        return this;
    }

    public int getExceededChars() {
        return this.exceededChars;
    }

    public TextParserLine setExceededChars(int value) {
        this.exceededChars = value;
        return this;
    }
}