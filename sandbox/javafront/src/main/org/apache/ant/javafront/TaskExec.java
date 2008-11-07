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
package org.apache.ant.javafront;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Map;

import org.apache.tools.ant.launch.*;
import org.apache.tools.ant.*;

import org.apache.ant.javafront.builder.TagBuilder;
import org.apache.ant.javafront.builder.Tag;


public class TaskExec implements AntMain {

    public void startAnt(String[] args, Properties additionalUserProperties, ClassLoader coreLoader) {
        Map<String, String> attributes = new Hashtable<String, String>();

        // Analyzing the command line arguments
        String taskname = args[0];
        for(int i=1; i<args.length; ) {
            String attrName  = args[i++];
            String attrValue = args[i++];
            attributes.put(attrName, attrValue);
        }

        // Initializing
        Project    project = initProject();
        TagBuilder builder = TagBuilder.forProject(project);

        // Initializing the Task
        Tag tag = builder.tag(taskname);
        for (String key : attributes.keySet()) {
            tag.withAttribute(key, attributes.get(key));
        }

        // Run the task
        tag.execute();
    }

    private Project initProject() {
        DefaultLogger logger = new DefaultLogger();
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        logger.setMessageOutputLevel(Project.MSG_INFO);

        Project rv = new Project();
        rv.addBuildListener(logger);
        rv.init();

        return rv;
    }

}