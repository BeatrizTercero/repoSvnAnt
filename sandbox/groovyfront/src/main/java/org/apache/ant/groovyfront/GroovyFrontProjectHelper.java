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
import groovy.lang.Script;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.ant.groovyfront.cache.CachedGroovyScriptLoader;
import org.apache.ant.groovyfront.cache.GroovyScriptCacheCleaner;
import org.apache.ant.groovyfront.cache.GroovyScriptLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.types.Resource;
import org.codehaus.groovy.control.CompilationFailedException;

public class GroovyFrontProjectHelper extends ProjectHelper {

    private static final String REFID_CONTEXT = "groovyfront.parsingcontext";

    private static final String REFID_BUILDER = "groovyfront.builder";

    private static final String SYSPROP_USECACHE = "ant.groovyfront.usecache";

    private static final String SYSPROP_CLEANER_MINPERIOD = "ant.groovyfront.cachecleaner.minperiod";

    private static final String SYSPROP_CLEANER_TIMETOLIVE = "ant.groovyfront.cachecleaner.timetolive";

    private static final String SYSPROP_CLEANER_MAXTOKEEP = "ant.groovyfront.cachecleaner.maxtokeep";

    private static final String SYSPROP_CACHEDIR = "ant.groovyfront.cachedir";

    private static final String USER_HOMEDIR = "user.home";
    private static final String ANT_PRIVATEDIR = ".ant";
    private static final String GROOVYFRONT_CACHE = "groovyfront-cache";

    private boolean useCache = Boolean.getBoolean(SYSPROP_USECACHE);

    private long cleanerMinPeriod;

    private Long cleanerTimetolive;

    private Integer cleanerMaxtokeep;

    public GroovyFrontProjectHelper() {
        String cleanerMinPeriodValue = System
                .getProperty(SYSPROP_CLEANER_MINPERIOD);
        if (cleanerMinPeriodValue == null) {
            // by default clean no more than every hour
            cleanerMinPeriod = 1000 * 60 * 60;
        } else {
            cleanerMinPeriod = Long.parseLong(cleanerMinPeriodValue);
        }
        String timetoliveValue = System.getProperty(SYSPROP_CLEANER_TIMETOLIVE);
        if (timetoliveValue != null) {
            cleanerTimetolive = new Long(timetoliveValue);
        }
        String maxtokeepValue = System.getProperty(SYSPROP_CLEANER_MAXTOKEEP);
        if (maxtokeepValue != null) {
            cleanerMaxtokeep = new Integer(maxtokeepValue);
        }
    }

    public String getDefaultBuildFile() {
        return "build.groovy";
    }

    public boolean canParseBuildFile(Resource buildFile) {
        return buildFile.getName().toLowerCase().endsWith(".groovy");
    }

    public void parse(Project project, Object source) throws BuildException {
        if (!(source instanceof Resource)) {
            throw new BuildException(
                    "The GroovyFrontProjectHelper is expecting a Resource as the source of the build file. Got "
                            + source.getClass().getName() + " instead.");
        }
        Resource resource = (Resource) source;
        Vector/* <Object> */stack = getImportStack();
        stack.addElement(source);
        GroovyFrontParsingContext context = null;
        context = (GroovyFrontParsingContext) project
                .getReference(REFID_CONTEXT);
        if (context == null) {
            context = new GroovyFrontParsingContext();
            project.addReference(REFID_CONTEXT, context);
        }

        if (getImportStack().size() > 1) {
            Map/* <String, Target> */currentTargets = context
                    .getCurrentTargets();
            String currentProjectName = context.getCurrentProjectName();
            boolean imported = context.isImported();
            try {
                context.setImported(true);
                context.setCurrentTargets(new HashMap/* <String, Target> */());
                parse(project, resource, context);
            } finally {
                context.setCurrentTargets(currentTargets);
                context.setCurrentProjectName(currentProjectName);
                context.setImported(imported);
            }
        } else {
            // top level file
            context.setCurrentTargets(new HashMap/* <String, Target> */());
            parse(project, resource, context);
        }
    }

    private void parse(Project project, Resource resource,
            GroovyFrontParsingContext context) throws BuildException {
        String buildFileName = resource.getName();

        // set explicitly before starting ?
        if (project.getProperty("basedir") != null) {
            project.setBasedir(project.getProperty("basedir"));
            // NB: this won't be overridden as it is a user property (see
            // GroovyFrontProject class)
        } else {
            // set the property even if it may be overridden within the groovy
            // file
            project.setBasedir(context.getBuildFileParent().getAbsolutePath());
        }

        // wrap the project instance so we can be in control of the set on the
        // properties on the project
        GroovyFrontProject groovyFrontProject;
        if (project instanceof GroovyFrontProject) {
            groovyFrontProject = (GroovyFrontProject) project;
        } else {
            groovyFrontProject = new GroovyFrontProject(project, context,
                    buildFileName);
        }

        GroovyFrontBuilder antBuilder = new GroovyFrontBuilder(
                groovyFrontProject);
        groovyFrontProject.addReference(REFID_BUILDER, antBuilder);
        Binding binding = new GroovyFrontBinding(groovyFrontProject, antBuilder);

        GroovyScriptLoader scriptLoader;
        if (useCache) {
            File cacheDir = getCacheDir();
            scriptLoader = new CachedGroovyScriptLoader(cacheDir);
            GroovyScriptCacheCleaner.launchClean(groovyFrontProject, cacheDir,
                    cleanerMinPeriod, cleanerTimetolive, cleanerMaxtokeep);
        } else {
            scriptLoader = new GroovyScriptLoader();
        }

        final Script script;
        try {
            script = scriptLoader.loadScript(resource, binding, this.getClass()
                    .getClassLoader());
        } catch (CompilationFailedException e) {
            throw new BuildException("Error reading groovy file "
                    + buildFileName + ": " + e.getMessage(), e);
        }

        script.setBinding(binding);
        script.setMetaClass(new GroovyFrontScriptMetaClass(script
                .getMetaClass(), groovyFrontProject, antBuilder, context));
        new GroovyRunner() {
            protected void doRun() {
                script.run();
            }
        }.run();
    }

    private File getCacheDir() {
        String cacheDirPath = System.getProperty(SYSPROP_CACHEDIR);
        File dir;
        if (cacheDirPath != null) {
            dir = new File(cacheDirPath);
        } else {
            String userHome = System.getProperty(USER_HOMEDIR);
            dir = new File(userHome + File.separatorChar + ANT_PRIVATEDIR
                    + File.separatorChar + GROOVYFRONT_CACHE);
        }
        return dir;
    }

}
