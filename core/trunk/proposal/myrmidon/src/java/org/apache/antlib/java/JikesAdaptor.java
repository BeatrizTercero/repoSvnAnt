/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.antlib.java;

import java.io.File;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.framework.Execute;
import org.apache.myrmidon.framework.file.Path;
import org.apache.tools.todo.types.Commandline;
import org.apache.tools.todo.types.PathUtil;

/**
 * An adaptor for the jikes compiler.
 *
 * @author James Davidson <a href="mailto:duncan@x180.com">duncan@x180.com</a>
 * @author Robin Green <a href="mailto:greenrd@hotmail.com">greenrd@hotmail.com
 *      </a>
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @author <a href="mailto:jayglanville@home.com">J D Glanville</a>
 * @author skanthak@muehlheim.de
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision$ $Date$
 *
 * @ant.type type="java-compiler" name="jikes"
 */
public class JikesAdaptor
    extends ExternalCompilerAdaptor
{
    /**
     * Builds the command-line to execute the compiler.
     */
    protected void buildCommandLine( final Execute exe, final File tempFile )
        throws TaskException
    {
        Path classpath = new Path();

        // Add the destination directory
        classpath.addLocation( getDestDir() );

        // Add the compile classpath
        classpath.add( getClassPath() );

        // If the user has set JIKESPATH we should add the contents as well
        String jikesPath = System.getProperty( "jikes.class.path" );
        if( jikesPath != null )
        {
            classpath.add( jikesPath );
        }

        // Add the runtime
        PathUtil.addJavaRuntime( classpath );

        // Build the command line
        final Commandline cmd = exe.getCommandline();
        cmd.setExecutable( "jikes" );

        if( isDeprecation() )
        {
            cmd.addArgument( "-deprecation" );
        }

        if( isDebug() )
        {
            cmd.addArgument( "-g" );
        }

        cmd.addArgument( "-d" );
        cmd.addArgument( getDestDir() );

        cmd.addArgument( "-classpath" );
        cmd.addArgument( PathUtil.formatPath( classpath, getContext() ) );

        // TODO - make this configurable
        cmd.addArgument( "+E" );

        cmd.addArgument( "@" + tempFile.getAbsolutePath() );
    }
}
