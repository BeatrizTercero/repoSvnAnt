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

    @Override
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

    @Override
    public String getName() {
        if (delegate.getName() != null) {
            return delegate.getName();
        }
        return buildFileName;
    }

    @Override
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

    @Override
    public void setBasedir(String baseD) throws BuildException {
        if (context.isImported()) {
            return;
        }
        setBaseDir(new File(baseD));
    }

    @Override
    public void setDefault(String defaultTarget) {
        if (context.isImported()) {
            return;
        }
        delegate.setDefault(defaultTarget);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setDefaultTarget(String defaultTarget) {
        if (context.isImported()) {
            return;
        }
        delegate.setDefaultTarget(defaultTarget);
    }

    @Override
    public void setDescription(String description) {
        if (context.isImported()) {
            return;
        }
        delegate.setDescription(description);
    }

    @Override
    public void addBuildListener(BuildListener listener) {
        delegate.addBuildListener(listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addDataTypeDefinition(String typeName, Class typeClass) {
        delegate.addDataTypeDefinition(typeName, typeClass);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addFilter(String token, String value) {
        delegate.addFilter(token, value);
    }

    @Override
    public void addIdReference(String id, Object value) {
        delegate.addIdReference(id, value);
    }

    @Override
    public void addOrReplaceTarget(String targetName, Target target) {
        delegate.addOrReplaceTarget(targetName, target);
    }

    @Override
    public void addOrReplaceTarget(Target target) {
        delegate.addOrReplaceTarget(target);
    }

    @Override
    public void addReference(String referenceName, Object value) {
        delegate.addReference(referenceName, value);
    }

    @Override
    public void addTarget(String targetName, Target target) throws BuildException {
        delegate.addTarget(targetName, target);
    }

    @Override
    public void addTarget(Target target) throws BuildException {
        delegate.addTarget(target);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addTaskDefinition(String taskName, Class taskClass) throws BuildException {
        delegate.addTaskDefinition(taskName, taskClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void checkTaskClass(Class taskClass) throws BuildException {
        delegate.checkTaskClass(taskClass);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyFile(File sourceFile, File destFile, boolean filtering, boolean overwrite,
            boolean preserveLastModified) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering, overwrite, preserveLastModified);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyFile(File sourceFile, File destFile, boolean filtering, boolean overwrite) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering, overwrite);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyFile(File sourceFile, File destFile, boolean filtering) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyFile(File sourceFile, File destFile) throws IOException {
        delegate.copyFile(sourceFile, destFile);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyFile(String sourceFile, String destFile, boolean filtering, boolean overwrite,
            boolean preserveLastModified) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering, overwrite, preserveLastModified);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyFile(String sourceFile, String destFile, boolean filtering, boolean overwrite) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering, overwrite);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyFile(String sourceFile, String destFile, boolean filtering) throws IOException {
        delegate.copyFile(sourceFile, destFile, filtering);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void copyFile(String sourceFile, String destFile) throws IOException {
        delegate.copyFile(sourceFile, destFile);
    }

    @Override
    public void copyInheritedProperties(Project other) {
        delegate.copyInheritedProperties(other);
    }

    @Override
    public void copyUserProperties(Project other) {
        delegate.copyUserProperties(other);
    }

    @Override
    public AntClassLoader createClassLoader(ClassLoader parent, Path path) {
        return delegate.createClassLoader(parent, path);
    }

    @Override
    public AntClassLoader createClassLoader(Path path) {
        return delegate.createClassLoader(path);
    }

    @Override
    public Object createDataType(String typeName) throws BuildException {
        return delegate.createDataType(typeName);
    }

    @Override
    public Project createSubProject() {
        return delegate.createSubProject();
    }

    @Override
    public Task createTask(String taskType) throws BuildException {
        return delegate.createTask(taskType);
    }

    @Override
    public int defaultInput(byte[] buffer, int offset, int length) throws IOException {
        return delegate.defaultInput(buffer, offset, length);
    }

    @Override
    public void demuxFlush(String output, boolean isError) {
        delegate.demuxFlush(output, isError);
    }

    @Override
    public int demuxInput(byte[] buffer, int offset, int length) throws IOException {
        return delegate.demuxInput(buffer, offset, length);
    }

    @Override
    public void demuxOutput(String output, boolean isWarning) {
        delegate.demuxOutput(output, isWarning);
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void executeSortedTargets(Vector sortedTargets) throws BuildException {
        delegate.executeSortedTargets(sortedTargets);
    }

    @Override
    public void executeTarget(String targetName) throws BuildException {
        delegate.executeTarget(targetName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void executeTargets(Vector names) throws BuildException {
        delegate.executeTargets(names);
    }

    @Override
    public void fireBuildFinished(Throwable exception) {
        delegate.fireBuildFinished(exception);
    }

    @Override
    public void fireBuildStarted() {
        delegate.fireBuildStarted();
    }

    @Override
    public void fireSubBuildFinished(Throwable exception) {
        delegate.fireSubBuildFinished(exception);
    }

    @Override
    public void fireSubBuildStarted() {
        delegate.fireSubBuildStarted();
    }

    @Override
    public File getBaseDir() {
        return delegate.getBaseDir();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Vector getBuildListeners() {
        return delegate.getBuildListeners();
    }

    @Override
    public ClassLoader getCoreLoader() {
        return delegate.getCoreLoader();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getDataTypeDefinitions() {
        return delegate.getDataTypeDefinitions();
    }

    @Override
    public InputStream getDefaultInputStream() {
        return delegate.getDefaultInputStream();
    }

    @Override
    public String getDefaultTarget() {
        return delegate.getDefaultTarget();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public String getElementName(Object element) {
        return delegate.getElementName(element);
    }

    @Override
    public Executor getExecutor() {
        return delegate.getExecutor();
    }

    @SuppressWarnings( { "unchecked", "deprecation" })
    @Override
    public Hashtable getFilters() {
        return delegate.getFilters();
    }

    @Override
    public FilterSet getGlobalFilterSet() {
        return delegate.getGlobalFilterSet();
    }

    @Override
    public InputHandler getInputHandler() {
        return delegate.getInputHandler();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getProperties() {
        return delegate.getProperties();
    }

    @Override
    public String getProperty(String propertyName) {
        return delegate.getProperty(propertyName);
    }

    @Override
    public Object getReference(String key) {
        return delegate.getReference(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getReferences() {
        return delegate.getReferences();
    }

    @Override
    public Resource getResource(String name) {
        return delegate.getResource(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getTargets() {
        return delegate.getTargets();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getTaskDefinitions() {
        return delegate.getTaskDefinitions();
    }

    @Override
    public Task getThreadTask(Thread thread) {
        return delegate.getThreadTask(thread);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getUserProperties() {
        return delegate.getUserProperties();
    }

    @Override
    public String getUserProperty(String propertyName) {
        return delegate.getUserProperty(propertyName);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public void inheritIDReferences(Project parent) {
        delegate.inheritIDReferences(parent);
    }

    @Override
    public void init() throws BuildException {
        delegate.init();
    }

    @Override
    public void initProperties() throws BuildException {
        delegate.initProperties();
    }

    @Override
    public void initSubProject(Project subProject) {
        delegate.initSubProject(subProject);
    }

    @Override
    public boolean isKeepGoingMode() {
        return delegate.isKeepGoingMode();
    }

    @Override
    public void log(String message, int msgLevel) {
        delegate.log(message, msgLevel);
    }

    @Override
    public void log(String message, Throwable throwable, int msgLevel) {
        delegate.log(message, throwable, msgLevel);
    }

    @Override
    public void log(String message) {
        delegate.log(message);
    }

    @Override
    public void log(Target target, String message, int msgLevel) {
        delegate.log(target, message, msgLevel);
    }

    @Override
    public void log(Target target, String message, Throwable throwable, int msgLevel) {
        delegate.log(target, message, throwable, msgLevel);
    }

    @Override
    public void log(Task task, String message, int msgLevel) {
        delegate.log(task, message, msgLevel);
    }

    @Override
    public void log(Task task, String message, Throwable throwable, int msgLevel) {
        delegate.log(task, message, throwable, msgLevel);
    }

    @Override
    public void registerThreadTask(Thread thread, Task task) {
        delegate.registerThreadTask(thread, task);
    }

    @Override
    public void removeBuildListener(BuildListener listener) {
        delegate.removeBuildListener(listener);
    }

    @Override
    public String replaceProperties(String value) throws BuildException {
        return delegate.replaceProperties(value);
    }

    @SuppressWarnings("deprecation")
    @Override
    public File resolveFile(String fileName, File rootDir) {
        return delegate.resolveFile(fileName, rootDir);
    }

    @Override
    public File resolveFile(String fileName) {
        return delegate.resolveFile(fileName);
    }

    @Override
    public void setCoreLoader(ClassLoader coreLoader) {
        delegate.setCoreLoader(coreLoader);
    }

    @Override
    public void setDefaultInputStream(InputStream defaultInputStream) {
        delegate.setDefaultInputStream(defaultInputStream);
    }

    @Override
    public void setExecutor(Executor e) {
        delegate.setExecutor(e);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setFileLastModified(File file, long time) throws BuildException {
        delegate.setFileLastModified(file, time);
    }

    @Override
    public void setInheritedProperty(String name, String value) {
        delegate.setInheritedProperty(name, value);
    }

    @Override
    public void setInputHandler(InputHandler handler) {
        delegate.setInputHandler(handler);
    }

    @Override
    public void setJavaVersionProperty() throws BuildException {
        delegate.setJavaVersionProperty();
    }

    @Override
    public void setKeepGoingMode(boolean keepGoingMode) {
        delegate.setKeepGoingMode(keepGoingMode);
    }

    @Override
    public void setNewProperty(String name, String value) {
        delegate.setNewProperty(name, value);
    }

    @Override
    public void setProperty(String name, String value) {
        delegate.setProperty(name, value);
    }

    @Override
    public void setSystemProperties() {
        delegate.setSystemProperties();
    }

    @Override
    public void setUserProperty(String name, String value) {
        delegate.setUserProperty(name, value);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hashtable getInheritedProperties() {
        return delegate.getInheritedProperties();
    }

    @Override
    public boolean hasReference(String key) {
        return delegate.hasReference(key);
    }

}
