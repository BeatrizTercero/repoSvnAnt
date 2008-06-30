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
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.util.FileNameMapper;

/**
 * Collects the Resources from one or more nested ResourceCollections under a single
 * directory on the filesystem, for tasks that require files to operate.
 */
public class CachedCollection extends Resources {
    private class MyIterator implements Iterator {
        private Iterator wrapped;
        private CachedResource[] set;
        private int pos;

        /**
         * Create a new MyIterator.
         */
        MyIterator() {
            this.wrapped = CachedCollection.super.iterator();
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            return set != null && pos < set.length || wrapped.hasNext();
        }

        /**
         * {@inheritDoc}
         */
        public Object next() {
            if (set != null && pos < set.length) {
                return set[pos++];
            }
            do {
                set = wrap((Resource) wrapped.next());
            } while (set.length == 0);
            pos = 1;
            return set[0];
        }

        /**
         * {@inheritDoc}
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private File basedir;
    private Boolean deleteOnExit;
    private FileNameMapper fileNameMapper;
    private boolean enableMultipleMappings;

    /**
     * {@inheritDoc}
     */
    public boolean isFilesystemOnly() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Iterator iterator() {
        return new MyIterator();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized int size() {
        if (fileNameMapper == null || !enableMultipleMappings) {
            return super.size();
        }
        int result = 0;
        for (Iterator iter = super.iterator(); iter.hasNext();) {
            String[] s = fileNameMapper.mapFileName(((Resource) iter.next()).getName());
            result += s == null || s.length == 0 ? 1 : s.length;
        }
        return result;
    }

    /**
     * Set the basedir. Defaults to a generated directory under <code>${java.io.tmpdir}</code>.
     * @param basedir the File to set
     */
    public void setBasedir(File basedir) {
        this.basedir = basedir;
        invalidateExistingIterators();
    }

    /**
     * Set the deleteOnExit. Default behavior is <code>false</code> if <code>basedir</code> is set, <code>true</code> otherwise.
     * @param deleteOnExit the boolean to set
     */
    public void setDeleteOnExit(boolean deleteOnExit) {
        this.deleteOnExit = deleteOnExit ? Boolean.TRUE : Boolean.FALSE;
        invalidateExistingIterators();
    }

    /**
     * Add the FileNameMapper to assist in converting resource names to filesystem paths below <code>basedir</code>.
     * @param fileNameMapper to add
     */
    public synchronized void addConfigured(FileNameMapper fileNameMapper) {
        if (this.fileNameMapper != null) {
            throw new BuildException("Cannot add > 1 filenamemapper");
        }
        this.fileNameMapper = fileNameMapper;
        invalidateExistingIterators();
    }

    /**
     * Add a FileNameMapper using the <mapper> element.
     * @param mapper to add
     */
    public void addConfigured(Mapper mapper) {
        addConfigured(mapper.getImplementation());
    }

    /**
     * Set the enableMultipleMappings. Default is false.
     * @param enableMultipleMappings the boolean to set
     */
    public void setEnableMultipleMappings(boolean enableMultipleMappings) {
        this.enableMultipleMappings = enableMultipleMappings;
    }

    private CachedResource[] wrap(Resource resource) {
        boolean del = Boolean.TRUE == deleteOnExit || deleteOnExit == null && basedir == null;
        File dir = basedir == null ? generateTempDir(del) : basedir;
        getProject().log("FilesystemCache del = " + del, Project.MSG_DEBUG);
        String[] names = fileNameMapper == null ? null : fileNameMapper.mapFileName(resource
                .getName());
        int count = names != null && names.length > 0 && enableMultipleMappings ? names.length : 1;
        CachedResource[] result = new CachedResource[count];
        if (names == null || names.length == 0) {
            result[0] = new CachedResource(getProject(), resource, dir,
                    resource.getName(), del);
        } else {
            for (int i = 0; i < count; i++) {
                result[i] = new CachedResource(getProject(), resource, dir, names[i], del);
            }
        }
        return result;
    }

    private static synchronized File generateTempDir(boolean deleteOnExit) {
        try {
            File result = new File(System.getProperty("java.io.tmpdir"),
                    "org.apache.tools.ant.types.resources.FilesystemCache"
                            + System.currentTimeMillis());
            if (deleteOnExit) {
                result.deleteOnExit();
            }
            return result;
        } finally {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
