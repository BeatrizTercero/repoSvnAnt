/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.tools.ant.taskdefs.rmic;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Commandline;

/**
 * The implementation of the rmic for SUN's JDK.
 *
 * @author <a href="mailto:tokamoto@rd.nttdata.co.jp">Takashi Okamoto</a>
 */
public class SunRmic extends DefaultRmicAdapter
{

    public boolean execute()
        throws BuildException
    {
        getRmic().log( "Using SUN rmic compiler", Project.MSG_VERBOSE );
        Commandline cmd = setupRmicCommand();

        // Create an instance of the rmic, redirecting output to
        // the project log
        LogOutputStream logstr = new LogOutputStream( getRmic(), Project.MSG_WARN );

        try
        {
            Class c = Class.forName( "sun.rmi.rmic.Main" );
            Constructor cons = c.getConstructor( new Class[]
                {OutputStream.class, String.class} );
            Object rmic = cons.newInstance( new Object[]{logstr, "rmic"} );

            Method doRmic = c.getMethod( "compile",
                new Class[]{String[].class} );
            Boolean ok = ( Boolean )doRmic.invoke( rmic,
                ( new Object[]{cmd.getArguments()} ) );
            return ok.booleanValue();
        }
        catch( ClassNotFoundException ex )
        {
            throw new BuildException( "Cannot use SUN rmic, as it is not available" +
                " A common solution is to set the environment variable" +
                " JAVA_HOME or CLASSPATH.", getRmic().getLocation() );
        }
        catch( Exception ex )
        {
            if( ex instanceof BuildException )
            {
                throw ( BuildException )ex;
            }
            else
            {
                throw new BuildException( "Error starting SUN rmic: ", ex, getRmic().getLocation() );
            }
        }
        finally
        {
            try
            {
                logstr.close();
            }
            catch( IOException e )
            {
                throw new BuildException( e );
            }
        }
    }
}
