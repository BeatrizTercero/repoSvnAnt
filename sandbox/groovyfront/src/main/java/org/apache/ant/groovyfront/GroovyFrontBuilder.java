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

import groovy.lang.Closure;
import groovy.util.AntBuilder;
import groovy.xml.QName;

import java.util.Collections;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.ConditionTask;
import org.apache.tools.ant.taskdefs.condition.AccessorHack;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class GroovyFrontBuilder extends AntBuilder {

    public GroovyFrontBuilder(GroovyFrontProject project) {
        super(project);
    }

    protected void setClosureDelegate(Closure closure, Object node) {
        super.setClosureDelegate(closure, node);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
    }

    boolean isTaskDefined(String taskName) {
        Object name = getName(taskName);

        String tagName = name.toString();
        String ns = "";

        if (name instanceof QName) {
            QName q = (QName) name;
            tagName = q.getLocalPart();
            ns = q.getNamespaceURI();
        }

        UnknownElement task = new UnknownElement(tagName);
        task.setProject(getProject());
        task.setNamespace(ns);
        task.setQName(tagName);
        task.setTaskType(ProjectHelper.genComponentName(task.getNamespace(), tagName));
        task.setTaskName(tagName);

        new RuntimeConfigurable(task, task.getTaskName());

        try {
            task.maybeConfigure();
        } catch (BuildException e) {
            return false;
        }
        return true;
    }

    public Condition createCondition(String methodName, Object[] arguments) {
        Object conditionNode = createNode("condition", Collections.singletonMap("property", "__groovyfront_condition__"));
        Object current = getCurrent();
        setCurrent(conditionNode);
        invokeMethod(methodName, arguments);
        setCurrent(current);
        nodeCompleted(current, conditionNode);
        UnknownElement element = (UnknownElement) postNodeCompletion(current, conditionNode);
        element.maybeConfigure();
        TaskAdapter taskAdapter = (TaskAdapter) element.getRealThing();
        ConditionTask conditionTask = (ConditionTask) taskAdapter.getProxy();
        return (Condition) AccessorHack.getConditions(conditionTask).nextElement();
    }
}
