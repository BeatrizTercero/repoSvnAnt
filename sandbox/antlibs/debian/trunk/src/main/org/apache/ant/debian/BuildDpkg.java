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

import java.io.File;


/**
 * Build a debian package with dpkg
 */
public class BuildDpkg extends AbstractDpkgTask {

    /** the directory to use as a source for dpkg */
    private File dir;

    /** the name of the .deb package to create */
    private String packageName;

    public void execute() throws BuildException {
        validate();
        Commandline c = new Commandline();
        c.setExecutable("dpkg");
        String[] args = { "--build", dir.getAbsolutePath(), packageName };
        c.addArguments(args);
        super.addConfiguredCommandline(c);
        super.execute();
    }

    private void validate() throws BuildException {
        if (null == dir || dir.toString().length() == 0) {
            throw new BuildException("You must set a dir that contains the files you wish to package.");
        }
        if (!dir.canRead()) {
            throw new BuildException("Cannot read contents of ["+dir+"].");
        }
        if (null == packageName || packageName.length() == 0) {
           log("You haven't specified a package name, the file will be created under ["+dir+"].");
        }
        //TODO add checks for missing control file, and package name checks
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}