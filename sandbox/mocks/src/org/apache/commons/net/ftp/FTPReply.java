package org.apache.commons.net.ftp;

public class FTPReply {
    public static int CODE_550 = 0;
    public static int CODE_553 = 0;
    public static boolean isPositiveCompletion(Object replyCode) {
        return false;
    }
    public static boolean isPositiveCompletion(int replyCode) {
        return false;
    }
}
