/*
 * Copyright  2000,2002-2004 Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Keyword substitution. Input file is written to output file.
 * Do not make input file same as output file.
 * Keywords in input files look like this: @foo@. See the docs for the
 * setKeys method to understand how to do the substitutions.
 *
 * @author Jon S. Stevens <a href="mailto:jon@clearink.com">jon@clearink.com</a>
 * @since Ant 1.1
 * @deprecated KeySubst is deprecated since Ant 1.1. Use Filter + Copy
 * instead.
 */
public class KeySubst extends Task {
    private File source = null;
    private File dest = null;
    private String sep = "*";
    private Hashtable replacements = new Hashtable();

    /**
        Do the execution.
    */
    public void execute() throws BuildException {
        log("!! KeySubst is deprecated. Use Filter + Copy instead. !!");
        log("Performing Substitutions");
        if (source == null || dest == null) {
            log("Source and destinations must not be null");
            return;
        }
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(source));
            dest.delete();
            bw = new BufferedWriter(new FileWriter(dest));

            String line = null;
            String newline = null;
            line = br.readLine();
            while (line != null) {
                if (line.length() == 0) {
                    bw.newLine();
                } else {
                    newline = KeySubst.replace(line, replacements);
                    bw.write(newline);
                    bw.newLine();
                }
                line = br.readLine();
            }
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
        Set the source file.
    */
    public void setSrc(File s) {
        this.source = s;
    }

    /**
        Set the destination file.
    */
    public void setDest(File dest) {
        this.dest = dest;
    }

    /**
        Sets the separator between name=value arguments
        in setKeys(). By default it is "*".
    */
    public void setSep(String sep) {
        this.sep = sep;
    }
    /**
     * Sets the keys.
     *
        Format string is like this:
        <p>
        name=value*name2=value
        <p>
        Names are case sensitive.
        <p>
        Use the setSep() method to change the * to something else
        if you need to use * as a name or value.
    */
    public void setKeys(String keys) {
        if (keys != null && keys.length() > 0) {
            StringTokenizer tok =
            new StringTokenizer(keys, this.sep, false);
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken().trim();
                StringTokenizer itok =
                new StringTokenizer(token, "=", false);

                String name = itok.nextToken();
                String value = itok.nextToken();
                replacements.put(name, value);
            }
        }
    }


    public static void main(String[] args) {
        try {
            Hashtable hash = new Hashtable();
            hash.put("VERSION", "1.0.3");
            hash.put("b", "ffff");
            System.out.println(KeySubst.replace("$f ${VERSION} f ${b} jj $",
                                                hash));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
        Does replacement on text using the hashtable of keys.

        @return the string with the replacements in it.
    */
    public static String replace(String origString, Hashtable keys)
        throws BuildException {
        StringBuffer finalString = new StringBuffer();
        int index = 0;
        int i = 0;
        String key = null;
        while ((index = origString.indexOf("${", i)) > -1) {
            key = origString.substring(index + 2, origString.indexOf("}",
                                       index + 3));
            finalString.append (origString.substring(i, index));
            if (keys.containsKey(key)) {
                finalString.append (keys.get(key));
            } else {
                finalString.append ("${");
                finalString.append (key);
                finalString.append ("}");
            }
            i = index + 3 + key.length();
        }
        finalString.append (origString.substring(i));
        return finalString.toString();
    }
}
