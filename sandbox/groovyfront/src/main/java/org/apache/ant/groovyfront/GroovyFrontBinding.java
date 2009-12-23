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

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;

import java.util.Map;

import org.apache.tools.ant.BuildException;

public class GroovyFrontBinding extends Binding {

    public static final String NULL_TOSTRING = "null";

    private final GroovyFrontProject project;

    private final GroovyFrontBuilder groovyFrontBuilder;

    public GroovyFrontBinding(final GroovyFrontProject project, final GroovyFrontBuilder grovyFrontBuilder) {
        this.project = project;
        this.groovyFrontBuilder = grovyFrontBuilder;
    }

    @Override
    public Object getVariable(final String name) {
        Object returnValue = project.getProperty(name);
        if (returnValue == null) {
            returnValue = project.getReference(name);
            if (returnValue == null) {
                returnValue = groovyFrontBuilder.getProperty(name);
                if (returnValue == null) {
                    throw new MissingPropertyException(name + " is undefined");
                }
            }
        }
        return returnValue;
    }

    @Override
    public void setVariable(final String name, final Object newValue) {
        if ("project".equals(name)) {
            throw new BuildException("The variable 'project' cannot be overriden");
        }
        project.setNewProperty(name, newValue == null ? NULL_TOSTRING : newValue.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map getVariables() {
        return project.getProperties();
    }

}
