/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * 4. The names "Ant" and "Apache Software
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

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/**
 * Task to CreateBaseline command to ClearCase.
 * <p>
 * The following attributes are interpreted:
 * <table border="1">
 *   <tr>
 *     <th>Attribute</th>
 *     <th>Values</th>
 *     <th>Required</th>
 *   </tr>
 *   <tr>
 *      <td>comment</td>
 *      <td>Specify a comment. Only one of comment or cfile may be
used.</td>
 *      <td>No</td>
 *   </tr>
 *   <tr>
 *      <td>commentfile</td>
 *      <td>Specify a file containing a comment. Only one of comment or
cfile may be used.</td>
 *      <td>No</td>
 *   </tr>
 *   <tr>
 *      <td>baselinerootname</td>
 *      <td>Specify the name to be associated with the baseline.</td>
 *      <td>Yes</td>
 *   </tr>
 *   <tr>
 *      <td>nowarn</td>
 *      <td>Suppress warning messages</td>
 *      <td>No</td>
 *   <tr>
 *   <tr>
 *      <td>identical</td>
 *      <td>Allows the baseline to be created even if it is identical to the
previous baseline.</td>
 *      <td>No</td>
 *   </tr>
 *   <tr>
 *      <td>full</td>
 *      <td>Creates a full baseline.</td>
 *      <td>No</td>
 *   </tr>
 *   <tr>
 *      <td>nlabel</td>
 *      <td>Allows the baseline to be created without a label.</td>
 *      <td>No</td>
 *   </tr>
 * </table>
 *
 * @author Robert Anderson
 */
public class CCMkbl extends ClearCase {
    private String m_Comment = null;
    private String m_Cfile = null;
    private String m_BaselineRootName = null;
    private boolean m_Nwarn = false;
    private boolean m_Identical = true;
    private boolean m_Full = false;
    private boolean m_Nlabel = false;


    /**
     * Executes the task.
     * <p>
     * Builds a command line to execute cleartool and then calls Exec's run method
     * to execute the command line.
     */
    public void execute() throws BuildException {
        Commandline commandLine = new Commandline();
        Project aProj = getProject();
        int result = 0;

        // Default the viewpath to basedir if it is not specified
        if (getViewPath() == null) {
            setViewPath(aProj.getBaseDir().getPath());
        }

        // build the command line from what we got. the format is
        // cleartool checkin [options...] [viewpath ...]
        // as specified in the CLEARTOOL.EXE help
        commandLine.setExecutable(getClearToolCommand());
        commandLine.createArgument().setValue(COMMAND_MKBL);

        checkOptions(commandLine);

        result = run(commandLine);
        if (Execute.isFailure(result)) {
            String msg = "Failed executing: " + commandLine.toString();
            throw new BuildException(msg, location);
        }
    }


    /**
     * Check the command line options.
     */
    private void checkOptions(Commandline cmd) {
        if (getComment() != null) {
            // -c
            getCommentCommand(cmd);
        } else {
            if (getCommentFile() != null) {
                // -cfile
                getCommentFileCommand(cmd);
            } else {
                cmd.createArgument().setValue(FLAG_NOCOMMENT);
            }
        }

        if (getIdentical()) {
            // -identical
            cmd.createArgument().setValue(FLAG_IDENTICAL);
        }

       if (getFull()) {
           // -full
           cmd.createArgument().setValue(FLAG_FULL);
       } else {
           // -incremental
           cmd.createArgument().setValue(FLAG_INCREMENTAL);
       }

       if (getNlabel()) {
           // -nlabel
           cmd.createArgument().setValue(FLAG_NLABEL);
       }

       // baseline_root_name
        cmd.createArgument().setValue(getBaselineRootName());

    }


    /**
     * Set comment string
     *
     * @param comment the comment string
     */
    public void setComment(String comment) {
        m_Comment = comment;
    }

    /**
     * Get comment string
     *
     * @return String containing the comment
     */
    public String getComment() {
        return m_Comment;
    }

    /**
     * Set comment file
     *
     * @param cfile the path to the comment file
     */
    public void setCommentFile(String cfile) {
        m_Cfile = cfile;
    }

    /**
     * Get comment file
     *
     * @return String containing the path to the comment file
     */
    public String getCommentFile() {
        return m_Cfile;
    }

    /**
     * Set baseline_root_name
     *
     * @param baseline_root_name the name of the baseline
     */
    public void setBaselineRootName(String baseline_root_name) {
        m_BaselineRootName = baseline_root_name;
    }

    /**
     * Get baseline_root_name
     *
     * @return String containing the name of the baseline
     */
    public String getBaselineRootName() {
        return m_BaselineRootName;
    }

    /**

    /**
     * Set the nowarn flag
     *
     * @param nwarn the status to set the flag to
     */
    public void setNoWarn(boolean nwarn) {
        m_Nwarn = nwarn;
    }

    /**
     * Get nowarn flag status
     *
     * @return boolean containing status of nwarn flag
     */
    public boolean getNoWarn() {
        return m_Nwarn;
    }

    /**
     * Set the identical flag
     *
     * @param identical the status to set the flag to
     */
    public void setIdentical(boolean identical) {
        m_Identical = identical;
    }

    /**
     * Get identical flag status
     *
     * @return boolean containing status of identical flag
     */
    public boolean getIdentical() {
        return m_Identical;
    }

    /**
     * Set the full flag
     *
     * @param full the status to set the flag to
     */
    public void setFull(boolean full) {
        m_Full = full;
    }

    /**
     * Get full flag status
     *
     * @return boolean containing status of full flag
     */
    public boolean getFull() {
        return m_Full;
    }

    /**
     * Set the nlabel flag
     *
     * @param nlabel the status to set the flag to
     */
    public void setNlabel(boolean nlabel) {
        m_Nlabel = nlabel;
    }

    /**
     * Get nlabel status
     *
     * @return boolean containing status of nlabel flag
     */
    public boolean getNlabel() {
        return m_Nlabel;
    }

    /**
     * Get the 'comment' command
     *
     * @return the 'comment' command if the attribute was specified,
     *         otherwise an empty string
     *
     * @param CommandLine containing the command line string with or
     *                    without the comment flag and string appended
     */
    private void getCommentCommand(Commandline cmd) {
        if (getComment() != null) {
            /* Had to make two separate commands here because if a space is
               inserted between the flag and the value, it is treated as a
               Windows filename with a space and it is enclosed in double
               quotes ("). This breaks clearcase.
            */
            cmd.createArgument().setValue(FLAG_COMMENT);
            cmd.createArgument().setValue(getComment());
        }
    }

    /**
     * Get the 'commentfile' command
     *
     * @return the 'commentfile' command if the attribute was specified,
     *         otherwise an empty string
     *
     * @param cmd CommandLine containing the command line string with or
     *                    without the commentfile flag and file appended
     */
    private void getCommentFileCommand(Commandline cmd) {
        if (getCommentFile() != null) {
            /* Had to make two separate commands here because if a space is
               inserted between the flag and the value, it is treated as a
               Windows filename with a space and it is enclosed in double
               quotes ("). This breaks clearcase.
            */
            cmd.createArgument().setValue(FLAG_COMMENTFILE);
            cmd.createArgument().setValue(getCommentFile());
        }
    }


        /**
     * -c flag -- comment to attach to the file
     */
    public static final String FLAG_COMMENT = "-c";
        /**
     * -cfile flag -- file containing a comment to attach to the file
     */
    public static final String FLAG_COMMENTFILE = "-cfile";
        /**
     * -nc flag -- no comment is specified
     */
    public static final String FLAG_NOCOMMENT = "-nc";
        /**
     * -identical flag -- allows the file to be checked in even if it is identical to the original
     */
    public static final String FLAG_IDENTICAL = "-identical";
       /**
     * -incremental flag -- baseline to be created is incremental
     */
    public static final String FLAG_INCREMENTAL = "-incremental";
       /**
     * -full flag -- baseline to be created is full
     */
    public static final String FLAG_FULL = "-full";
       /**
     * -nlabel -- baseline to be created without a label
     */
    public static final String FLAG_NLABEL = "-nlabel";


}
