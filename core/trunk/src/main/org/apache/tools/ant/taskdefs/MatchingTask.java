/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.*;

import java.io.*;
import java.util.*;

/**
 * This is an abstract task that should be used by all those tasks that 
 * require to include or exclude files based on pattern matching.
 *
 * @author Arnout J. Kuiper <a href="mailto:ajkuiper@wxs.nl">ajkuiper@wxs.nl</a> 
 * @author Stefano Mazzocchi <a href="mailto:stefano@apache.org">stefano@apache.org</a>
 * @author Jon S. Stevens <a href="mailto:jon@clearink.com">jon@clearink.com</a>
 */

public abstract class MatchingTask extends Task {

    protected Vector includeList = new Vector();
    protected Vector excludeList = new Vector();
    protected boolean useDefaultExcludes = true;

    // inner class to hold a name on list
    public class NameEntry {
        private boolean valid = true;
        private String name;

        public String getName() { return valid ? name : null; }
        public void setName(String name) { this.name = name; }

        public void setIfClassFound(String name) {
            try {
              Class.forName(name);
            } catch (ClassNotFoundException cnf) {
              valid = false;
            }
        }

        public void setUnlessClassFound(String name) {
            try {
              Class.forName(name);
              valid = false;
            } catch (ClassNotFoundException cnf) {
            }
        }
    }

    /**
     * add a name entry on the include list
     */
    public NameEntry createInclude() {
        NameEntry result = new NameEntry();
        includeList.addElement(result);
        return result;
    }
    
    /**
     * add a name entry on the exclude list
     */
    public NameEntry createExclude() {
        NameEntry result = new NameEntry();
        excludeList.addElement(result);
        return result;
    }

    /**
     * Sets the set of include patterns. Patterns may be separated by a comma
     * or a space.
     *
     * @param includes the string containing the include patterns
     */
    public void setIncludes(String includes) {
        if (includes != null && includes.length() > 0) {
            createInclude().setName(includes);
        }
    }

    /**
     * Set this to be the items in the base directory that you want to be
     * included. You can also specify "*" for the items (ie: items="*") 
     * and it will include all the items in the base directory.
     *
     * @param itemString the string containing the files to include.
     */
    public void setItems(String itemString) {
        project.log("The items attribute is deprecated. " +
                    "Please use the includes attribute.",
                    Project.MSG_WARN);
        if (itemString == null || itemString.equals("*")) {
            createInclude().setName("**");
        } else {
            StringTokenizer tok = new StringTokenizer(itemString, ", ");
            while (tok.hasMoreTokens()) {
                String pattern = tok.nextToken().trim();
                if (pattern.length() > 0) {
                    createInclude().setName(pattern+"/**");
                }
            }
        }
    }
    
    /**
     * Sets the set of exclude patterns. Patterns may be separated by a comma
     * or a space.
     *
     * @param excludes the string containing the exclude patterns
     */
    public void setExcludes(String excludes) {
        if (excludes != null && excludes.length() > 0) {
            createExclude().setName(excludes);
        }
    }

    /**
     * List of filenames and directory names to not include. They should be 
     * either , or " " (space) separated. The ignored files will be logged.
     *
     * @param ignoreString the string containing the files to ignore.
     */
    public void setIgnore(String ignoreString) {
        project.log("The ignore attribute is deprecated." + 
                    "Please use the excludes attribute.",
                    Project.MSG_WARN);
        if (ignoreString != null && ignoreString.length() > 0) {
            Vector tmpExcludes = new Vector();
            StringTokenizer tok = new StringTokenizer(ignoreString, ", ", false);
            while (tok.hasMoreTokens()) {
                createExclude().setName("**/"+tok.nextToken().trim()+"/**");
            }
        }
    }
    
    /**
     * Sets whether default exclusions should be used or not.
     *
     * @param useDefaultExcludes "true"|"on"|"yes" when default exclusions 
     *                           should be used, "false"|"off"|"no" when they
     *                           shouldn't be used.
     */
    public void setDefaultexcludes(String useDefaultExcludes) {
        this.useDefaultExcludes = Project.toBoolean(useDefaultExcludes);
    }
    
    /**
     * Convert a vector of NameEntry elements into an array of Strings.
     */
    private String[] makeArray(Vector list) {
        if (list.size() == 0) return null;

        Vector tmpNames = new Vector();
        for (Enumeration e = list.elements() ; e.hasMoreElements() ;) {
            String includes = ((NameEntry)e.nextElement()).getName();
            if (includes == null) continue;
            StringTokenizer tok = new StringTokenizer(includes, ", ", false);
            while (tok.hasMoreTokens()) {
                String pattern = tok.nextToken().trim();
                if (pattern.length() > 0) {
                    tmpNames.addElement(pattern);
                }
            }
        }

        String result[] = new String[tmpNames.size()];
        for (int i = 0; i < tmpNames.size(); i++) {
            result[i] = (String)tmpNames.elementAt(i);
        }

        return result;
    }
        
    /**
     * Returns the directory scanner needed to access the files to process.
     */
    protected DirectoryScanner getDirectoryScanner(File baseDir) {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(baseDir);
        ds.setIncludes(makeArray(includeList));
        ds.setExcludes(makeArray(excludeList));
        if (useDefaultExcludes) ds.addDefaultExcludes();
        ds.scan();
        return ds;
    }
}
