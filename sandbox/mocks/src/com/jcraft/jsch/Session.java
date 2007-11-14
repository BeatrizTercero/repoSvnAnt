package com.jcraft.jsch;

public class Session extends Channel {
    public boolean isConnected() {
        return false;
    }
    public ChannelExec openChannel(String string) throws JSchException {
        return null;
    }
    public void setTimeout(int maxwait) {}
    public void setUserInfo(UserInfo userInfo) {}
}
