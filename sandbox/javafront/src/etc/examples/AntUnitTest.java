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

@AntProject(Name="example using AntUnit", BaseDir="../../..",
            DefaultTarget="antunit")
public class AntUnitTest extends BuildFileBase {
    public AntUnitTest(Project p) {
        super(p);
    }

    @AntTarget(Description="describes this build file")
    public void describe() {
        build().newTag("echo")
            .withAttribute("message", "${ant.version}").execute();
        build().newTag("echo")
            .withNestedText("Demonstrates running an AntUnit test.").execute();
    }

    @AntTarget
    public void setUp() {
        build().newTag("mkdir")
            .withAttribute("dir", "build/testoutput").execute();
    }

    @AntTarget
    public void tearDown() {
        build().newTag("delete")
            .withAttribute("dir", "build/testoutput").execute();
    }

    @AntTarget
    public void testCopy() {
        build().newCopy().withAttribute("verbose", "true")
            .file("build.xml").toDir("build/testoutput")
            .execute();
        build().newTag("assertFileExists", "antlib:org.apache.ant.antunit")
            .withAttribute("file", "build/testoutput/build.xml")
            .execute();
    }

    @AntTarget(Description="runs the test")
    public void antunit() {
        build().newTag("antunit", "antlib:org.apache.ant.antunit")
            .withChild(build().newTag("plainlistener",
                                      "antlib:org.apache.ant.antunit"))
            .withChild(build().newTag("file").withAttribute("file",
                                                            "${ant.file}"))
            .execute();
    }
}