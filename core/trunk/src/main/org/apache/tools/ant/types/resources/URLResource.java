/*
 * Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;

/**
 * Exposes a URL as a Resource.
 * @since Ant 1.7
 */
public class URLResource extends Resource {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final int NULL_URL
        = Resource.getMagicNumber("null URL".getBytes());

    private URL url;
    private String javaResource;
    private URLConnection conn;
    private Path classpath;

    /**
     * Default constructor.
     */
    public URLResource() {
    }

    /**
     * Convenience constructor.
     * @param u the URL to expose.
     */
    public URLResource(URL u) {
        setURL(u);
    }

    /**
     * Convenience constructor.
     * @param f the File to set as a URL.
     */
    public URLResource(File f) {
        setFile(f);
    }

    /**
     * String constructor for Ant attribute introspection.
     * @param u String representation of this URL.
     * @see org.apache.tools.ant.IntrospectionHelper
     */
    public URLResource(String u) {
        this(newURL(u));
    }

    /**
     * Set the URL for this URLResource.
     * @param u the URL to expose.
     */
    public synchronized void setURL(URL u) {
        checkAttributesAllowed();
        url = u;
    }

    /**
     * Set the URL from a File.
     * @param f the File to set as a URL.
     */
    public synchronized void setFile(File f) {
        try {
            setURL(FILE_UTILS.getFileURL(f));
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Set the resource name with which to expose a Java resource.
     * @param s the Java resource name.
     * @see java.lang.ClassLoader#getResource()
     */
    public synchronized void setJavaResource(String s) {
        checkAttributesAllowed();
        javaResource = s;
    }

    /**
     * Set the classpath for this URLResource.
     * @param p the Path against which to resolve Java resources.
     */
    public synchronized void setClasspath(Path p) {
        checkAttributesAllowed();
        addConfiguredClasspath(p);
    }

    /**
     * Create a nested classpath element.
     * @return a Path object.
     */
    public synchronized void addConfiguredClasspath(Path p) {
        checkChildrenAllowed();
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        classpath.add(p);
    }

    /**
     * Get the URL used by this URLResource.
     * @return a URL object.
     */
    public synchronized URL getURL() {
        if (isReference()) {
            return ((URLResource) getCheckedRef()).getURL();
        }
        if (url == null && javaResource != null) {
            ClassLoader cl = null;
            AntClassLoader acl = null;
            if (classpath != null) {
                acl = getProject().createClassLoader(classpath);
                cl = acl;
            } else {
                cl = getClass().getClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }
            }
            if (cl != null) {
                setURL(cl.getResource(javaResource));
                if (acl != null) {
                    acl.cleanup();
                }
            }
        }
        return url;
     }

    /**
     * Overrides the super version.
     * @param r the Reference to set.
     */
    public synchronized void setRefid(Reference r) {
        //not using the accessor in this case to avoid side effects
        if (url != null || javaResource != null) {
            throw tooManyAttributes();
        }
        super.setRefid(r);
    }

    /**
     * Get the name of this URLResource
     * (its file component minus the leading separator).
     * @return the name of this resource.
     */
    public synchronized String getName() {
        return isReference() ? ((Resource) getCheckedRef()).getName()
            : getURL().getFile().substring(1);
    }

    /**
     * Return this URLResource formatted as a String.
     * @return a String representation of this URLResource.
     */
    public synchronized String toString() {
        return isReference()
            ? getCheckedRef().toString() : String.valueOf(getURL());
    }

    /**
     * Find out whether the URL exists .
     * @return true if this resource exists.
     */
    public synchronized boolean isExists() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).isExists();
        }
        if (getURL() == null) {
            return false;
        }
        try {
            connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Tells the modification time in milliseconds since 01.01.1970 .
     *
     * @return 0 if the resource does not exist to mirror the behavior
     * of {@link java.io.File File}.
     */
    public synchronized long getLastModified() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getLastModified();
        }
        if (!isExists()) {
            return 0L;
        }
        try {
            connect();
            return conn.getLastModified();
        } catch (IOException e) {
            return 0L;
        }
    }

    /**
     * Tells if the resource is a directory.
     * @return boolean whether the resource is a directory.
     */
    public synchronized boolean isDirectory() {
        return isReference()
            ? ((Resource) getCheckedRef()).isDirectory()
            : getName().endsWith("/");
    }

    /**
     * Get the size of this Resource.
     * @return the size, as a long, 0 if the Resource does not exist (for
     *         compatibility with java.io.File), or UNKNOWN_SIZE if not known.
     */
    public synchronized long getSize() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getSize();
        }
        if (!isExists()) {
            return 0L;
        }
        try {
            connect();
            return conn.getContentLength();
        } catch (IOException e) {
            return UNKNOWN_SIZE;
        }
    }

    /**
     * Test whether an Object equals this URLResource.
     * @param another the other Object to compare.
     * @return true if the specified Object is equal to this Resource.
     */
    public synchronized boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (isReference()) {
            return getCheckedRef().equals(another);
        }
        if (!(another.getClass().equals(getClass()))) {
            return false;
        }
        URLResource otheru = (URLResource) another;
        return getURL() == null
            ? otheru.getURL() == null
            : getURL().equals(otheru.getURL());
    }

    /**
     * Get the hash code for this Resource.
     * @return hash code as int.
     */
    public synchronized int hashCode() {
        if (isReference()) {
            return getCheckedRef().hashCode();
        }
        return MAGIC * ((getURL() == null) ? NULL_URL : getURL().hashCode());
    }

    /**
     * Get an InputStream for the Resource.
     * @return an InputStream containing this Resource's content.
     * @throws IOException if unable to provide the content of this
     *         Resource as a stream.
     * @throws UnsupportedOperationException if InputStreams are not
     *         supported for this Resource type.
     */
    public synchronized InputStream getInputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        connect();
        try {
            return conn.getInputStream();
        } finally {
            conn = null;
        }
    }

    /**
     * Get an OutputStream for the Resource.
     * @return an OutputStream to which content can be written.
     * @throws IOException if unable to provide the content of this
     *         Resource as a stream.
     * @throws UnsupportedOperationException if OutputStreams are not
     *         supported for this Resource type.
     */
    public synchronized OutputStream getOutputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getOutputStream();
        }
        connect();
        try {
            return conn.getOutputStream();
        } finally {
            conn = null;
        }
    }

    /**
     * Ensure that we have a connection.
     */
    protected synchronized void connect() throws IOException {
        URL u = getURL();
        if (u == null) {
            throw new BuildException("URL not set");
        }
        if (conn == null) {
            try {
                conn = u.openConnection();
                conn.connect();
            } catch (IOException e) {
                log(e.toString(), Project.MSG_ERR);
                conn = null;
                throw e;
            }
        }
    }

    /**
     * Finalize this URLResource.
     * @throws Throwable on error.
     */
    protected void finalize() throws Throwable {
        conn = null;
        super.finalize();
    }

    private static URL newURL(String u) {
        try {
            return new URL(u);
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }

}
