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
 *
 */
package org.apache.tools.ant.taskdefs.optional.starteam;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.BuildException;

import java.util.StringTokenizer;
import java.io.IOException;

import com.starbase.starteam.File;
import com.starbase.starteam.Folder;
import com.starbase.starteam.View;
import com.starbase.starteam.ViewConfiguration;
import com.starbase.util.OLEDate;
import com.starbase.starteam.StarTeamFinder;

/**
 * FileBasedTask.java
 * This abstract class is the base for any tasks that are tree-based, that
 * is, for tasks which iterate over a tree of folders in StarTeam which
 * is reflected in a tree of folder the local machine.  
 *
 * This class provides the tree-iteration functionality.  Derived classes
 * will implement their specific task functionality by the visitor pattern,
 * specifically by implementing the method 
 * <code>visit(Folder rootStarteamFolder, java.io.File rootLocalFolder)</code>
 *
 * Created: Sat Dec 15 16:55:19 2001
 *
 * @author <a href="mailto:stevec@ignitesports.com">Steve Cohen</a>
 * @version 1.0
 * @see <A HREF="http://www.starbase.com/">StarBase Web Site</A> 
 */

public abstract class TreeBasedTask extends StarTeamTask {

 

    ///////////////////////////////////////////////////////////////
    // default values for attributes.
    ///////////////////////////////////////////////////////////////    
    /**
     * This constant sets the filter to include all files. This default has
     * the same result as <CODE>setIncludes("*")</CODE>.
     *
     * @see #getIncludes()
     * @see #setIncludes(String includes)
     */
    public final static String DEFAULT_INCLUDESETTING = "*";

    /**
     * This disables the exclude filter by default. In other words, no files
     * are excluded. This setting is equivalent to
     * <CODE>setExcludes(null)</CODE>.
     *
     * @see #getExcludes()
     * @see #setExcludes(String excludes)
     */
    public final static String DEFAULT_EXCLUDESETTING = null;

    //ATTRIBUTES settable from ant.

    /**
     * The root folder of the operation in StarTeam.
     */
    private String rootStarteamFolder = "/";

    /**
     * The local folder corresponding to starteamFolder.  If not specified
     * the Star Team defalt folder will be used.
     */
    private String rootLocalFolder = null;

    /**
     * All files that fit this pattern are checked out.
     */
    private String includes = DEFAULT_INCLUDESETTING;

    /**
     * All files fitting this pattern are ignored.
     */
    private String excludes = DEFAULT_EXCLUDESETTING;

    ///////////////////////////////////////////////////////////////
    // GET/SET methods.  
    // Setters, of course are where ant user passes in values.
    ///////////////////////////////////////////////////////////////

    /**
     * Set the root folder in the Starteam repository for this operation
     */
    public void setRootStarteamFolder(String rootStarteamFolder) {
        this.rootStarteamFolder = rootStarteamFolder;
    }

    /**
     * returns the root folder in the Starteam repository 
     * used for this operation
     */
    public String getRootStarteamFolder() {
        return this.rootStarteamFolder;
    }

    /**
     * Set the local folder corresponding to the 
     * starteam folder for this operation.
     * If not specified, the StarTeam default will be used 
     * the default is used.
     */
    public void setRootLocalFolder(String rootLocalFolder) {
        this.rootLocalFolder = rootLocalFolder;
    }
    /**
     * Returns the local folder specified by the user, 
     * corresponding to the starteam folder for this operation.
     * or null if not specified
     */
    public String getRootLocalFolder() {
        return this.rootLocalFolder;
    }

    /**
     * sets the pattern of files to be included.  See setExcludes() for a 
     * description
     * @param includes A string of filter patterns to include. Separate the
     *                 patterns by spaces.
     * @see #getIncludes()
     * @see #setExcludes(String excludes)
     * @see #getExcludes()
     */
    public void setIncludes(String includes) {
        this.includes = includes;
    }

    /**
     * Gets the patterns from the include filter. Rather that duplicate the
     * details of AntStarTeanCheckOut's filtering here, refer to these
     * links:
     * 
     * @return A string of filter patterns separated by spaces.
     * @see #setIncludes(String includes)
     * @see #setExcludes(String excludes)
     * @see #getExcludes()
     */
    public String getIncludes() {
        return includes;
    }

    /**
     * Sets the exclude filter. When filtering files, AntStarTeamCheckOut
     * uses an unmodified version of <CODE>DirectoryScanner</CODE>'s
     * <CODE>match</CODE> method, so here are the patterns straight from the
     * Ant source code:
     * <BR><BR>
     * Matches a string against a pattern. The pattern contains two special
     * characters:
     * <BR>'*' which means zero or more characters,
     * <BR>'?' which means one and only one character.
     * <BR><BR>
     *  For example, if you want to check out all files except .XML and
     * .HTML files, you would put the following line in your program:
     * <CODE>setExcludes("*.XML,*.HTML");</CODE>
     * Finally, note that filters have no effect on the <B>directories</B>
     * that are scanned; you could not skip over all files in directories
     * whose names begin with "project," for instance.
     * <BR><BR>
     * Treatment of overlapping inlcudes and excludes: To give a simplistic
     * example suppose that you set your include filter to "*.htm *.html"
     * and your exclude filter to "index.*". What happens to index.html?
     * AntStarTeamCheckOut will not check out index.html, as it matches an
     * exclude filter ("index.*"), even though it matches the include
     * filter, as well.
     * <BR><BR>
     * Please also read the following sections before using filters:
     * 
     * @param excludes A string of filter patterns to exclude. Separate the
     *                 patterns by spaces.
     * @see #setIncludes(String includes)
     * @see #getIncludes()
     * @see #getExcludes()
     */
    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    /**
     * Gets the patterns from the exclude filter. Rather that duplicate the
     * details of AntStarTeanCheckOut's filtering here, refer to these
     * links:
     *
     * @return A string of filter patterns separated by spaces.
     * @see #setExcludes(String excludes)
     * @see #setIncludes(String includes)
     * @see #getIncludes()
     */
    public String getExcludes() {
        return excludes;
    }

    ///////////////////////////////////////////////////////////////
    // INCLUDE-EXCLUDE processing
    ///////////////////////////////////////////////////////////////

    /**
     * Look if the file should be processed by the task. 
     * Don't process it if it fits no include filters or if 
     * it fits an exclude filter.
     * @param pName the item name to look for being included.
     * @return whether the file should be checked out or not.
     */
    protected boolean shouldProcess(String pName){
        boolean includeIt = matchPatterns(getIncludes(), pName);
        boolean excludeIt = matchPatterns(getExcludes(), pName);
        return (includeIt && !excludeIt);
    }

    /**
     * Convenience method to see if a string match a one pattern
     * in given set of space-separated patterns.
     * @param patterns the space-separated list of patterns.
     * @param pName the name to look for matching.
     * @return whether the name match at least one pattern.
     */
    protected boolean matchPatterns(String patterns, String pName){
        if (patterns == null){
            return false;
        }
        StringTokenizer exStr = new StringTokenizer(patterns, ",");
        while (exStr.hasMoreTokens())
        {
            if (DirectoryScanner.match(exStr.nextToken(), pName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * This method does the work of opening the supplied  Starteam view and calling 
     * the visit method to perform the task.
     *
     * @exception BuildException if any error occurs in the processing
     */
     
     public void execute() throws BuildException {
        // Get view as of the current time?
        View view = StarTeamFinder.openView(getUserName() + ":" + getPassword()
                + "@" + getURL());
        View snapshot = new View(view, ViewConfiguration.createFromTime(
                new OLEDate()));
        Folder starteamrootfolder = 
	    StarTeamFinder.findFolder(snapshot.getRootFolder(), this.rootStarteamFolder);

        if ( null == starteamrootfolder) {
            throw new BuildException("Unable to find root folderin repository.");
        }

	java.io.File localrootfolder;

	if (null == this.rootLocalFolder) {
	    // use Star Team's default
	    localrootfolder = new java.io.File(starteamrootfolder.getPath());
	} else {
	    // force StarTeam to use our folder
	    localrootfolder = new java.io.File(getRootLocalFolder());
	    log("overriding local folder to " + localrootfolder);
	}

        // Inspect everything in the root folder and then recursively
        visit(starteamrootfolder, localrootfolder);
    }

    /**
     * Derived classes must override this class to define the actual processing 
     * to performed on each folder in the tree defined for the task
     *
     * @param rootStarteamFolder the StarTeam folderto be visited
     * @param rootLocalFolder the local mapping of rootStarteamFolder
     */
    protected abstract void visit(Folder rootStarteamFolder, java.io.File rootLocalFolder) 
	throws BuildException;

}




