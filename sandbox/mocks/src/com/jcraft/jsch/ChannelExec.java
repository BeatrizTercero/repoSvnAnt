package com.jcraft.jsch;

import java.io.OutputStream;

public class ChannelExec extends Channel {
    public int getExitStatus() {
        return 0;
    }
    public boolean isClosed() {
        return false;
    }
    public void setCommand(String command) {}
    public void setExtOutputStream(OutputStream tee) {}
    public void setOutputStream(OutputStream tee) {
    }
}
