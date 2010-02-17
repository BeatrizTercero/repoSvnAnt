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
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.groovy.control.CompilationFailedException;

public class GroovyFrontProjectHelper extends ProjectHelper {

    private static final String REFID_CONTEXT = "groovyfront.parsingcontext";

    private static final String REFID_BUILDER = "groovyfront.builder";

    public String getDefaultBuildFile() {
        return "build.groovy";
    }

    public boolean canParseBuildFile(Resource buildFile) {
        return buildFile.getName().toLowerCase().endsWith(".groovy");
    }

    public void parse(Project project, Object source) throws BuildException {
        Vector/*<Object>*/ stack = getImportStack();
        stack.addElement(source);
        GroovyFrontParsingContext context = null;
        context = (GroovyFrontParsingContext) project.getReference(REFID_CONTEXT);
        if (context == null) {
            context = new GroovyFrontParsingContext();
            project.addReference(REFID_CONTEXT, context);
        }

        if (getImportStack().size() > 1) {
            Map/*<String, Target>*/ currentTargets = context.getCurrentTargets();
            String currentProjectName = context.getCurrentProjectName();
            boolean imported = context.isImported();
            try {
                context.setImported(true);
                context.setCurrentTargets(new HashMap/*<String, Target>*/());
                parse(project, source, context);
            } finally {
                context.setCurrentTargets(currentTargets);
                context.setCurrentProjectName(currentProjectName);
                context.setImported(imported);
            }
        } else {
            // top level file
            context.setCurrentTargets(new HashMap/*<String, Target>*/());
            parse(project, source, context);
        }
    }

    private void parse(Project project, Object source, GroovyFrontParsingContext context) throws BuildException {
        InputStream in;
        String buildFileName = null;

        try {
            if (source instanceof File) {
                File buildFile = (File) source;
                buildFileName = buildFile.toString();
                buildFile = FileUtils.getFileUtils().normalize(buildFile.getAbsolutePath());
                context.setBuildFile(buildFile);
                in = new FileInputStream(buildFile);
                // } else if (source instanceof InputStream ) {
            } else if (source instanceof URL) {
                URL url = (URL) source;
                buildFileName = url.toString();
                in = url.openStream();
                // } else if (source instanceof InputSource ) {
            } else {
                throw new BuildException("Source " + source.getClass().getName() + " not supported by this plugin");
            }
        } catch (IOException e) {
            throw new BuildException("Error reading groovy file " + buildFileName + ": " + e.getMessage(), e);
        }

        // set explicitly before starting ?
        if (project.getProperty("basedir") != null) {
            project.setBasedir(project.getProperty("basedir"));
            // NB: this won't be overridden as it is a user property (see GroovyFrontProject class)
        } else {
            // set the property even if it may be overridden within the groovy file
            project.setBasedir(context.getBuildFileParent().getAbsolutePath());
        }

        // wrap the project instance so we can be in control of the set on the properties on the project
        GroovyFrontProject groovyFrontProject;
        if (project instanceof GroovyFrontProject) {
            groovyFrontProject = (GroovyFrontProject) project;
        } else {
            groovyFrontProject = new GroovyFrontProject(project, context, buildFileName);
        }

        GroovyFrontBuilder antBuilder = new GroovyFrontBuilder(groovyFrontProject);
        groovyFrontProject.addReference(REFID_BUILDER, antBuilder);
        Binding binding = new GroovyFrontBinding(groovyFrontProject, antBuilder);
        GroovyShell groovyShell = new GroovyShell(getClass().getClassLoader(), binding);
        final Script script;
        try {
            script = groovyShell.parse(in, buildFileName);
        } catch (CompilationFailedException e) {
            throw new BuildException("Error reading groovy file " + buildFileName + ": " + e.getMessage(), e);
        }
        script.setBinding(binding);
        script.setMetaClass(new GroovyFrontScriptMetaClass(script.getMetaClass(), groovyFrontProject, antBuilder, context));
        new GroovyRunner() {
            protected void doRun() {
                script.run();
            }
        }.run();
    }

}
