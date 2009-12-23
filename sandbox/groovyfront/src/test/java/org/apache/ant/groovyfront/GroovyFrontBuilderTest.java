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
package org.apache.ant.groovyfront;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.ant.groovyfront.GroovyFrontBuilder;
import org.apache.ant.groovyfront.GroovyFrontParsingContext;
import org.apache.ant.groovyfront.GroovyFrontProject;
import org.apache.tools.ant.Project;
import org.junit.Test;

public class GroovyFrontBuilderTest {

    @Test
    public void testIsTaskDefined() throws Exception {
        GroovyFrontProject project = new GroovyFrontProject(new Project(), new GroovyFrontParsingContext(), "build.groovy");
        project.init();
        GroovyFrontBuilder groovyFront = new GroovyFrontBuilder(project);
        assertTrue(groovyFront.isTaskDefined("echo"));
        assertFalse(groovyFront.isTaskDefined("mytask"));
        project.addTaskDefinition("mytask", MyTestTask.class);
        assertTrue(groovyFront.isTaskDefined("mytask"));
    }
}
