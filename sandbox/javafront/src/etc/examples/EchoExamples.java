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
package org.example;

import org.apache.ant.javafront.BuildFileBase;
import org.apache.ant.javafront.annotations.AntProject;
import org.apache.ant.javafront.annotations.AntTarget;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import static org.apache.ant.javafront.builder.EchoBuilder.echoMessage;

@AntProject(Description="lists all build files in the current directory",
            DefaultTarget="list")
public class EchoExamples extends BuildFileBase {
    public EchoExamples(Project p) {
        super(p);
    }

    @AntTarget(Description="list the files in the example dir")
    public void list() {
        FileSet fs = (FileSet)
            build().tag("fileset")
            .withAttribute("dir", ".")
            .withAttribute("includes", "*.java")
            .build();
        DirectoryScanner ds = fs.getDirectoryScanner();
        for (String file : ds.getIncludedFiles()) {
            echoMessage(getProject(), "found " + file);
        }
    }
}