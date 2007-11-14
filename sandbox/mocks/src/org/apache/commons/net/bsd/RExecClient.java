package org.apache.commons.net.bsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RExecClient {
    public static int DEFAULT_PORT = 0;
    public void connect(String s, int i) throws IOException {}
    public void disconnect() throws IOException {}
    public OutputStream getOutputStream() {
        return null;
    }
    public InputStream getInputStream() {
        return null;
    }
    public boolean isConnected() {
        return false;
    }
    public void rexec(String s1, String s2, String s3) throws IOException {}
}
