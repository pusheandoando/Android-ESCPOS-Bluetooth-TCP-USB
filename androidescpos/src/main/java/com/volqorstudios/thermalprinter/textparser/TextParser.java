// androidescpos/src/main/java/com/volqorstudios/thermalprinter/textparser/TextParser.java
package com.volqorstudios.thermalprinter.textparser;

import com.volqorstudios.thermalprinter.ThermalPrinter;
import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.exceptions.PrinterParserException;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;
import com.volqorstudios.thermalprinter.exceptions.PrinterEncodingException;





public class TextParser {
    public static final String TAG_ALIGN_LEFT = "L";
    public static final String TAG_ALIGN_CENTER = "C";
    public static final String TAG_ALIGN_RIGHT = "R";
    public static final String[] TAGS_ALIGN = {
        TextParser.TAG_ALIGN_LEFT,
        TextParser.TAG_ALIGN_CENTER,
        TextParser.TAG_ALIGN_RIGHT
    };

    public static final String TAG_IMAGE = "img";
    public static final String TAG_BARCODE = "barcode";
    public static final String TAG_QRCODE = "qrcode";

    public static final String ATTR_BARCODE_WIDTH = "width";
    public static final String ATTR_BARCODE_HEIGHT = "height";
    public static final String ATTR_BARCODE_TYPE = "type";
    public static final String ATTR_BARCODE_TYPE_EAN8 = "ean8";
    public static final String ATTR_BARCODE_TYPE_EAN13 = "ean13";
    public static final String ATTR_BARCODE_TYPE_UPCA = "upca";
    public static final String ATTR_BARCODE_TYPE_UPCE = "upce";
    public static final String ATTR_BARCODE_TYPE_128 = "128";
    public static final String ATTR_BARCODE_TYPE_39 = "39";
    public static final String ATTR_BARCODE_TEXT_POSITION = "text";
    public static final String ATTR_BARCODE_TEXT_NONE = "none";
    public static final String ATTR_BARCODE_TEXT_ABOVE = "above";
    public static final String ATTR_BARCODE_TEXT_BELOW = "below";

    public static final String TAG_FONT = "font";
    public static final String TAG_BOLD = "b";
    public static final String TAG_UNDERLINE = "u";
    public static final String[] TAGS_FORMAT = {
        TextParser.TAG_FONT,
        TextParser.TAG_BOLD,
        TextParser.TAG_UNDERLINE
    };

    public static final String ATTR_UNDERLINE_TYPE = "type";
    public static final String ATTR_UNDERLINE_NORMAL = "normal";
    public static final String ATTR_UNDERLINE_DOUBLE = "double";

    public static final String ATTR_FONT_SIZE = "size";
    public static final String ATTR_FONT_SIZE_NORMAL = "normal";
    public static final String ATTR_FONT_SIZE_WIDE = "wide";
    public static final String ATTR_FONT_SIZE_TALL = "tall";
    public static final String ATTR_FONT_SIZE_BIG = "big";
    public static final String ATTR_FONT_SIZE_BIG_2 = "big-2";
    public static final String ATTR_FONT_SIZE_BIG_3 = "big-3";
    public static final String ATTR_FONT_SIZE_BIG_4 = "big-4";
    public static final String ATTR_FONT_SIZE_BIG_5 = "big-5";
    public static final String ATTR_FONT_SIZE_BIG_6 = "big-6";

    public static final String ATTR_FONT_COLOR = "color";
    public static final String ATTR_FONT_COLOR_BLACK = "black";
    public static final String ATTR_FONT_COLOR_BG_BLACK = "bg-black";
    public static final String ATTR_FONT_COLOR_RED = "red";
    public static final String ATTR_FONT_COLOR_BG_RED = "bg-red";

    public static final String ATTR_QRCODE_SIZE = "size";

    private static String regexAlignTags;

    public static String getAlignTagRegex() {
        if (TextParser.regexAlignTags == null) {
            StringBuilder regex = new StringBuilder();
            
            for (int i = 0; i < TextParser.TAGS_ALIGN.length; i++) {
                regex.append("|\\[").append(TextParser.TAGS_ALIGN[i]).append("\\]");
            }

            TextParser.regexAlignTags = regex.toString().substring(1);
        }

        return TextParser.regexAlignTags;
    }

    public static boolean isFormatTag(String tagName) {
        if (tagName.substring(0, 1).equals("/")) {
            tagName = tagName.substring(1);
        }

        for (String tag : TextParser.TAGS_FORMAT) {
            if (tag.equals(tagName)) {
                return true;
            }
        }

        return false;
    }

    public static byte[][] dropLastByteArray(byte[][] arr) {
        if (arr.length == 0) {
            return arr;
        }

        byte[][] result = new byte[arr.length - 1][];
        System.arraycopy(arr, 0, result, 0, result.length);
        return result;
    }

    public static byte[][] appendByteArray(byte[][] arr, byte[] item) {
        byte[][] result = new byte[arr.length + 1][];
        System.arraycopy(arr, 0, result, 0, arr.length);
        result[arr.length] = item;
        return result;
    }

    private ThermalPrinter printer;
    private byte[][] textSize = {PrinterCommands.TEXT_SIZE_NORMAL};
    private byte[][] textColor = {PrinterCommands.TEXT_COLOR_BLACK};
    private byte[][] textReverseColor = {PrinterCommands.TEXT_COLOR_REVERSE_OFF};
    private byte[][] textBold = {PrinterCommands.TEXT_WEIGHT_NORMAL};
    private byte[][] textUnderline = {PrinterCommands.TEXT_UNDERLINE_OFF};
    private byte[][] textDoubleStrike = {PrinterCommands.TEXT_DOUBLE_STRIKE_OFF};
    private String text = "";

    public TextParser(ThermalPrinter printer) {
        this.printer = printer;
    }

    public ThermalPrinter getPrinter() {
        return this.printer;
    }

    public TextParser setFormattedText(String text) {
        this.text = text;
        return this;
    }

    public byte[] getLastTextSize() {
        return this.textSize[this.textSize.length - 1];
    }

    public TextParser pushTextSize(byte[] value) {
        this.textSize = TextParser.appendByteArray(this.textSize, value);
        return this;
    }

    public TextParser popTextSize() {
        if (this.textSize.length > 1) {
            this.textSize = TextParser.dropLastByteArray(this.textSize);
        }

        return this;
    }

    public byte[] getLastTextColor() {
        return this.textColor[this.textColor.length - 1];
    }

    public TextParser pushTextColor(byte[] value) {
        this.textColor = TextParser.appendByteArray(this.textColor, value);
        return this;
    }

    public TextParser popTextColor() {
        if (this.textColor.length > 1) {
            this.textColor = TextParser.dropLastByteArray(this.textColor);
        }
        return this;
    }

    public byte[] getLastTextReverseColor() {
        return this.textReverseColor[this.textReverseColor.length - 1];
    }

    public TextParser pushTextReverseColor(byte[] value) {
        this.textReverseColor = TextParser.appendByteArray(this.textReverseColor, value);
        return this;
    }

    public TextParser popTextReverseColor() {
        if (this.textReverseColor.length > 1) {
            this.textReverseColor = TextParser.dropLastByteArray(this.textReverseColor);
        }

        return this;
    }

    public byte[] getLastTextBold() {
        return this.textBold[this.textBold.length - 1];
    }

    public TextParser pushTextBold(byte[] value) {
        this.textBold = TextParser.appendByteArray(this.textBold, value);
        return this;
    }

    public TextParser popTextBold() {
        if (this.textBold.length > 1) {
            this.textBold = TextParser.dropLastByteArray(this.textBold);
        }
        return this;
    }

    public byte[] getLastTextUnderline() {
        return this.textUnderline[this.textUnderline.length - 1];
    }

    public TextParser pushTextUnderline(byte[] value) {
        this.textUnderline = TextParser.appendByteArray(this.textUnderline, value);
        return this;
    }

    public TextParser popTextUnderline() {
        if (this.textUnderline.length > 1) {
            this.textUnderline = TextParser.dropLastByteArray(this.textUnderline);
        }
        return this;
    }

    public byte[] getLastTextDoubleStrike() {
        return this.textDoubleStrike[this.textDoubleStrike.length - 1];
    }

    public TextParser pushTextDoubleStrike(byte[] value) {
        this.textDoubleStrike = TextParser.appendByteArray(this.textDoubleStrike, value);
        return this;
    }

    public TextParser popTextDoubleStrike() {
        if (this.textDoubleStrike.length > 1) {
            this.textDoubleStrike = TextParser.dropLastByteArray(this.textDoubleStrike);
        }
        return this;
    }

    public TextParserLine[] parse() throws PrinterParserException, PrinterBarcodeException, PrinterEncodingException {
        String[] rawLines = this.text.split("\n|\r\n");
        TextParserLine[] lines = new TextParserLine[rawLines.length];
        
        int i = 0;
        for (String rawLine : rawLines) {
            lines[i++] = new TextParserLine(this, rawLine);
        }
        
        return lines;
    }
}