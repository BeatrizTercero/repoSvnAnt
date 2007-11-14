package org.apache.log4j;

public class Logger {
    public void error(String s) {}
    public void debug(String s) {}
    public void error(String s, Throwable t) {}
    public Object getAllAppenders() {
        return null;
    }
    public static Logger getLogger(String logAnt) {
        return null;
    }
    public static Logger getRootLogger() {
        return null;
    }
    public void info(String string) {}
    public void warn(String message) {}
}
