/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
