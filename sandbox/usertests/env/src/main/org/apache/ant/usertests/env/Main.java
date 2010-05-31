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

package org.apache.ant.usertests.env;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        System.out.println("Detected OS_NAME: " + Os.OS_NAME);
        System.out.println("Detected OS_ARCH: " + Os.OS_ARCH);
        System.out.println("Detected OS_VERSION: " + Os.OS_VERSION);

        final boolean verbose = args.length > 0 && args[0].equals("-v");
        Vector oldStyle = Execute.getProcEnvironment();
        Collections.sort(oldStyle);
        if (verbose) {
            System.out.println("");
            System.out.println("Ant 1.8.1 way:");
            System.out.println("==============");
            for (Object s : oldStyle) {
                System.out.println(s);
            }
        }
        Vector newStyle = newStyle();
        Collections.sort(newStyle);
        if (verbose) {
            System.out.println("");
            System.out.println("Ant 1.8.2 way:");
            System.out.println("==============");
            for (Object s : newStyle) {
                System.out.println(s);
            }
        }

        System.out.println("");

        int os = oldStyle.size(), ns = newStyle.size();
        boolean differences = os != ns;
        for (int oi = 0, ni = 0; oi < os && ni < ns; ) {
            String oldLine = (String) oldStyle.get(oi);
            String newLine = (String) newStyle.get(ni);
            if (oldLine.equals(newLine)) {
                oi++;
                ni++;
            } else if (oldLine.compareTo(newLine) <= 0) {
                System.out.println("Only in 1.8.1: " + oldLine);
                oi++;
            } else {
                System.out.println("Only in 1.8.2: " + newLine);
                ni++;
            }
        }

        if (!differences) {
            System.out.println("Both methods returned the same"
                               + " environment information");
        }
    }

    private static Vector newStyle() {
        Vector procEnvironment = new Vector();
        Map<String,String> env = System.getenv();
        Iterator<Map.Entry<String, String>> it = env.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            procEnvironment.add(entry.getKey() + "=" + entry.getValue());
        }
        return procEnvironment;
    }
}