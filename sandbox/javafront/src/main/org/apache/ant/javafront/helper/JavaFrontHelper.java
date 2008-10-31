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
package org.apache.ant.javafront.helper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ant.javafront.annotations.AntProject;
import org.apache.ant.javafront.annotations.AntTarget;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/**
 * Projecthelper that compiles the "build file", loads it,
 * instantiates it and creates targets from the annotated methods.
 */
public class JavaFrontHelper extends ProjectHelper {

    private static final File BUILD_FILE_DIR =
        new File(new File(System.getProperty("java.io.tmpdir")), "javafront");

    private static final Pattern PACKAGE_NAME =
        Pattern.compile(".*?package\\s+(\\S+)\\s*;.*", Pattern.DOTALL);
    private static final Pattern CLASS_NAME =
        Pattern.compile(".*?class\\s+(\\S+)\\s.*", Pattern.DOTALL);

    @Override
    public void parse(Project project, Object source) throws BuildException {
        if (source instanceof File) {
            File buildFile = (File) source;
            try {
                String className = preParse(buildFile);
                compile(project, buildFile, className);
                Object o = createInstance(project, className);
                populateProject(project, o, buildFile);
                createTargets(project, o);
            } catch (IOException e) {
                throw new BuildException("failed to parse " + source, e);
            }
        } else {
            throw new BuildException("Don't know how to parse "
                                     + source.getClass());
        }
    }

    /**
     * Tries to determine the class's fully qualified name
     */
    private String preParse(File buildFile) throws IOException {
        FileReader r = null;
        String sourceText = "";
        try {
            r = new FileReader(buildFile);
            sourceText = FileUtils.readFully(r);
        } finally {
            FileUtils.close(r);
        }
        String packageName = "";
        Matcher m = PACKAGE_NAME.matcher(sourceText);
        if (m.matches()) {
            // not the anonymous package
            packageName = m.group(1);
        }
        m = CLASS_NAME.matcher(sourceText);
        if (!m.matches()) {
            throw new BuildException("Can't find class name in " + buildFile
                                     + " is it a Java source file?");
        }
        String className = m.group(1);
        return packageName + (packageName.length() > 0 ? "." : "") + className;
    }

    /**
     * Copies build file to temporary directory and compiles it.
     */
    private void compile(Project p, File buildFile, String className)
        throws IOException {
        File targetFile = new File(BUILD_FILE_DIR,
                                   className.replace('.', File.separatorChar)
                                   + ".java");
        File parentDir = targetFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new BuildException("failed to create " + parentDir);
        }
        FileUtils.getFileUtils().copyFile(buildFile, targetFile);
        Javac javac = new Javac();
        javac.setProject(p);
        javac.setSrcdir(new Path(p, BUILD_FILE_DIR.getAbsolutePath()));
        javac.setDestdir(BUILD_FILE_DIR);
        javac.setTaskName("buildfile");
        javac.execute();
    }

    /**
     * Loads the given class and tries to instantiate it, setting the
     * project as constructor argument or via a setProject method if
     * present.
     */
    private Object createInstance(Project p, String className) {
        AntClassLoader loader =
            new AntClassLoader(p,
                               new Path(p, BUILD_FILE_DIR.getAbsolutePath()));
        Class clazz = null;
        try {
            clazz = loader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new BuildException("failed to load " + className, e);
        }

        // try a public constructor with a Project arg first
        try {
            Constructor con = clazz.getConstructor(Project.class);
            return con.newInstance(p);
        } catch (SecurityException e) {
            throw new BuildException("failed to reflect on " + className, e);
        } catch (InstantiationException e) {
            throw new BuildException(className + " seems to be abstract", e);
        } catch (IllegalAccessException e) {
            throw new BuildException("failed to invoke Project-constructor in "
                                     + className, e);
        } catch (IllegalArgumentException e) {
            throw new BuildException("failed to invoke Project-constructor in "
                                     + className, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof BuildException) {
                throw (BuildException) e.getCause();
            }
            throw new BuildException("failed to invoke Project-constructor in "
                                     + className, e);
        } catch (NoSuchMethodException e) {
            // No problem, fall back to no-arg constructor
        }

        Object o = null;
        // next is a the no-arg constructor
        try {
            o = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BuildException(className + " seems to be abstract", e);
        } catch (SecurityException e) {
            throw new BuildException("failed to reflect on " + className, e);
        } catch (IllegalAccessException e) {
            throw new BuildException("failed to invoke no-arg constructor in "
                                     + className, e);
        } catch (IllegalArgumentException e) {
            throw new BuildException("failed to invoke no-arg constructor in "
                                     + className, e);
        }

        // look for a public * setProject(Project) method
        try {
            Method setProject = clazz.getMethod("setProject", Project.class);
            setProject.invoke(o, p);
        } catch (SecurityException e) {
            throw new BuildException("failed to reflect on " + className, e);
        } catch (IllegalAccessException e) {
            throw new BuildException("failed to invoke setProject in "
                                     + className, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof BuildException) {
                throw (BuildException) e.getCause();
            }
            throw new BuildException("failed to invoke setProject in "
                                     + className, e);
        } catch (NoSuchMethodException e) {
            // No problem, we just asked
        }
        return o;
    }

    /**
     * sets project attributes.
     */
    private void populateProject(Project targetProject, Object buildFile,
                                 File buildFileLocation) {
        AntProject projectAttributes = buildFile.getClass()
            .getAnnotation(AntProject.class);
        boolean baseDirSet = false;
        if (projectAttributes != null) {
            if (projectAttributes.Name().length() > 0) {
                targetProject.setName(projectAttributes.Name());
            }
            if (projectAttributes.DefaultTarget().length() > 0) {
                targetProject.setDefault(projectAttributes.DefaultTarget());
            }
            if (projectAttributes.BaseDir().length() > 0) {
                File bd = FileUtils.getFileUtils()
                    .resolveFile(buildFileLocation.getParentFile(),
                                 projectAttributes.BaseDir());
                targetProject.setBaseDir(bd);
                baseDirSet = true;
            }
        }
        if (!baseDirSet) {
            targetProject.setBaseDir(buildFileLocation.getParentFile());
        }
    }

    /**
     * creates targets for methods based on annotations.
     */
    private void createTargets(Project targetProject, Object buildFile) {
        try {
            for (Method m : buildFile.getClass().getMethods()) {
                AntTarget t = m.getAnnotation(AntTarget.class);
                if (t != null) {
                    Class[] params = m.getParameterTypes();
                    if (params.length == 0) {
                        Target target = new Target(m, buildFile);
                        if (t.Name().length() > 0) {
                            target.setName(t.Name());
                        } else {
                            target.setName(m.getName());
                        }
                        if (t.Depends().length() > 0) {
                            target.setDepends(t.Depends());
                        }
                        if (t.If().length() > 0) {
                            target.setIf(t.If());
                        }
                        if (t.Unless().length() > 0) {
                            target.setUnless(t.Unless());
                        }
                        if (t.Description().length() > 0) {
                            target.setDescription(t.Description());
                        }
                        targetProject.addTarget(target);
                    } else {
                        throw new BuildException("Only no-arg methods are"
                                                 + " supported as targets");
                    }
                }
            }
        } catch (SecurityException e) {
            throw new BuildException("failed to reflect on "
                                     + buildFile.getClass(), e);
        }
    }
}