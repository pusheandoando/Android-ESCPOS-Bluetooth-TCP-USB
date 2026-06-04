// androidescpos/src/main/java/com/volqorstudios/thermalprinter/connection/tcp/NetworkConnection.java
package com.volqorstudios.thermalprinter.connection.tcp;

import java.net.Socket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.volqorstudios.thermalprinter.connection.DeviceConnection;
import com.volqorstudios.thermalprinter.exceptions.PrinterConnectionException;





public class NetworkConnection extends DeviceConnection {
    private Socket socket = null;
    private String address;
    private int port;
    private int timeoutMs;

    public NetworkConnection(String address, int port) {
        this(address, port, 30);
    }

    public NetworkConnection(String address, int port, int timeoutMs) {
        super();
        this.address = address;
        this.port = port;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected() && super.isConnected();
    }

    @Override
    public NetworkConnection connect() throws PrinterConnectionException {
        if (this.isConnected()) {
            return this;
        }

        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(InetAddress.getByName(this.address), this.port), this.timeoutMs);
            this.outputStream = this.socket.getOutputStream();
            this.data = new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            this.disconnect();
            throw new PrinterConnectionException("Unable to connect to TCP device.");
        }
        
        return this;
    }

    @Override
    public NetworkConnection disconnect() {
        this.data = new byte[0];
        
        if (this.outputStream != null) {
            try {
                this.outputStream.close();
                this.outputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.socket != null) {
            try {
                this.socket.close();
                this.socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this;
    }
}