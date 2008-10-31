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
package org.apache.ant.javafront.example;

import org.apache.ant.javafront.BuildFileBase;
import org.apache.ant.javafront.annotations.AntProject;
import org.apache.ant.javafront.annotations.AntTarget;
import org.apache.tools.ant.Project;

import static org.apache.ant.javafront.builder.EchoBuilder.echoMessage;
import static org.apache.ant.javafront.builder.DeleteBuilder.deleteDir;
import static org.apache.ant.javafront.builder.MkdirBuilder.mkdir;

@AntProject(Name="example using AntUnit", BaseDir="../../..",
            DefaultTarget="antunit")
public class AntUnitTest extends BuildFileBase {
    private static final String FILE = "build.xml";
    private static final String OUTPUT = "${output}";

    public AntUnitTest(Project p) {
        super(p);
        build().property().withName("output").andLocation("build/testoutput")
            .execute();
    }

    @AntTarget(Description="describes this build file")
    public void describe() {
        echoMessage(getProject(), "${ant.version}");
        echoMessage(getProject(), "Demonstrates running an AntUnit test.");
    }

    @AntTarget
    public void setUp() {
        mkdir(getProject(), OUTPUT);
    }

    @AntTarget
    public void tearDown() {
        deleteDir(getProject(), OUTPUT);
    }

    @AntTarget
    public void testCopy() {
        build().copy().withAttribute("verbose", "true")
            .file(FILE).toDir(OUTPUT)
            .execute();
        build().tagWithNs("assertFileExists", "antlib:org.apache.ant.antunit")
            .withAttribute("file", OUTPUT + "/" + FILE)
            .execute();
    }

    @AntTarget(Description="runs the test")
    public void antunit() {
        build().tagWithNs("antunit", "antlib:org.apache.ant.antunit")
            .withChild(build().tagWithNs("plainlistener",
                                         "antlib:org.apache.ant.antunit"))
            .withChild(build().tag("file").withAttribute("file",
                                                         "${ant.file}"))
            .execute();
    }
}