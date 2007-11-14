package org.apache.regexp;

public class RE {
    public static int MATCH_NORMAL = 0;
    public static int MATCH_CASEINDEPENDENT = 0;
    public static int MATCH_MULTILINE = 0;
    public static int MATCH_SINGLELINE = 0;
    public static int REPLACE_FIRSTONLY = 0;
    public static int REPLACE_ALL = 0;
    
    public RE(String s) {}

    public String getParen(int i) {
        return null;
    }
    public int getParenCount() {
        return 0;
    }
    public boolean match(String input) {
        return false;
    }
    public void setMatchFlags(int options) throws RESyntaxException {}
    public String subst(String input, String argument, int options) {
        return null;
    }
}
