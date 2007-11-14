package com.jcraft.jsch;

import java.io.File;
import java.util.Vector;

public class ChannelSftp extends ChannelExec {
    public static int SSH_FX_NO_SUCH_FILE;
    public class LsEntry {
        public A getAttrs() {
            return null;
        }
        public String getFilename() {
            return null;
        }
    }
    public void cd(String s) {}
    public void get(String s1, String s2, SftpProgressMonitor p) {}
    public Vector ls(String remoteFile) {
        return null;
    }
    public void mkdir(String dir) {}
    public void put(String absolutePath, String remotePath, SftpProgressMonitor monitor) {}
    public String pwd() {
        return null;
    }
    public SftpATTRS stat(String remoteFile) throws SftpException {
        return null;
    }
    
    public class A extends File {
        private static final long serialVersionUID = 0L;
        public A() {
            super("");
        }
        public long getSize() {
            return 0;
        }
        public boolean isDir() {
            return false;
        }
    }
}
