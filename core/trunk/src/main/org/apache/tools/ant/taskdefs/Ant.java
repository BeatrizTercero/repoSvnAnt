/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.util.FileUtils;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Call Ant in a sub-project
 *
 *  <pre>
 *  &lt;target name=&quot;foo&quot; depends=&quot;init&quot;&gt;
 *    &lt;ant antfile=&quot;build.xml&quot; target=&quot;bar&quot; &gt;
 *      &lt;property name=&quot;property1&quot; value=&quot;aaaaa&quot; /&gt;
 *      &lt;property name=&quot;foo&quot; value=&quot;baz&quot; /&gt;
 *    &lt;/ant&gt;</SPAN>
 *  &lt;/target&gt;</SPAN>
 *
 *  &lt;target name=&quot;bar&quot; depends=&quot;init&quot;&gt;
 *    &lt;echo message=&quot;prop is ${property1} ${foo}&quot; /&gt;
 *  &lt;/target&gt;
 * </pre>
 *
 *
 * @author costin@dnt.ro
 */
public class Ant extends Task {

    /** the basedir where is executed the build file */
    private File dir = null;
    
    /** the build.xml file (can be absolute) in this case dir will be ignored */
    private String antFile = null;
    
    /** the target to call if any */
    private String target = null;
    
    /** the output */
    private String output = null;
    
    /** should we inherit properties from the parent ? */
    private boolean inheritAll = true;
    
    /** the properties to pass to the new project */
    private Vector properties = new Vector();
    
    /** the temporary project created to run the build file */
    private Project newProject;

    /**
     * If true, inherit all properties from parent Project
     * If false, inherit only userProperties and those defined
     * inside the ant call itself
     */
    public void setInheritAll(boolean value) {
       inheritAll = value;
    }

    public void init() {
        newProject = new Project();
        newProject.setJavaVersionProperty();
        newProject.addTaskDefinition("property", 
                             (Class)project.getTaskDefinitions().get("property"));
    }

    private void reinit() {
        init();
        for (int i=0; i<properties.size(); i++) {
            Property p = (Property) properties.elementAt(i);
            Property newP = (Property) newProject.createTask("property");
            newP.setName(p.getName());
            if (p.getValue() != null) {
                newP.setValue(p.getValue());
            }
            if (p.getFile() != null) {
                newP.setFile(p.getFile());
            } 
            if (p.getResource() != null) {
                newP.setResource(p.getResource());
            }
            properties.setElementAt(newP, i);
        }
    }

    private void initializeProject() {
        Vector listeners = project.getBuildListeners();
        for (int i = 0; i < listeners.size(); i++) {
            newProject.addBuildListener((BuildListener)listeners.elementAt(i));
        }

        if (output != null) {
            try {
                PrintStream out = new PrintStream(new FileOutputStream(output));
                DefaultLogger logger = new DefaultLogger();
                logger.setMessageOutputLevel(Project.MSG_INFO);
                logger.setOutputPrintStream(out);
                logger.setErrorPrintStream(out);
                newProject.addBuildListener(logger);
            }
            catch( IOException ex ) {
                log( "Ant: Can't set output to " + output );
            }
        }

        Hashtable taskdefs = project.getTaskDefinitions();
        Enumeration et = taskdefs.keys();
        while (et.hasMoreElements()) {
            String taskName = (String) et.nextElement();
            if (taskName.equals("property")) {
                // we have already added this taskdef in #init
                continue;
            }
            Class taskClass = (Class) taskdefs.get(taskName);
            newProject.addTaskDefinition(taskName, taskClass);
        }

        Hashtable typedefs = project.getDataTypeDefinitions();
        Enumeration e = typedefs.keys();
        while (e.hasMoreElements()) {
            String typeName = (String) e.nextElement();
            Class typeClass = (Class) typedefs.get(typeName);
            newProject.addDataTypeDefinition(typeName, typeClass);
        }

        // set user-defined or all properties from calling project
        Hashtable prop1;
        if (inheritAll == true) {
           prop1 = project.getProperties();
        }
        else {
           prop1 = project.getUserProperties();

           // set Java built-in properties separately,
           // b/c we won't inherit them.
           newProject.setSystemProperties();
        }
        
        e = prop1.keys();
        while (e.hasMoreElements()) {
            String arg = (String) e.nextElement();
            if ("basedir".equals(arg) || "ant.file".equals(arg)) {
                // basedir and ant.file get special treatment in execute()
                continue;
            }
            
            String value = (String) prop1.get(arg);
            if (inheritAll == true){
               newProject.setProperty(arg, value);
            } else {
               newProject.setUserProperty(arg, value);
            }
        }
    }

    protected void handleOutput(String line) {
        if (newProject != null) {
            newProject.demuxOutput(line, false);
        }
        else {
            super.handleOutput(line);
        }
    }
    
    protected void handleErrorOutput(String line) {
        if (newProject != null) {
            newProject.demuxOutput(line, true);
        }
        else {
            super.handleErrorOutput(line);
        }
    }
    
    /**
     * Do the execution.
     */
    public void execute() throws BuildException {
        try {
            if (newProject == null) {
                reinit();
            }
        
            if ( (dir == null) && (inheritAll == true) ) {
                dir = project.getBaseDir();
            }

            initializeProject();

            if (dir != null) {
                newProject.setBaseDir(dir);
                newProject.setUserProperty("basedir" , dir.getAbsolutePath());
            } else {
                dir = project.getBaseDir();
            }

            // Override with local-defined properties
            Enumeration e = properties.elements();
            while (e.hasMoreElements()) {
                Property p=(Property) e.nextElement();
                p.setProject(newProject);
                p.execute();
            }
            
            if (antFile == null) {
                antFile = "build.xml";
            }

            File file = FileUtils.newFileUtils().resolveFile(dir, antFile);
            antFile = file.getAbsolutePath();

            newProject.setUserProperty( "ant.file" , antFile );
            ProjectHelper.configureProject(newProject, new File(antFile));
            
            if (target == null) {
                target = newProject.getDefaultTarget();
            }

            // Are we trying to call the target in which we are defined?
            if (newProject.getBaseDir().equals(project.getBaseDir()) &&
                newProject.getProperty("ant.file").equals(project.getProperty("ant.file")) &&
                getOwningTarget() != null &&
                target.equals(this.getOwningTarget().getName())) { 

                throw new BuildException("ant task calling its own parent target");
            }

            newProject.executeTarget(target);
        } finally {
            // help the gc
            newProject = null;
        }
    }

    /**
     * ...
     */
    public void setDir(File d) {
        this.dir = d;
    }

    /**
     * set the build file, it can be either absolute or relative.
     * If it is absolute, <tt>dir</tt> will be ignored, if it is
     * relative it will be resolved relative to <tt>dir</tt>.
     */
    public void setAntfile(String s) {
        // @note: it is a string and not a file to handle relative/absolute
        // otherwise a relative file will be resolved based on the current
        // basedir.
        this.antFile = s;
    }

    /**
     * set the target to execute. If none is defined it will
     * execute the default target of the build file
     */
    public void setTarget(String s) {
        this.target = s;
    }

    public void setOutput(String s) {
        this.output = s;
    }

    /** create a property to pass to the new project as a 'user property' */
    public Property createProperty() {
        if (newProject == null) {
            reinit();
        }
        Property p = new Property(true);
        p.setProject(newProject);
        p.setTaskName("property");
        properties.addElement( p );
        return p;
    }
}
