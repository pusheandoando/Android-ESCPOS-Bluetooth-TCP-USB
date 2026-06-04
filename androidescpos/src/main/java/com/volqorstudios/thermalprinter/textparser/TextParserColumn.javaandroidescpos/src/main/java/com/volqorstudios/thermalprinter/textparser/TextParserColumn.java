// androidescpos/src/main/java/com/volqorstudios/thermalprinter/textparser/TextParserColumn.java
package com.volqorstudios.thermalprinter.textparser;

import java.util.Hashtable;

import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.exceptions.PrinterParserException;
import com.volqorstudios.thermalprinter.exceptions.PrinterBarcodeException;
import com.volqorstudios.thermalprinter.exceptions.PrinterEncodingException;





public class TextParserColumn {
    private static String buildSpaces(int count) {
        StringBuilder spaces = new StringBuilder();
        
        for (int i = 0; i < count; i++) {
            spaces.append(" ");
        }

        return spaces.toString();
    }

    private TextParserLine line;
    private IPrinterTextElement[] elements = new IPrinterTextElement[0];

    public TextParserColumn(TextParserLine line, String rawColumn) throws PrinterParserException, PrinterBarcodeException, PrinterEncodingException {
        this.line = line;
        TextParser parser = this.line.getTextParser();
        String textAlign = TextParser.TAG_ALIGN_LEFT;

        byte[] underlineAtStart = parser.getLastTextUnderline();
        byte[] doubleStrikeAtStart = parser.getLastTextDoubleStrike();
        byte[] colorAtStart = parser.getLastTextColor();
        byte[] reverseColorAtStart = parser.getLastTextReverseColor();

        if (rawColumn.length() > 2) {
            String prefix = rawColumn.substring(0, 3).toUpperCase();
            
            if (prefix.equals("[" + TextParser.TAG_ALIGN_LEFT + "]") || prefix.equals("[" + TextParser.TAG_ALIGN_CENTER + "]") || prefix.equals("[" + TextParser.TAG_ALIGN_RIGHT + "]")) {
                textAlign = rawColumn.substring(1, 2).toUpperCase();
                rawColumn = rawColumn.substring(3);
            }
        }

        String trimmedColumn = rawColumn.trim();
        boolean isSpecialLine = false;

        if (this.line.getColumnCount() == 1 && trimmedColumn.indexOf("<") == 0) {
            int openTagIndex = trimmedColumn.indexOf("<");
            int openTagEnd = trimmedColumn.indexOf(">", openTagIndex + 1) + 1;

            if (openTagIndex < openTagEnd) {
                TextParserTag tag = new TextParserTag(trimmedColumn.substring(openTagIndex, openTagEnd));

                switch (tag.getTagName()) {
                    case TextParser.TAG_IMAGE:
                    case TextParser.TAG_BARCODE:
                    case TextParser.TAG_QRCODE:
                        String closeTag = "</" + tag.getTagName() + ">";
                        int closeTagPos = trimmedColumn.length() - closeTag.length();

                        if (trimmedColumn.substring(closeTagPos).equals(closeTag)) {
                            String innerContent = trimmedColumn.substring(openTagEnd, closeTagPos);

                            if (tag.getTagName().equals(TextParser.TAG_IMAGE)) {
                                this.addElement(new TextParserImage(this, textAlign, innerContent));
                            } else if (tag.getTagName().equals(TextParser.TAG_BARCODE)) {
                                this.addElement(new TextParserBarcode(this, textAlign, tag.getAttributes(), innerContent));
                            } else if (tag.getTagName().equals(TextParser.TAG_QRCODE)) {
                                this.addElement(new TextParserQRCode(this, textAlign, tag.getAttributes(), innerContent));
                            }

                            isSpecialLine = true;
                        }
                        break;
                }
            }
        }

        if (!isSpecialLine) {
            int offset = 0;
            while (true) {
                int openTagIndex = rawColumn.indexOf("<", offset);
                int closeTagIndex = -1;

                if (openTagIndex != -1) {
                    closeTagIndex = rawColumn.indexOf(">", openTagIndex);
                } else {
                    openTagIndex = rawColumn.length();
                }

                this.appendText(rawColumn.substring(offset, openTagIndex));

                if (closeTagIndex == -1) {
                    break;
                }

                closeTagIndex++;
                TextParserTag tag = new TextParserTag(rawColumn.substring(openTagIndex, closeTagIndex));

                if (TextParser.isFormatTag(tag.getTagName())) {
                    if (tag.isCloseTag()) {
                        switch (tag.getTagName()) {
                            case TextParser.TAG_BOLD:
                                parser.popTextBold();
                                break;
                            case TextParser.TAG_UNDERLINE:
                                parser.popTextUnderline();
                                parser.popTextDoubleStrike();
                                break;
                            case TextParser.TAG_FONT:
                                parser.popTextSize();
                                parser.popTextColor();
                                parser.popTextReverseColor();
                                break;
                        }
                    } else {
                        switch (tag.getTagName()) {
                            case TextParser.TAG_BOLD:
                                parser.pushTextBold(PrinterCommands.TEXT_WEIGHT_BOLD);
                                break;
                            case TextParser.TAG_UNDERLINE:
                                if (tag.hasAttribute(TextParser.ATTR_UNDERLINE_TYPE) && tag.getAttribute(TextParser.ATTR_UNDERLINE_TYPE).equals(TextParser.ATTR_UNDERLINE_DOUBLE)) {
                                    parser.pushTextUnderline(parser.getLastTextUnderline());
                                    parser.pushTextDoubleStrike(PrinterCommands.TEXT_DOUBLE_STRIKE_ON);
                                } else {
                                    parser.pushTextUnderline(PrinterCommands.TEXT_UNDERLINE_LARGE);
                                    parser.pushTextDoubleStrike(parser.getLastTextDoubleStrike());
                                }
                                break;
                            case TextParser.TAG_FONT:
                                applyFontSize(parser, tag);
                                applyFontColor(parser, tag);
                                break;
                        }
                    }

                    offset = closeTagIndex;
                } else {
                    this.appendText("<");
                    offset = openTagIndex + 1;
                }
            }

            int charsPerColumn = this.line.getCharsPerColumn();
            int forgottenChars = this.line.getForgottenChars();
            int exceededChars = this.line.getExceededChars();
            int textCharCount = 0;
            int leftPadding = 0;
            int rightPadding = 0;

            for (IPrinterTextElement element : this.elements) {
                textCharCount += element.length();
            }

            if (textAlign.equals(TextParser.TAG_ALIGN_LEFT)) {
                rightPadding = charsPerColumn - textCharCount;
            } else if (textAlign.equals(TextParser.TAG_ALIGN_CENTER)) {
                leftPadding = (int) Math.floor((((float) charsPerColumn) - ((float) textCharCount)) / 2f);
                rightPadding = charsPerColumn - textCharCount - leftPadding;
            } else if (textAlign.equals(TextParser.TAG_ALIGN_RIGHT)) {
                leftPadding = charsPerColumn - textCharCount;
            }

            if (forgottenChars > 0) {
                forgottenChars -= 1;
                rightPadding++;
            }

            if (exceededChars < 0) {
                leftPadding += exceededChars;
                exceededChars = 0;
                
                if (leftPadding < 1) {
                    rightPadding += leftPadding - 1;
                    leftPadding = 1;
                }
            }

            if (leftPadding < 0) {
                exceededChars += leftPadding;
                leftPadding = 0;
            }
            if (rightPadding < 0) {
                exceededChars += rightPadding;
                rightPadding = 0;
            }

            if (leftPadding > 0) {
                this.prependElement(new TextParserString(this, buildSpaces(leftPadding), PrinterCommands.TEXT_SIZE_NORMAL, colorAtStart, reverseColorAtStart, PrinterCommands.TEXT_WEIGHT_NORMAL, underlineAtStart, doubleStrikeAtStart));
            }
            if (rightPadding > 0) {
                this.addElement(new TextParserString(this, buildSpaces(rightPadding), PrinterCommands.TEXT_SIZE_NORMAL, parser.getLastTextColor(), parser.getLastTextReverseColor(), PrinterCommands.TEXT_WEIGHT_NORMAL, parser.getLastTextUnderline(), parser.getLastTextDoubleStrike()));
            }

            this.line.setForgottenChars(forgottenChars);
            this.line.setExceededChars(exceededChars);
        }
    }

    private void applyFontSize(TextParser parser, TextParserTag tag) {
        if (!tag.hasAttribute(TextParser.ATTR_FONT_SIZE)) {
            parser.pushTextSize(parser.getLastTextSize());
            return;
        }

        switch (tag.getAttribute(TextParser.ATTR_FONT_SIZE)) {
            case TextParser.ATTR_FONT_SIZE_TALL:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_DOUBLE_HEIGHT);
                break;
            case TextParser.ATTR_FONT_SIZE_WIDE:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_DOUBLE_WIDTH);
                break;
            case TextParser.ATTR_FONT_SIZE_BIG:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_BIG);
                break;
            case TextParser.ATTR_FONT_SIZE_BIG_2:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_BIG_2);
                break;
            case TextParser.ATTR_FONT_SIZE_BIG_3:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_BIG_3);
                break;
            case TextParser.ATTR_FONT_SIZE_BIG_4:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_BIG_4);
                break;
            case TextParser.ATTR_FONT_SIZE_BIG_5:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_BIG_5);
                break;
            case TextParser.ATTR_FONT_SIZE_BIG_6:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_BIG_6);
                break;
            default:
                parser.pushTextSize(PrinterCommands.TEXT_SIZE_NORMAL);
                break;
        }
    }

    private void applyFontColor(TextParser parser, TextParserTag tag) {
        if (!tag.hasAttribute(TextParser.ATTR_FONT_COLOR)) {
            parser.pushTextColor(parser.getLastTextColor());
            parser.pushTextReverseColor(parser.getLastTextReverseColor());
            return;
        }
        
        switch (tag.getAttribute(TextParser.ATTR_FONT_COLOR)) {
            case TextParser.ATTR_FONT_COLOR_BG_BLACK:
                parser.pushTextColor(PrinterCommands.TEXT_COLOR_BLACK);
                parser.pushTextReverseColor(PrinterCommands.TEXT_COLOR_REVERSE_ON);
                break;
            case TextParser.ATTR_FONT_COLOR_RED:
                parser.pushTextColor(PrinterCommands.TEXT_COLOR_RED);
                parser.pushTextReverseColor(PrinterCommands.TEXT_COLOR_REVERSE_OFF);
                break;
            case TextParser.ATTR_FONT_COLOR_BG_RED:
                parser.pushTextColor(PrinterCommands.TEXT_COLOR_RED);
                parser.pushTextReverseColor(PrinterCommands.TEXT_COLOR_REVERSE_ON);
                break;
            default:
                parser.pushTextColor(PrinterCommands.TEXT_COLOR_BLACK);
                parser.pushTextReverseColor(PrinterCommands.TEXT_COLOR_REVERSE_OFF);
                break;
        }
    }

    private void appendText(String text) {
        TextParser parser = this.line.getTextParser();
        this.addElement(new TextParserString(this, text, parser.getLastTextSize(), parser.getLastTextColor(), parser.getLastTextReverseColor(), parser.getLastTextBold(), parser.getLastTextUnderline(), parser.getLastTextDoubleStrike()));
    }

    private void addElement(IPrinterTextElement element) {
        IPrinterTextElement[] updated = new IPrinterTextElement[this.elements.length + 1];
        System.arraycopy(this.elements, 0, updated, 0, this.elements.length);
        updated[this.elements.length] = element;
        this.elements = updated;
    }

    private void prependElement(IPrinterTextElement element) {
        IPrinterTextElement[] updated = new IPrinterTextElement[this.elements.length + 1];
        updated[0] = element;
        System.arraycopy(this.elements, 0, updated, 1, this.elements.length);
        this.elements = updated;
    }

    public TextParserLine getLine() {
        return this.line;
    }

    public IPrinterTextElement[] getElements() {
        return this.elements;
    }
}