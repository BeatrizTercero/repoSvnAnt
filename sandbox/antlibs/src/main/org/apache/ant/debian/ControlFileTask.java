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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ControlFile task
 * Use to create a debian control file
 */
public class ControlFileTask extends Task {

    /**
     * The file to write the control file info to
     */
    private File file;

    /**
     * The file encoding
     * defaults to utf-8
     */
    private String encoding = "UTF-8";

    private ControlFile controlFile = new ControlFile();

    /**
     * Create the debian control file when used as a task.
     * @throws org.apache.tools.ant.BuildException if the control file cannot be written.
     */
    public void execute() throws BuildException {
        if (file == null) {
            throw new BuildException("the file attribute is required");
        }
        PrintWriter w = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
            w = new PrintWriter(osw);
            controlFile.write(w);
        } catch (IOException e) {
            throw new BuildException("Failed to write " + file,
                                     e, getLocation());
        } finally {
           FileUtils.close(w);
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setControlFile(ControlFile controlFile) {
        this.controlFile = controlFile;
    }

    public void setPackage(String debPackage) {
        controlFile.setDebPackage(debPackage);
    }

    public void setSection(String debSection) {
        controlFile.setDebSection(debSection);
    }

    public void setVersion(String debVersion) {
        controlFile.setDebVersion(debVersion);
    }

    public void setPriority(String debPriority) {
        controlFile.setDebPriority(debPriority);
    }

    public void setArchitecture(String debArchitecture) {
        controlFile.setDebArchitecture(debArchitecture);
    }

    public void setEssential(String debEssential) {
        controlFile.setDebEssential(debEssential);
    }

    public void setMaintainer(String debMaintainer) {
        controlFile.setDebMaintainer(debMaintainer);
    }

    public void setProvides(String debProvides) {
        controlFile.setDebProvides(debProvides);
    }

    public void addDescription(ControlFile.Description description) {
        controlFile.addDescription(description);
    }

    public void addDependency(ControlFile.Dependency dependency) {
        controlFile.addDependency(dependency);
    }
}