package org.apache.commons.net.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TelnetClient {
    public void connect(String s, int i) throws IOException {}
    public void disconnect() throws IOException {}
    public InputStream getInputStream() {
        return null;
    }
    public OutputStream getOutputStream() {
        return null;
    }
    public boolean isConnected() {
        return false;
    }
}
