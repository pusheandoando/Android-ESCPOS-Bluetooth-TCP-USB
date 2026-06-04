// androidescpos/src/main/java/com/volqorstudios/thermalprinter/textparser/IPrinterTextElement.java
package com.volqorstudios.thermalprinter.textparser;

import com.volqorstudios.thermalprinter.PrinterCommands;
import com.volqorstudios.thermalprinter.exceptions.PrinterEncodingException;
import com.volqorstudios.thermalprinter.exceptions.PrinterConnectionException;





public interface IPrinterTextElement {
    int length() throws PrinterEncodingException;
    IPrinterTextElement print(PrinterCommands printerCommands) throws PrinterEncodingException, PrinterConnectionException;
}