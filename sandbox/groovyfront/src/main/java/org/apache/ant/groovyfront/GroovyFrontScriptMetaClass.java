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
import groovy.lang.MetaClass;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.AntlibDefinition;

public class GroovyFrontScriptMetaClass extends GroovyFrontMetaClass {

    private final GroovyFrontProject project;
    private final GroovyFrontParsingContext context;

    public GroovyFrontScriptMetaClass(MetaClass metaClass, GroovyFrontProject project, GroovyFrontBuilder groovyFrontBuilder,
            GroovyFrontParsingContext context) {
        super(metaClass, groovyFrontBuilder);
        this.project = project;
        this.context = context;
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        if ("target".equals(methodName)) {
            defineTarget(arguments);
            return null;
        }
        if ("include".equals(methodName)) {
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
            return new SimpleNamespaceBuilder(groovyFrontBuilder, prefix, uri);
        }
        Object returnObject = super.invokeMethod(object, methodName, arguments);
        if (returnObject instanceof AntlibDefinition) {
            AntlibDefinition antlibDefinition = (AntlibDefinition) returnObject;
            returnObject = new SimpleNamespaceBuilder(groovyFrontBuilder, antlibDefinition.getURI(), antlibDefinition.getURI());
        }
        return returnObject;
    }

    private void defineTarget(Object[] args) {
        if (args.length != 2 || !(args[0] instanceof Map) || !(args[1] instanceof Closure)) {
            throw new BuildException("A target is ill formed. Expecting map, closure but was: " + Arrays.toString(args));
        }
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) args[0];
        Closure closure = (Closure) args[1];
        closure.setMetaClass(new GroovyFrontMetaClass(closure.getMetaClass(), groovyFrontBuilder));
        String name = map.get("name");
        String description = map.get("description");
        String depends = map.get("depends");

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

        Hashtable<?, ?> projectTargets = project.getTargets();
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
