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
package org.apache.ant.debian;

import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.BuildException;

/**
 * Remove a debian package 
 */
public class RemoveDpkg extends AbstractDpkgTask {

    /** the name of the .deb package to create */
    private String packageName;
    
    /** are we going to remove config files too ? */
    private boolean purge = false;
    
    public void execute() throws BuildException {
        validate();
        Commandline c = new Commandline();
        c.setExecutable("dpkg");
        String[] args;
        if(purge) {
            args = new String[] { "--purge", packageName };
        } else {
            args = new String[] { "--remove", packageName };
        }
        c.addArguments(args);
        super.addConfiguredCommandline(c);
        super.execute();
    }
    
    private void validate() throws BuildException {
        if((packageName == null || packageName.length() == 0)) {
            throw new BuildException("You must specify a packagename.");
        }
    }

    /* setters */
    public void setPackageName(final String p) {
        packageName = p;
    }
    
    public void setPurge(final boolean p) {
        purge = p;
    }
}