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
import groovy.lang.MissingMethodException;
import groovy.util.AntBuilder;
import groovy.xml.QName;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.ConditionTask;
import org.apache.tools.ant.taskdefs.condition.AccessorHack;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.codehaus.groovy.runtime.InvokerHelper;

public class GroovyFrontBuilder extends AntBuilder {

    // TODO: find a way to handle that list dynamically
    private static final Set CONDITION_TASK_NAMES = new HashSet(Arrays.asList(new Object[] { "available", "uptodate",
            "antversion" }));

    private IntrospectionHelper conditionHelper;

    public GroovyFrontBuilder(GroovyFrontProject project) {
        super(project);
        conditionHelper = IntrospectionHelper.getHelper(project, ConditionTask.class);
    }

    protected Object doInvokeMethod(String methodName, Object name, Object args) {
        Project p = getProject();
        logDebug(p, "trying", methodName);
        try {
            Object o;
            try {
                o = super.doInvokeMethod(methodName, name, args);
            } catch (BuildException be) {
                // try to find out why we failed
                Object[] argsArray = InvokerHelper.asList(args).toArray();
                if (isTaskDefined(methodName) && isNotCondition(methodName, argsArray)) {
                    // this should have been an ant task but there is an Ant configuration error
                    throw be;
                }
                // just continue to lookup
                throw new MissingMethodException(methodName, getClass(), argsArray, false);
                // TODO check that failing to resolve a nested type is properly caught up by some parent builder
            }
            logDebug(p, "caught", methodName);
            return o;
        } catch (MissingMethodException mme) {
            logDebug(p, "missed", methodName);
            throw mme;
        }
    }

    private void logDebug(Project p, String status, String methodName) {
        p.log(status + " in GroovyFrontBuilder: " + methodName, Project.MSG_DEBUG);
    }

    protected void setClosureDelegate(Closure closure, Object node) {
        super.setClosureDelegate(closure, node);
        // ensure that we first hit the delegate: the builder
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

    /**
     * Check that the method invoked is not a 'pure' condition. This function is taking into account the difference
     * between the conditions like 'available' which as task must has 'property' set but as 'pure' condition doesn't.
     * 
     * @param methodName
     * @param arguments
     * @return
     */
    public boolean isNotCondition(String methodName, Object[] arguments) {
        if (CONDITION_TASK_NAMES.contains(methodName)) {
            return arguments.length > 0 && arguments[0] instanceof Map && ((Map) arguments[0]).containsKey("property");
        }
        Enumeration elements = conditionHelper.getNestedElements();
        while (elements.hasMoreElements()) {
            if (elements.nextElement().equals(methodName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a 'pure' condition
     * 
     * @param methodName
     * @param arguments
     * @return
     */
    public Condition createCondition(String methodName, Object[] arguments) {
        Object conditionNode = createNode("condition", Collections
                .singletonMap("property", "__groovyfront_condition__"));
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
