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
package org.apache.ant.fscache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.ResourceUtils;

/**
 * Resource that caches another Resource to the filesystem before returning its content.
 */
public class CachedResource extends FileResource {
    private Resource wrapped;
    private boolean deleteOnExit;

    private boolean cached;

    /**
     * Create a new FilesystemCacheResource.
     */
    public CachedResource() {
    }

    /**
     * Create a new FilesystemCacheResource.
     */
    public CachedResource(Project project, Resource wrapped, File basedir, String name,
            boolean deleteOnExit) {
        super(basedir, name);
        setProject(project);
        this.wrapped = wrapped;
        this.deleteOnExit = deleteOnExit;
    }

    /**
     * Add the wrapped Resource.
     * @param wrapped to set
     */
    public void addConfigured(Resource wrapped) {
        if (this.wrapped != null) {
            throw new BuildException("Cannot wrap > 1 Resource");
        }
        this.wrapped = wrapped;
        
    }

    /**
     * Set whether to delete the cached content on JVM exit. Defaults to <code>false</code>
     * if <code>file</code> or <code>basedir</code> have been specified.
     * @param deleteOnExit to set
     */
    public void setDeleteOnExit(boolean deleteOnExit) {
        this.deleteOnExit = deleteOnExit;
    }

    /**
     * {@inheritDoc}
     */
    public File getFile() {
        cacheContent();
        return super.getFile();
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream() throws IOException {
        cacheContent();
        return super.getInputStream();
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void setFile(File f) {
        super.setFile(f);
        cached = false;
    }

    private synchronized void cacheContent() {
        if (cached) {
            return;
        }
        if (wrapped == null) {
            throw new BuildException("A wrapped Resource is required");
        }
        boolean del = deleteOnExit;
        File f = super.getFile();
        if (f == null) {
            File basedir = super.getBaseDir();
            if (basedir == null) {
                basedir = new File(System.getProperty("java.io.tmpdir"));
                setBaseDir(basedir);
                del = true;
            }
            f = new File(basedir, wrapped.getName());
            setFile(f);
        }
        try {
            ResourceUtils.copyResource(wrapped, new FileResource(f), null, null, true, true, null,
                    null, getProject());
        } catch (IOException e) {
            throw new BuildException(e);
        }
        if (del) {
            f.deleteOnExit();
        }
        cached = true;
    }
}
