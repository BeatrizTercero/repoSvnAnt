/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.framework.Os;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;

/**
 * A Task to record explicit dependencies. If any of the target files are out of
 * date with respect to any of the source files, all target files are removed.
 * This is useful where dependencies cannot be computed (for example,
 * dynamically interpreted parameters or files that need to stay in synch but
 * are not directly linked) or where the ant task in question could compute them
 * but does not (for example, the linked DTD for an XML file using the style
 * task). nested arguments:
 * <ul>
 *   <li> srcfileset (fileset describing the source files to examine)
 *   <li> srcfilelist (filelist describing the source files to examine)
 *   <li> targetfileset (fileset describing the target files to examine)
 *   <li> targetfilelist (filelist describing the target files to examine)
 * </ul>
 * At least one instance of either a fileset or filelist for both source and
 * target are required. <p>
 *
 * This task will examine each of the source files against each of the target
 * files. If any target files are out of date with respect to any of the source
 * files, all targets are removed. If any files named in a (src or target)
 * filelist do not exist, all targets are removed. Hint: If missing files should
 * be ignored, specify them as include patterns in filesets, rather than using
 * filelists. </p> <p>
 *
 * This task attempts to optimize speed of dependency checking. It will stop
 * after the first out of date file is found and remove all targets, rather than
 * exhaustively checking every source vs target combination unnecessarily. </p>
 * <p>
 *
 * Example uses:
 * <ul>
 *   <li> Record the fact that an XML file must be up to date with respect to
 *   its XSD (Schema file), even though the XML file itself includes no
 *   reference to its XSD. </li>
 *   <li> Record the fact that an XSL stylesheet includes other sub-stylesheets
 *   </li>
 *   <li> Record the fact that java files must be recompiled if the ant build
 *   file changes </li>
 * </ul>
 *
 *
 * @author <a href="mailto:cstrong@arielpartners.com">Craeg Strong</a>
 * @version $Revision$ $Date$
 */
public class DependSet extends MatchingTask
{

    private Vector sourceFileSets = new Vector();
    private Vector sourceFileLists = new Vector();
    private Vector targetFileSets = new Vector();
    private Vector targetFileLists = new Vector();

    /**
     * Creates a new DependSet Task.
     */
    public DependSet()
    {
    }

    /**
     * Nested &lt;srcfilelist&gt; element.
     *
     * @param fl The feature to be added to the Srcfilelist attribute
     */
    public void addSrcfilelist( FileList fl )
    {
        sourceFileLists.addElement( fl );
    }//-- DependSet

    /**
     * Nested &lt;srcfileset&gt; element.
     *
     * @param fs The feature to be added to the Srcfileset attribute
     */
    public void addSrcfileset( FileSet fs )
    {
        sourceFileSets.addElement( fs );
    }

    /**
     * Nested &lt;targetfilelist&gt; element.
     *
     * @param fl The feature to be added to the Targetfilelist attribute
     */
    public void addTargetfilelist( FileList fl )
    {
        targetFileLists.addElement( fl );
    }

    /**
     * Nested &lt;targetfileset&gt; element.
     *
     * @param fs The feature to be added to the Targetfileset attribute
     */
    public void addTargetfileset( FileSet fs )
    {
        targetFileSets.addElement( fs );
    }

    /**
     * Executes the task.
     *
     * @exception TaskException Description of Exception
     */
    public void execute()
        throws TaskException
    {
        if( ( sourceFileSets.size() == 0 ) && ( sourceFileLists.size() == 0 ) )
        {
            throw new TaskException( "At least one <srcfileset> or <srcfilelist> element must be set" );
        }

        if( ( targetFileSets.size() == 0 ) && ( targetFileLists.size() == 0 ) )
        {
            throw new TaskException( "At least one <targetfileset> or <targetfilelist> element must be set" );
        }

        long now = ( new Date() ).getTime();
        /*
         * If we're on Windows, we have to munge the time up to 2 secs to
         * be able to check file modification times.
         * (Windows has a max resolution of two secs for modification times)
         */
        if( Os.isFamily( "windows" ) )
        {
            now += 2000;
        }

        //
        // Grab all the target files specified via filesets
        //
        Vector allTargets = new Vector();
        Enumeration enumTargetSets = targetFileSets.elements();
        while( enumTargetSets.hasMoreElements() )
        {

            FileSet targetFS = (FileSet)enumTargetSets.nextElement();
            DirectoryScanner targetDS = targetFS.getDirectoryScanner( project );
            String[] targetFiles = targetDS.getIncludedFiles();

            for( int i = 0; i < targetFiles.length; i++ )
            {

                File dest = new File( targetFS.getDir( project ), targetFiles[ i ] );
                allTargets.addElement( dest );

                if( dest.lastModified() > now )
                {
                    log( "Warning: " + targetFiles[ i ] + " modified in the future.",
                         Project.MSG_WARN );
                }
            }
        }

        //
        // Grab all the target files specified via filelists
        //
        boolean upToDate = true;
        Enumeration enumTargetLists = targetFileLists.elements();
        while( enumTargetLists.hasMoreElements() )
        {

            FileList targetFL = (FileList)enumTargetLists.nextElement();
            String[] targetFiles = targetFL.getFiles( project );

            for( int i = 0; i < targetFiles.length; i++ )
            {

                File dest = new File( targetFL.getDir( project ), targetFiles[ i ] );
                if( !dest.exists() )
                {
                    log( targetFiles[ i ] + " does not exist.", Project.MSG_VERBOSE );
                    upToDate = false;
                    continue;
                }
                else
                {
                    allTargets.addElement( dest );
                }
                if( dest.lastModified() > now )
                {
                    log( "Warning: " + targetFiles[ i ] + " modified in the future.",
                         Project.MSG_WARN );
                }
            }
        }

        //
        // Check targets vs source files specified via filesets
        //
        if( upToDate )
        {
            Enumeration enumSourceSets = sourceFileSets.elements();
            while( upToDate && enumSourceSets.hasMoreElements() )
            {

                FileSet sourceFS = (FileSet)enumSourceSets.nextElement();
                DirectoryScanner sourceDS = sourceFS.getDirectoryScanner( project );
                String[] sourceFiles = sourceDS.getIncludedFiles();

                for( int i = 0; upToDate && i < sourceFiles.length; i++ )
                {
                    File src = new File( sourceFS.getDir( project ), sourceFiles[ i ] );

                    if( src.lastModified() > now )
                    {
                        log( "Warning: " + sourceFiles[ i ] + " modified in the future.",
                             Project.MSG_WARN );
                    }

                    Enumeration enumTargets = allTargets.elements();
                    while( upToDate && enumTargets.hasMoreElements() )
                    {

                        File dest = (File)enumTargets.nextElement();
                        if( src.lastModified() > dest.lastModified() )
                        {
                            log( dest.getPath() + " is out of date with respect to " +
                                 sourceFiles[ i ], Project.MSG_VERBOSE );
                            upToDate = false;

                        }
                    }
                }
            }
        }

        //
        // Check targets vs source files specified via filelists
        //
        if( upToDate )
        {
            Enumeration enumSourceLists = sourceFileLists.elements();
            while( upToDate && enumSourceLists.hasMoreElements() )
            {

                FileList sourceFL = (FileList)enumSourceLists.nextElement();
                String[] sourceFiles = sourceFL.getFiles( project );

                int i = 0;
                do
                {
                    File src = new File( sourceFL.getDir( project ), sourceFiles[ i ] );

                    if( src.lastModified() > now )
                    {
                        log( "Warning: " + sourceFiles[ i ] + " modified in the future.",
                             Project.MSG_WARN );
                    }

                    if( !src.exists() )
                    {
                        log( sourceFiles[ i ] + " does not exist.", Project.MSG_VERBOSE );
                        upToDate = false;
                        break;
                    }

                    Enumeration enumTargets = allTargets.elements();
                    while( upToDate && enumTargets.hasMoreElements() )
                    {

                        File dest = (File)enumTargets.nextElement();

                        if( src.lastModified() > dest.lastModified() )
                        {
                            log( dest.getPath() + " is out of date with respect to " +
                                 sourceFiles[ i ], Project.MSG_VERBOSE );
                            upToDate = false;

                        }
                    }
                } while( upToDate && ( ++i < sourceFiles.length ) );
            }
        }

        if( !upToDate )
        {
            log( "Deleting all target files. ", Project.MSG_VERBOSE );
            for( Enumeration e = allTargets.elements(); e.hasMoreElements(); )
            {
                File fileToRemove = (File)e.nextElement();
                log( "Deleting file " + fileToRemove.getAbsolutePath(), Project.MSG_VERBOSE );
                fileToRemove.delete();
            }
        }

    }//-- execute

}//-- DependSet.java
