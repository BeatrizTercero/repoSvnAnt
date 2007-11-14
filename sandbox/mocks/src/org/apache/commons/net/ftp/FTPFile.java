package org.apache.commons.net.ftp;

import java.util.Date;

public class FTPFile {
    public String getLink() {
        return null;
    }
    public String getName() {
        return null;
    }
    public A getTimestamp() {
        return null;
    }
    public boolean isDirectory() {
        return false;
    }
    public boolean isFile() {
        return false;
    }
    public boolean isSymbolicLink() {
        return false;
    }
    
    public class A {
        public Date getTime() {
            return null;
        }
    }
}
