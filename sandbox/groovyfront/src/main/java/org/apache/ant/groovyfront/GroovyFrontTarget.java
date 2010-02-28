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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;

public class GroovyFrontTarget extends Target {

    private final Closure closure;
    private final GroovyFrontBuilder groovyFrontBuilder;

    public GroovyFrontTarget(GroovyFrontBuilder groovyFrontBuilder, Closure closure) {
        this.groovyFrontBuilder = groovyFrontBuilder;
        this.closure = closure;
    }

    public GroovyFrontTarget(GroovyFrontTarget other) {
        super(other);
        this.closure = other.closure;
        this.groovyFrontBuilder = other.groovyFrontBuilder;
    }

    public void execute() throws BuildException {
        groovyFrontBuilder.getAntXmlContext().setCurrentTarget(this);
        if (closure == null) {
            return;
        }
        final Closure c = closure;
        // if (testIfCondition() && testUnlessCondition()) {
        new GroovyRunner() {
            protected void doRun() {
                c.call();
            }
        }.run();
        // } else if (!testIfCondition()) {
        // project.log(this, "Skipped because property '" + project.replaceProperties(ifCondition) + "' not set.",
        // Project.MSG_VERBOSE);
        // } else {
        // project.log(this, "Skipped because property '" + project.replaceProperties(unlessCondition) + "' set.",
        // Project.MSG_VERBOSE);
        // }
    }
}
