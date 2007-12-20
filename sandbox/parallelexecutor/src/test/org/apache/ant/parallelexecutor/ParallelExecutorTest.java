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
package org.apache.ant.parallelexecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * Test class for the ParallelExecutor.
 */
public class ParallelExecutorTest extends TestCase {
    
    /**
     * Runs the project and returns the order of the executed targets.
     * @param pathToBuildfile relative path to the buildfile
     * @param arguments additional arguments to pass to Ant
     * @return the configured Ant project
     */
    @SuppressWarnings("unchecked")
    protected List<String> runProject(String pathToBuildfile, String targets) {
        Vector targetNames = new Vector();
        for (String target : targets.split(" ")) {
            targetNames.add(target);
        }
        Project project = new Project();
        project.init();
        File antFile = new File(System.getProperty("root"), pathToBuildfile);
        project.setUserProperty("ant.file" , antFile.getAbsolutePath());
        project.setUserProperty("ant.executor.class", "org.apache.ant.parallelexecutor.ParallelExecutor");
        BuildOrderListener orderListener = new BuildOrderListener();
        project.addBuildListener(orderListener);
        ProjectHelper.configureProject(project, antFile);
        project.executeTargets(targetNames);
        return orderListener.getOrder();
    }

    
    /**
     * BuildListener which collects the order of executed targets.
     */
    class BuildOrderListener implements BuildListener {
        List<String> order = new ArrayList<String>();
        public List<String> getOrder() {
            return order;
        }
        public void targetStarted(BuildEvent event) {
            order.add(event.getTarget().getName());
        }
        // methods we dont need here
        public void buildFinished(BuildEvent event) {}
        public void buildStarted(BuildEvent event) {}
        public void messageLogged(BuildEvent event) {}
        public void targetFinished(BuildEvent event) {}
        public void taskFinished(BuildEvent event) {}
        public void taskStarted(BuildEvent event) {}
    }
    
    
    /**
     * Converts a string-list into a string for easier comparison without
     * any separation string.
     * @param list input
     * @return converted list
     */
    protected String list2String(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String string : list) {
            sb.append(string);
        }
        return sb.toString();
    }
    
    
    public void testOneTarget() {
        String result = list2String(runProject("src/testdata/simple.xml", "A"));
        assertEquals("A", result);
    }
    
    public void testDependsOn1Target() {
        String result = list2String(runProject("src/testdata/simple-dep.xml", "A"));
        assertEquals("BA", result);
    }

    public void testDependsOn2Targets() {
        String result = list2String(runProject("src/testdata/double-deps.xml", "A"));
        assertTrue("Wrong order: " + result, result.equals("BCA") || result.equals("CBA") );
    }
    
    public void testDependsOn2and1Targets() {
        String result = list2String(runProject("src/testdata/multiple-deps.xml", "A"));
        assertTrue("Wrong order: " + result,  result.equals("DBCA") || result.equals("DCBA") );
    }
    
    public void testFailingTarget1() {
        String result = list2String(runProject("src/testdata/failures.xml", "A"));
        assertEquals("A", result);
        //TODO: check that A throws a BuildException
    }
    
    public void testFailingTarget2() {
        String result = list2String(runProject("src/testdata/failures.xml", "B"));
        assertEquals("C", result);
        //TODO: check that C throws a BuildException
    }

    public void testFailingTarget3() {
        String result = list2String(runProject("src/testdata/failures.xml", "D"));
        assertEquals("ED", result);
        //TODO: check that D throws a BuildException
    }

    public void testFailingTarget4() {
        String result = list2String(runProject("src/testdata/failures.xml", "F"));
        assertTrue("Wrong order: " + result,  result.equals("GH") || result.equals("HG") );
        //TODO: check that G and H throw a BuildException
    }

}
