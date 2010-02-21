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
import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.Tuple;

import java.util.Hashtable;
import java.util.Map;

import org.apache.ant.groovyfront.jvm15backport.Arrays;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.AntlibDefinition;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.codehaus.groovy.runtime.MetaClassHelper;

public class GroovyFrontScriptMetaClass extends DelegatingMetaClass {

    private final GroovyFrontProject project;
    private final GroovyFrontParsingContext context;
    private final GroovyFrontBuilder groovyFrontBuilder;

    public GroovyFrontScriptMetaClass(MetaClass metaClass, GroovyFrontProject project,
            GroovyFrontBuilder groovyFrontBuilder, GroovyFrontParsingContext context) {
        super(metaClass);
        this.project = project;
        this.groovyFrontBuilder = groovyFrontBuilder;
        this.context = context;
    }

    public Object invokeMethod(final Object object, final String methodName, final Object arguments) {
        if (arguments == null) {
            return invokeMethod(object, methodName, MetaClassHelper.EMPTY_ARRAY);
        } else if (arguments instanceof Tuple) {
            return invokeMethod(object, methodName, ((Tuple) arguments).toArray());
        } else if (arguments instanceof Object[]) {
            return invokeMethod(object, methodName, (Object[]) arguments);
        } else {
            return invokeMethod(object, methodName, new Object[] { arguments });
        }
    }

    public Object invokeMethod(final String name, final Object args) {
        return invokeMethod(this, name, args);
    }

    public Object invokeMethod(final Class sender, final Object receiver, final String methodName,
            final Object[] arguments, final boolean isCallToSuper, final boolean fromInsideClass) {
        return invokeMethod(receiver, methodName, arguments);
    }

    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        logDebug("trying", methodName);

        if ("target".equals(methodName)) {
            logDebug("caught", methodName);
            defineTarget(arguments);
            return null;
        }

        if ("include".equals(methodName)) {
            logDebug("caught", methodName);
            return importScript(arguments);
        }

        if ("groovyns".equals(methodName)) {
            if (arguments.length != 1 && !(arguments[0] instanceof Map)) {
                throw new BuildException("Invalid method signature for 'groovyns'");
            }
            String uri = (String) ((Map) arguments[0]).get("uri");
            if (uri == null) {
                throw new BuildException("Missing 'uri' argument on 'groovyns'");
            }
            String prefix = (String) ((Map) arguments[0]).get("prefix");
            logDebug("caught", methodName);
            return new SimpleNamespaceBuilder(groovyFrontBuilder, prefix, uri);
        }

        if (!groovyFrontBuilder.isNotCondition(methodName, arguments)) {
            Condition condition = groovyFrontBuilder.createCondition(methodName, arguments);
            logDebug("caught", methodName);
            return Boolean.valueOf(condition.eval());
        }

        Object returnObject;
        try {
            // try the script functions
            returnObject = super.invokeMethod(object, methodName, arguments);
        } catch (MissingMethodException mme) {
            // this may be a call to an ant task
            if (groovyFrontBuilder.isTaskDefined(methodName)
                    && groovyFrontBuilder.isNotCondition(methodName, arguments)) {
                return groovyFrontBuilder.invokeMethod(methodName, arguments);
            }
            logDebug("missed", methodName);
            throw mme;
        }
        logDebug("caught", methodName);

        if (returnObject instanceof AntlibDefinition) {
            // wrap the antlib into a builder
            AntlibDefinition antlibDefinition = (AntlibDefinition) returnObject;
            returnObject = new SimpleNamespaceBuilder(groovyFrontBuilder, antlibDefinition.getURI(), antlibDefinition
                    .getURI());
        }

        return returnObject;
    }

    private void logDebug(String status, String methodName) {
        project.log(status + " in GroovyFrontScriptMetaClass: " + methodName, Project.MSG_DEBUG);
    }

    private void defineTarget(Object[] args) {
        if (args.length != 2 || !(args[0] instanceof Map) || !(args[1] instanceof Closure)) {
            throw new BuildException("A target is ill formed. Expecting map, closure but was: " + Arrays.toString(args));
        }
        Map/* <String, String> */map = (Map/* <String, String> */) args[0];
        Closure closure = (Closure) args[1];
        closure.setDelegate(groovyFrontBuilder);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        String name = (String) map.get("name");
        String description = (String) map.get("description");
        String depends = (String) map.get("depends");

        if (name == null) {
            throw new BuildException("a target name must be set");
        }
        if (name.length() == 0) {
            throw new BuildException("a target name must not be empty");
        }
        // Check if this target is in the current build file
        if (context.getCurrentTargets().get(name) != null) {
            throw new BuildException("Duplicate target '" + name + "'");
        }

        GroovyFrontTarget target = new GroovyFrontTarget(groovyFrontBuilder, closure);
        target.setName(name);
        target.setProject(project);
        target.setLocation(new Location(groovyFrontBuilder.getAntXmlContext().getLocator()));

        if (depends != null) {
            target.setDepends(depends);
        }
        if (description != null) {
            target.setDescription(description);
        }

        Hashtable/* <?, ?> */projectTargets = project.getTargets();
        // If the name has not already been defined, log an override
        // NB: unlike ant xml project helper, the imported file are executed before the target definition of the main
        // file
        if (projectTargets.containsKey(name)) {
            project.log("Overrided target " + name + ": it has already defined in a previous import",
                    Project.MSG_VERBOSE);
        }
        context.getCurrentTargets().put(name, target);
        project.addOrReplaceTarget(name, target);

        if (context.isImported() && context.getCurrentProjectName() != null
                && context.getCurrentProjectName().length() != 0) {
            // In an imported file (and not completely ignoring the project tag)
            String newName = context.getCurrentProjectName() + "." + name;
            Target newTarget = new GroovyFrontTarget(target);
            newTarget.setName(newName);
            context.getCurrentTargets().put(newName, newTarget);
            project.addOrReplaceTarget(newName, newTarget);
        }
    }

    private Object importScript(Object[] arguments) {
        return groovyFrontBuilder.invokeMethod("import", arguments);
    }

}
