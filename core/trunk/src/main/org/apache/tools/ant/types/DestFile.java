/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.tools.ant.types;

import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 * This wrapper class is used to represent Destination Files.
 *
 * @author <a href="mailto:umagesh@apache.org">Magesh Umasankar</a>
 */
public final class DestFile extends ValidatedFileAttribute {

    private String message;

    /**
     * empty constructor
     */
    public DestFile() {}


     /**
     * file constructor; performs validation
     * @param file the file to use
     */
    public DestFile(File file) throws BuildException {
        setFile(file);
    }

    protected final String getMessage() {
        return message;
    }

    /**
     * @return false if DestFile is null or if DestFile exists, but
     * is not a file.  Return true, otherwise.
     */
    protected final boolean isValid() {
        File f = getFile();
        if (f == null) {
            message = "DestFile must not be null";
            return false;
        }
        if (f.exists() && !f.isFile()) {
            message = f + " is not a file.";
            return false;
        }
        //If DestFile does not exist, make sure it is well formed.
        if (!f.exists()) {
            File tmp = f;
            while (tmp.getParent() != null) {
                File parent = new File(tmp.getParent());
                if (parent.exists()) {
                    if (!parent.isDirectory()) {
                        message = f + " contains the path "
                                  + parent + " that is not a directory.";
                        return false;
                    }
                    break;
                }
                tmp = parent;
            }
        }
        return true;
    }

    /**
     * test for the dest file being newer than the file passed in.
     * returns true iff the dest exists and is the same age or newer
     * @pre getFile()!=null && dependent!=null
     * @param dependent file we are dependent on
     * @return true iff we are up to date
     */
    public boolean isUpToDate(File dependent) {
      if(!getFile().exists())
          return false;
      return getFile().lastModified() >= dependent.lastModified();
    }

    /**
     * test for the dest file being newer than the SrcFile passed in.
     * returns true iff the dest exists and is the same age or newer
     * @pre getFile()!=null
     * @pre dependent!=null && depedent.getFile!=null;
     * @param dependent file we are dependent on
     * @return true iff we are up to date
     */
     public boolean isUpToDate(SrcFile dependent) {
        return isUpToDate(dependent.getFile());
    }
}
