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

/**
 * Query status of a package 
 */
public class StatusDpkg extends AbstractDpkgTask {

    private String packageName;
    
    public void execute() throws BuildException {
        validate();
        Commandline c = new Commandline();
        c.setExecutable("dpkg");
        String[] args = new String[] { "--status", packageName };
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
}