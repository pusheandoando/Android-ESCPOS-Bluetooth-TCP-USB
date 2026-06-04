// androidescpos/src/main/java/com/volqorstudios/thermalprinter/PrinterCharset.java
package com.volqorstudios.thermalprinter;





public class PrinterCharset {
    private String charsetName;
    private byte[] charsetCommand;

    public PrinterCharset(String charsetName, int escPosCharsetId) {
        this.charsetName = charsetName;
        this.charsetCommand = new byte[]{0x1B, 0x74, (byte) escPosCharsetId};
    }

    public byte[] getCommand() {
        return this.charsetCommand;
    }

    public String getName() {
        return this.charsetName;
    }
}