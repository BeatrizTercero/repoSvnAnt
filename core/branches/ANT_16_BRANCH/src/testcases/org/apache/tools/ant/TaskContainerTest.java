/*
 * Copyright  2003-2004 The Apache Software Foundation
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

package org.apache.tools.ant;

public class TaskContainerTest extends BuildFileTest {

    public TaskContainerTest(String name) {
        super(name);
    }

    public void setUp() {
        configureProject("src/etc/testcases/core/taskcontainer.xml");
    }

    public void tearDown() {
        executeTarget("cleanup");
    }

    public void testPropertyExpansion() {
        executeTarget("testPropertyExpansion");
        assertTrue("attribute worked",
                   getLog().indexOf("As attribute: it worked") > -1);
        assertTrue("nested text worked",
                   getLog().indexOf("As nested text: it worked") > -1);
    }

    public void testTaskdef() {
        executeTarget("testTaskdef");
        assertTrue("attribute worked",
                   getLog().indexOf("As attribute: it worked") > -1);
        assertTrue("nested text worked",
                   getLog().indexOf("As nested text: it worked") > -1);
        assertTrue("nested text worked",
                   getLog().indexOf("As nested task: it worked") > -1);
    }

    public void testCaseInsensitive() {
        executeTarget("testCaseInsensitive");
        assertTrue("works outside of container",
                   getLog().indexOf("hello ") > -1);
        assertTrue("works inside of container",
                   getLog().indexOf("world") > -1);
    }

}
