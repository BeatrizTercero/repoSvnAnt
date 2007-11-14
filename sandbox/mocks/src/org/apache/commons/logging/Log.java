package org.apache.commons.logging;

public class Log {
    public void debug(String s) {}
    public void error(String s) {}
    public void error(String s, Throwable t) {}
    public void info(String message, Throwable t) {}
    public void info(String message) {}
    public boolean isTraceEnabled() {
        return false;
    }
    public void warn(String message, Throwable t) {}
    public void warn(String message) {}
}
