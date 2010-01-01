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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;

public class GroovyFrontProject extends Project {

    private final Project delegate;

    private final GroovyFrontParsingContext context;

    private final String buildFileName;

    public GroovyFrontProject(Project project, GroovyFrontParsingContext context, String buildFileName) {
        this.delegate = project;
        this.context = context;
        this.buildFileName = buildFileName;
    }

    protected Project getDelegate() {
        return delegate;
    }

    public void setName(String name) {
        delegate.setName(name);
        context.setCurrentProjectName(name);
        String antFileProp = MagicNames.ANT_FILE + name;
        String dup = delegate.getProperty(antFileProp);
        if (dup != null) {
            File dupFile = new File(dup);
            if (context.isImported() && !dupFile.equals(context.getBuildFile())) {
                delegate.log("Duplicated project name in import. Project " + name + " defined first in " + dup
                        + " and again in " + context.getBuildFile(), Project.MSG_WARN);
            }
        }

        if (context.getBuildFile() != null) {
            delegate.setUserProperty(antFileProp, context.getBuildFile().toString());
        }
    }

    public String getName() {
        if (delegate.getName() != null) {
            return delegate.getName();
        }
        return buildFileName;
    }

    public void setBaseDir(File baseDir) throws BuildException {
        if (context.isImported()) {
            return;
        }
        if (getDelegate().getUserProperty(MagicNames.PROJECT_BASEDIR) != null) {
            // the user specified it explicitly (probably on the command line
            // so we won't override anything
            return;
        }
        // check whether the user has specified an absolute path
        if (baseDir.isAbsolute()) {
            getDelegate().setBaseDir(baseDir);
        } else {
            getDelegate().setBaseDir(
                    FileUtils.getFileUtils().resolveFile(context.getBuildFileParent(), baseDir.getPath()));
        }
    }

    public void setBasedir(String baseD) throws BuildException {
        if (context.isImported()) {
            return;
        }
        setBaseDir(new File(baseD));
    }

    public void setDefault(String defaultTarget) {
        if (context.isImported()) {
            return;
        }
        delegate.setDefault(defaultTarget);
    }

    public void setDefaultTarget(String defaultTarget) {
        if (context.isImported()) {
            return;
        }
        delegate.setDefaultTarget(defaultTarget);
    }

    public void setDescription(String description) {
        if (context.isImported()) {
            return;
        }
        delegate.setDescription(description);
    }

    public void addBuildListener(BuildListener listener) {
        delegate.addBuildListener(listener);
    }

    public void addDataTypeDefinition(String typeName, Class typeClass) {
        delegate.addDataTypeDefinition(typeName, typeClass);
    }

    public void addFilter(String token, String value) {
        delegate.addFilter(token, value);
    }

    public void addIdReference(String id, Object value) {
        delegate.addIdReference(id, value);
    }

    public void addOrReplaceTarget(String targetName, Target target) {
        delegate.addOrReplaceTarget(targetName, target);
    }

    public void addOrReplaceTarget(Target target) {
        delegate.addOrReplaceTarget(target);
    }

    public void addReference(String referenceName, Object value) {
        delegate.addReference(referenceName, value);
    }

    public void addTarget(String targetName, Target target) throws BuildException {
        delegate.addTarget(targetName, target);
    }

    public void addTarget(Target target) throws BuildException {
        delegate.addTarget(target);
    }

    public void addTaskDefinition(String taskName, Class taskClass) throws BuildException {
        delegate.addTaskDefinition(taskName, taskClass);
    }

    public void checkTaskClass(Class taskClass) throws BuildException {
        delegate.checkTaskClass(taskClass);
    }

    public void copyFile(File sourceFile, File destFile, boolean filtering, boolean overwrite,
            boolean preserveLastModified) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering, overwrite, preserveLastModified);
    }

    public void copyFile(File sourceFile, File destFile, boolean filtering, boolean overwrite) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering, overwrite);
    }

    public void copyFile(File sourceFile, File destFile, boolean filtering) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering);
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        delegate.copyFile(sourceFile, destFile);
    }

    public void copyFile(String sourceFile, String destFile, boolean filtering, boolean overwrite,
            boolean preserveLastModified) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering, overwrite, preserveLastModified);
    }

    public void copyFile(String sourceFile, String destFile, boolean filtering, boolean overwrite) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering, overwrite);
    }

    public void copyFile(String sourceFile, String destFile, boolean filtering) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering);
    }

    public void copyFile(String sourceFile, String destFile) throws IOException {
        delegate.copyFile(sourceFile, destFile);
    }

    public void copyInheritedProperties(Project other) {
        delegate.copyInheritedProperties(other);
    }

    public void copyUserProperties(Project other) {
        delegate.copyUserProperties(other);
    }

    public AntClassLoader createClassLoader(ClassLoader parent, Path path) {
        return delegate.createClassLoader(parent, path);
    }

    public AntClassLoader createClassLoader(Path path) {
        return delegate.createClassLoader(path);
    }

    public Object createDataType(String typeName) throws BuildException {
        return delegate.createDataType(typeName);
    }

    public Project createSubProject() {
        return delegate.createSubProject();
    }

    public Task createTask(String taskType) throws BuildException {
        return delegate.createTask(taskType);
    }

    public int defaultInput(byte[] buffer, int offset, int length) throws IOException {
        return delegate.defaultInput(buffer, offset, length);
    }

    public void demuxFlush(String output, boolean isError) {
        delegate.demuxFlush(output, isError);
    }

    public int demuxInput(byte[] buffer, int offset, int length) throws IOException {
        return delegate.demuxInput(buffer, offset, length);
    }

    public void demuxOutput(String output, boolean isWarning) {
        delegate.demuxOutput(output, isWarning);
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public void executeSortedTargets(Vector sortedTargets) throws BuildException {
        delegate.executeSortedTargets(sortedTargets);
    }

    public void executeTarget(String targetName) throws BuildException {
        delegate.executeTarget(targetName);
    }

    public void executeTargets(Vector names) throws BuildException {
        delegate.executeTargets(names);
    }

    public void fireBuildFinished(Throwable exception) {
        delegate.fireBuildFinished(exception);
    }

    public void fireBuildStarted() {
        delegate.fireBuildStarted();
    }

    public void fireSubBuildFinished(Throwable exception) {
        delegate.fireSubBuildFinished(exception);
    }

    public void fireSubBuildStarted() {
        delegate.fireSubBuildStarted();
    }

    public File getBaseDir() {
        return delegate.getBaseDir();
    }

    public Vector getBuildListeners() {
        return delegate.getBuildListeners();
    }

    public ClassLoader getCoreLoader() {
        return delegate.getCoreLoader();
    }

    public Hashtable getDataTypeDefinitions() {
        return delegate.getDataTypeDefinitions();
    }

    public InputStream getDefaultInputStream() {
        return delegate.getDefaultInputStream();
    }

    public String getDefaultTarget() {
        return delegate.getDefaultTarget();
    }

    public String getDescription() {
        return delegate.getDescription();
    }

    public String getElementName(Object element) {
        return delegate.getElementName(element);
    }

    public Executor getExecutor() {
        return delegate.getExecutor();
    }

    public Hashtable getFilters() {
        return delegate.getFilters();
    }

    public FilterSet getGlobalFilterSet() {
        return delegate.getGlobalFilterSet();
    }

    public InputHandler getInputHandler() {
        return delegate.getInputHandler();
    }

    public Hashtable getProperties() {
        return delegate.getProperties();
    }

    public String getProperty(String propertyName) {
        return delegate.getProperty(propertyName);
    }

    public Object getReference(String key) {
        return delegate.getReference(key);
    }

    public Hashtable getReferences() {
        return delegate.getReferences();
    }

    public Resource getResource(String name) {
        return delegate.getResource(name);
    }

    public Hashtable getTargets() {
        return delegate.getTargets();
    }

    public Hashtable getTaskDefinitions() {
        return delegate.getTaskDefinitions();
    }

    public Task getThreadTask(Thread thread) {
        return delegate.getThreadTask(thread);
    }

    public Hashtable getUserProperties() {
        return delegate.getUserProperties();
    }

    public String getUserProperty(String propertyName) {
        return delegate.getUserProperty(propertyName);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public void inheritIDReferences(Project parent) {
        delegate.inheritIDReferences(parent);
    }

    public void init() throws BuildException {
        delegate.init();
    }

    public void initProperties() throws BuildException {
        delegate.initProperties();
    }

    public void initSubProject(Project subProject) {
        delegate.initSubProject(subProject);
    }

    public boolean isKeepGoingMode() {
        return delegate.isKeepGoingMode();
    }

    public void log(String message, int msgLevel) {
        delegate.log(message, msgLevel);
    }

    public void log(String message, Throwable throwable, int msgLevel) {
        delegate.log(message, throwable, msgLevel);
    }

    public void log(String message) {
        delegate.log(message);
    }

    public void log(Target target, String message, int msgLevel) {
        delegate.log(target, message, msgLevel);
    }

    public void log(Target target, String message, Throwable throwable, int msgLevel) {
        delegate.log(target, message, throwable, msgLevel);
    }

    public void log(Task task, String message, int msgLevel) {
        delegate.log(task, message, msgLevel);
    }

    public void log(Task task, String message, Throwable throwable, int msgLevel) {
        delegate.log(task, message, throwable, msgLevel);
    }

    public void registerThreadTask(Thread thread, Task task) {
        delegate.registerThreadTask(thread, task);
    }

    public void removeBuildListener(BuildListener listener) {
        delegate.removeBuildListener(listener);
    }

    public String replaceProperties(String value) throws BuildException {
        return delegate.replaceProperties(value);
    }

    public File resolveFile(String fileName, File rootDir) {
        return delegate.resolveFile(fileName, rootDir);
    }

    public File resolveFile(String fileName) {
        return delegate.resolveFile(fileName);
    }

    public void setCoreLoader(ClassLoader coreLoader) {
        delegate.setCoreLoader(coreLoader);
    }

    public void setDefaultInputStream(InputStream defaultInputStream) {
        delegate.setDefaultInputStream(defaultInputStream);
    }

    public void setExecutor(Executor e) {
        delegate.setExecutor(e);
    }

    public void setFileLastModified(File file, long time) throws BuildException {
        delegate.setFileLastModified(file, time);
    }

    public void setInheritedProperty(String name, String value) {
        delegate.setInheritedProperty(name, value);
    }

    public void setInputHandler(InputHandler handler) {
        delegate.setInputHandler(handler);
    }

    public void setJavaVersionProperty() throws BuildException {
        delegate.setJavaVersionProperty();
    }

    public void setKeepGoingMode(boolean keepGoingMode) {
        delegate.setKeepGoingMode(keepGoingMode);
    }

    public void setNewProperty(String name, String value) {
        delegate.setNewProperty(name, value);
    }

    public void setProperty(String name, String value) {
        delegate.setProperty(name, value);
    }

    public void setSystemProperties() {
        delegate.setSystemProperties();
    }

    public void setUserProperty(String name, String value) {
        delegate.setUserProperty(name, value);
    }

    public String toString() {
        return delegate.toString();
    }

    public Hashtable getInheritedProperties() {
        return delegate.getInheritedProperties();
    }

    public boolean hasReference(String key) {
        return delegate.hasReference(key);
    }

}
