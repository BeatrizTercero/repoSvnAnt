/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.tools.ant.taskdefs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;
import org.apache.myrmidon.api.TaskException;

/**
 * Call Ant in a sub-project <pre>
 *  &lt;target name=&quot;foo&quot; depends=&quot;init&quot;&gt;
 *    &lt;ant antfile=&quot;build.xml&quot; target=&quot;bar&quot; &gt;
 *      &lt;property name=&quot;property1&quot; value=&quot;aaaaa&quot; /&gt;
 *      &lt;property name=&quot;foo&quot; value=&quot;baz&quot; /&gt;
 *    &lt;/ant&gt;</SPAN> &lt;/target&gt;</SPAN> &lt;target name=&quot;bar&quot;
 * depends=&quot;init&quot;&gt; &lt;echo message=&quot;prop is ${property1}
 * ${foo}&quot; /&gt; &lt;/target&gt; </pre>
 *
 * @author costin@dnt.ro
 */
public class Ant extends Task
{

    /**
     * the basedir where is executed the build file
     */
    private File dir = null;

    /**
     * the build.xml file (can be absolute) in this case dir will be ignored
     */
    private String antFile = null;

    /**
     * the target to call if any
     */
    private String target = null;

    /**
     * the output
     */
    private String output = null;

    /**
     * should we inherit properties from the parent ?
     */
    private boolean inheritAll = true;

    /**
     * should we inherit references from the parent ?
     */
    private boolean inheritRefs = false;

    /**
     * the properties to pass to the new project
     */
    private Vector properties = new Vector();

    /**
     * the references to pass to the new project
     */
    private Vector references = new Vector();

    /**
     * the temporary project created to run the build file
     */
    private Project newProject;

    /**
     * set the build file, it can be either absolute or relative. If it is
     * absolute, <tt>dir</tt> will be ignored, if it is relative it will be
     * resolved relative to <tt>dir</tt> .
     *
     * @param s The new Antfile value
     */
    public void setAntfile( String s )
    {
        // @note: it is a string and not a file to handle relative/absolute
        // otherwise a relative file will be resolved based on the current
        // basedir.
        this.antFile = s;
    }

    /**
     * ...
     *
     * @param d The new Dir value
     */
    public void setDir( File d )
    {
        this.dir = d;
    }

    /**
     * If true, inherit all properties from parent Project If false, inherit
     * only userProperties and those defined inside the ant call itself
     *
     * @param value The new InheritAll value
     */
    public void setInheritAll( boolean value )
    {
        inheritAll = value;
    }

    /**
     * If true, inherit all references from parent Project If false, inherit
     * only those defined inside the ant call itself
     *
     * @param value The new InheritRefs value
     */
    public void setInheritRefs( boolean value )
    {
        inheritRefs = value;
    }

    public void setOutput( String s )
    {
        this.output = s;
    }

    /**
     * set the target to execute. If none is defined it will execute the default
     * target of the build file
     *
     * @param s The new Target value
     */
    public void setTarget( String s )
    {
        this.target = s;
    }

    /**
     * create a reference element that identifies a data type that should be
     * carried over to the new project.
     *
     * @param r The feature to be added to the Reference attribute
     */
    public void addReference( Reference r )
    {
        references.addElement( r );
    }

    /**
     * create a property to pass to the new project as a 'user property'
     *
     * @return Description of the Returned Value
     */
    public Property createProperty()
    {
        if( newProject == null )
        {
            reinit();
        }
        Property p = new Property( true );
        p.setProject( newProject );
        p.setTaskName( "property" );
        properties.addElement( p );
        return p;
    }

    /**
     * Do the execution.
     *
     * @exception TaskException Description of Exception
     */
    public void execute()
        throws TaskException
    {
        try
        {
            if( newProject == null )
            {
                reinit();
            }

            if( ( dir == null ) && ( inheritAll == true ) )
            {
                dir = project.getBaseDir();
            }

            initializeProject();

            if( dir != null )
            {
                newProject.setBaseDir( dir );
                newProject.setUserProperty( "basedir", dir.getAbsolutePath() );
            }
            else
            {
                dir = project.getBaseDir();
            }

            overrideProperties();

            if( antFile == null )
            {
                antFile = "build.xml";
            }

            File file = FileUtils.newFileUtils().resolveFile( dir, antFile );
            antFile = file.getAbsolutePath();

            newProject.setUserProperty( "ant.file", antFile );
            ProjectHelper.configureProject( newProject, new File( antFile ) );

            if( target == null )
            {
                target = newProject.getDefaultTarget();
            }

            addReferences();

            // Are we trying to call the target in which we are defined?
            if( newProject.getBaseDir().equals( project.getBaseDir() ) &&
                newProject.getProperty( "ant.file" ).equals( project.getProperty( "ant.file" ) ) &&
                getOwningTarget() != null &&
                target.equals( this.getOwningTarget().getName() ) )
            {

                throw new TaskException( "ant task calling its own parent target" );
            }

            newProject.executeTarget( target );
        }
        finally
        {
            // help the gc
            newProject = null;
        }
    }

    public void init()
    {
        newProject = new Project();
        newProject.setJavaVersionProperty();
        newProject.addTaskDefinition( "property",
            ( Class )project.getTaskDefinitions().get( "property" ) );
    }

    protected void handleErrorOutput( String line )
    {
        if( newProject != null )
        {
            newProject.demuxOutput( line, true );
        }
        else
        {
            super.handleErrorOutput( line );
        }
    }

    protected void handleOutput( String line )
    {
        if( newProject != null )
        {
            newProject.demuxOutput( line, false );
        }
        else
        {
            super.handleOutput( line );
        }
    }

    /**
     * Add the references explicitly defined as nested elements to the new
     * project. Also copy over all references that don't override existing
     * references in the new project if inheritall has been requested.
     *
     * @exception TaskException Description of Exception
     */
    private void addReferences()
        throws TaskException
    {
        Hashtable thisReferences = ( Hashtable )project.getReferences().clone();
        Hashtable newReferences = newProject.getReferences();
        Enumeration e;
        if( references.size() > 0 )
        {
            for( e = references.elements(); e.hasMoreElements();  )
            {
                Reference ref = ( Reference )e.nextElement();
                String refid = ref.getRefId();
                if( refid == null )
                {
                    throw new TaskException( "the refid attribute is required for reference elements" );
                }
                if( !thisReferences.containsKey( refid ) )
                {
                    log( "Parent project doesn't contain any reference '"
                         + refid + "'",
                        Project.MSG_WARN );
                    continue;
                }

                Object o = thisReferences.remove( refid );
                String toRefid = ref.getToRefid();
                if( toRefid == null )
                {
                    toRefid = refid;
                }
                copyReference( refid, toRefid );
            }
        }

        // Now add all references that are not defined in the
        // subproject, if inheritRefs is true
        if( inheritRefs )
        {
            for( e = thisReferences.keys(); e.hasMoreElements();  )
            {
                String key = ( String )e.nextElement();
                if( newReferences.containsKey( key ) )
                {
                    continue;
                }
                copyReference( key, key );
            }
        }
    }

    /**
     * Try to clone and reconfigure the object referenced by oldkey in the
     * parent project and add it to the new project with the key newkey. <p>
     *
     * If we cannot clone it, copy the referenced object itself and keep our
     * fingers crossed.</p>
     *
     * @param oldKey Description of Parameter
     * @param newKey Description of Parameter
     */
    private void copyReference( String oldKey, String newKey )
    {
        Object orig = project.getReference( oldKey );
        Class c = orig.getClass();
        Object copy = orig;
        try
        {
            Method cloneM = c.getMethod( "clone", new Class[0] );
            if( cloneM != null )
            {
                copy = cloneM.invoke( orig, new Object[0] );
            }
        }
        catch( Exception e )
        {
            // not Clonable
        }

        if( copy instanceof ProjectComponent )
        {
            ( ( ProjectComponent )copy ).setProject( newProject );
        }
        else
        {
            try
            {
                Method setProjectM =
                    c.getMethod( "setProject", new Class[]{Project.class} );
                if( setProjectM != null )
                {
                    setProjectM.invoke( copy, new Object[]{newProject} );
                }
            }
            catch( NoSuchMethodException e )
            {
                // ignore this if the class being referenced does not have
                // a set project method.
            }
            catch( Exception e2 )
            {
                String msg = "Error setting new project instance for reference with id "
                     + oldKey;
                throw new TaskException( msg, e2, location );
            }
        }
        newProject.addReference( newKey, copy );
    }

    private void initializeProject()
    {
        Vector listeners = project.getBuildListeners();
        for( int i = 0; i < listeners.size(); i++ )
        {
            newProject.addBuildListener( ( BuildListener )listeners.elementAt( i ) );
        }

        if( output != null )
        {
            try
            {
                PrintStream out = new PrintStream( new FileOutputStream( output ) );
                DefaultLogger logger = new DefaultLogger();
                logger.setMessageOutputLevel( Project.MSG_INFO );
                logger.setOutputPrintStream( out );
                logger.setErrorPrintStream( out );
                newProject.addBuildListener( logger );
            }
            catch( IOException ex )
            {
                log( "Ant: Can't set output to " + output );
            }
        }

        Hashtable taskdefs = project.getTaskDefinitions();
        Enumeration et = taskdefs.keys();
        while( et.hasMoreElements() )
        {
            String taskName = ( String )et.nextElement();
            if( taskName.equals( "property" ) )
            {
                // we have already added this taskdef in #init
                continue;
            }
            Class taskClass = ( Class )taskdefs.get( taskName );
            newProject.addTaskDefinition( taskName, taskClass );
        }

        Hashtable typedefs = project.getDataTypeDefinitions();
        Enumeration e = typedefs.keys();
        while( e.hasMoreElements() )
        {
            String typeName = ( String )e.nextElement();
            Class typeClass = ( Class )typedefs.get( typeName );
            newProject.addDataTypeDefinition( typeName, typeClass );
        }

        // set user-defined or all properties from calling project
        Hashtable prop1;
        if( inheritAll == true )
        {
            prop1 = project.getProperties();
        }
        else
        {
            prop1 = project.getUserProperties();

            // set Java built-in properties separately,
            // b/c we won't inherit them.
            newProject.setSystemProperties();
        }

        e = prop1.keys();
        while( e.hasMoreElements() )
        {
            String arg = ( String )e.nextElement();
            if( "basedir".equals( arg ) || "ant.file".equals( arg ) )
            {
                // basedir and ant.file get special treatment in execute()
                continue;
            }

            String value = ( String )prop1.get( arg );
            if( inheritAll == true )
            {
                newProject.setProperty( arg, value );
            }
            else
            {
                newProject.setUserProperty( arg, value );
            }
        }
    }

    /**
     * Override the properties in the new project with the one explicitly
     * defined as nested elements here.
     *
     * @exception TaskException Description of Exception
     */
    private void overrideProperties()
        throws TaskException
    {
        Enumeration e = properties.elements();
        while( e.hasMoreElements() )
        {
            Property p = ( Property )e.nextElement();
            p.setProject( newProject );
            p.execute();
        }
    }

    private void reinit()
    {
        init();
        for( int i = 0; i < properties.size(); i++ )
        {
            Property p = ( Property )properties.elementAt( i );
            Property newP = ( Property )newProject.createTask( "property" );
            newP.setName( p.getName() );
            if( p.getValue() != null )
            {
                newP.setValue( p.getValue() );
            }
            if( p.getFile() != null )
            {
                newP.setFile( p.getFile() );
            }
            if( p.getResource() != null )
            {
                newP.setResource( p.getResource() );
            }
            properties.setElementAt( newP, i );
        }
    }

    /**
     * Helper class that implements the nested &lt;reference&gt; element of
     * &lt;ant&gt; and &lt;antcall&gt;.
     *
     * @author RT
     */
    public static class Reference
         extends org.apache.tools.ant.types.Reference
    {

        private String targetid = null;

        public Reference()
        {
            super();
        }

        public void setToRefid( String targetid )
        {
            this.targetid = targetid;
        }

        public String getToRefid()
        {
            return targetid;
        }
    }
}
